
package com.example.demo.Controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.example.demo.Model.Equipe;
import java.util.List;

public class CadastroEquipeRequest {

    @Valid // Valida o objeto aninhado
    @NotNull
    private EquipeInfoRequestDTO equipe;

    @NotEmpty(message = "A equipe deve ter pelo menos um atleta.")
    private List<String> matriculasAtletas;

    // Getters e Setters
    public EquipeInfoRequestDTO getEquipe() { return equipe; }
    public void setEquipe(EquipeInfoRequestDTO equipe) { this.equipe = equipe; }
    public List<String> getMatriculasAtletas() { return matriculasAtletas; }
    public void setMatriculasAtletas(List<String> matriculasAtletas) { this.matriculasAtletas = matriculasAtletas; }
}
