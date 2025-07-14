// src/main/java/com/example/demo/Controller/TorneioController.java
package com.example.demo.Controller;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Torneio;
import com.example.demo.Service.TorneioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// DTO para receber os dados para iniciar o torneio
class IniciarTorneioRequest {
    private Long esporteId;
    private CategoriaCurso categoria;

    // Getters e Setters
    public Long getEsporteId() { return esporteId; }
    public void setEsporteId(Long esporteId) { this.esporteId = esporteId; }
    public CategoriaCurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaCurso categoria) { this.categoria = categoria; }
}

@RestController
@RequestMapping("/api/torneios")
public class TorneioController {

    @Autowired
    private TorneioService torneioService;

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarFaseDeGrupos(@RequestBody IniciarTorneioRequest request) {
        try {
            // No mundo real, precisaríamos buscar o objeto Esporte pelo ID.
            // Por simplicidade aqui, vamos assumir que o service pode lidar com isso.
            // O ideal seria o service receber o ID e buscar no repositório.
            Esporte esporte = new Esporte();
            esporte.setId(request.getEsporteId());

            Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(esporte, request.getCategoria());
            return new ResponseEntity<>(torneioIniciado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
