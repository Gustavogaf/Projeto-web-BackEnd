package com.example.demo.Controller.dto;

import com.example.demo.Model.Partida;
import com.example.demo.Model.StatusPartida;
import java.time.LocalDateTime;

public class PartidaResponseDTO {
    private Long id;
    private EquipePartidaDTO equipeA;
    private EquipePartidaDTO equipeB;
    private Integer placarEquipeA;
    private Integer placarEquipeB;
    private LocalDateTime dataHora;
    private StatusPartida status;

    public PartidaResponseDTO(Partida partida) {
        this.id = partida.getId();
        this.equipeA = new EquipePartidaDTO(partida.getEquipeA());
        this.equipeB = new EquipePartidaDTO(partida.getEquipeB());
        this.placarEquipeA = partida.getPlacarEquipeA();
        this.placarEquipeB = partida.getPlacarEquipeB();
        this.dataHora = partida.getDataHora();
        this.status = partida.getStatus();
    }

    // Getters
    public Long getId() { return id; }
    public EquipePartidaDTO getEquipeA() { return equipeA; }
    public EquipePartidaDTO getEquipeB() { return equipeB; }
    public Integer getPlacarEquipeA() { return placarEquipeA; }
    public Integer getPlacarEquipeB() { return placarEquipeB; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusPartida getStatus() { return status; }
}
