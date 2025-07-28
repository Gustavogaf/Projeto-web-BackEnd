package com.example.demo.Controller.dto;

import com.example.demo.Model.Equipe;

public class EquipePartidaDTO {
    private Long id;
    private String nome;

    public EquipePartidaDTO(Equipe equipe) {
        this.id = equipe.getId();
        this.nome = equipe.getNome();
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
}
