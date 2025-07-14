// src/main/java/com/example/demo/Controller/dto/EsporteResponseDTO.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.Esporte;

public class EsporteResponseDTO {
    private Long id;
    private String nome;
    private int minAtletas;
    private int maxAtletas;

    // Construtor que converte a entidade Esporte para o DTO
    public EsporteResponseDTO(Esporte esporte) {
        this.id = esporte.getId();
        this.nome = esporte.getNome();
        this.minAtletas = esporte.getMinAtletas();
        this.maxAtletas = esporte.getMaxAtletas();
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public int getMinAtletas() { return minAtletas; }
    public int getMaxAtletas() { return maxAtletas; }
}
