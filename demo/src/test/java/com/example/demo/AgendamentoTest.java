// Crie o arquivo: src/test/java/com/example/demo/AgendamentoTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AgendamentoTest {

    @Autowired private TorneioService torneioService;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;

    @Test
    void deveAgendarPartidasSemConflitosDeHorarioParaAsEquipes() throws Exception {
        // --- 1. CENÁRIO ---
        // Criar 6 equipes, o que resultará em 2 grupos de 3.
        // Isso é ideal para testar se o agendamento de um grupo não interfere no outro.
        Esporte esporte = esporteRepository.save(new Esporte("Xadrez", 1, 1));
        CategoriaCurso categoria = CategoriaCurso.SUPERIOR;

        for (int i = 1; i <= 6; i++) {
            criarEquipeCompleta("Equipe " + i, esporte, categoria, i);
        }

        // --- 2. AÇÃO ---
        Torneio torneio = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        // --- 3. VERIFICAÇÃO ---
        List<Partida> partidasAgendadas = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneio.getId());
        
        // Com 2 grupos de 3, teremos 3 partidas por grupo, totalizando 6 partidas.
        assertThat(partidasAgendadas).hasSize(6);

        // Verificar se há conflitos
        Map<Long, List<LocalDateTime>> horariosPorEquipe = new HashMap<>();
        for (Partida partida : partidasAgendadas) {
            Long equipeAId = partida.getEquipeA().getId();
            Long equipeBId = partida.getEquipeB().getId();
            LocalDateTime horario = partida.getDataHora();

            // Adicionar horário para a equipe A
            horariosPorEquipe.computeIfAbsent(equipeAId, k -> new ArrayList<>()).add(horario);
            // Adicionar horário para a equipe B
            horariosPorEquipe.computeIfAbsent(equipeBId, k -> new ArrayList<>()).add(horario);
        }

        // Para cada equipe, verificar se os horários de seus jogos são únicos
        for (Map.Entry<Long, List<LocalDateTime>> entry : horariosPorEquipe.entrySet()) {
            List<LocalDateTime> horarios = entry.getValue();
            long countDistintos = horarios.stream().distinct().count();
            // Se o número de horários não for igual ao número de horários distintos, há um conflito.
            assertThat(horarios.size()).isEqualTo(countDistintos);
        }
    }

    private void criarEquipeCompleta(String nomeEquipe, Esporte esporte, CategoriaCurso categoria, int idOffset) {
        Curso curso = cursoRepository.save(new Curso("Curso de Agendamento " + idOffset, categoria));
        Tecnico tecnico = new Tecnico();
        tecnico.setMatricula("tecAg" + idOffset);
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