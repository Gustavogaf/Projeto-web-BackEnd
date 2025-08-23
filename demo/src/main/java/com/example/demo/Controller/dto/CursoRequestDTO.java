package com.example.demo.Controller.dto;

import com.example.demo.Model.CategoriaCurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CursoRequestDTO {

    @NotBlank(message = "O nome do curso não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome do curso deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotNull(message = "A categoria do curso é obrigatória.")
    private CategoriaCurso categoria;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public CategoriaCurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaCurso categoria) { this.categoria = categoria; }
}