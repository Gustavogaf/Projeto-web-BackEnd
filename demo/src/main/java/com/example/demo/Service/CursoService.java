// src/main/java/com/example/demo/Service/CursoService.java
package com.example.demo.Service;

import com.example.demo.Model.Curso;
import com.example.demo.Repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Curso criarCurso(Curso curso) throws Exception {
        if (curso.getNome() == null || curso.getNome().isBlank()) {
            throw new Exception("O nome do curso não pode ser vazio.");
        }
        if (cursoRepository.existsByNome(curso.getNome())) {
            throw new Exception("Já existe um curso cadastrado com o nome: " + curso.getNome());
        }
        return cursoRepository.save(curso);
    }
}
