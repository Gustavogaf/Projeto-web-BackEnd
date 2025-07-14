// src/main/java/com/example/demo/Service/AdminService.java
package com.example.demo.Service;

import com.example.demo.Model.Coordenador;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;
import com.example.demo.Repository.UsuarioRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Coordenador cadastrarCoordenador(Coordenador novoCoordenador) throws Exception {
        // 1. VERIFICAR SE A MATRÍCULA JÁ ESTÁ EM USO
        if (usuarioRepository.existsById(novoCoordenador.getMatricula())) {
            throw new Exception("Já existe um usuário cadastrado com a matrícula: " + novoCoordenador.getMatricula());
        }

        // 2. DEFINIR O TIPO E SALVAR O NOVO COORDENADOR
        novoCoordenador.setTipo(TipoUsuario.COORDENADOR);
        return usuarioRepository.save(novoCoordenador);
    }

    public List<Usuario> listarCoordenadores() {
        return usuarioRepository.findByTipo(TipoUsuario.COORDENADOR);
    }
}
