package com.example.demo.Controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EsporteRequestDTO {

    @NotBlank(message = "O nome do esporte não pode ser vazio.")
    @Size(min = 3, max = 50, message = "O nome do esporte deve ter entre 3 e 50 caracteres.")
    private String nome;

    @NotNull(message = "O número mínimo de atletas é obrigatório.")
    @Min(value = 1, message = "O esporte deve permitir pelo menos 1 atleta.")
    private Integer minAtletas;

    @NotNull(message = "O número máximo de atletas é obrigatório.")
    @Min(value = 1, message = "O esporte deve permitir pelo menos 1 atleta.")
    private Integer maxAtletas;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getMinAtletas() { return minAtletas; }
    public void setMinAtletas(Integer minAtletas) { this.minAtletas = minAtletas; }
    public Integer getMaxAtletas() { return maxAtletas; }
    public void setMaxAtletas(Integer maxAtletas) { this.maxAtletas = maxAtletas; }
}