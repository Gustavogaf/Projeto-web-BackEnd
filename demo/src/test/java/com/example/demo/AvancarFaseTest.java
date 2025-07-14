// src/test/java/com/example/demo/AvancarFaseTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.ArbitroService;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AvancarFaseTest {

    @Autowired
    private MockMvc mockMvc;

    // Injetando services e repositórios necessários
    @Autowired private TorneioService torneioService;
    @Autowired private ArbitroService arbitroService;
    @Autowired private EsporteRepository esporteRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private PartidaRepository partidaRepository;
    @Autowired private TorneioRepository torneioRepository;

    private Torneio torneio;
    private Arbitro arbitro;

    @BeforeEach
    void setUp() throws Exception {
        // Limpeza completa
        partidaRepository.deleteAll();
        torneioRepository.deleteAll();
        equipeRepository.deleteAll();
        usuarioRepository.deleteAll();
        cursoRepository.deleteAll();
        esporteRepository.deleteAll();

        // --- CENÁRIO: SEMIFINAL ---
        // 1. Criar 4 equipes para ir direto para a semifinal
        Esporte esporte = esporteRepository.save(new Esporte("Tênis de Mesa", 1, 2));
        CategoriaCurso categoria = CategoriaCurso.SUBSEQUENTE;
        for (int i = 1; i <= 4; i++) {
            criarEquipeCompleta("Equipe Semifinal " + i, esporte, categoria, i);
        }

        // 2. Iniciar o torneio para gerar os jogos da semifinal
        torneio = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        // 3. Criar um árbitro
        arbitro = new Arbitro();
        arbitro.setMatricula("arbFinal");
        arbitro.setTipo(TipoUsuario.ARBITRO);
        arbitro.setNome("Juiz Final");
        arbitro.setSenha("final123");
        usuarioRepository.save(arbitro);
    }

    @Test
    void deveAvancarDaSemifinalParaFinal() throws Exception {
        // --- PARTE 1: JOGAR A SEMIFINAL ---
        List<Partida> semifinais = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneio.getId());
        assertThat(semifinais).hasSize(2);

        // Definir os vencedores da semifinal
        Partida semi1 = semifinais.get(0);
        Partida semi2 = semifinais.get(1);
        arbitroService.registrarResultado(arbitro.getMatricula(), semi1.getId(), 10, 5); // Vencedor: Equipe A da semi 1
        arbitroService.registrarResultado(arbitro.getMatricula(), semi2.getId(), 3, 10); // Vencedor: Equipe B da semi 2

        // --- PARTE 2: ACIONAR O AVANÇO DE FASE ---
        // Ação
        ResultActions resultado = mockMvc.perform(post("/api/torneios/{torneioId}/avancar-fase", torneio.getId()));

        // Verificação
        resultado.andExpect(status().isCreated()) // Esperamos 201 Created, pois a final foi criada
                 .andExpect(jsonPath("$").isArray()) // A resposta deve ser um array de partidas
                 .andExpect(jsonPath("$.length()").value(1)); // Deve haver apenas 1 partida na final

        // --- PARTE 3: VERIFICAR O FIM DO TORNEIO ---
        // Agora, jogamos a final
        Partida finalDoTorneio = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneio.getId()).get(0);
        arbitroService.registrarResultado(arbitro.getMatricula(), finalDoTorneio.getId(), 21, 19);

        // Ação
        ResultActions resultadoFinal = mockMvc.perform(post("/api/torneios/{torneioId}/avancar-fase", torneio.getId()));

        // Verificação
        resultadoFinal.andExpect(status().isOk()) // Esperamos 200 OK
                      .andExpect(jsonPath("$").value("Torneio finalizado! Campeão determinado.")); // E a mensagem de fim de torneio
    }

    private void criarEquipeCompleta(String nomeEquipe, Esporte esporte, CategoriaCurso categoria, int idOffset) {
        Curso curso = cursoRepository.save(new Curso("Curso " + nomeEquipe, categoria));
        Tecnico tecnico = new Tecnico();
        tecnico.setMatricula("tecFinal" + idOffset);
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
