// src/main/java/com/example/demo/Controller/dto/CadastroEquipeRequest.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.Equipe;
import java.util.List;

public class CadastroEquipeRequest {
    private Equipe equipe;
    private List<String> matriculasAtletas;

    // Getters e Setters
    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
    public List<String> getMatriculasAtletas() { return matriculasAtletas; }
    public void setMatriculasAtletas(List<String> matriculasAtletas) { this.matriculasAtletas = matriculasAtletas; }
}
