
package com.example.demo.Controller.dto;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Torneio;
import java.util.List;
import java.util.stream.Collectors;

public class TorneioResponseDTO {
    private Long id;
    private String nomeEsporte;
    private CategoriaCurso categoria;
    private List<GrupoResponseDTO> grupos;

    public TorneioResponseDTO(Torneio torneio) {
        this.id = torneio.getId();
        this.nomeEsporte = torneio.getEsporte().getNome();
        this.categoria = torneio.getCategoria();
        this.grupos = torneio.getGrupos().stream()
                .map(GrupoResponseDTO::new)
                .collect(Collectors.toList());
    }
    // Getters
    public Long getId() { return id; }
    public String getNomeEsporte() { return nomeEsporte; }
    public CategoriaCurso getCategoria() { return categoria; }
    public List<GrupoResponseDTO> getGrupos() { return grupos; }
}
