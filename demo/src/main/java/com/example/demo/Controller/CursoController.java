package com.example.demo.Controller;

import com.example.demo.Controller.dto.CursoResponseDTO;
import com.example.demo.Model.Curso;
import com.example.demo.Service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.Controller.dto.CursoRequestDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarCursos() {
        List<Curso> cursos = cursoService.listarTodos();
        List<CursoResponseDTO> response = cursos.stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCursoPorId(@PathVariable Long id) {
        try {
            Curso curso = cursoService.buscarPorId(id);
            // Retorna o DTO do curso encontrado com status 200 OK
            return ResponseEntity.ok(new CursoResponseDTO(curso));
        } catch (Exception e) {
            // Retorna a mensagem de erro com status 404 Not Found
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarCurso(@Valid @RequestBody CursoRequestDTO cursoDTO) {
        try {
            // Convertemos o DTO para a entidade antes de passar para o serviço
            Curso curso = new Curso(cursoDTO.getNome(), cursoDTO.getCategoria());
            Curso novoCurso = cursoService.criarCurso(curso);
            return new ResponseEntity<>(new CursoResponseDTO(novoCurso), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCurso(@PathVariable Long id, @RequestBody Curso cursoDetails) {
        try {
            Curso cursoAtualizado = cursoService.atualizarCurso(id, cursoDetails);
            return ResponseEntity.ok(new CursoResponseDTO(cursoAtualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(@PathVariable Long id) {
        try {
            cursoService.deletarCurso(id);
            return ResponseEntity.ok("Curso deletado com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

