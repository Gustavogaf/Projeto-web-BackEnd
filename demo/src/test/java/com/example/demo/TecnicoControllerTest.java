package com.example.demo;

import com.example.demo.Controller.dto.*;
import com.example.demo.Model.*;
import com.example.demo.Service.AuthUserDetailsService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @MockBean
    private AuthUserDetailsService authUserDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private String tokenTecnico;
    private final String MATRICULA_TECNICO = "tec007";
    private final String SENHA_TECNICO = "senhaTecnico";

    @BeforeEach
    void setUp() throws Exception {
        UserDetails userDetails = User.builder()
                .username(MATRICULA_TECNICO)
                .password("senhaCriptografadaQualquer")
                .roles("TECNICO")
                .build();

        when(authUserDetailsService.loadUserByUsername(MATRICULA_TECNICO)).thenReturn(userDetails);
        when(passwordEncoder.matches(eq(SENHA_TECNICO), anyString())).thenReturn(true);

        this.tokenTecnico = obterToken(MATRICULA_TECNICO, SENHA_TECNICO);
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
    public void deveCadastrarUmaEquipeComSucesso() throws Exception {
        EquipeInfoRequestDTO equipeInfo = new EquipeInfoRequestDTO();
        equipeInfo.setNome("Os Invencíveis");
        equipeInfo.setCursoId(1L);
        equipeInfo.setEsporteId(1L);
        CadastroEquipeRequest request = new CadastroEquipeRequest();
        request.setEquipe(equipeInfo);
        request.setMatriculasAtletas(List.of("atl01"));

        Equipe equipeSalva = new Equipe();
        equipeSalva.setId(10L);
        equipeSalva.setNome("Os Invencíveis");
        equipeSalva.setCurso(new Curso("Curso Teste", CategoriaCurso.SUPERIOR));
        equipeSalva.setEsporte(new Esporte("Esporte Teste", 1, 1));
        equipeSalva.setTecnico(new Tecnico());
        equipeSalva.setAtletas(List.of(new Atleta()));

        when(tecnicoService.cadastrarEquipe(eq(MATRICULA_TECNICO), any(Equipe.class), any(List.class))).thenReturn(equipeSalva);

        mockMvc.perform(post("/api/tecnicos/{matricula}/equipes", MATRICULA_TECNICO)
                .header("Authorization", "Bearer " + tokenTecnico)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void deveDeletarAtletaDoBancoComSucesso() throws Exception {
        String matriculaAtleta = "atl-delete";
        doNothing().when(tecnicoService).deletarAtleta(MATRICULA_TECNICO, matriculaAtleta);

        mockMvc.perform(delete("/api/tecnicos/{mt}/atletas/{ma}/db", MATRICULA_TECNICO, matriculaAtleta)
                .header("Authorization", "Bearer " + tokenTecnico))
                .andExpect(status().isOk());
    }
    
    @Test
    public void naoDevePermitirAcessoSemToken() throws Exception {
         mockMvc.perform(delete("/api/tecnicos/{mt}/atletas/{ma}/db", MATRICULA_TECNICO, "atl-delete"))
                .andExpect(status().isForbidden());
    }
}