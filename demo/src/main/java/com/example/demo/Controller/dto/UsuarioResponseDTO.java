// src/main/java/com/example/demo/Controller/dto/UsuarioResponseDTO.java
package com.example.demo.Controller.dto;

import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;

public class UsuarioResponseDTO {
    private String matricula;
    private String nome;
    private TipoUsuario tipo;

    public UsuarioResponseDTO(Usuario usuario) {
        this.matricula = usuario.getMatricula();
        this.nome = usuario.getNome();
        this.tipo = usuario.getTipo();
    }

    // Getters
    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public TipoUsuario getTipo() { return tipo; }
}
