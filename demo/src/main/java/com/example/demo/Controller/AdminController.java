// src/main/java/com/example/demo/Controller/AdminController.java
package com.example.demo.Controller;

import com.example.demo.Model.Coordenador;
import com.example.demo.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/coordenadores")
    public ResponseEntity<?> cadastrarCoordenador(@RequestBody Coordenador coordenador) {
        try {
            Coordenador novoCoordenador = adminService.cadastrarCoordenador(coordenador);
            return new ResponseEntity<>(novoCoordenador, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}