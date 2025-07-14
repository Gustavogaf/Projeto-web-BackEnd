// src/test/java/com/example/demo/ArbitroControllerTest.java
package com.example.demo;

import com.example.demo.Controller.dto.PlacarRequest; // Importe o DTO
import com.example.demo.Model.Partida;
import com.example.demo.Model.StatusPartida;
import com.example.demo.Service.ArbitroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArbitroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArbitroService arbitroService;

    @Test
    public void deveRegistrarResultadoComSucesso() throws Exception {
        // 1. CENÁRIO
        String matriculaArbitro = "arb001";
        Long partidaId = 1L;
        
        // DTO com os dados do placar
        PlacarRequest placar = new PlacarRequest();
        placar.setPlacarA(25);
        placar.setPlacarB(20);

        // Objeto que o nosso serviço "mockado" irá retornar
        Partida partidaAtualizada = new Partida();
        partidaAtualizada.setId(partidaId);
        partidaAtualizada.setStatus(StatusPartida.FINALIZADA);
        partidaAtualizada.setPlacarEquipeA(25);
        partidaAtualizada.setPlacarEquipeB(20);

        // "Ensinamos" o dublê do serviço
        when(arbitroService.registrarResultado(matriculaArbitro, partidaId, 25, 20))
                .thenReturn(partidaAtualizada);

        // 2. AÇÃO
        // Simulamos uma requisição PUT para o endpoint do árbitro
        ResultActions resultado = mockMvc.perform(put("/api/arbitros/{matArbitro}/partidas/{partidaId}/resultado", matriculaArbitro, partidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(placar)));

        // 3. VERIFICAÇÃO
        // Verificamos se a API retornou 200 OK e os dados da partida atualizada
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(partidaId))
                .andExpect(jsonPath("$.status").value("FINALIZADA"))
                .andExpect(jsonPath("$.placarEquipeA").value(25));
    }
}