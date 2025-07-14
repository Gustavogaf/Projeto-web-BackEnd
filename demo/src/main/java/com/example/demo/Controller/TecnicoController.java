// src/main/java/com/example/demo/Controller/TecnicoController.java
package com.example.demo.Controller;

import com.example.demo.Model.Atleta;
import com.example.demo.Model.Equipe;
import com.example.demo.Model.Usuario;
import com.example.demo.Service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.Controller.dto.AtletaResponseDTO;
import com.example.demo.Controller.dto.CadastroEquipeRequest;
import com.example.demo.Controller.dto.UsuarioResponseDTO;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @PostMapping("/{matriculaTecnico}/equipes")
    public ResponseEntity<?> cadastrarEquipe(
            @PathVariable String matriculaTecnico,
            @RequestBody CadastroEquipeRequest request) {

        try {
            Equipe equipeSalva = tecnicoService.cadastrarEquipe(
                    matriculaTecnico,
                    request.getEquipe(),
                    request.getMatriculasAtletas()
            );
            return new ResponseEntity<>(equipeSalva, HttpStatus.CREATED);
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

    @PostMapping("/{matriculaTecnico}/atletas")
    public ResponseEntity<?> cadastrarAtleta(
            @PathVariable String matriculaTecnico,
            @RequestBody Atleta novoAtleta) {

        try {
            Atleta atletaSalvo = tecnicoService.cadastrarAtleta(matriculaTecnico, novoAtleta);
            // Retorna o DTO do atleta criado (reutilizando o AtletaResponseDTO)
            return new ResponseEntity<>(new AtletaResponseDTO(atletaSalvo), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
