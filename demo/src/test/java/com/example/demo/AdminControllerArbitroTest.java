package com.example.demo;

import com.example.demo.Controller.dto.AuthRequestDTO;
import com.example.demo.Model.Arbitro;
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
public class AdminControllerArbitroTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    private String tokenAdmin;

    // --- NOVO MÉTODO: Executa antes de cada teste para obter o token de admin ---
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
    public void deveCadastrarUmArbitroComSucesso() throws Exception {
        Arbitro arbitroParaCadastrar = new Arbitro();
        arbitroParaCadastrar.setMatricula("arb001");
        arbitroParaCadastrar.setNome("Arbitro Oficial 1");
        arbitroParaCadastrar.setSenha("senha123");
        Arbitro arbitroSalvo = new Arbitro();
        arbitroSalvo.setMatricula("arb001");
        arbitroSalvo.setNome("Arbitro Oficial 1");
        arbitroSalvo.setTipo(TipoUsuario.ARBITRO);

        when(adminService.cadastrarArbitro(any(Arbitro.class))).thenReturn(arbitroSalvo);

        mockMvc.perform(post("/api/admin/arbitros")
                .header("Authorization", "Bearer " + tokenAdmin) // <-- ADICIONA O TOKEN
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(arbitroParaCadastrar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("arb001"));
    }

    @Test
    public void deveAtualizarUmArbitroComSucesso() throws Exception {
        String matricula = "arb002";
        Arbitro detalhesArbitro = new Arbitro();
        detalhesArbitro.setNome("Nome Arbitro Atualizado");
        Arbitro arbitroAtualizado = new Arbitro();
        arbitroAtualizado.setMatricula(matricula);
        arbitroAtualizado.setNome("Nome Arbitro Atualizado");
        arbitroAtualizado.setTipo(TipoUsuario.ARBITRO);

        when(adminService.atualizarArbitro(eq(matricula), any(Arbitro.class))).thenReturn(arbitroAtualizado);

        mockMvc.perform(put("/api/admin/arbitros/{matricula}", matricula)
                .header("Authorization", "Bearer " + tokenAdmin) // <-- ADICIONA O TOKEN
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalhesArbitro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Arbitro Atualizado"));
    }

    @Test
    public void deveDeletarUmArbitroComSucesso() throws Exception {
        String matricula = "arb003";
        doNothing().when(adminService).deletarArbitro(matricula);

        mockMvc.perform(delete("/api/admin/arbitros/{matricula}", matricula)
                .header("Authorization", "Bearer " + tokenAdmin)) // <-- ADICIONA O TOKEN
                .andExpect(status().isOk());
        
        verify(adminService, times(1)).deletarArbitro(matricula);
    }
    
    @Test
    public void naoDevePermitirAcessoSemToken() throws Exception {
        mockMvc.perform(delete("/api/admin/arbitros/{matricula}", "arb99"))
                .andExpect(status().isForbidden());
    }
}