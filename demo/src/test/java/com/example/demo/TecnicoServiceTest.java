// src/test/java/com/example/demo/TecnicoServiceTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TecnicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TecnicoServiceTest {

    @Autowired
    private TecnicoService tecnicoService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;

    private Tecnico tecnico;
    private Curso curso;
    private Esporte esporte;

    @BeforeEach
    void setUp() {
        // Limpa os repositórios para garantir que cada teste seja independente
        partidaRepository.deleteAll();
        equipeRepository.deleteAll();
        usuarioRepository.deleteAll();
        esporteRepository.deleteAll();
        cursoRepository.deleteAll();

        // Cenário base
        curso = cursoRepository.save(new Curso("Sistemas de Informação", CategoriaCurso.SUPERIOR));
        esporte = esporteRepository.save(new Esporte("Futsal", 5, 10));
        tecnico = new Tecnico();
        tecnico.setMatricula("tec001");
        tecnico.setNome("Técnico Principal");
        tecnico.setSenha("senha123");
        tecnico.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnico);
    }

    @Test
    void deveDeletarEquipeComSucesso_e_DesassociarAtletas() throws Exception {
        // Cenário
        Equipe equipe = new Equipe();
        equipe.setNome("Time a ser Deletado");
        equipe.setCurso(curso);
        equipe.setEsporte(esporte);
        equipe.setTecnico(tecnico);
        equipe.setAtletas(new ArrayList<>());

        Atleta atleta1 = new Atleta();
        atleta1.setMatricula("atl001");
        atleta1.setNome("Atleta 1");
        atleta1.setSenha("123");
        atleta1.setTelefone("111");
        atleta1.setTipo(TipoUsuario.ATLETA);
        atleta1.setEquipe(equipe);
        equipe.getAtletas().add(atleta1);

        // A cascade a partir da equipe vai salvar o atleta também
        equipeRepository.save(equipe);

        // Ação
        tecnicoService.deletarEquipe(tecnico.getMatricula(), equipe.getId());

        // Verificação
        assertThat(equipeRepository.existsById(equipe.getId())).isFalse();
        
        // O atleta ainda deve existir
        Atleta atletaVerificado = (Atleta) usuarioRepository.findById("atl001").orElseThrow();
        assertThat(atletaVerificado.getEquipe()).isNull();
    }

    @Test
    void naoDeveDeletarEquipeDeOutroTecnico() {
        // Cenário
        Tecnico outroTecnico = new Tecnico();
        outroTecnico.setMatricula("tec002");
        outroTecnico.setNome("Técnico Intruso");
        outroTecnico.setSenha("senha456");
        outroTecnico.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(outroTecnico);

        Equipe equipe = new Equipe();
        equipe.setNome("Time do Técnico Principal");
        equipe.setCurso(curso);
        equipe.setEsporte(esporte);
        equipe.setTecnico(tecnico); // Dono é o técnico principal
        equipeRepository.save(equipe);

        // Ação e Verificação
        Exception exception = assertThrows(Exception.class, () -> {
            // Tentativa de exclusão pelo técnico intruso
            tecnicoService.deletarEquipe(outroTecnico.getMatricula(), equipe.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("Você não tem permissão para deletar esta equipe.");
    }

    @Test
    void naoDeveDeletarEquipeComPartidasAssociadas() {
        // Cenário
        // Criação correta do Técnico B
        Tecnico tecnicoB = new Tecnico();
        tecnicoB.setMatricula("tecB");
        tecnicoB.setNome("Técnico B");
        tecnicoB.setSenha("senhaB");
        tecnicoB.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnicoB);

        Equipe equipeA = new Equipe();
        equipeA.setNome("Time A com Jogo");
        equipeA.setCurso(curso);
        equipeA.setEsporte(esporte);
        equipeA.setTecnico(tecnico);
        equipeRepository.save(equipeA);

        Equipe equipeB = new Equipe();
        equipeB.setNome("Time B com Jogo");
        equipeB.setCurso(cursoRepository.save(new Curso("Outro Curso", CategoriaCurso.SUPERIOR)));
        equipeB.setEsporte(esporte);
        equipeB.setTecnico(tecnicoB); // Associação com o técnico B já salvo
        equipeRepository.save(equipeB);
        
        Partida partida = new Partida();
        partida.setEquipeA(equipeA);
        partida.setEquipeB(equipeB);
        partida.setDataHora(LocalDateTime.now());
        partidaRepository.save(partida);

        // Ação e Verificação
        Exception exception = assertThrows(Exception.class, () -> {
            tecnicoService.deletarEquipe(tecnico.getMatricula(), equipeA.getId());
        });

        assertThat(exception.getMessage()).isEqualTo("Não é possível deletar esta equipe, pois ela já está associada a partidas em um torneio.");
    }
}