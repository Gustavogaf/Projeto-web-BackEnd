package com.example.demo.Controller;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Partida;
import com.example.demo.Model.Torneio;
import com.example.demo.Service.TorneioService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Controller.dto.IniciarTorneioRequest;
import com.example.demo.Controller.dto.TorneioResponseDTO;


@RestController
@RequestMapping("/api/torneios")
public class TorneioController {

    @Autowired
    private TorneioService torneioService;

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarFaseDeGrupos(@RequestBody IniciarTorneioRequest request) {
        try {
            
            Esporte esporte = new Esporte();
            esporte.setId(request.getEsporteId());

            Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(esporte, request.getCategoria());
            return new ResponseEntity<>(torneioIniciado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{torneioId}/avancar-fase")
    public ResponseEntity<?> avancarFaseDoTorneio(@PathVariable Long torneioId) {
        try {
            List<Partida> proximaFase = torneioService.avancarFase(torneioId);
            
            if (proximaFase.isEmpty()) {
                // Se a lista de partidas estiver vazia, significa que o torneio acabou.
                return new ResponseEntity<>("Torneio finalizado! Campeão determinado.", HttpStatus.OK);
            }
            
            // Retorna a lista de partidas da nova fase.
            return new ResponseEntity<>(proximaFase, HttpStatus.CREATED);
        } catch (Exception e) {
            // Retorna a mensagem de erro (ex: "Ainda existem partidas agendadas...")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<TorneioResponseDTO>> listarTorneios() {
        List<Torneio> torneios = torneioService.listarTodos();
        List<TorneioResponseDTO> response = torneios.stream()
                .map(TorneioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

