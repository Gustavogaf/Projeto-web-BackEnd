// src/test/java/com/example/demo/PartidaFluxoTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.CursoRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.EsporteRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.Service.ArbitroService;
import com.example.demo.Service.TecnicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class PartidaFluxoTest {

    @Autowired
    private ArbitroService arbitroService;
    @Autowired
    private TecnicoService tecnicoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private EquipeRepository equipeRepository; 


    private Arbitro arbitro;
    private Partida partidaAgendada;

    @BeforeEach
    void setUp() throws Exception {
        // Limpando dados para garantir a independência dos testes
        partidaRepository.deleteAll();
        equipeRepository.deleteAll();
        usuarioRepository.deleteAll();
        cursoRepository.deleteAll();
        esporteRepository.deleteAll();

        // --- CENÁRIO INICIAL ---
        // 1. Criar dados base (curso, esporte, tecnico A)
        Curso cursoA = cursoRepository.save(new Curso("Sistemas de Informação", CategoriaCurso.SUPERIOR));
        Esporte esporte = esporteRepository.save(new Esporte("Vôlei", 6, 12));
        Tecnico tecnicoA = new Tecnico();
        tecnicoA.setMatricula("tecVoleiA");
        tecnicoA.setNome("Bernardinho");
        tecnicoA.setSenha("123");
        tecnicoA.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnicoA);

        // Criar um segundo técnico para a outra equipe
        Tecnico tecnicoB = new Tecnico();
        tecnicoB.setMatricula("tecVoleiB");
        tecnicoB.setNome("Zé Roberto");
        tecnicoB.setSenha("456");
        tecnicoB.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnicoB);
        Curso cursoB = cursoRepository.save(new Curso("Engenharia Civil", CategoriaCurso.SUPERIOR));

        // 2. Criar duas equipes com técnicos DIFERENTES
        Equipe equipeA = criarEquipe("Equipe A", cursoA, esporte, tecnicoA, 1);
        Equipe equipeB = criarEquipe("Equipe B", cursoB, esporte, tecnicoB, 2);
        // ***** FIM DA CORREÇÃO *****
        equipeRepository.save(equipeA); // Salva a equipe A no banco
        equipeRepository.save(equipeB); // Salva a equipe B no banco

        // 3. Criar e salvar o árbitro
        arbitro = new Arbitro();
        arbitro.setMatricula("arb001");
        arbitro.setNome("Árbitro Oficial");
        arbitro.setSenha("senha");
        arbitro.setTipo(TipoUsuario.ARBITRO);
        usuarioRepository.save(arbitro);

        // 4. Criar e salvar uma partida entre as equipes
        Partida novaPartida = new Partida();
        novaPartida.setEquipeA(equipeA);
        novaPartida.setEquipeB(equipeB);
        novaPartida.setDataHora(LocalDateTime.now());
        partidaAgendada = partidaRepository.save(novaPartida);
    }

    @Test
    void arbitroDeveRegistrarResultadoComSucesso() throws Exception {
        // Ação
        Partida partidaFinalizada = arbitroService.registrarResultado(arbitro.getMatricula(), partidaAgendada.getId(), 25, 20);

        // Verificação
        assertThat(partidaFinalizada).isNotNull();
        assertThat(partidaFinalizada.getStatus()).isEqualTo(StatusPartida.FINALIZADA);
        assertThat(partidaFinalizada.getPlacarEquipeA()).isEqualTo(25);
        assertThat(partidaFinalizada.getPlacarEquipeB()).isEqualTo(20);
    }

    @Test
    void naoDevePermitirQueNaoArbitroRegistreResultado() {
        // Ação e Verificação
        Exception exception = assertThrows(Exception.class, () -> {
            arbitroService.registrarResultado("tecVolei", partidaAgendada.getId(), 25, 20);
        });

        assertThat(exception.getMessage()).isEqualTo("Apenas usuários do tipo ARBITRO podem registrar resultados.");
    }
    
    @Test
    void naoDeveRegistrarResultadoDePartidaJaFinalizada() throws Exception {
        // Cenário: finalizar a partida uma vez
        arbitroService.registrarResultado(arbitro.getMatricula(), partidaAgendada.getId(), 25, 20);

        // Ação e Verificação: tentar finalizar de novo
        Exception exception = assertThrows(Exception.class, () -> {
            arbitroService.registrarResultado(arbitro.getMatricula(), partidaAgendada.getId(), 21, 18);
        });

        assertThat(exception.getMessage()).isEqualTo("O resultado desta partida já foi registrado.");
    }


    // Método auxiliar para facilitar a criação de equipes
    private Equipe criarEquipe(String nome, Curso curso, Esporte esporte, Tecnico tecnico, int idOffset) {
        Equipe equipe = new Equipe();
        equipe.setNome(nome);
        equipe.setCurso(curso);
        equipe.setEsporte(esporte);
        equipe.setTecnico(tecnico);
        
        List<Atleta> atletas = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Atleta atleta = new Atleta();
            atleta.setMatricula("atl" + idOffset + i);
            atleta.setNome("Atleta " + nome + " " + i);
            atleta.setSenha("123");
            atleta.setTipo(TipoUsuario.ATLETA);
            atleta.setTelefone("99999" + idOffset + i);
            atleta.setEquipe(equipe);
            atletas.add(atleta);
        }
        usuarioRepository.saveAll(atletas);
        equipe.setAtletas(atletas);
        return equipe;
    }
}