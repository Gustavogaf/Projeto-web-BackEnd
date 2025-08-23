package com.example.demo;

import com.example.demo.Controller.dto.AuthRequestDTO;
import com.example.demo.Model.Coordenador;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Service.AdminService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    private String tokenAdmin;

    // --- NOVO MÉTODO: Executa antes de cada teste para obter o token ---
    @BeforeEach
    void setUp() throws Exception {
        this.tokenAdmin = obterToken("admin", "admin");
    }

    // --- NOVO MÉTODO AUXILIAR: Realiza o login e extrai o token ---
    private String obterToken(String matricula, String senha) throws Exception {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setMatricula(matricula);
        authRequest.setSenha(senha);

        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));

        String responseString = result.andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseString);
        return jsonNode.get("token").asText();
    }

    @Test
    public void deveAtualizarUmCoordenadorComSucesso() throws Exception {
        String matricula = "coord01";
        Coordenador detalhesCoordenador = new Coordenador();
        detalhesCoordenador.setNome("Novo Nome Coordenador");
        Coordenador coordenadorAtualizado = new Coordenador();
        coordenadorAtualizado.setMatricula(matricula);
        coordenadorAtualizado.setNome("Novo Nome Coordenador");
        coordenadorAtualizado.setTipo(TipoUsuario.COORDENADOR);

        when(adminService.atualizarCoordenador(eq(matricula), any(Coordenador.class))).thenReturn(coordenadorAtualizado);

        mockMvc.perform(put("/api/admin/coordenadores/{matricula}", matricula)
                .header("Authorization", "Bearer " + tokenAdmin) // <-- ADICIONA O TOKEN
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalhesCoordenador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome Coordenador"));
    }

    @Test
    public void deveDeletarUmCoordenadorComSucesso() throws Exception {
        String matricula = "coord02";
        doNothing().when(adminService).deletarCoordenador(matricula);

        mockMvc.perform(delete("/api/admin/coordenadores/{matricula}", matricula)
                .header("Authorization", "Bearer " + tokenAdmin)) // <-- ADICIONA O TOKEN
                .andExpect(status().isOk());
        
        verify(adminService, times(1)).deletarCoordenador(matricula);
    }
    
    @Test
    public void naoDevePermitirAcessoSemToken() throws Exception {
        // Tenta acessar um endpoint protegido SEM o token
        mockMvc.perform(delete("/api/admin/coordenadores/{matricula}", "coord99"))
                .andExpect(status().isForbidden()); // Espera 403 Forbidden
    }
}