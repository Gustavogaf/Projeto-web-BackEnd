// src/test/java/com/example/demo/TorneioServiceTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de teste reescrita para focar na validação da criação do torneio.
 * Usamos @DirtiesContext para garantir que cada teste execute em um ambiente limpo,
 * evitando erros de estado do Hibernate/JPA.
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TorneioServiceTest {

    @Autowired private TorneioService torneioService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private PartidaRepository partidaRepository;

    @Test
    void deveIniciarTorneioCom11Equipes_e_DistribuirEmGruposDe5_3_3() throws Exception {
        // --- 1. CENÁRIO (SETUP) ---
        Esporte futsal = esporteRepository.save(new Esporte("Futsal", 5, 10));
        CategoriaCurso categoria = CategoriaCurso.SUPERIOR;

        // Criar 11 equipes completas
        for (int i = 1; i <= 11; i++) {
            Curso curso = cursoRepository.save(new Curso("Curso de Teste " + i, categoria));
            
            Tecnico tecnico = new Tecnico();
            tecnico.setMatricula("tec" + i);
            tecnico.setNome("Técnico " + i);
            tecnico.setSenha("senha123");
            tecnico.setTipo(TipoUsuario.TECNICO);
            usuarioRepository.save(tecnico);

            Equipe equipe = new Equipe();
            equipe.setNome("Equipe " + i);
            equipe.setCurso(curso);
            equipe.setEsporte(futsal);
            equipe.setTecnico(tecnico);

            List<Atleta> atletas = new ArrayList<>();
            for (int j = 1; j <= 5; j++) {
                Atleta atleta = new Atleta();
                atleta.setMatricula("atl" + i + "-" + j);
                atleta.setNome("Atleta " + j + " da Equipe " + i);
                atleta.setApelido("Craque " + j);
                atleta.setTelefone("99999-" + i + j);
                atleta.setSenha("senha123");
                atleta.setTipo(TipoUsuario.ATLETA);
                atleta.setEquipe(equipe);
                atletas.add(atleta);
            }
            equipe.setAtletas(atletas);

            equipeRepository.save(equipe);
        }

        // --- 2. AÇÃO ---
        Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(futsal, categoria);

        // --- 3. VERIFICAÇÃO ---
        assertThat(torneioIniciado).isNotNull();
        assertThat(torneioIniciado.getId()).isNotNull();

        List<Long> tamanhosDosGrupos = torneioIniciado.getGrupos().stream()
                .map(grupo -> (long) grupo.getEquipes().size())
                .toList();
        
        assertThat(tamanhosDosGrupos).containsExactlyInAnyOrder(5L, 3L, 3L);

        long totalPartidasGeradas = partidaRepository.count();
        assertThat(totalPartidasGeradas).isEqualTo(16);
    }
}