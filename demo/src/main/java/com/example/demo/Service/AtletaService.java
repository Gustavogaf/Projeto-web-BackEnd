// src/main/java/com/example/demo/Service/AtletaService.java
package com.example.demo.Service;

import com.example.demo.Model.Atleta;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtletaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Page<Atleta> listarTodos(Pageable paginacao) {
        return usuarioRepository.findByTipo(TipoUsuario.ATLETA, paginacao)
                .map(usuario -> (Atleta) usuario);
    }

    public Atleta buscarPorMatricula(String matricula) throws Exception {
        return (Atleta) usuarioRepository.findById(matricula)
                .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                .orElseThrow(() -> new Exception("Atleta com a matrícula " + matricula + " não encontrado."));
    }
}
