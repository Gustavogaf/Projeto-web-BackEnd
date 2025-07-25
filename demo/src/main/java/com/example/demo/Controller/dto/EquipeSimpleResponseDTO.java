
package com.example.demo.Controller.dto;

import com.example.demo.Model.Equipe;

public class EquipeSimpleResponseDTO {
    private Long id;
    private String nome;
    private String nomeCurso;

    public EquipeSimpleResponseDTO(Equipe equipe) {
        this.id = equipe.getId();
        this.nome = equipe.getNome();
        this.nomeCurso = equipe.getCurso().getNome();
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeCurso() { return nomeCurso; }
}
