package com.example.demo.Controller.dto;

import com.example.demo.Model.CategoriaCurso;

public class IniciarTorneioRequest {
    private Long esporteId;
    private CategoriaCurso categoria;

    // Getters e Setters
    public Long getEsporteId() { return esporteId; }
    public void setEsporteId(Long esporteId) { this.esporteId = esporteId; }
    public CategoriaCurso getCategoria() { return categoria; }
    public void setCategoria(CategoriaCurso categoria) { this.categoria = categoria; }
}
