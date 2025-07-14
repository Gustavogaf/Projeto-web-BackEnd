// src/main/java/com/example/demo/Model/Arbitro.java
package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula")
public class Arbitro extends Usuario {
    // Não precisa de campos adicionais por enquanto.
}
