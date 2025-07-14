// src/test/java/com/example/demo/TorneioControllerTest.java
package com.example.demo;

import com.example.demo.Controller.dto.IniciarTorneioRequest;
import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Torneio;
import com.example.demo.Service.TorneioService;
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
@AutoConfigureMockMvc
public class TorneioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TorneioService torneioService;

    @Test
    public void deveIniciarTorneioComSucesso() throws Exception {
        // 1. CENÁRIO
        // DTO com os dados para iniciar o torneio
        IniciarTorneioRequest request = new IniciarTorneioRequest();
        request.setEsporteId(1L);
        request.setCategoria(CategoriaCurso.SUPERIOR);

        // Objeto que o nosso serviço "mockado" irá retornar
        Torneio torneioRetornado = new Torneio();
        torneioRetornado.setId(1L);
        Esporte esporte = new Esporte();
        esporte.setId(1L);
        esporte.setNome("Futsal");
        torneioRetornado.setEsporte(esporte);
        torneioRetornado.setCategoria(CategoriaCurso.SUPERIOR);

        // "Ensinamos" o dublê do serviço
        when(torneioService.iniciarFaseDeGrupos(any(Esporte.class), any(CategoriaCurso.class)))
                .thenReturn(torneioRetornado);

        // 2. AÇÃO
        // Simulamos uma requisição POST para o endpoint de iniciar torneio
        ResultActions resultado = mockMvc.perform(post("/api/torneios/iniciar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // 3. VERIFICAÇÃO
        // Verificamos se a API retornou 201 Created e os dados do torneio criado
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.categoria").value("SUPERIOR"))
                .andExpect(jsonPath("$.esporte.nome").value("Futsal"));
    }
}
