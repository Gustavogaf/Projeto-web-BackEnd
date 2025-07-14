// src/main/java/com/example/demo/Service/EquipeService.java
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
}
