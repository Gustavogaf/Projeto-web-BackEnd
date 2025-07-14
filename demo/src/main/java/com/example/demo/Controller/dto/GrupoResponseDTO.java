// src/main/java/com/example/demo/Controller/dto/GrupoResponseDTO.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.Grupo;
import java.util.List;
import java.util.stream.Collectors;

public class GrupoResponseDTO {
    private Long id;
    private String nome;
    private List<EquipeSimpleResponseDTO> equipes;

    public GrupoResponseDTO(Grupo grupo) {
        this.id = grupo.getId();
        this.nome = grupo.getNome();
        this.equipes = grupo.getEquipes().stream()
                .map(EquipeSimpleResponseDTO::new)
                .collect(Collectors.toList());
    }
    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public List<EquipeSimpleResponseDTO> getEquipes() { return equipes; }
}
