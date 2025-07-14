// src/main/java/com/example/demo/Service/TecnicoService.java
package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    public Equipe cadastrarEquipe(String matriculaTecnico, Equipe novaEquipe, List<String> matriculasAtletas) throws Exception {
        // 1. VERIFICAR SE O SOLICITANTE É UM TÉCNICO
        Optional<Usuario> tecnicoOpt = usuarioRepository.findById(matriculaTecnico);
        if (tecnicoOpt.isEmpty() || tecnicoOpt.get().getTipo() != TipoUsuario.TECNICO) {
            throw new Exception("Apenas usuários do tipo TECNICO podem cadastrar equipes.");
        }
        Tecnico tecnico = (Tecnico) tecnicoOpt.get();
        novaEquipe.setTecnico(tecnico);

        // 2. VERIFICAR SE O CURSO JÁ POSSUI UMA EQUIPE NO ESPORTE (Requisito 2)
        if (equipeRepository.existsByCursoAndEsporte(novaEquipe.getCurso(), novaEquipe.getEsporte())) {
            throw new Exception("O curso '" + novaEquipe.getCurso().getNome() + "' já possui uma equipe de '" + novaEquipe.getEsporte().getNome() + "'.");
        }

        // 3. VERIFICAR A QUANTIDADE DE ATLETAS (Requisito 6)
        Esporte esporte = novaEquipe.getEsporte();
        if (matriculasAtletas.size() < esporte.getMinAtletas() || matriculasAtletas.size() > esporte.getMaxAtletas()) {
            throw new Exception("A quantidade de atletas (" + matriculasAtletas.size() + ") para " + esporte.getNome() +
                    " deve ser entre " + esporte.getMinAtletas() + " e " + esporte.getMaxAtletas() + ".");
        }

        // 4. VERIFICAR SE OS ATLETAS ESTÃO CADASTRADOS E ADICIONÁ-LOS (Requisito 11)
        for (String matriculaAtleta : matriculasAtletas) {
            Optional<Usuario> atletaOpt = usuarioRepository.findById(matriculaAtleta);
            if (atletaOpt.isEmpty() || atletaOpt.get().getTipo() != TipoUsuario.ATLETA) {
                throw new Exception("Atleta com a matrícula '" + matriculaAtleta + "' não encontrado ou não é do tipo ATLETA.");
            }
            Atleta atleta = (Atleta) atletaOpt.get();
            atleta.setEquipe(novaEquipe);
            novaEquipe.getAtletas().add(atleta);
        }

        // 5. SALVAR A EQUIPE E ATUALIZAR OS ATLETAS
        Equipe equipeSalva = equipeRepository.save(novaEquipe);
        usuarioRepository.saveAll(novaEquipe.getAtletas()); // Atualiza a referência da equipe em cada atleta

        return equipeSalva;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findByTipo(TipoUsuario.TECNICO);
    }

    public Atleta cadastrarAtleta(String matriculaTecnico, Atleta novoAtleta) throws Exception {
        // 1. VERIFICAR SE O SOLICITANTE É UM TÉCNICO
        if (!usuarioRepository.existsById(matriculaTecnico) || usuarioRepository.findById(matriculaTecnico).get().getTipo() != TipoUsuario.TECNICO) {
            throw new Exception("Apenas usuários do tipo TECNICO podem cadastrar atletas.");
        }

        // 2. VERIFICAR SE O NOVO ATLETA JÁ EXISTE
        if (usuarioRepository.existsById(novoAtleta.getMatricula())) {
            throw new Exception("Já existe um usuário cadastrado com a matrícula: " + novoAtleta.getMatricula());
        }

        // 3. DEFINIR O TIPO E SALVAR O NOVO ATLETA
        novoAtleta.setTipo(TipoUsuario.ATLETA);
        return usuarioRepository.save(novoAtleta);
    }

}
