package com.example.demo;

import com.example.demo.Controller.dto.AuthRequestDTO;
import com.example.demo.Controller.dto.PlacarRequest;
import com.example.demo.Model.Partida;
import com.example.demo.Model.StatusPartida;
import com.example.demo.Service.ArbitroService;
import com.example.demo.Service.AuthUserDetailsService;
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
import com.example.demo.Model.Equipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        @MockBean
        private AuthUserDetailsService authUserDetailsService;

        @MockBean
        private PasswordEncoder passwordEncoder;

        private String tokenArbitro;
        private final String MATRICULA_ARBITRO = "arb001";
        private final String SENHA_ARBITRO = "senhaArbitro";

        @BeforeEach
        void setUp() throws Exception {
                UserDetails userDetails = User.builder()
                                .username(MATRICULA_ARBITRO)
                                .password("senhaCriptografadaQualquer")
                                .roles("ARBITRO")
                                .build();

                when(authUserDetailsService.loadUserByUsername(MATRICULA_ARBITRO)).thenReturn(userDetails);

                // --- A CORREÇÃO ESTÁ AQUI ---
                // Usamos eq() para o primeiro argumento para satisfazer a regra do Mockito.
                when(passwordEncoder.matches(eq(SENHA_ARBITRO), anyString())).thenReturn(true);
                // --- FIM DA CORREÇÃO ---

                this.tokenArbitro = obterToken(MATRICULA_ARBITRO, SENHA_ARBITRO);
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
        public void deveRegistrarResultadoComSucesso() throws Exception {
                Long partidaId = 1L;
                PlacarRequest placar = new PlacarRequest();
                placar.setPlacarA(25);
                placar.setPlacarB(20);

                // --- INÍCIO DA CORREÇÃO ---
                // Criamos mocks completos para as equipes
                Equipe equipeAMock = new Equipe();
                equipeAMock.setId(1L);
                equipeAMock.setNome("Equipe A");

                Equipe equipeBMock = new Equipe();
                equipeBMock.setId(2L);
                equipeBMock.setNome("Equipe B");

                // Criamos um objeto Partida COMPLETO
                Partida partidaAtualizada = new Partida();
                partidaAtualizada.setId(partidaId);
                partidaAtualizada.setStatus(StatusPartida.FINALIZADA);
                partidaAtualizada.setPlacarEquipeA(25);
                partidaAtualizada.setPlacarEquipeB(20);
                partidaAtualizada.setEquipeA(equipeAMock); // Associamos a equipe A
                partidaAtualizada.setEquipeB(equipeBMock); // Associamos a equipe B
                // --- FIM DA CORREÇÃO ---

                when(arbitroService.registrarResultado(MATRICULA_ARBITRO, partidaId, 25, 20))
                                .thenReturn(partidaAtualizada);

                mockMvc.perform(put("/api/arbitros/{matArbitro}/partidas/{partidaId}/resultado", MATRICULA_ARBITRO,
                                partidaId)
                                .header("Authorization", "Bearer " + tokenArbitro)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(placar)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("FINALIZADA"))
                                .andExpect(jsonPath("$.equipeA.nome").value("Equipe A")); // Adicionamos uma verificação
                                                                                          // extra
        }

        @Test
        public void naoDevePermitirRegistrarResultadoSemToken() throws Exception {
                mockMvc.perform(put("/api/arbitros/{matArbitro}/partidas/{partidaId}/resultado", MATRICULA_ARBITRO, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new PlacarRequest())))
                                .andExpect(status().isForbidden());
        }
}