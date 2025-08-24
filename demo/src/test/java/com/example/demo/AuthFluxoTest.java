package com.example.demo;

import com.example.demo.Controller.dto.AuthRequestDTO;
import com.example.demo.Model.Coordenador;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Garante que o banco de dados seja limpo após cada teste
public class AuthFluxoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetamos o codificador de senhas real

    @BeforeEach
    void setUp() {
        // Limpa o repositório para garantir um teste limpo
        usuarioRepository.deleteAll();
    }

    @Test
    void deveAutenticarUsuarioDoBancoComSucesso() throws Exception {
        // 1. CENÁRIO: Criar um usuário diretamente no banco com a senha já criptografada
        String senhaTextoSimples = "senhaSegura123";
        Coordenador coordenador = new Coordenador();
        coordenador.setMatricula("coordLoginTest");
        coordenador.setNome("Coordenador de Teste");
        coordenador.setTipo(TipoUsuario.COORDENADOR);
        // Criptografamos a senha antes de salvar, como o serviço deveria fazer
        coordenador.setSenha(passwordEncoder.encode(senhaTextoSimples));
        usuarioRepository.save(coordenador);

        // 2. AÇÃO: Tentar fazer login com as credenciais em texto simples
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setMatricula("coordLoginTest");
        authRequest.setSenha(senhaTextoSimples);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                // 3. VERIFICAÇÃO:
                .andExpect(status().isOk()) // Esperamos status 200 OK
                .andExpect(jsonPath("$.token").isNotEmpty()); // E que o token seja retornado
    }
}