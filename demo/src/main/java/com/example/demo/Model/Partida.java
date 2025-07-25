package com.example.demo.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Equipe equipeA;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Equipe equipeB;

     @ManyToOne
    private Torneio torneio;

    private Integer placarEquipeA;
    private Integer placarEquipeB;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusPartida status;

    // Construtores, Getters e Setters

    public Partida() {
        this.status = StatusPartida.AGENDADA; // Uma nova partida sempre começa como agendada
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipe getEquipeA() {
        return equipeA;
    }

    public void setEquipeA(Equipe equipeA) {
        this.equipeA = equipeA;
    }

    public Equipe getEquipeB() {
        return equipeB;
    }

    public void setEquipeB(Equipe equipeB) {
        this.equipeB = equipeB;
    }

    public Integer getPlacarEquipeA() {
        return placarEquipeA;
    }

    public void setPlacarEquipeA(Integer placarEquipeA) {
        this.placarEquipeA = placarEquipeA;
    }

    public Integer getPlacarEquipeB() {
        return placarEquipeB;
    }

    public void setPlacarEquipeB(Integer placarEquipeB) {
        this.placarEquipeB = placarEquipeB;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public void setStatus(StatusPartida status) {
        this.status = status;
    }

    public Torneio getTorneio() { return torneio; }
    public void setTorneio(Torneio torneio) { this.torneio = torneio; }
}
