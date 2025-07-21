// src/test/java/com/example/demo/AvancarFaseTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AvancarFaseTest {

    @Autowired private TorneioService torneioService;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;
    @Autowired private TorneioRepository torneioRepository;

    @Test
    void deveGerarMataMataCom14Equipes_Dando2Byes_e_Criando6Partidas() throws Exception {
        // --- 1. CENÁRIO ---
        // Simular um torneio com 14 equipes classificadas (7 primeiros e 7 segundos)
        
        Esporte esporte = esporteRepository.save(new Esporte("Handebol", 7, 14));
        CategoriaCurso categoria = CategoriaCurso.INTEGRADO;
        
        Torneio torneio = new Torneio();
        torneio.setEsporte(esporte);
        torneio.setCategoria(categoria);
        
        // Criar 7 grupos, cada um com um 1º e 2º colocado bem definidos pelos pontos
        for (int i = 1; i <= 7; i++) {
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + i);
            grupo.setTorneio(torneio);
            
            // Adiciona o campeão do grupo (mais pontos)
            grupo.getEquipes().add(criarEquipe("Time " + i + "-A (1º)", esporte, categoria, (i*10)+1, 6));
            // Adiciona o vice-campeão do grupo (menos pontos)
            grupo.getEquipes().add(criarEquipe("Time " + i + "-B (2º)", esporte, categoria, (i*10)+2, 3));
            
            torneio.getGrupos().add(grupo);
        }
        torneioRepository.save(torneio);

        // Verificar o setup
        long totalEquipes = torneio.getGrupos().stream().mapToLong(g -> g.getEquipes().size()).sum();
        assertThat(totalEquipes).isEqualTo(14);

        // --- 2. AÇÃO ---
        List<Partida> partidasMataMata = torneioService.gerarMataMata(torneio.getId());
        
        // --- 3. VERIFICAÇÃO ---
        // Com 14 classificados, o objetivo é a fase de 8 (Quartas).
        // (14 - 8) * 2 = 12 times jogam a preliminar.
        // 14 - 12 = 2 times recebem bye.
        // 12 times / 2 = 6 partidas.
        assertThat(partidasMataMata).isNotNull();
        assertThat(partidasMataMata).hasSize(6);

        // Verificação extra: garantir que as equipes que receberam bye são as de maior pontuação.
        // Coletar os IDs de todos os times que estão jogando.
        List<Long> idsJogando = partidasMataMata.stream()
                .flatMap(p -> List.of(p.getEquipeA().getId(), p.getEquipeB().getId()).stream())
                .collect(Collectors.toList());

        // Coletar todos os times que foram criados.
        List<Equipe> todasAsEquipes = equipeRepository.findAll();

        // Identificar os times que NÃO estão jogando (os que receberam bye).
        List<Equipe> equipesComBye = todasAsEquipes.stream()
                .filter(e -> !idsJogando.contains(e.getId()))
                .collect(Collectors.toList());

        // Os 2 times que receberam bye devem estar entre os 7 campeões de grupo.
        assertThat(equipesComBye).hasSize(2);
        for(Equipe equipe : equipesComBye) {
            assertThat(equipe.getNome()).contains("(1º)");
        }
    }
    
    private Equipe criarEquipe(String nomeEquipe, Esporte esporte, CategoriaCurso categoria, int idOffset, int pontos) {
        Curso curso = cursoRepository.save(new Curso("Curso " + idOffset, categoria));
        Tecnico tecnico = new Tecnico();
        tecnico.setMatricula("tec" + idOffset);
        tecnico.setTipo(TipoUsuario.TECNICO);
        tecnico.setNome("Téc " + nomeEquipe);
        tecnico.setSenha("123");
        usuarioRepository.save(tecnico);

        Equipe equipe = new Equipe();
        equipe.setNome(nomeEquipe);
        equipe.setCurso(curso);
        equipe.setEsporte(esporte);
        equipe.setTecnico(tecnico);
        equipe.setPontos(pontos); // Definimos os pontos manualmente para o teste
        return equipeRepository.save(equipe);
    }
}
