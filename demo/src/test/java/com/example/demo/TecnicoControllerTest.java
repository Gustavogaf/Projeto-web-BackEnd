// src/test/java/com/example/demo/TecnicoControllerTest.java
package com.example.demo;

import com.example.demo.Controller.CadastroEquipeRequest; // Importe o DTO
import com.example.demo.Model.*;
import com.example.demo.Service.TecnicoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TecnicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TecnicoService tecnicoService;

    @Test
    public void deveCadastrarUmaEquipeComSucesso() throws Exception {
        // 1. CENÁRIO
        String matriculaTecnico = "tec007";

        // Preparando os dados da requisição
        Equipe dadosEquipe = new Equipe();
        Curso curso = new Curso();
        curso.setId(1L);
        Esporte esporte = new Esporte();
        esporte.setId(1L);
        dadosEquipe.setCurso(curso);
        dadosEquipe.setEsporte(esporte);
        dadosEquipe.setNome("Os Invencíveis");
        
        List<String> matriculasAtletas = List.of("atl01", "atl02", "atl03");

        CadastroEquipeRequest request = new CadastroEquipeRequest();
        request.setEquipe(dadosEquipe);
        request.setMatriculasAtletas(matriculasAtletas);

        // Preparando a resposta que o serviço "mockado" irá retornar
        Equipe equipeSalva = new Equipe();
        equipeSalva.setId(10L); // Simula o ID dado pelo banco
        equipeSalva.setNome("Os Invencíveis");
        
        // "Ensinamos" o dublê do serviço
        when(tecnicoService.cadastrarEquipe(eq(matriculaTecnico), any(Equipe.class), any(List.class)))
                .thenReturn(equipeSalva);

        // 2. AÇÃO
        // Simulamos a requisição POST com o DTO no corpo
        ResultActions resultado = mockMvc.perform(post("/api/tecnicos/{matricula}/equipes", matriculaTecnico)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // 3. VERIFICAÇÃO
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Os Invencíveis"));
    }
}
