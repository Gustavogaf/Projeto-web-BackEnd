
package com.example.demo.Controller.dto;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Curso;

public class CursoResponseDTO {
    private Long id;
    private String nome;
    private CategoriaCurso categoria;

    public CursoResponseDTO(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.categoria = curso.getCategoria();
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public CategoriaCurso getCategoria() { return categoria; }
}
