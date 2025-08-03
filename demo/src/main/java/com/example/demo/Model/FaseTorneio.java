package com.example.demo.Model;

public enum FaseTorneio {
    FASE_DE_GRUPOS("Fase de Grupos"),
    TRINTA_E_DOIS_AVOS("32-avos de Final"),
    DEZESSEIS_AVOS("16-avos de Final"),
    OITAVAS_DE_FINAL("Oitavas de Final"),
    QUARTAS_DE_FINAL("Quartas de Final"),
    SEMIFINAL("Semifinal"),
    FINAL("Final"),
    CONCLUIDO("Concluído"); 

    private final String descricao;

    FaseTorneio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    
    public FaseTorneio proximaFase() {
        switch (this) {
            case FASE_DE_GRUPOS:
                return OITAVAS_DE_FINAL; // Valor padrão, será ajustado dinamicamente
            case TRINTA_E_DOIS_AVOS:
                return DEZESSEIS_AVOS;
            case DEZESSEIS_AVOS:
                return OITAVAS_DE_FINAL;
            case OITAVAS_DE_FINAL:
                return QUARTAS_DE_FINAL;
            case QUARTAS_DE_FINAL:
                return SEMIFINAL;
            case SEMIFINAL:
                return FINAL;
            case FINAL:
                return CONCLUIDO;
            default:
                return CONCLUIDO;
        }
    }
}