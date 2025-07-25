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
    
    boolean existsByCursoAndEsporte(Curso curso, Esporte esporte);
    
    List<Equipe> findByEsporteAndCurso_Categoria(Esporte esporte, CategoriaCurso categoria);
    
    boolean existsByTecnico(Tecnico tecnico);
}
