package com.example.demo.Controller;

import com.example.demo.Controller.dto.AtletaResponseDTO;
import com.example.demo.Model.Atleta;
import com.example.demo.Service.AtletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atletas")
public class AtletaController {

    @Autowired
    private AtletaService atletaService;

    @GetMapping
    public ResponseEntity<List<AtletaResponseDTO>> listarAtletas() {
        List<Atleta> atletas = atletaService.listarTodos();
        List<AtletaResponseDTO> response = atletas.stream()
                .map(AtletaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<?> buscarAtletaPorMatricula(@PathVariable String matricula) {
        try {
            Atleta atleta = atletaService.buscarPorMatricula(matricula);
            return ResponseEntity.ok(new AtletaResponseDTO(atleta));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
