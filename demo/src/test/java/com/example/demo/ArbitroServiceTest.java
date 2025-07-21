// src/test/java/com/example/demo/ArbitroServiceTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.ArbitroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArbitroServiceTest {

    @Autowired private ArbitroService arbitroService;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;

    @Test
    void deveRegistrarWOCorretamente() throws Exception {
        // --- 1. CENÁRIO (SETUP) ---
        // Criando todo o cenário necessário dentro do próprio teste para garantir o isolamento.
        Curso cursoA = cursoRepository.save(new Curso("Sistemas de Informação", CategoriaCurso.SUPERIOR));
        Curso cursoB = cursoRepository.save(new Curso("Engenharia Civil", CategoriaCurso.SUPERIOR));
        Esporte esporte = esporteRepository.save(new Esporte("Vôlei", 6, 12));

        Tecnico tecnicoA = new Tecnico();
        tecnicoA.setMatricula("tecVoleiA");
        tecnicoA.setTipo(TipoUsuario.TECNICO);
        tecnicoA.setNome("Bernardinho");
        tecnicoA.setSenha("123");
        usuarioRepository.save(tecnicoA);

        Tecnico tecnicoB = new Tecnico();
        tecnicoB.setMatricula("tecVoleiB");
        tecnicoB.setTipo(TipoUsuario.TECNICO);
        tecnicoB.setNome("Zé Roberto");
        tecnicoB.setSenha("456");
        usuarioRepository.save(tecnicoB);

        Equipe equipeA = new Equipe();
        equipeA.setNome("Equipe A");
        equipeA.setCurso(cursoA);
        equipeA.setEsporte(esporte);
        equipeA.setTecnico(tecnicoA);
        equipeRepository.save(equipeA);

        Equipe equipeB = new Equipe();
        equipeB.setNome("Equipe B");
        equipeB.setCurso(cursoB);
        equipeB.setEsporte(esporte);
        equipeB.setTecnico(tecnicoB);
        equipeRepository.save(equipeB);

        Arbitro arbitro = new Arbitro();
        arbitro.setMatricula("arb001");
        arbitro.setNome("Árbitro Oficial");
        arbitro.setSenha("senha");
        arbitro.setTipo(TipoUsuario.ARBITRO);
        usuarioRepository.save(arbitro);

        Partida partidaAgendada = new Partida();
        partidaAgendada.setEquipeA(equipeA);
        partidaAgendada.setEquipeB(equipeB);
        partidaAgendada.setDataHora(LocalDateTime.now());
        partidaRepository.save(partidaAgendada);

        // --- 2. AÇÃO ---
        Partida partidaFinalizada = arbitroService.registrarWO(
                arbitro.getMatricula(),
                partidaAgendada.getId(),
                equipeA.getId() // Equipe A venceu por W.O.
        );

        // --- 3. VERIFICAÇÃO ---
        assertThat(partidaFinalizada).isNotNull();
        assertThat(partidaFinalizada.getStatus()).isEqualTo(StatusPartida.WO_EQUIPE_B); 
        assertThat(partidaFinalizada.getPlacarEquipeA()).isEqualTo(3);
        assertThat(partidaFinalizada.getPlacarEquipeB()).isEqualTo(0);

        Equipe equipeAVerificada = equipeRepository.findById(equipeA.getId()).orElseThrow();
        Equipe equipeBVerificada = equipeRepository.findById(equipeB.getId()).orElseThrow();

        assertThat(equipeAVerificada.getPontos()).isEqualTo(3);
        assertThat(equipeBVerificada.getPontos()).isZero();
    }
}