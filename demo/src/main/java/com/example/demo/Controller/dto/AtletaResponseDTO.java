// src/main/java/com/example/demo/Controller/dto/AtletaResponseDTO.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.Atleta;
import com.example.demo.Model.TipoUsuario;

public class AtletaResponseDTO {
    private String matricula;
    private String nome;
    private String apelido;
    private String telefone;
    private TipoUsuario tipo;

    public AtletaResponseDTO(Atleta atleta) {
        this.matricula = atleta.getMatricula();
        this.nome = atleta.getNome();
        this.apelido = atleta.getApelido();
        this.telefone = atleta.getTelefone();
        this.tipo = atleta.getTipo();
    }

    // Getters
    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public String getApelido() { return apelido; }
    public String getTelefone() { return telefone; }
    public TipoUsuario getTipo() { return tipo; }
}
