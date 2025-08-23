package com.example.demo.Controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EquipeInfoRequestDTO {

    @NotBlank(message = "O nome da equipe não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome da equipe deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotNull(message = "O ID do curso é obrigatório.")
    private Long cursoId;

    @NotNull(message = "O ID do esporte é obrigatório.")
    private Long esporteId;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
    public Long getEsporteId() { return esporteId; }
    public void setEsporteId(Long esporteId) { this.esporteId = esporteId; }
}