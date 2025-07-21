// src/main/java/com/example/demo/Controller/EsporteController.java
package com.example.demo.Controller;

import com.example.demo.Controller.dto.EsporteResponseDTO;
import com.example.demo.Model.Esporte;
import com.example.demo.Service.EsporteService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/esportes") // Define o prefixo da URL para todos os endpoints neste controller
public class EsporteController {

    @Autowired
    private EsporteService esporteService;

    @PostMapping // Mapeia para requisições HTTP POST para /api/esportes
    public ResponseEntity<?> criarEsporte(@RequestBody Esporte esporte) {
        try {
            Esporte novoEsporte = esporteService.criarEsporte(esporte);
            // Retorna o novo esporte criado e o status 201 Created
            return new ResponseEntity<>(novoEsporte, HttpStatus.CREATED);
        } catch (Exception e) {
            // Em caso de erro (ex: nome duplicado), retorna a mensagem de erro e o status 400 Bad Request
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping // Mapeia para requisições HTTP GET para /api/esportes
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
