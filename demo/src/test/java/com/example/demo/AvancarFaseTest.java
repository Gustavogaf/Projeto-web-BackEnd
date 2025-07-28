// // src/test/java/com/example/demo/AvancarFaseTest.java
// package com.example.demo;

// import com.example.demo.Model.*;
// import com.example.demo.Repository.*;
// import com.example.demo.Service.ArbitroService;
// import com.example.demo.Service.TorneioService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.stream.Collectors;

// import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
// @Transactional
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// public class AvancarFaseTest {

//     @Autowired private TorneioService torneioService;
//     @Autowired private ArbitroService arbitroService;
//     @Autowired private PartidaRepository partidaRepository;
//     @Autowired private EquipeRepository equipeRepository;
//     @Autowired private UsuarioRepository usuarioRepository;
//     @Autowired private CursoRepository cursoRepository;
//     @Autowired private EsporteRepository esporteRepository;
//     @Autowired private TorneioRepository torneioRepository;

//     private Arbitro arbitro;
//     private Esporte esporte;
//     private CategoriaCurso categoria;

//     @BeforeEach
//     void setUp() {
//         arbitro = new Arbitro();
//         arbitro.setMatricula("arbitro-test");
//         arbitro.setNome("Arbitro Teste");
//         arbitro.setSenha("123");
//         arbitro.setTipo(TipoUsuario.ARBITRO);
//         usuarioRepository.save(arbitro);

//         esporte = esporteRepository.save(new Esporte("Teste", 1, 2));
//         categoria = CategoriaCurso.SUPERIOR;
//     }

//     @Test
//     void deveAvancarCorretamentePorTodasAsFasesDoTorneio() throws Exception {
//         // --- 1. CENÁRIO: Iniciar um torneio com 7 equipes ---
//         for (int i = 1; i <= 7; i++) {
//             criarEquipeCompleta("Equipe " + i, i);
//         }
//         Torneio torneio = torneioService.iniciarFaseDeGrupos(esporte, categoria);
        
//         List<Partida> faseDeGrupos = partidaRepository.findByTorneioIdAndFase(torneio.getId(), FaseTorneio.FASE_DE_GRUPOS);
//         assertThat(faseDeGrupos).hasSize(9); // 6 jogos no grupo de 4, 3 no grupo de 3

//         // --- 2. AÇÃO: Finalizar fase de grupos ---
//         for (Partida p : faseDeGrupos) {
//             arbitroService.registrarResultado(arbitro.getMatricula(), p.getId(), 1, 0); // Equipe A sempre vence
//         }

//         // --- 3. AÇÃO E VERIFICAÇÃO: Avançar para Semifinais ---
//         List<Partida> semifinais = torneioService.avancarFase(torneio.getId());
//         assertThat(semifinais).hasSize(2); // 4 classificados geram 2 semifinais
//         for (Partida p : semifinais) {
//             assertThat(p.getFase()).isEqualTo(FaseTorneio.SEMIFINAL);
//         }

//         // --- 4. AÇÃO: Finalizar semifinais ---
//         for (Partida p : semifinais) {
//             arbitroService.registrarResultado(arbitro.getMatricula(), p.getId(), 1, 0); // Equipe A sempre vence
//         }

//         // --- 5. AÇÃO E VERIFICAÇÃO: Avançar para a Final ---
//         List<Partida> finalUnica = torneioService.avancarFase(torneio.getId());
//         assertThat(finalUnica).hasSize(1); // 2 vencedores geram 1 final
//         assertThat(finalUnica.get(0).getFase()).isEqualTo(FaseTorneio.FINAL);

//         // --- 6. AÇÃO: Finalizar a final ---
//         arbitroService.registrarResultado(arbitro.getMatricula(), finalUnica.get(0).getId(), 1, 0);

//         // --- 7. AÇÃO E VERIFICAÇÃO: Tentar avançar após a final ---
//         List<Partida> aposFinal = torneioService.avancarFase(torneio.getId());
//         assertThat(aposFinal).isEmpty(); // Deve retornar lista vazia, indicando fim do torneio
//     }

//     private Equipe criarEquipeCompleta(String nomeEquipe, int idOffset) {
//         Curso curso = cursoRepository.save(new Curso("Curso " + idOffset, categoria));
//         Tecnico tecnico = new Tecnico();
//         tecnico.setMatricula("tec" + idOffset);
//         tecnico.setTipo(TipoUsuario.TECNICO);
//         tecnico.setNome("Téc " + nomeEquipe);
//         tecnico.setSenha("123");
//         usuarioRepository.save(tecnico);

//         Equipe equipe = new Equipe();
//         equipe.setNome(nomeEquipe);
//         equipe.setCurso(curso);
//         equipe.setEsporte(esporte);
//         equipe.setTecnico(tecnico);
//         return equipeRepository.save(equipe);
//     }
// }