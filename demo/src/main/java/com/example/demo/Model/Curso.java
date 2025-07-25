package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;

@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING) // Armazena o nome da enumeração como String no banco
    @Column(nullable = false)
    private CategoriaCurso categoria;

    // Construtor padrão exigido pelo JPA
    public Curso() {
    }

    public Curso(String nome, CategoriaCurso categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    // Getters e Setters 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriaCurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCurso categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Curso{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", categoria=" + categoria +
               '}';
    }
}
