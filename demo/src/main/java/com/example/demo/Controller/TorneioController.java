package com.example.demo.Controller;

import com.example.demo.Controller.dto.PartidaResponseDTO;
import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Partida;
import com.example.demo.Model.Torneio;
import com.example.demo.Repository.PartidaRepository;
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
import com.example.demo.Controller.dto.PartidaResponseDTO;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Controller.dto.AvancoFaseResponse;


@RestController
@RequestMapping("/api/torneios")
public class TorneioController {

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private PartidaRepository partidaRepository;

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
            // O serviço retorna um objeto de resposta completo
            AvancoFaseResponse response = torneioService.avancarFase(torneioId);
            
            // Retorna a resposta completa com status OK
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
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

    @GetMapping("/{torneioId}/partidas")
    public ResponseEntity<List<PartidaResponseDTO>> listarPartidasDoTorneio(@PathVariable Long torneioId) {
        List<Partida> partidas = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneioId);
        // Mapeia a lista de Partida para uma lista de PartidaResponseDTO
        List<PartidaResponseDTO> response = partidas.stream()
                .map(PartidaResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
