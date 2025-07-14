// src/main/java/com/example/demo/Controller/CoordenadorController.java
package com.example.demo.Controller;

import com.example.demo.Model.Tecnico;
import com.example.demo.Service.CoordenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coordenadores")
public class CoordenadorController {

    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping("/{matriculaCoordenador}/tecnicos")
    public ResponseEntity<?> cadastrarTecnico(
            @PathVariable String matriculaCoordenador,
            @RequestBody Tecnico novoTecnico) {
        
        try {
            Tecnico tecnicoSalvo = coordenadorService.cadastrarTecnico(matriculaCoordenador, novoTecnico);
            return new ResponseEntity<>(tecnicoSalvo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}