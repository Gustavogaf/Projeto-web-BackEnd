package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.CursoRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.EsporteRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Equipe cadastrarEquipe(String matriculaTecnico, Equipe equipeInfoRequest, List<String> matriculasAtletas)
            throws Exception {

        // 1. Validar o Técnico
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception(
                        "Técnico com a matrícula " + matriculaTecnico + " não encontrado ou não é um técnico."));

        // 2. Validar e carregar as entidades dependentes (Curso e Esporte)
        if (equipeInfoRequest.getCurso() == null || equipeInfoRequest.getCurso().getId() == null) {
            throw new Exception("O ID do curso é obrigatório.");
        }
        if (equipeInfoRequest.getEsporte() == null || equipeInfoRequest.getEsporte().getId() == null) {
            throw new Exception("O ID do esporte é obrigatório.");
        }

        Curso curso = cursoRepository.findById(equipeInfoRequest.getCurso().getId())
                .orElseThrow(() -> new Exception(
                        "Curso com o ID " + equipeInfoRequest.getCurso().getId() + " não encontrado."));

        Esporte esporte = esporteRepository.findById(equipeInfoRequest.getEsporte().getId())
                .orElseThrow(() -> new Exception(
                        "Esporte com o ID " + equipeInfoRequest.getEsporte().getId() + " não encontrado."));

        // 3. Validar Regras de Negócio com os dados completos
        if (equipeRepository.existsByCursoAndEsporte(curso, esporte)) {
            throw new Exception(
                    "O curso '" + curso.getNome() + "' já possui uma equipe de '" + esporte.getNome() + "'.");
        }
        if (matriculasAtletas.size() < esporte.getMinAtletas() || matriculasAtletas.size() > esporte.getMaxAtletas()) {
            throw new Exception("A quantidade de atletas (" + matriculasAtletas.size() + ") para " + esporte.getNome() +
                    " deve ser entre " + esporte.getMinAtletas() + " e " + esporte.getMaxAtletas() + ".");
        }

        // 4. Criar e Salvar a nova Equipe primeiro para obter um ID
        Equipe novaEquipe = new Equipe();
        novaEquipe.setNome(equipeInfoRequest.getNome());
        novaEquipe.setTecnico(tecnico);
        novaEquipe.setCurso(curso);
        novaEquipe.setEsporte(esporte);
        Equipe equipeSalva = equipeRepository.save(novaEquipe);

        // 5. Buscar, validar e associar os Atletas à equipe já salva
        if (!matriculasAtletas.isEmpty()) {
            List<Atleta> atletasParaSalvar = new ArrayList<>();
            for (String matriculaAtleta : matriculasAtletas) {
                Atleta atleta = (Atleta) usuarioRepository.findById(matriculaAtleta)
                        .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                        .orElseThrow(() -> new Exception(
                                "Atleta com a matrícula '" + matriculaAtleta + "' não encontrado ou não é um atleta."));

                atleta.setEquipe(equipeSalva);
                atletasParaSalvar.add(atleta);
            }
            usuarioRepository.saveAll(atletasParaSalvar);
        }

        // Retorna a equipe salva, agora com todas as associações corretas.
        return equipeRepository.findById(equipeSalva.getId()).get();
    }

    @Transactional
    public Equipe atualizarEquipe(String matriculaTecnico, Long equipeId, Equipe equipeInfoRequest,
            List<String> novasMatriculasAtletas) throws Exception {
        // 1. Validações
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new Exception("Equipe com o ID " + equipeId + " não encontrada."));

        if (!equipe.getTecnico().getMatricula().equals(matriculaTecnico)) {
            throw new Exception("Você não tem permissão para editar esta equipe.");
        }

        Esporte esporte = equipe.getEsporte(); // Pega o esporte da equipe existente
        if (novasMatriculasAtletas.size() < esporte.getMinAtletas()
                || novasMatriculasAtletas.size() > esporte.getMaxAtletas()) {
            throw new Exception(
                    "A quantidade de atletas (" + novasMatriculasAtletas.size() + ") para " + esporte.getNome() +
                            " deve ser entre " + esporte.getMinAtletas() + " e " + esporte.getMaxAtletas() + ".");
        }

        // 2. Atualiza o nome da equipe
        equipe.setNome(equipeInfoRequest.getNome());

        // 3. Gerencia os atletas
        // Remove a associação de todos os atletas atuais da equipe
        for (Atleta atleta : new ArrayList<>(equipe.getAtletas())) {
            atleta.setEquipe(null);
            usuarioRepository.save(atleta);
        }
        equipe.getAtletas().clear(); // Limpa a lista na entidade equipe

        // Associa os novos atletas selecionados
        List<Atleta> novosAtletas = new ArrayList<>();
        for (String matriculaAtleta : novasMatriculasAtletas) {
            Atleta atleta = (Atleta) usuarioRepository.findById(matriculaAtleta)
                    .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                    .orElseThrow(
                            () -> new Exception("Atleta com a matrícula '" + matriculaAtleta + "' não encontrado."));

            // Verifica se o atleta já não pertence a outra equipe
            if (atleta.getEquipe() != null && !atleta.getEquipe().getId().equals(equipeId)) {
                throw new Exception("O atleta " + atleta.getNome() + " já pertence a outra equipe.");
            }

            atleta.setEquipe(equipe);
            novosAtletas.add(atleta);
        }

        // Adiciona a nova lista de atletas à equipe
        equipe.getAtletas().addAll(novosAtletas);

        // 4. Salva a equipe atualizada
        return equipeRepository.save(equipe);
    }

    public Atleta cadastrarAtleta(String matriculaTecnico, Atleta novoAtleta) throws Exception {
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Apenas usuários do tipo TECNICO podem cadastrar atletas."));

        if (usuarioRepository.existsById(novoAtleta.getMatricula())) {
            throw new Exception("Já existe um usuário cadastrado com a matrícula: " + novoAtleta.getMatricula());
        }

        novoAtleta.setSenha(passwordEncoder.encode(novoAtleta.getSenha()));

        novoAtleta.setTipo(TipoUsuario.ATLETA);
        novoAtleta.setCadastradoPor(tecnico);
        return usuarioRepository.save(novoAtleta);
    }

    public Page<Usuario> listarTodos(Pageable paginacao) {
        return usuarioRepository.findByTipo(TipoUsuario.TECNICO, paginacao);
    }

    public Usuario buscarPorMatricula(String matricula) throws Exception {
        return usuarioRepository.findById(matricula)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Técnico com a matrícula " + matricula + " não encontrado."));
    }

    public Atleta atualizarAtleta(String matriculaTecnico, String matriculaAtleta, Atleta detalhesAtleta)
            throws Exception {
        // Valida se quem está requisitando é um técnico
        usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Apenas usuários do tipo TECNICO podem atualizar atletas."));

        // Busca o atleta a ser atualizado
        Atleta atleta = (Atleta) usuarioRepository.findById(matriculaAtleta)
                .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                .orElseThrow(() -> new Exception("Atleta com a matrícula " + matriculaAtleta + " não encontrado."));
        if (detalhesAtleta.getNome() != null && !detalhesAtleta.getNome().isBlank()) {
            atleta.setNome(detalhesAtleta.getNome());
        }
        if (detalhesAtleta.getApelido() != null) {
            atleta.setApelido(detalhesAtleta.getApelido());
        }
        if (detalhesAtleta.getTelefone() != null && !detalhesAtleta.getTelefone().isBlank()) {
            atleta.setTelefone(detalhesAtleta.getTelefone());
        }
        if (detalhesAtleta.getSenha() != null && !detalhesAtleta.getSenha().isBlank()) {
            atleta.setSenha(passwordEncoder.encode(detalhesAtleta.getSenha()));
        }

        return usuarioRepository.save(atleta);
    }

    public void removerAtletaDaEquipe(String matriculaTecnico, String matriculaAtleta) throws Exception {
        // Valida se quem está requisitando é um técnico
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Usuário com a matrícula " + matriculaTecnico + " não é um técnico."));

        // Busca o atleta
        Atleta atleta = (Atleta) usuarioRepository.findById(matriculaAtleta)
                .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                .orElseThrow(() -> new Exception("Atleta com a matrícula " + matriculaAtleta + " não encontrado."));

        // Valida se o atleta tem uma equipe
        if (atleta.getEquipe() == null) {
            throw new Exception("O atleta não pertence a nenhuma equipe.");
        }

        // Valida se o técnico que está requisitando é o técnico da equipe do atleta
        if (!atleta.getEquipe().getTecnico().getMatricula().equals(tecnico.getMatricula())) {
            throw new Exception("Você não tem permissão para remover atletas desta equipe.");
        }

        // Remove a associação do atleta com a equipe
        atleta.setEquipe(null);
        usuarioRepository.save(atleta);
    }

    public void deletarAtleta(String matriculaTecnico, String matriculaAtleta) throws Exception {
        // Valida se quem está requisitando é um técnico
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(matriculaTecnico)
                .filter(u -> u.getTipo() == TipoUsuario.TECNICO)
                .orElseThrow(() -> new Exception("Usuário com a matrícula " + matriculaTecnico + " não é um técnico."));

        // Busca o atleta
        Atleta atleta = (Atleta) usuarioRepository.findById(matriculaAtleta)
                .filter(u -> u.getTipo() == TipoUsuario.ATLETA)
                .orElseThrow(() -> new Exception("Atleta com a matrícula " + matriculaAtleta + " não encontrado."));

        // Regra de Negócio: Não permitir deleção se o atleta estiver em uma equipe
        if (atleta.getEquipe() != null) {
            throw new Exception(
                    "Não é possível deletar o atleta, pois ele está associado a uma equipe. Remova-o da equipe primeiro.");
        }

        // Regra de Negócio: Somente o técnico que o cadastrou pode deletar
        if (atleta.getCadastradoPor() != null
                && !atleta.getCadastradoPor().getMatricula().equals(tecnico.getMatricula())) {
            throw new Exception("Você não tem permissão para deletar este atleta, pois não foi você quem o cadastrou.");
        }

        // Deleta o atleta do banco de dados
        usuarioRepository.deleteById(matriculaAtleta);
    }

    @Transactional
    public void deletarEquipe(String matriculaTecnico, Long equipeId) throws Exception {
        // Validar e buscar a equipe
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new Exception("Equipe com o ID " + equipeId + " não encontrada."));

        // Validar se o requisitante é o técnico da equipe
        if (!equipe.getTecnico().getMatricula().equals(matriculaTecnico)) {
            throw new Exception("Você não tem permissão para deletar esta equipe.");
        }

        // Regra de Negócio: Não permitir deleção se a equipe tiver partidas
        if (partidaRepository.existsByEquipeAOrEquipeB(equipe, equipe)) {
            throw new Exception(
                    "Não é possível deletar esta equipe, pois ela já está associada a partidas em um torneio.");
        }

        // Desassociar todos os atletas da equipe
        for (Atleta atleta : new ArrayList<>(equipe.getAtletas())) {
            atleta.setEquipe(null);
            usuarioRepository.save(atleta);
        }
        equipe.getAtletas().clear();

        // Deletar a equipe
        equipeRepository.delete(equipe);
    }
}