// src/main/java/com/example/demo/Service/CoordenadorService.java
package com.example.demo.Service;

import com.example.demo.Model.Tecnico;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoordenadorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Tecnico cadastrarTecnico(String matriculaCoordenador, Tecnico novoTecnico) throws Exception {
        // 1. VERIFICAR SE O SOLICITANTE É UM COORDENADOR
        Optional<Usuario> coordenadorOpt = usuarioRepository.findById(matriculaCoordenador);
        if (coordenadorOpt.isEmpty()) {
            throw new Exception("Coordenador com a matrícula " + matriculaCoordenador + " não encontrado.");
        }
        Usuario coordenador = coordenadorOpt.get();
        if (coordenador.getTipo() != TipoUsuario.COORDENADOR) {
            throw new Exception("Apenas usuários do tipo COORDENADOR podem cadastrar técnicos.");
        }

        // 2. VERIFICAR SE O NOVO TÉCNICO JÁ EXISTE
        if (usuarioRepository.existsById(novoTecnico.getMatricula())) {
            throw new Exception("Já existe um usuário cadastrado com a matrícula: " + novoTecnico.getMatricula());
        }

        // 3. DEFINIR O TIPO E SALVAR O NOVO TÉCNICO
        novoTecnico.setTipo(TipoUsuario.TECNICO);
        return usuarioRepository.save(novoTecnico);
    }
}
