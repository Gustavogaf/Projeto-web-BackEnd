package com.example.demo.Service;

import com.example.demo.Model.Grupo;
import com.example.demo.Repository.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    public Grupo buscarPorId(Long id) throws Exception {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new Exception("Grupo com o ID " + id + " não encontrado."));
    }
}