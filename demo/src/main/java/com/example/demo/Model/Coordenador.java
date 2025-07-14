// src/main/java/com/example/demo/Model/Coordenador.java
package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula") // Padrão que definimos para a chave de herança
public class Coordenador extends Usuario {
    // Não precisa de campos adicionais por enquanto.
    // Herda tudo de Usuario.
}
