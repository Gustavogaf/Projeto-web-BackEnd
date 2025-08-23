// src/test/java/com/example/demo/AdminControllerTest.java
package com.example.demo;

import com.example.demo.Model.Coordenador;
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
import com.example.demo.Controller.dto.CoordenadorRequestDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @Test
    public void deveAtualizarUmCoordenadorComSucesso() throws Exception {
        // Cenário
        String matricula = "coord01";
        Coordenador detalhesCoordenador = new Coordenador();
        detalhesCoordenador.setNome("Novo Nome Coordenador");

        Coordenador coordenadorAtualizado = new Coordenador();
        coordenadorAtualizado.setMatricula(matricula);
        coordenadorAtualizado.setNome("Novo Nome Coordenador");
        coordenadorAtualizado.setTipo(TipoUsuario.COORDENADOR);

        when(adminService.atualizarCoordenador(eq(matricula), any(Coordenador.class)))
                .thenReturn(coordenadorAtualizado);

        // Ação
        ResultActions resultado = mockMvc.perform(put("/api/admin/coordenadores/{matricula}", matricula)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalhesCoordenador)));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula))
                .andExpect(jsonPath("$.nome").value("Novo Nome Coordenador"));
    }

    @Test
    public void deveDeletarUmCoordenadorComSucesso() throws Exception {
        // Cenário
        String matricula = "coord02";
        // O método do service não retorna nada (void), então não precisamos do
        // when/then
        // Apenas garantimos que nenhuma exceção é lançada
        doNothing().when(adminService).deletarCoordenador(matricula);

        // Ação
        ResultActions resultado = mockMvc.perform(delete("/api/admin/coordenadores/{matricula}", matricula));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(content().string("Coordenador com a matrícula " + matricula + " deletado com sucesso."));

        // Verifica se o método do service foi chamado exatamente uma vez
        verify(adminService, times(1)).deletarCoordenador(matricula);
    }

    @Test
    public void naoDeveDeletarCoordenadorInexistente() throws Exception {
        // Cenário
        String matricula = "coord99";
        // "Ensinamos" o mock a lançar a exceção que o serviço real lançaria
        doThrow(new Exception("Coordenador com a matrícula " + matricula + " não encontrado."))
                .when(adminService).deletarCoordenador(matricula);

        // Ação
        ResultActions resultado = mockMvc.perform(delete("/api/admin/coordenadores/{matricula}", matricula));

        // Verificação
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().string("Coordenador com a matrícula " + matricula + " não encontrado."));
    }

    @Test
    public void naoDeveCadastrarCoordenadorComMatriculaVazia() throws Exception {
        // Cenário
        CoordenadorRequestDTO request = new CoordenadorRequestDTO();
        request.setMatricula(""); // Matrícula vazia
        request.setNome("Nome Válido");
        request.setSenha("senhaValida123");

        // Ação e Verificação
        mockMvc.perform(post("/api/admin/coordenadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void naoDeveCadastrarCoordenadorComNomeVazio() throws Exception {
        // Cenário
        CoordenadorRequestDTO request = new CoordenadorRequestDTO();
        request.setMatricula("coordValido");
        request.setNome(""); // Nome vazio
        request.setSenha("senhaValida123");

        // Ação e Verificação
        mockMvc.perform(post("/api/admin/coordenadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void naoDeveCadastrarCoordenadorComSenhaCurta() throws Exception {
        // Cenário
        CoordenadorRequestDTO request = new CoordenadorRequestDTO();
        request.setMatricula("coordValido");
        request.setNome("Nome Válido");
        request.setSenha("123"); // Senha com menos de 6 caracteres

        // Ação e Verificação
        mockMvc.perform(post("/api/admin/coordenadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
