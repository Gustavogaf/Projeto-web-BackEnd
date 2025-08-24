package com.example.demo.Service;

import com.example.demo.Model.Tecnico;
import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoordenadorService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

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

        novoTecnico.setSenha(passwordEncoder.encode(novoTecnico.getSenha()));

        // 3. DEFINIR O TIPO E SALVAR O NOVO TÉCNICO
        novoTecnico.setTipo(TipoUsuario.TECNICO);
        return usuarioRepository.save(novoTecnico);
    }

    public Tecnico atualizarTecnico(String matriculaCoordenador, String matriculaTecnico, Tecnico detalhesTecnico)
            throws Exception {
        // Valida se quem está requisitando é um coordenador
        usuarioRepository.findById(matriculaCoordenador)
                .filter(u -> u.getTipo() == TipoUsuario.COORDENADOR)
                .orElseThrow(() -> new Exception("Apenas usuários do tipo COORDENADOR podem atualizar técnicos."));

        // Busca o técnico a ser atualizado
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Técnico com a matrícula " + matriculaTecnico + " não encontrado."));

        if (detalhesTecnico.getNome() != null && !detalhesTecnico.getNome().isBlank()) {
            tecnico.setNome(detalhesTecnico.getNome());
        }
        if (detalhesTecnico.getSenha() != null && !detalhesTecnico.getSenha().isBlank()) {
            tecnico.setSenha(detalhesTecnico.getSenha());
        }

        return usuarioRepository.save(tecnico);
    }

    // DELETAR TÉCNICO
    public void deletarTecnico(String matriculaCoordenador, String matriculaTecnico) throws Exception {
        // Valida o coordenador
        usuarioRepository.findById(matriculaCoordenador)
                .filter(u -> u.getTipo() == TipoUsuario.COORDENADOR)
                .orElseThrow(() -> new Exception("Apenas usuários do tipo COORDENADOR podem deletar técnicos."));

        // Busca o técnico
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Técnico com a matrícula " + matriculaTecnico + " não encontrado."));

        // Regra de Negócio Crítica: Não permitir deleção se o técnico estiver em uma
        // equipe
        if (equipeRepository.existsByTecnico(tecnico)) {
            throw new Exception("Não é possível deletar o técnico, pois ele já está associado a uma equipe.");
        }

        usuarioRepository.deleteById(matriculaTecnico);
    }
}
