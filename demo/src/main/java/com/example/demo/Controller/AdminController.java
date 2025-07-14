// src/main/java/com/example/demo/Controller/AdminController.java
package com.example.demo.Controller;

import com.example.demo.Controller.dto.UsuarioResponseDTO;
import com.example.demo.Model.Coordenador;
import com.example.demo.Model.Usuario;
import com.example.demo.Service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/coordenadores")
    public ResponseEntity<List<UsuarioResponseDTO>> listarCoordenadores() {
        List<Usuario> coordenadores = adminService.listarCoordenadores();
        List<UsuarioResponseDTO> response = coordenadores.stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}