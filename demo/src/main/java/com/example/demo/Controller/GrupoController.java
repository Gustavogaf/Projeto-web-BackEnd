package com.example.demo.Controller;

import com.example.demo.Controller.dto.GrupoResponseDTO;
import com.example.demo.Model.Grupo;
import com.example.demo.Service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarGrupoPorId(@PathVariable Long id) {
        try {
            Grupo grupo = grupoService.buscarPorId(id);
            return ResponseEntity.ok(new GrupoResponseDTO(grupo));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}