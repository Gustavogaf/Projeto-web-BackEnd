// src/test/java/com/example/demo/CoordenadorControllerTest.java
package com.example.demo;

import com.example.demo.Model.Tecnico;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Service.CoordenadorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.example.demo.Controller.dto.TecnicoRequestDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CoordenadorControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CoordenadorService coordenadorService;

        @Test
        public void deveCadastrarUmTecnicoComSucesso() throws Exception {
                // 1. CENÁRIO
                String matriculaCoordenador = "coord123";

                // Dados do técnico que enviaremos no corpo da requisição
                Tecnico tecnicoParaEnviar = new Tecnico();
                tecnicoParaEnviar.setMatricula("tec001");
                tecnicoParaEnviar.setNome("Professor Pardal");
                tecnicoParaEnviar.setSenha("senha123");

                // Dados que esperamos que o serviço retorne
                Tecnico tecnicoSalvo = new Tecnico();
                tecnicoSalvo.setMatricula("tec001");
                tecnicoSalvo.setNome("Professor Pardal");
                tecnicoSalvo.setTipo(TipoUsuario.TECNICO);

                // "Ensinamos" o dublê do serviço:
                // QUANDO o método cadastrarTecnico for chamado com a matrícula "coord123" E
                // QUALQUER objeto Tecnico...
                when(coordenadorService.cadastrarTecnico(eq(matriculaCoordenador), any(Tecnico.class)))
                                // ENTÃO, ele deve retornar o nosso objeto tecnicoSalvo.
                                .thenReturn(tecnicoSalvo);

                // 2. AÇÃO
                // Simulamos a requisição POST para a nossa API, incluindo a variável na URL
                ResultActions resultado = mockMvc
                                .perform(post("/api/coordenadores/{matricula}/tecnicos", matriculaCoordenador)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(tecnicoParaEnviar)));

                // 3. VERIFICAÇÃO
                // Verificamos a resposta da API
                resultado.andExpect(status().isCreated())
                                .andExpect(jsonPath("$.matricula").value("tec001"))
                                .andExpect(jsonPath("$.nome").value("Professor Pardal"))
                                .andExpect(jsonPath("$.tipo").value("TECNICO"));
        }

        @Test
        public void deveAtualizarUmTecnicoComSucesso() throws Exception {
                // Cenário
                String matriculaCoordenador = "coord01";
                String matriculaTecnico = "tec01";

                Tecnico detalhesTecnico = new Tecnico();
                detalhesTecnico.setNome("Novo Nome Tecnico");

                Tecnico tecnicoAtualizado = new Tecnico();
                tecnicoAtualizado.setMatricula(matriculaTecnico);
                tecnicoAtualizado.setNome("Novo Nome Tecnico");
                tecnicoAtualizado.setTipo(TipoUsuario.TECNICO);

                when(coordenadorService.atualizarTecnico(eq(matriculaCoordenador), eq(matriculaTecnico),
                                any(Tecnico.class)))
                                .thenReturn(tecnicoAtualizado);

                // Ação
                ResultActions resultado = mockMvc.perform(
                                put("/api/coordenadores/{mc}/tecnicos/{mt}", matriculaCoordenador, matriculaTecnico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(detalhesTecnico)));

                // Verificação
                resultado.andExpect(status().isOk())
                                .andExpect(jsonPath("$.matricula").value(matriculaTecnico))
                                .andExpect(jsonPath("$.nome").value("Novo Nome Tecnico"));
        }

        @Test
        public void deveDeletarUmTecnicoComSucesso() throws Exception {
                // Cenário
                String matriculaCoordenador = "coord01";
                String matriculaTecnico = "tec02";
                doNothing().when(coordenadorService).deletarTecnico(matriculaCoordenador, matriculaTecnico);

                // Ação
                ResultActions resultado = mockMvc.perform(delete("/api/coordenadores/{mc}/tecnicos/{mt}",
                                matriculaCoordenador, matriculaTecnico));

                // Verificação
                resultado.andExpect(status().isOk())
                                .andExpect(content().string("Técnico com matrícula " + matriculaTecnico
                                                + " deletado com sucesso."));

                verify(coordenadorService, times(1)).deletarTecnico(matriculaCoordenador, matriculaTecnico);
        }

        @Test
        public void naoDeveDeletarTecnicoAssociadoAEquipe() throws Exception {
                // Cenário
                String matriculaCoordenador = "coord01";
                String matriculaTecnico = "tec03";

                // "Ensinamos" o mock a lançar a exceção da nossa regra de negócio
                doThrow(new Exception("Não é possível deletar o técnico, pois ele já está associado a uma equipe."))
                                .when(coordenadorService).deletarTecnico(matriculaCoordenador, matriculaTecnico);

                // Ação
                ResultActions resultado = mockMvc.perform(delete("/api/coordenadores/{mc}/tecnicos/{mt}",
                                matriculaCoordenador, matriculaTecnico));

                // Verificação
                resultado.andExpect(status().isBadRequest())
                                .andExpect(content().string(
                                                "Não é possível deletar o técnico, pois ele já está associado a uma equipe."));
        }

        @Test
        public void naoDeveCadastrarTecnicoComNomeVazio() throws Exception {
                // Cenário
                String matriculaCoordenador = "coord123";
                TecnicoRequestDTO tecnicoInvalido = new TecnicoRequestDTO();
                tecnicoInvalido.setMatricula("tecValido");
                tecnicoInvalido.setNome(""); // Nome inválido
                tecnicoInvalido.setSenha("senhaValida");

                // Ação e Verificação
                mockMvc.perform(post("/api/coordenadores/{matricula}/tecnicos", matriculaCoordenador)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tecnicoInvalido)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void naoDeveCadastrarTecnicoComMatriculaCurta() throws Exception {
                // Cenário
                String matriculaCoordenador = "coord123";
                TecnicoRequestDTO tecnicoInvalido = new TecnicoRequestDTO();
                tecnicoInvalido.setMatricula("tec1"); // Matrícula inválida (curta)
                tecnicoInvalido.setNome("Nome Valido");
                tecnicoInvalido.setSenha("senhaValida");

                // Ação e Verificação
                mockMvc.perform(post("/api/coordenadores/{matricula}/tecnicos", matriculaCoordenador)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tecnicoInvalido)))
                                .andExpect(status().isBadRequest());
        }
}
