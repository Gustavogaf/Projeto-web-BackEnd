package com.example.demo.Controller;

import com.example.demo.Controller.dto.ArbitroRequestDTO;
import com.example.demo.Controller.dto.CoordenadorRequestDTO;
import com.example.demo.Controller.dto.UsuarioResponseDTO;
import com.example.demo.Model.Coordenador;
import com.example.demo.Model.Arbitro;
import com.example.demo.Model.Usuario;
import com.example.demo.Service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> cadastrarCoordenador(@Valid @RequestBody CoordenadorRequestDTO coordenadorDTO) {
        try {
            // Convertemos o DTO para a entidade
            Coordenador coordenador = new Coordenador();
            coordenador.setMatricula(coordenadorDTO.getMatricula());
            coordenador.setNome(coordenadorDTO.getNome());
            coordenador.setSenha(coordenadorDTO.getSenha());

            Coordenador novoCoordenador = adminService.cadastrarCoordenador(coordenador);
            return new ResponseEntity<>(new UsuarioResponseDTO(novoCoordenador), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/coordenadores")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarCoordenadores(
            @PageableDefault(size = 10, sort = { "nome" }) Pageable paginacao) {
        Page<Usuario> coordenadores = adminService.listarCoordenadores(paginacao);
        Page<UsuarioResponseDTO> response = coordenadores.map(UsuarioResponseDTO::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coordenadores/{matricula}")
    public ResponseEntity<?> buscarCoordenadorPorMatricula(@PathVariable String matricula) {
        try {
            Coordenador coordenador = adminService.buscarCoordenadorPorMatricula(matricula);
            return ResponseEntity.ok(new UsuarioResponseDTO(coordenador));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
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
    public ResponseEntity<?> cadastrarArbitro(@Valid @RequestBody ArbitroRequestDTO arbitroDTO) {
        try {
            // Convertemos o DTO para a entidade
            Arbitro arbitro = new Arbitro();
            arbitro.setMatricula(arbitroDTO.getMatricula());
            arbitro.setNome(arbitroDTO.getNome());
            arbitro.setSenha(arbitroDTO.getSenha());

            Arbitro novoArbitro = adminService.cadastrarArbitro(arbitro);
            return new ResponseEntity<>(new UsuarioResponseDTO(novoArbitro), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/arbitros/{matricula}")
    public ResponseEntity<?> buscarArbitroPorMatricula(@PathVariable String matricula) {
        try {
            Arbitro arbitro = adminService.buscarArbitroPorMatricula(matricula);
            return ResponseEntity.ok(new UsuarioResponseDTO(arbitro));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/arbitros")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarArbitros(
            @PageableDefault(size = 10, sort = { "nome" }) Pageable paginacao) {
        Page<Usuario> arbitros = adminService.listarArbitros(paginacao);
        Page<UsuarioResponseDTO> response = arbitros.map(UsuarioResponseDTO::new);
        return ResponseEntity.ok(response);
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