package com.example.demo.Controller;

import com.example.demo.Controller.dto.EquipeResponseDTO;
import com.example.demo.Model.Equipe;
import com.example.demo.Service.EquipeService;
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
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @GetMapping
    public ResponseEntity<List<EquipeResponseDTO>> listarEquipes() {
        List<Equipe> equipes = equipeService.listarTodas();
        List<EquipeResponseDTO> response = equipes.stream()
                .map(EquipeResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEquipePorId(@PathVariable Long id) {
        try {
            Equipe equipe = equipeService.buscarPorId(id);
            return ResponseEntity.ok(new EquipeResponseDTO(equipe));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
