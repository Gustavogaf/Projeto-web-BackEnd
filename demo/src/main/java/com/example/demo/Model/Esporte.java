
package com.example.demo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Esporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) 
    private String nome;

    @Column(nullable = false) 
    private int minAtletas;

    @Column(nullable = false) 
    private int maxAtletas;

    // Construtor padrão
    public Esporte() {
    }

    // Construtor com parâmetros
    public Esporte(String nome, int minAtletas, int maxAtletas) {
        this.nome = nome;
        this.minAtletas = minAtletas;
        this.maxAtletas = maxAtletas;
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

    public int getMinAtletas() {
        return minAtletas;
    }

    public void setMinAtletas(int minAtletas) {
        this.minAtletas = minAtletas;
    }

    public int getMaxAtletas() {
        return maxAtletas;
    }

    public void setMaxAtletas(int maxAtletas) {
        this.maxAtletas = maxAtletas;
    }
}
