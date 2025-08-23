package com.example.demo.Controller;

import com.example.demo.Controller.dto.EsporteResponseDTO;
import com.example.demo.Model.Esporte;
import com.example.demo.Service.EsporteService;
import com.example.demo.Controller.dto.EsporteRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esportes")
public class EsporteController {

    @Autowired
    private EsporteService esporteService;

    @PostMapping
    public ResponseEntity<?> criarEsporte(@Valid @RequestBody EsporteRequestDTO esporteDTO) {
        try {
            // Convertemos o DTO para a entidade
            Esporte esporte = new Esporte(esporteDTO.getNome(), esporteDTO.getMinAtletas(), esporteDTO.getMaxAtletas());
            Esporte novoEsporte = esporteService.criarEsporte(esporte);
            return new ResponseEntity<>(new EsporteResponseDTO(novoEsporte), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<EsporteResponseDTO>> listarEsportes() {
        // 1. Busca todos os esportes do serviço
        List<Esporte> esportes = esporteService.listarTodos();

        // 2. Converte a lista de entidades para uma lista de DTOs
        List<EsporteResponseDTO> response = esportes.stream()
                .map(EsporteResponseDTO::new)
                .toList();

        // 3. Retorna a lista de DTOs com o status 200 OK
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEsporte(@PathVariable Long id, @RequestBody Esporte esporteDetails) {
        try {
            Esporte esporteAtualizado = esporteService.atualizarEsporte(id, esporteDetails);
            return ResponseEntity.ok(esporteAtualizado);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEsporte(@PathVariable Long id) {
        try {
            esporteService.deletarEsporte(id);
            return ResponseEntity.ok("Esporte deletado com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
