// src/test/java/com/example/demo/AdminControllerArbitroTest.java
package com.example.demo;

import com.example.demo.Model.Arbitro;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Service.AdminService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerArbitroTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    public void deveCadastrarUmArbitroComSucesso() throws Exception {
        // Cenário
        Arbitro arbitroParaCadastrar = new Arbitro();
        arbitroParaCadastrar.setMatricula("arb001");
        arbitroParaCadastrar.setNome("Arbitro Oficial 1");
        arbitroParaCadastrar.setSenha("senha123");

        Arbitro arbitroSalvo = new Arbitro();
        arbitroSalvo.setMatricula("arb001");
        arbitroSalvo.setNome("Arbitro Oficial 1");
        arbitroSalvo.setTipo(TipoUsuario.ARBITRO);

        when(adminService.cadastrarArbitro(any(Arbitro.class))).thenReturn(arbitroSalvo);

        // Ação
        ResultActions resultado = mockMvc.perform(post("/api/admin/arbitros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(arbitroParaCadastrar)));

        // Verificação
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("arb001"))
                .andExpect(jsonPath("$.tipo").value("ARBITRO"));
    }

    @Test
    public void deveAtualizarUmArbitroComSucesso() throws Exception {
        // Cenário
        String matricula = "arb002";
        Arbitro detalhesArbitro = new Arbitro();
        detalhesArbitro.setNome("Nome Arbitro Atualizado");

        Arbitro arbitroAtualizado = new Arbitro();
        arbitroAtualizado.setMatricula(matricula);
        arbitroAtualizado.setNome("Nome Arbitro Atualizado");
        arbitroAtualizado.setTipo(TipoUsuario.ARBITRO);

        when(adminService.atualizarArbitro(eq(matricula), any(Arbitro.class))).thenReturn(arbitroAtualizado);

        // Ação
        ResultActions resultado = mockMvc.perform(put("/api/admin/arbitros/{matricula}", matricula)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalhesArbitro)));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Arbitro Atualizado"));
    }

    @Test
    public void deveDeletarUmArbitroComSucesso() throws Exception {
        // Cenário
        String matricula = "arb003";
        doNothing().when(adminService).deletarArbitro(matricula);

        // Ação
        ResultActions resultado = mockMvc.perform(delete("/api/admin/arbitros/{matricula}", matricula));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(content().string("Árbitro com a matrícula " + matricula + " deletado com sucesso."));
        
        verify(adminService, times(1)).deletarArbitro(matricula);
    }
}