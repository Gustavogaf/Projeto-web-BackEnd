package com.example.demo.Controller;

import com.example.demo.Model.Atleta;
import com.example.demo.Model.Curso;
import com.example.demo.Model.Equipe;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Tecnico;
import com.example.demo.Model.Usuario;
import com.example.demo.Service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Controller.dto.AtletaRequestDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.Controller.dto.AtletaResponseDTO;
import com.example.demo.Controller.dto.CadastroEquipeRequest;
import com.example.demo.Controller.dto.UsuarioResponseDTO;
import com.example.demo.Controller.dto.EquipeResponseDTO;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @PostMapping("/{matriculaTecnico}/equipes")
    public ResponseEntity<?> cadastrarEquipe(
            @PathVariable String matriculaTecnico,
            @Valid @RequestBody CadastroEquipeRequest request) {

        try {
            // --- INÍCIO DA CORREÇÃO ---
            // Agora construímos a entidade Equipe com os objetos completos
            Equipe equipeInfo = new Equipe();
            equipeInfo.setNome(request.getEquipe().getNome());

            // Criamos instâncias vazias para carregar as informações
            Curso curso = new Curso();
            curso.setId(request.getEquipe().getCursoId());

            Esporte esporte = new Esporte();
            esporte.setId(request.getEquipe().getEsporteId());

            // Associamos os objetos com apenas o ID à equipe
            equipeInfo.setCurso(curso);
            equipeInfo.setEsporte(esporte);
            // --- FIM DA CORREÇÃO ---

            Equipe equipeSalva = tecnicoService.cadastrarEquipe(
                    matriculaTecnico,
                    equipeInfo,
                    request.getMatriculasAtletas());
            return new ResponseEntity<>(new EquipeResponseDTO(equipeSalva), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTecnicos() {
        List<Usuario> tecnicos = tecnicoService.listarTodos();
        List<UsuarioResponseDTO> response = tecnicos.stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<?> buscarTecnicoPorMatricula(@PathVariable String matricula) {
        try {
            Usuario tecnico = tecnicoService.buscarPorMatricula(matricula);
            return ResponseEntity.ok(new UsuarioResponseDTO(tecnico));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{matriculaTecnico}/atletas")
    public ResponseEntity<?> cadastrarAtleta(
            @PathVariable String matriculaTecnico,
            @Valid @RequestBody AtletaRequestDTO atletaDTO) {

        try {
            // Convertemos o DTO para a entidade
            Atleta novoAtleta = new Atleta();
            novoAtleta.setMatricula(atletaDTO.getMatricula());
            novoAtleta.setNome(atletaDTO.getNome());
            novoAtleta.setApelido(atletaDTO.getApelido());
            novoAtleta.setTelefone(atletaDTO.getTelefone());
            novoAtleta.setSenha(atletaDTO.getSenha());

            Atleta atletaSalvo = tecnicoService.cadastrarAtleta(matriculaTecnico, novoAtleta);
            return new ResponseEntity<>(new AtletaResponseDTO(atletaSalvo), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{matriculaTecnico}/atletas/{matriculaAtleta}")
    public ResponseEntity<?> atualizarAtleta(
            @PathVariable String matriculaTecnico,
            @PathVariable String matriculaAtleta,
            @RequestBody Atleta detalhesAtleta) {

        try {
            Atleta atletaAtualizado = tecnicoService.atualizarAtleta(matriculaTecnico, matriculaAtleta, detalhesAtleta);
            return ResponseEntity.ok(new AtletaResponseDTO(atletaAtualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matriculaTecnico}/atletas/{matriculaAtleta}")
    public ResponseEntity<?> removerAtletaDaEquipe(
            @PathVariable String matriculaTecnico,
            @PathVariable String matriculaAtleta) {

        try {
            tecnicoService.removerAtletaDaEquipe(matriculaTecnico, matriculaAtleta);
            return ResponseEntity.ok("Atleta " + matriculaAtleta + " removido da sua equipe com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matriculaTecnico}/atletas/{matriculaAtleta}/db")
    public ResponseEntity<?> deletarAtleta(
            @PathVariable String matriculaTecnico,
            @PathVariable String matriculaAtleta) {

        try {
            tecnicoService.deletarAtleta(matriculaTecnico, matriculaAtleta);
            return ResponseEntity.ok("Atleta com matrícula " + matriculaAtleta + " foi permanentemente deletado.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matriculaTecnico}/equipes/{equipeId}")
    public ResponseEntity<?> deletarEquipe(
            @PathVariable String matriculaTecnico,
            @PathVariable Long equipeId) {

        try {
            tecnicoService.deletarEquipe(matriculaTecnico, equipeId);
            return ResponseEntity.ok("Equipe com ID " + equipeId + " deletada com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
