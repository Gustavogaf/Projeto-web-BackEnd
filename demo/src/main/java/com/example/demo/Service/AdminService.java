package com.example.demo.Service;

import com.example.demo.Model.Arbitro;
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

    public Coordenador atualizarCoordenador(String matricula, Coordenador detalhesCoordenador) throws Exception {
        Usuario usuario = usuarioRepository.findById(matricula)
                .orElseThrow(() -> new Exception("Usuário com a matrícula " + matricula + " não encontrado."));

        if (usuario.getTipo() != TipoUsuario.COORDENADOR) {
            throw new Exception("O usuário não é um coordenador.");
        }

        Coordenador coordenador = (Coordenador) usuario;

        // Atualiza o nome se for fornecido
        if (detalhesCoordenador.getNome() != null && !detalhesCoordenador.getNome().isBlank()) {
            coordenador.setNome(detalhesCoordenador.getNome());
        }

        // Atualiza a senha se for fornecida
        if (detalhesCoordenador.getSenha() != null && !detalhesCoordenador.getSenha().isBlank()) {
            coordenador.setSenha(detalhesCoordenador.getSenha());
        }

        return usuarioRepository.save(coordenador);
    }

    // DELETAR COORDENADOR
    public void deletarCoordenador(String matricula) throws Exception {
        Usuario usuario = usuarioRepository.findById(matricula)
                .orElseThrow(() -> new Exception("Coordenador com a matrícula " + matricula + " não encontrado."));

        if (usuario.getTipo() != TipoUsuario.COORDENADOR) {
            throw new Exception("O usuário especificado não é um coordenador e não pode ser deletado por esta função.");
        }

        usuarioRepository.deleteById(matricula);
    }

    public Arbitro cadastrarArbitro(Arbitro novoArbitro) throws Exception {
        if (usuarioRepository.existsById(novoArbitro.getMatricula())) {
            throw new Exception("Já existe um usuário cadastrado com a matrícula: " + novoArbitro.getMatricula());
        }
        novoArbitro.setTipo(TipoUsuario.ARBITRO);
        return usuarioRepository.save(novoArbitro);
    }

    public Arbitro atualizarArbitro(String matricula, Arbitro detalhesArbitro) throws Exception {
        Arbitro arbitro = (Arbitro) usuarioRepository.findById(matricula)
                .filter(u -> u.getTipo() == TipoUsuario.ARBITRO)
                .orElseThrow(() -> new Exception("Árbitro com a matrícula " + matricula + " não encontrado."));

        if (detalhesArbitro.getNome() != null && !detalhesArbitro.getNome().isBlank()) {
            arbitro.setNome(detalhesArbitro.getNome());
        }
        if (detalhesArbitro.getSenha() != null && !detalhesArbitro.getSenha().isBlank()) {
            arbitro.setSenha(detalhesArbitro.getSenha());
        }
        return usuarioRepository.save(arbitro);
    }

    public void deletarArbitro(String matricula) throws Exception {
        if (!usuarioRepository.existsById(matricula)) {
            throw new Exception("Árbitro com a matrícula " + matricula + " não encontrado.");
        }



        usuarioRepository.deleteById(matricula);
    }
}
