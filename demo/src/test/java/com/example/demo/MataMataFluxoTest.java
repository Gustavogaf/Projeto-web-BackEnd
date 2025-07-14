// src/test/java/com/example/demo/MataMataFluxoTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.ArbitroService;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MataMataFluxoTest {

    @Autowired
    private TorneioService torneioService;
    @Autowired
    private ArbitroService arbitroService;

    // Repositórios
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

        // --- CENÁRIO INICIAL ---
        // 1. Criar 6 equipes para ter 2 grupos de 3
        Esporte esporte = esporteRepository.save(new Esporte("Basquete", 5, 12));
        CategoriaCurso categoria = CategoriaCurso.SUPERIOR;
        for (int i = 1; i <= 6; i++) {
            criarEquipeCompleta("Equipe " + i, esporte, categoria, i);
        }

        // 2. Iniciar a fase de grupos para criar o torneio e as partidas
        torneio = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        // 3. Criar um árbitro para registrar os resultados
        arbitro = new Arbitro();
        arbitro.setMatricula("arbMataMata");
        arbitro.setNome("Árbitro Oficial");
        arbitro.setSenha("123");
        arbitro.setTipo(TipoUsuario.ARBITRO);
        usuarioRepository.save(arbitro);

        // 4. SIMULAR OS JOGOS DA FASE DE GRUPOS
        List<Partida> partidasDaFaseDeGrupos = partidaRepository.findAll();
        for (Partida partida : partidasDaFaseDeGrupos) {
            // Simulando um resultado simples: Equipe A sempre vence por 10 a 0
            arbitroService.registrarResultado(arbitro.getMatricula(), partida.getId(), 10, 0);
        }
    }

    @Test
    void deveGerarFaseMataMataComOsClassificadosCorretos() throws Exception {
        // Ação
        List<Partida> partidasMataMata = torneioService.gerarMataMata(torneio.getId());

        // Verificação
        // Com 2 grupos, teremos 4 classificados (os 2 melhores de cada).
        // Isso deve gerar 2 partidas na próxima fase (Quartas de Final, nesse caso).
        assertThat(partidasMataMata).isNotNull();
        assertThat(partidasMataMata).hasSize(2);

        // Verificar se os times nas novas partidas são os que tiveram mais pontos
        Partida primeiraPartida = partidasMataMata.get(0);
        Equipe equipeA_partida1 = primeiraPartida.getEquipeA();
        Equipe equipeB_partida1 = primeiraPartida.getEquipeB();

        // No nosso cenário, os vencedores tiveram 2 vitórias (6 pontos)
        // e os segundos colocados tiveram 1 vitória (3 pontos).
        // Todos os classificados devem ter mais de 0 pontos.
        assertThat(equipeA_partida1.getPontos()).isGreaterThan(0);
        assertThat(equipeB_partida1.getPontos()).isGreaterThan(0);

        // Verificar se a partida foi agendada corretamente
        assertThat(primeiraPartida.getStatus()).isEqualTo(StatusPartida.AGENDADA);
        assertThat(primeiraPartida.getPlacarEquipeA()).isNull();
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
