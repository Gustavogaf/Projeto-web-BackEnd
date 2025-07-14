// src/main/java/com/example/demo/Controller/TecnicoController.java
package com.example.demo.Controller;

import com.example.demo.Model.Equipe;
import com.example.demo.Service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DTO (Data Transfer Object) para encapsular os dados da requisição
class CadastroEquipeRequest {
    private Equipe equipe;
    private List<String> matriculasAtletas;

    // Getters e Setters
    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
    public List<String> getMatriculasAtletas() { return matriculasAtletas; }
    public void setMatriculasAtletas(List<String> matriculasAtletas) { this.matriculasAtletas = matriculasAtletas; }
}

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
}
