package com.example.demo.Controller;

import com.example.demo.Controller.dto.UsuarioResponseDTO;
import com.example.demo.Model.Tecnico;
import com.example.demo.Service.CoordenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Controller.dto.TecnicoRequestDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coordenadores")
public class CoordenadorController {

    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping("/{matriculaCoordenador}/tecnicos")
    public ResponseEntity<?> cadastrarTecnico(
            @PathVariable String matriculaCoordenador,
            @Valid @RequestBody TecnicoRequestDTO tecnicoDTO) {

        try {
            // Convertemos o DTO para a entidade
            Tecnico novoTecnico = new Tecnico();
            novoTecnico.setMatricula(tecnicoDTO.getMatricula());
            novoTecnico.setNome(tecnicoDTO.getNome());
            novoTecnico.setSenha(tecnicoDTO.getSenha());
            
            Tecnico tecnicoSalvo = coordenadorService.cadastrarTecnico(matriculaCoordenador, novoTecnico);
            return new ResponseEntity<>(new UsuarioResponseDTO(tecnicoSalvo), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/{matriculaCoordenador}/tecnicos/{matriculaTecnico}")
    public ResponseEntity<?> atualizarTecnico(
            @PathVariable String matriculaCoordenador,
            @PathVariable String matriculaTecnico,
            @RequestBody Tecnico detalhesTecnico) {

        try {
            Tecnico tecnicoAtualizado = coordenadorService.atualizarTecnico(matriculaCoordenador, matriculaTecnico,
                    detalhesTecnico);
            return ResponseEntity.ok(new UsuarioResponseDTO(tecnicoAtualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{matriculaCoordenador}/tecnicos/{matriculaTecnico}")
    public ResponseEntity<?> deletarTecnico(
            @PathVariable String matriculaCoordenador,
            @PathVariable String matriculaTecnico) {

        try {
            coordenadorService.deletarTecnico(matriculaCoordenador, matriculaTecnico);
            return ResponseEntity.ok("Técnico com matrícula " + matriculaTecnico + " deletado com sucesso.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}