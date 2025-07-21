// src/test/java/com/example/demo/MataMataFluxoTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.ArbitroService;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MataMataFluxoTest {

    @Autowired private TorneioService torneioService;
    @Autowired private ArbitroService arbitroService;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;

    @Test
    void deveGerarMataMataComChaveamentoCorreto_Com6Equipes() throws Exception {
        // --- 1. CENÁRIO ---
        // Criar 6 equipes para garantir a formação de 2 grupos de 3.
        Esporte esporte = esporteRepository.save(new Esporte("Basquete", 5, 12));
        CategoriaCurso categoria = CategoriaCurso.SUPERIOR;
        
        // Grupo A
        criarEquipeCompleta("Time A1 (Forte)", esporte, categoria, 1);
        criarEquipeCompleta("Time A2 (Médio)", esporte, categoria, 2);
        criarEquipeCompleta("Time A3 (Fraco)", esporte, categoria, 3);
        // Grupo B
        criarEquipeCompleta("Time B1 (Forte)", esporte, categoria, 4);
        criarEquipeCompleta("Time B2 (Médio)", esporte, categoria, 5);
        criarEquipeCompleta("Time B3 (Fraco)", esporte, categoria, 6);

        Torneio torneio = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        Arbitro arbitro = new Arbitro();
        arbitro.setMatricula("arbMataMata");
        arbitro.setTipo(TipoUsuario.ARBITRO);
        arbitro.setNome("Árbitro Oficial");
        arbitro.setSenha("123");
        usuarioRepository.save(arbitro);

        // Simular os resultados da fase de grupos para definir os 1º e 2º lugares
        for (Partida partida : partidaRepository.findAll()) {
            if (partida.getEquipeA().getNome().contains("Forte")) {
                arbitroService.registrarResultado(arbitro.getMatricula(), partida.getId(), 10, 0);
            } else if (partida.getEquipeA().getNome().contains("Médio")) {
                 arbitroService.registrarResultado(arbitro.getMatricula(), partida.getId(), 5, 0);
            } else {
                 arbitroService.registrarResultado(arbitro.getMatricula(), partida.getId(), 0, 1);
            }
        }
        
        // --- 2. AÇÃO ---
        List<Partida> partidasMataMata = torneioService.gerarMataMata(torneio.getId());

        // --- 3. VERIFICAÇÃO ---
        assertThat(partidasMataMata).isNotNull();
        assertThat(partidasMataMata).hasSize(2); // Com 4 classificados (2 de cada grupo), esperamos 2 jogos.

        // Identificar os IDs dos times que terminaram em 1º e 2º
        Set<Long> primeirosColocadosIds = torneio.getGrupos().stream()
                .map(g -> {
                    g.getEquipes().sort((e1, e2) -> Integer.compare(e2.getPontos(), e1.getPontos()));
                    return g.getEquipes().get(0).getId();
                }).collect(Collectors.toSet());

        Set<Long> segundosColocadosIds = torneio.getGrupos().stream()
                .map(g -> {
                    g.getEquipes().sort((e1, e2) -> Integer.compare(e2.getPontos(), e1.getPontos()));
                    return g.getEquipes().get(1).getId();
                }).collect(Collectors.toSet());
        
        // Verificar se os jogos do mata-mata sempre opõem um 1º contra um 2º
        for (Partida partida : partidasMataMata) {
            Long idEquipeA = partida.getEquipeA().getId();
            Long idEquipeB = partida.getEquipeB().getId();

            boolean aEhPrimeiro = primeirosColocadosIds.contains(idEquipeA);
            boolean bEhPrimeiro = primeirosColocadosIds.contains(idEquipeB);
            boolean aEhSegundo = segundosColocadosIds.contains(idEquipeA);
            boolean bEhSegundo = segundosColocadosIds.contains(idEquipeB);

            assertThat((aEhPrimeiro && bEhSegundo) || (aEhSegundo && bEhPrimeiro)).isTrue();
        }
    }

    private void criarEquipeCompleta(String nomeEquipe, Esporte esporte, CategoriaCurso categoria, int idOffset) {
        Curso curso = cursoRepository.save(new Curso("Curso " + nomeEquipe, categoria));
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
        equipeRepository.save(equipe);
    }
}