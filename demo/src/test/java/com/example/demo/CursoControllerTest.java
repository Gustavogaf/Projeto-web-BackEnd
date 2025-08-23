package com.example.demo;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Curso;
import com.example.demo.Service.CursoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.example.demo.Controller.dto.CursoRequestDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CursoService cursoService;

    @Test
    public void deveRetornarCursoPorIdComSucesso() throws Exception {
        // 1. CENÁRIO
        Long cursoId = 1L;
        Curso cursoMock = new Curso("Sistemas de Informação", CategoriaCurso.SUPERIOR);
        cursoMock.setId(cursoId);

        // "Ensinamos" o nosso dublê do serviço a retornar o curso quando
        // buscarPorId(1L) for chamado.
        when(cursoService.buscarPorId(cursoId)).thenReturn(cursoMock);

        // 2. AÇÃO
        // Simulamos uma requisição GET para a nossa API
        ResultActions resultado = mockMvc.perform(get("/api/cursos/{id}", cursoId));

        // 3. VERIFICAÇÃO
        // Verificamos se a resposta da API é a esperada
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cursoId))
                .andExpect(jsonPath("$.nome").value("Sistemas de Informação"))
                .andExpect(jsonPath("$.categoria").value("SUPERIOR"));
    }

    @Test
    public void deveRetornarNotFoundQuandoCursoNaoExistir() throws Exception {
        // 1. CENÁRIO
        Long cursoIdInexistente = 99L;
        String mensagemErro = "Curso com o ID " + cursoIdInexistente + " não encontrado.";

        // "Ensinamos" o dublê a lançar uma exceção quando um curso que não existe for
        // buscado.
        when(cursoService.buscarPorId(cursoIdInexistente)).thenThrow(new Exception(mensagemErro));

        // 2. AÇÃO
        ResultActions resultado = mockMvc.perform(get("/api/cursos/{id}", cursoIdInexistente));

        // 3. VERIFICAÇÃO
        // Verificamos se a API tratou o erro corretamente, retornando 404 Not Found
        resultado.andExpect(status().isNotFound());
    }

    @Test
public void naoDeveCriarCursoComNomeVazio() throws Exception {
    // Cenário
    CursoRequestDTO cursoInvalido = new CursoRequestDTO();
    cursoInvalido.setNome(""); // Nome vazio
    cursoInvalido.setCategoria(CategoriaCurso.SUPERIOR);

    // Ação e Verificação
    mockMvc.perform(post("/api/cursos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cursoInvalido)))
            .andExpect(status().isBadRequest()); // Esperamos um erro 400
}

@Test
public void naoDeveCriarCursoComCategoriaNula() throws Exception {
    // Cenário
    CursoRequestDTO cursoInvalido = new CursoRequestDTO();
    cursoInvalido.setNome("Curso Válido");
    cursoInvalido.setCategoria(null); // Categoria nula

    // Ação e Verificação
    mockMvc.perform(post("/api/cursos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cursoInvalido)))
            .andExpect(status().isBadRequest());
}
}