// src/test/java/com/example/demo/EsporteControllerTest.java
package com.example.demo;

import com.example.demo.Model.Esporte;
import com.example.demo.Service.EsporteService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Habilita e configura o MockMvc
public class EsporteControllerTest {

        @Autowired
        private MockMvc mockMvc; // O objeto principal para simular as requisições

        @Autowired
        private ObjectMapper objectMapper; // Helper para converter objetos Java em JSON

        @MockBean // Cria um "dublê" (mock) do nosso serviço
        private EsporteService esporteService;

        @Test
        public void deveCriarUmEsporteComSucesso() throws Exception {
                // 1. CENÁRIO
                // Dados do esporte que enviaremos na requisição
                Esporte esporteParaEnviar = new Esporte("Natação", 1, 1);

                // Dados do esporte que esperamos que o serviço retorne após salvar
                Esporte esporteSalvo = new Esporte("Natação", 1, 1);
                esporteSalvo.setId(1L); // Simulando que o banco de dados deu um ID

                // "Ensinamos" o nosso dublê do serviço:
                // QUANDO o método criarEsporte for chamado COM QUALQUER objeto Esporte...
                when(esporteService.criarEsporte(any(Esporte.class)))
                                // ENTÃO, ele deve retornar o nosso objeto esporteSalvo.
                                .thenReturn(esporteSalvo);

                // 2. AÇÃO
                // Simulamos uma requisição POST para a nossa API
                ResultActions resultado = mockMvc.perform(post("/api/esportes")
                                .contentType(MediaType.APPLICATION_JSON) // Dizemos que o corpo é JSON
                                .content(objectMapper.writeValueAsString(esporteParaEnviar))); // Convertemos nosso
                                                                                               // objeto para JSON

                // 3. VERIFICAÇÃO
                // Verificamos se a resposta da API é a esperada
                resultado.andExpect(status().isCreated()) // Esperamos o status HTTP 201 Created
                                .andExpect(jsonPath("$.id").value(1L)) // Esperamos que o JSON de resposta tenha um
                                                                       // campo "id" com valor 1
                                .andExpect(jsonPath("$.nome").value("Natação")); // E um campo "nome" com valor
                                                                                 // "Natação"
        }

        @Test
        public void deveRetornarErroQuandoServicoFalhar() throws Exception {
                // 1. CENÁRIO
                Esporte esporteParaEnviar = new Esporte("Nome Duplicado", 1, 1);

                // "Ensinamos" o dublê a simular um erro:
                // QUANDO o método criarEsporte for chamado...
                when(esporteService.criarEsporte(any(Esporte.class)))
                                // ENTÃO, ele deve lançar uma exceção (como faria se o nome fosse duplicado).
                                .thenThrow(new Exception("Já existe um esporte com este nome."));

                // 2. AÇÃO
                ResultActions resultado = mockMvc.perform(post("/api/esportes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(esporteParaEnviar)));

                // 3. VERIFICAÇÃO
                // Verificamos se a API tratou o erro corretamente
                resultado.andExpect(status().isBadRequest()); // Esperamos o status HTTP 400 Bad Request
        }

        @Test
        public void naoDeveCriarEsporteComNomeVazio() throws Exception {
                // Cenário
                Esporte esporteParaEnviar = new Esporte("", 1, 1); // Nome vazio

                // Ação & Verificação
                mockMvc.perform(post("/api/esportes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(esporteParaEnviar)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void naoDeveCriarEsporteComMinAtletasInvalido() throws Exception {
                // Cenário
                Esporte esporteParaEnviar = new Esporte("Natação", 0, 1); // minAtletas < 1

                // Ação & Verificação
                mockMvc.perform(post("/api/esportes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(esporteParaEnviar)))
                                .andExpect(status().isBadRequest());
        }
}
