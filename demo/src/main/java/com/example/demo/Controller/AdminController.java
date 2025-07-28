package com.example.demo.Controller;

import com.example.demo.Controller.dto.UsuarioResponseDTO;
import com.example.demo.Model.Coordenador;
import com.example.demo.Model.Arbitro;
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
            return new ResponseEntity<>(new UsuarioResponseDTO(novoCoordenador), HttpStatus.CREATED);
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

    @PutMapping("/coordenadores/{matricula}")
    public ResponseEntity<?> atualizarCoordenador(@PathVariable String matricula,
            @RequestBody Coordenador detalhesCoordenador) {
        try {
            Coordenador coordenadorAtualizado = adminService.atualizarCoordenador(matricula, detalhesCoordenador);
            return ResponseEntity.ok(new UsuarioResponseDTO(coordenadorAtualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/coordenadores/{matricula}")
    public ResponseEntity<?> deletarCoordenador(@PathVariable String matricula) {
        try {
            adminService.deletarCoordenador(matricula);
            return ResponseEntity.ok("Coordenador com a matrícula " + matricula + " deletado com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/arbitros")
    public ResponseEntity<?> cadastrarArbitro(@RequestBody Arbitro arbitro) {
        try {
            Arbitro novoArbitro = adminService.cadastrarArbitro(arbitro);
            return new ResponseEntity<>(new UsuarioResponseDTO(novoArbitro), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/arbitros/{matricula}")
    public ResponseEntity<?> atualizarArbitro(@PathVariable String matricula, @RequestBody Arbitro detalhesArbitro) {
        try {
            Arbitro arbitroAtualizado = adminService.atualizarArbitro(matricula, detalhesArbitro);
            return ResponseEntity.ok(new UsuarioResponseDTO(arbitroAtualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/arbitros/{matricula}")
    public ResponseEntity<?> deletarArbitro(@PathVariable String matricula) {
        try {
            adminService.deletarArbitro(matricula);
            return ResponseEntity.ok("Árbitro com a matrícula " + matricula + " deletado com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}