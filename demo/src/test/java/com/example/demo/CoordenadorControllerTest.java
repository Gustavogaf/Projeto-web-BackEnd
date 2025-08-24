package com.example.demo;

import com.example.demo.Controller.dto.AuthRequestDTO;
import com.example.demo.Model.Tecnico;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Service.AuthUserDetailsService;
import com.example.demo.Service.CoordenadorService;
import com.example.demo.Service.TecnicoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.example.demo.Model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class CoordenadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoordenadorService coordenadorService;

    @MockBean
    private AuthUserDetailsService authUserDetailsService;

    // --- NOVO MOCK: Mockamos o PasswordEncoder ---
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean // Mockamos o TecnicoService também para este teste
    private TecnicoService tecnicoService;

    private String tokenCoordenador;
    private final String MATRICULA_COORDENADOR = "coord123";
    private final String SENHA_COORDENADOR = "senha123";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Crie um UserDetails que represente nosso coordenador.
        UserDetails userDetails = User.builder()
                .username(MATRICULA_COORDENADOR)
                .password("senhaCriptografadaQualquer") // A senha aqui é apenas um placeholder.
                .roles("COORDENADOR")
                .build();

        // 2. "Ensine" o authUserDetailsService a retornar nosso UserDetails.
        when(authUserDetailsService.loadUserByUsername(MATRICULA_COORDENADOR)).thenReturn(userDetails);

        // 3. --- A CORREÇÃO CRUCIAL ---
        // "Ensine" o PasswordEncoder a sempre retornar TRUE quando a senha correta for
        // verificada.
        when(passwordEncoder.matches(eq(SENHA_COORDENADOR), anyString())).thenReturn(true);

        // 4. Obtenha o token.
        this.tokenCoordenador = obterToken(MATRICULA_COORDENADOR, SENHA_COORDENADOR);
    }

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
    public void deveCadastrarUmTecnicoComSucesso() throws Exception {
        // (O corpo deste teste permanece o mesmo, mas agora ele usa o token obtido
        // corretamente)
        Tecnico tecnicoParaEnviar = new Tecnico();
        tecnicoParaEnviar.setMatricula("tec001");
        tecnicoParaEnviar.setNome("Professor Pardal");
        tecnicoParaEnviar.setSenha("senha123");
        Tecnico tecnicoSalvo = new Tecnico();
        tecnicoSalvo.setMatricula("tec001");
        tecnicoSalvo.setNome("Professor Pardal");
        tecnicoSalvo.setTipo(TipoUsuario.TECNICO);

        when(coordenadorService.cadastrarTecnico(eq(MATRICULA_COORDENADOR), any(Tecnico.class)))
                .thenReturn(tecnicoSalvo);

        mockMvc.perform(post("/api/coordenadores/{matricula}/tecnicos", MATRICULA_COORDENADOR)
                .header("Authorization", "Bearer " + tokenCoordenador)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tecnicoParaEnviar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("tec001"));
    }

    @Test
    void coordenadorAutenticadoDeveListarTecnicos() throws Exception {
        // 1. CENÁRIO
        // "Ensine" o tecnicoService a retornar uma página vazia quando for chamado.
        // O conteúdo não importa, apenas que o método seja alcançado.
        Page<Usuario> paginaVazia = new PageImpl<>(List.of());
        when(tecnicoService.listarTodos(any())).thenReturn(paginaVazia);

        // 2. AÇÃO & VERIFICAÇÃO
        // Tenta acessar o endpoint GET de tecnicos com o token do Coordenador
        mockMvc.perform(get("/api/tecnicos")
                .header("Authorization", "Bearer " + tokenCoordenador)) // Usa o token do Coordenador
                .andExpect(status().isOk()); // Esperamos 200 OK
    }
}