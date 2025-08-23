// src/test/java/com/example/demo/TecnicoControllerTest.java
package com.example.demo;

import com.example.demo.Controller.dto.AtletaResponseDTO;
import com.example.demo.Controller.dto.CadastroEquipeRequest; // Importe o DTO
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
import com.example.demo.Controller.dto.AtletaRequestDTO;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.demo.Controller.dto.EquipeInfoRequestDTO;

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

        // Dados da requisição (DTOs)
        EquipeInfoRequestDTO equipeInfo = new EquipeInfoRequestDTO();
        equipeInfo.setNome("Os Invencíveis");
        equipeInfo.setCursoId(1L);
        equipeInfo.setEsporteId(1L);

        List<String> matriculasAtletas = List.of("atl01");

        CadastroEquipeRequest request = new CadastroEquipeRequest();
        request.setEquipe(equipeInfo);
        request.setMatriculasAtletas(matriculasAtletas);

        // --- INÍCIO DA CORREÇÃO ---
        // Preparando uma resposta COMPLETA e realista que o serviço retornaria
        Curso cursoMock = new Curso("Sistemas de Informação", CategoriaCurso.SUPERIOR);
        Esporte esporteMock = new Esporte("Futsal", 5, 10);
        Tecnico tecnicoMock = new Tecnico();
        tecnicoMock.setNome("Professor Tite");
        Atleta atletaMock = new Atleta();
        atletaMock.setMatricula("atl01");
        atletaMock.setNome("Neymar");

        Equipe equipeSalva = new Equipe();
        equipeSalva.setId(10L);
        equipeSalva.setNome("Os Invencíveis");
        equipeSalva.setCurso(cursoMock);
        equipeSalva.setEsporte(esporteMock);
        equipeSalva.setTecnico(tecnicoMock);
        equipeSalva.setAtletas(List.of(atletaMock));
        // --- FIM DA CORREÇÃO ---

        // "Ensinamos" o dublê do serviço a retornar o objeto completo
        when(tecnicoService.cadastrarEquipe(eq(matriculaTecnico), any(Equipe.class), any(List.class)))
                .thenReturn(equipeSalva);

        // 2. AÇÃO
        ResultActions resultado = mockMvc.perform(post("/api/tecnicos/{matricula}/equipes", matriculaTecnico)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // 3. VERIFICAÇÃO
        resultado.andExpect(status().isCreated()) // Agora sim!
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("Os Invencíveis"))
                .andExpect(jsonPath("$.nomeCurso").value("Sistemas de Informação"));
    }

    @Test
    public void deveAtualizarUmAtletaComSucesso() throws Exception {
        // Cenário
        String matriculaTecnico = "tec007";
        String matriculaAtleta = "atl01";

        Atleta detalhesAtleta = new Atleta();
        detalhesAtleta.setApelido("Craque");
        detalhesAtleta.setTelefone("79988887777");

        Atleta atletaAtualizado = new Atleta();
        atletaAtualizado.setMatricula(matriculaAtleta);
        atletaAtualizado.setNome("Nome do Atleta");
        atletaAtualizado.setApelido("Craque");
        atletaAtualizado.setTelefone("79988887777");
        atletaAtualizado.setTipo(TipoUsuario.ATLETA);

        when(tecnicoService.atualizarAtleta(eq(matriculaTecnico), eq(matriculaAtleta), any(Atleta.class)))
                .thenReturn(atletaAtualizado);

        // Ação
        ResultActions resultado = mockMvc
                .perform(put("/api/tecnicos/{mt}/atletas/{ma}", matriculaTecnico, matriculaAtleta)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(detalhesAtleta)));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value("Craque"))
                .andExpect(jsonPath("$.telefone").value("79988887777"));
    }

    @Test
    public void deveRemoverUmAtletaDaEquipeComSucesso() throws Exception {
        // Cenário
        String matriculaTecnico = "tec007";
        String matriculaAtleta = "atl02";
        doNothing().when(tecnicoService).removerAtletaDaEquipe(matriculaTecnico, matriculaAtleta);

        // Ação
        ResultActions resultado = mockMvc
                .perform(delete("/api/tecnicos/{mt}/atletas/{ma}", matriculaTecnico, matriculaAtleta));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(content().string("Atleta " + matriculaAtleta + " removido da sua equipe com sucesso."));

        verify(tecnicoService, times(1)).removerAtletaDaEquipe(matriculaTecnico, matriculaAtleta);
    }

    @Test
    public void naoDeveRemoverAtletaSeTecnicoNaoForDaEquipe() throws Exception {
        // Cenário
        String matriculaTecnico = "tec008"; // Outro técnico
        String matriculaAtleta = "atl01";

        // "Ensinamos" o mock a lançar a exceção de permissão
        doThrow(new Exception("Você não tem permissão para remover atletas desta equipe."))
                .when(tecnicoService).removerAtletaDaEquipe(matriculaTecnico, matriculaAtleta);

        // Ação
        ResultActions resultado = mockMvc
                .perform(delete("/api/tecnicos/{mt}/atletas/{ma}", matriculaTecnico, matriculaAtleta));

        // Verificação
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().string("Você não tem permissão para remover atletas desta equipe."));
    }

    @Test
    public void deveDeletarAtletaDoBancoComSucesso() throws Exception {
        // Cenário
        String matriculaTecnico = "tec-cadastro";
        String matriculaAtleta = "atl-delete";
        doNothing().when(tecnicoService).deletarAtleta(matriculaTecnico, matriculaAtleta);

        // Ação
        ResultActions resultado = mockMvc
                .perform(delete("/api/tecnicos/{mt}/atletas/{ma}/db", matriculaTecnico, matriculaAtleta));

        // Verificação
        resultado.andExpect(status().isOk())
                .andExpect(
                        content().string("Atleta com matrícula " + matriculaAtleta + " foi permanentemente deletado."));

        verify(tecnicoService, times(1)).deletarAtleta(matriculaTecnico, matriculaAtleta);
    }

    @Test
    public void naoDeveDeletarAtletaSeNaoForCadastradoPeloTecnico() throws Exception {
        // Cenário
        String matriculaTecnico = "tec-intruso";
        String matriculaAtleta = "atl-protegido";

        String mensagemErro = "Você não tem permissão para deletar este atleta, pois não foi você quem o cadastrou.";
        doThrow(new Exception(mensagemErro))
                .when(tecnicoService).deletarAtleta(matriculaTecnico, matriculaAtleta);

        // Ação
        ResultActions resultado = mockMvc
                .perform(delete("/api/tecnicos/{mt}/atletas/{ma}/db", matriculaTecnico, matriculaAtleta));

        // Verificação
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().string(mensagemErro));
    }

    @Test
    public void naoDeveCadastrarAtletaComTelefoneVazio() throws Exception {
        // Cenário
        String matriculaTecnico = "tec007";
        AtletaRequestDTO atletaInvalido = new AtletaRequestDTO();
        atletaInvalido.setMatricula("atlValido");
        atletaInvalido.setNome("Nome Válido");
        atletaInvalido.setSenha("senhaValida");
        atletaInvalido.setTelefone(""); // Telefone inválido

        // Ação e Verificação
        mockMvc.perform(post("/api/tecnicos/{mt}/atletas", matriculaTecnico)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atletaInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void naoDeveCadastrarAtletaComNomeCurto() throws Exception {
        // Cenário
        String matriculaTecnico = "tec007";
        AtletaRequestDTO atletaInvalido = new AtletaRequestDTO();
        atletaInvalido.setMatricula("atlValido2");
        atletaInvalido.setNome("Al"); // Nome inválido (curto)
        atletaInvalido.setSenha("senhaValida");
        atletaInvalido.setTelefone("12345678");

        // Ação e Verificação
        mockMvc.perform(post("/api/tecnicos/{mt}/atletas", matriculaTecnico)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atletaInvalido)))
                .andExpect(status().isBadRequest());
    }
}