package com.example.demo.Controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos no JSON
public class AvancoFaseResponse {

    private String status;
    private List<PartidaResponseDTO> proximasPartidas;
    private EquipeResponseDTO campeao;

    // Construtor para quando há próximas partidas
    public AvancoFaseResponse(String status, List<PartidaResponseDTO> proximasPartidas) {
        this.status = status;
        this.proximasPartidas = proximasPartidas;
    }

    // Construtor para quando o torneio finaliza
    public AvancoFaseResponse(String status, EquipeResponseDTO campeao) {
        this.status = status;
        this.campeao = campeao;
    }

    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<PartidaResponseDTO> getProximasPartidas() { return proximasPartidas; }
    public void setProximasPartidas(List<PartidaResponseDTO> proximasPartidas) { this.proximasPartidas = proximasPartidas; }
    public EquipeResponseDTO getCampeao() { return campeao; }
    public void setCampeao(EquipeResponseDTO campeao) { this.campeao = campeao; }
}