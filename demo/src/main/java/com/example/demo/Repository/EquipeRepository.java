// src/main/java/com/example/demo/Repository/EquipeRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Curso;
import com.example.demo.Model.Equipe;
import com.example.demo.Model.Esporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    // Requisito 2: Um curso pode ter apenas uma equipe em cada esporte.
    boolean existsByCursoAndEsporte(Curso curso, Esporte esporte);
}
