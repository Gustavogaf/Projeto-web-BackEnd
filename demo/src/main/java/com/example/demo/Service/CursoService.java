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

    public Curso atualizarCurso(Long id, Curso cursoDetails) throws Exception {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new Exception("Curso com o ID " + id + " não encontrado."));

        // Valida se o novo nome já não está em uso por OUTRO curso
        if (cursoDetails.getNome() != null && !cursoDetails.getNome().isBlank() && !curso.getNome().equalsIgnoreCase(cursoDetails.getNome())) {
            if (cursoRepository.existsByNome(cursoDetails.getNome())) {
                throw new Exception("Já existe um curso cadastrado com o nome: " + cursoDetails.getNome());
            }
            curso.setNome(cursoDetails.getNome());
        }

        if (cursoDetails.getCategoria() != null) {
            curso.setCategoria(cursoDetails.getCategoria());
        }

        return cursoRepository.save(curso);
    }

    public void deletarCurso(Long id) throws Exception {
        if (!cursoRepository.existsById(id)) {
            throw new Exception("Curso com o ID " + id + " não encontrado.");
        }
        
        cursoRepository.deleteById(id);
    }
}
