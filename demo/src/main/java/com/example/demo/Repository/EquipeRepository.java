// src/main/java/com/example/demo/Repository/EquipeRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.CategoriaCurso;
import com.example.demo.Model.Curso;
import com.example.demo.Model.Equipe;
import com.example.demo.Model.Esporte;
import com.example.demo.Model.Tecnico;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    // Requisito 2: Um curso pode ter apenas uma equipe em cada esporte.
    boolean existsByCursoAndEsporte(Curso curso, Esporte esporte);
    // Novo método para buscar equipes por esporte e categoria do curso
    List<Equipe> findByEsporteAndCurso_Categoria(Esporte esporte, CategoriaCurso categoria);
    // NOVO MÉTODO: VERIFICA SE UM TÉCNICO ESTÁ ASSOCIADO A ALGUMA EQUIPE
    boolean existsByTecnico(Tecnico tecnico);
}
