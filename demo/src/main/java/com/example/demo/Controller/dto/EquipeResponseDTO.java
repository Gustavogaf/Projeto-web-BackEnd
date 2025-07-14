// src/main/java/com/example/demo/Controller/dto/EquipeResponseDTO.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.Equipe;
import java.util.List;
import java.util.stream.Collectors;

public class EquipeResponseDTO {
    private Long id;
    private String nome;
    private String nomeCurso;
    private String nomeEsporte;
    private String nomeTecnico;
    private List<AtletaResponseDTO> atletas;

    public EquipeResponseDTO(Equipe equipe) {
        this.id = equipe.getId();
        this.nome = equipe.getNome();
        this.nomeCurso = equipe.getCurso().getNome();
        this.nomeEsporte = equipe.getEsporte().getNome();
        this.nomeTecnico = equipe.getTecnico().getNome();
        this.atletas = equipe.getAtletas().stream()
                .map(AtletaResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeCurso() { return nomeCurso; }
    public String getNomeEsporte() { return nomeEsporte; }
    public String getNomeTecnico() { return nomeTecnico; }
    public List<AtletaResponseDTO> getAtletas() { return atletas; }
}
