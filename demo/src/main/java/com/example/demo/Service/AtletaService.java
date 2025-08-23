// src/main/java/com/example/demo/Service/AtletaService.java
package com.example.demo.Service;

import com.example.demo.Model.Atleta;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtletaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Atleta> listarTodos() {
        // Busca todos os usuários do tipo ATLETA e os converte para o tipo Atleta
        return usuarioRepository.findByTipo(TipoUsuario.ATLETA).stream()
                .map(usuario -> (Atleta) usuario)
                .collect(Collectors.toList());
    }

    public Atleta buscarPorMatricula(String matricula) throws Exception {
        return (Atleta) usuarioRepository.findById(matricula)
                .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                .orElseThrow(() -> new Exception("Atleta com a matrícula " + matricula + " não encontrado."));
    }
}
