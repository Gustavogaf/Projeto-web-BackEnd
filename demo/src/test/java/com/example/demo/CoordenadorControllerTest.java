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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        // QUANDO o método cadastrarTecnico for chamado com a matrícula "coord123" E QUALQUER objeto Tecnico...
        when(coordenadorService.cadastrarTecnico(eq(matriculaCoordenador), any(Tecnico.class)))
                // ENTÃO, ele deve retornar o nosso objeto tecnicoSalvo.
                .thenReturn(tecnicoSalvo);
        
        // 2. AÇÃO
        // Simulamos a requisição POST para a nossa API, incluindo a variável na URL
        ResultActions resultado = mockMvc.perform(post("/api/coordenadores/{matricula}/tecnicos", matriculaCoordenador)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tecnicoParaEnviar)));

        // 3. VERIFICAÇÃO
        // Verificamos a resposta da API
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("tec001"))
                .andExpect(jsonPath("$.nome").value("Professor Pardal"))
                .andExpect(jsonPath("$.tipo").value("TECNICO"));
    }
}
