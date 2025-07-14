// src/test/java/com/example/demo/TorneioServiceTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TorneioServiceTest {

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private TorneioRepository torneioRepository;

    private Esporte esporte;
    private CategoriaCurso categoria;

    @BeforeEach
    void setUp() {
        // Limpeza completa para garantir independência
        partidaRepository.deleteAll();
        torneioRepository.deleteAll();
        equipeRepository.deleteAll();
        usuarioRepository.deleteAll();
        cursoRepository.deleteAll();
        esporteRepository.deleteAll();

        // --- CENÁRIO INICIAL ---
        // 1. Definir o esporte e a categoria do torneio
        esporte = esporteRepository.save(new Esporte("Futsal", 5, 10));
        categoria = CategoriaCurso.SUPERIOR;

        // 2. Criar 7 equipes para este esporte e categoria
        for (int i = 1; i <= 7; i++) {
            Curso curso = cursoRepository.save(new Curso("Curso " + i, categoria));
            Tecnico tecnico = new Tecnico();
            tecnico.setMatricula("tec" + i);
            tecnico.setNome("Técnico " + i);
            tecnico.setSenha("123");
            tecnico.setTipo(TipoUsuario.TECNICO);
            usuarioRepository.save(tecnico);

            criarEquipe("Equipe " + i, curso, esporte, tecnico, i);
        }
    }

    @Test
    void deveGerarFaseDeGruposCorretamente() throws Exception {
        // Ação
        Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        // Verificação
        assertThat(torneioIniciado).isNotNull();
        assertThat(torneioIniciado.getId()).isNotNull();
        assertThat(torneioIniciado.getEsporte().getNome()).isEqualTo("Futsal");
        assertThat(torneioIniciado.getCategoria()).isEqualTo(CategoriaCurso.SUPERIOR);

        // Verificar a distribuição dos grupos (baseado na nossa lógica simplificada)
        // 7 equipes devem ser divididas em 2 grupos (um de 4, um de 3)
        assertThat(torneioIniciado.getGrupos()).hasSize(2);
        long totalDeEquipesNosGrupos = torneioIniciado.getGrupos().stream()
                .mapToLong(grupo -> grupo.getEquipes().size())
                .sum();
        assertThat(totalDeEquipesNosGrupos).isEqualTo(7);

        // Verificar se as partidas foram geradas
        // Grupo A (4 equipes) -> 6 partidas (A vs B, A vs C, A vs D, B vs C, B vs D, C vs D)
        // Grupo B (3 equipes) -> 3 partidas (A vs B, A vs C, B vs C)
        // Total = 9 partidas
        long totalPartidasGeradas = partidaRepository.count();
        assertThat(totalPartidasGeradas).isEqualTo(9);
    }

    private void criarEquipe(String nome, Curso curso, Esporte esporte, Tecnico tecnico, int idOffset) {
        Equipe equipe = new Equipe();
        equipe.setNome(nome);
        equipe.setCurso(curso);
        equipe.setEsporte(esporte);
        equipe.setTecnico(tecnico);

        // 1. PRIMEIRO, SALVE A EQUIPE PARA TORNÁ-LA PERSISTENTE
        // Agora o objeto 'equipe' tem um ID e não é mais transiente.
        equipeRepository.save(equipe);

        // 2. AGORA, CRIE E CONFIGURE OS ATLETAS
        List<Atleta> atletas = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Atleta atleta = new Atleta();
            atleta.setMatricula("atl" + idOffset + i);
            atleta.setNome("Atleta " + nome + " " + i);
            atleta.setSenha("123");
            atleta.setTipo(TipoUsuario.ATLETA);
            atleta.setTelefone("999" + idOffset + i);
            
            // Associe o atleta à equipe JÁ PERSISTIDA
            atleta.setEquipe(equipe); 
            atletas.add(atleta);
        }

        // 3. SALVE OS ATLETAS, QUE AGORA TÊM UMA REFERÊNCIA VÁLIDA
        usuarioRepository.saveAll(atletas);

        // 4. ATUALIZE A LISTA DE ATLETAS NA EQUIPE
        equipe.setAtletas(atletas);
        equipeRepository.save(equipe); // Salva a associação final
    }
}