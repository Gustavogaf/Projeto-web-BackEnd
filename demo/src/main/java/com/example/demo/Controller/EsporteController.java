// src/main/java/com/example/demo/Controller/EsporteController.java
package com.example.demo.Controller;

import com.example.demo.Model.Esporte;
import com.example.demo.Service.EsporteService;
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
}
