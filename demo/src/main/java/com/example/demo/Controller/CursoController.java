// src/main/java/com/example/demo/Controller/CursoController.java
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

    @PostMapping
    public ResponseEntity<?> criarCurso(@RequestBody Curso curso) {
        try {
            Curso novoCurso = cursoService.criarCurso(curso);
            // Retorna o DTO do novo curso e o status 201 Created
            return new ResponseEntity<>(new CursoResponseDTO(novoCurso), HttpStatus.CREATED);
        } catch (Exception e) {
            // Retorna a mensagem de erro com o status 400 Bad Request
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
