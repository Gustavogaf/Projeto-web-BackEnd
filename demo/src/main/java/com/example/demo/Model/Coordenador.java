package com.example.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "matricula") 
public class Coordenador extends Usuario {
    // Herda tudo de Usuario.
}
