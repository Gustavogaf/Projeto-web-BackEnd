package com.example.demo.Service;

import com.example.demo.Model.Equipe;
import com.example.demo.Repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public List<Equipe> listarTodas() {
        return equipeRepository.findAll();
    }

    public Equipe buscarPorId(Long id) throws Exception {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new Exception("Equipe com o ID " + id + " não encontrada."));
    }
}
