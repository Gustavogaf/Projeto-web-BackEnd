package com.example.demo.Model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Torneio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Esporte esporte;

    @Enumerated(EnumType.STRING)
    private CategoriaCurso categoria;

    @OneToMany(mappedBy = "torneio", cascade = CascadeType.ALL)
    private List<Grupo> grupos = new ArrayList<>();

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Esporte getEsporte() { return esporte; }
    public void setEsporte(Esporte esporte) { this.esporte = esporte; }
    public CategoriaCurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaCurso categoria) { this.categoria = categoria; }
    public List<Grupo> getGrupos() { return grupos; }
    public void setGrupos(List<Grupo> grupos) { this.grupos = grupos; }
}
