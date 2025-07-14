// src/main/java/com/example/demo/Model/Tecnico.java
package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula") // <-- ALTERE DE "usuario_matricula" PARA "matricula"
public class Tecnico extends Usuario {
    // Por enquanto, sem campos adicionais.
}