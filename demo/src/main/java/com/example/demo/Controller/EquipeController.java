package com.example.demo.Controller;

import com.example.demo.Controller.dto.EquipeResponseDTO;
import com.example.demo.Model.Equipe;
import com.example.demo.Service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
