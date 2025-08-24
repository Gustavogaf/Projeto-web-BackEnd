package com.example.demo.Controller;

import com.example.demo.Controller.dto.EsporteResponseDTO;
import com.example.demo.Model.Esporte;
import com.example.demo.Service.EsporteService;
import com.example.demo.Controller.dto.EsporteRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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
    public ResponseEntity<Page<EsporteResponseDTO>> listarEsportes(
            @PageableDefault(size = 10, sort = { "nome" }) Pageable paginacao) {
        Page<Esporte> esportes = esporteService.listarTodos(paginacao);
        Page<EsporteResponseDTO> response = esportes.map(EsporteResponseDTO::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEsportePorId(@PathVariable Long id) {
        try {
            Esporte esporte = esporteService.buscarPorId(id);
            return ResponseEntity.ok(new EsporteResponseDTO(esporte));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
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
