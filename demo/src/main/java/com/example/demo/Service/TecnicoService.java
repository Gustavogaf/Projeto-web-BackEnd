// src/main/java/com/example/demo/Service/TecnicoService.java
package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.CursoRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.EsporteRepository;
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

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EsporteRepository esporteRepository;

    

    public Equipe cadastrarEquipe(String matriculaTecnico, Equipe novaEquipe, List<String> matriculasAtletas) throws Exception {
        // 1. VERIFICAR SE O SOLICITANTE É UM TÉCNICO
        Optional<Usuario> tecnicoOpt = usuarioRepository.findById(matriculaTecnico);
        if (tecnicoOpt.isEmpty() || tecnicoOpt.get().getTipo() != TipoUsuario.TECNICO) {
            throw new Exception("Apenas usuários do tipo TECNICO podem cadastrar equipes.");
        }
        Tecnico tecnico = (Tecnico) tecnicoOpt.get();
        novaEquipe.setTecnico(tecnico);

        // 2. CARREGAR AS ENTIDADES COMPLETAS DO BANCO DE DADOS
    Curso curso = cursoRepository.findById(equipeDadosRequest.getCurso().getId())
            .orElseThrow(() -> new Exception("Curso com o ID " + equipeDadosRequest.getCurso().getId() + " não encontrado."));

    Esporte esporte = esporteRepository.findById(equipeDadosRequest.getEsporte().getId())
            .orElseThrow(() -> new Exception("Esporte com o ID " + equipeDadosRequest.getEsporte().getId() + " não encontrado."));

    // 3. VERIFICAR SE O CURSO JÁ POSSUI UMA EQUIPE NO ESPORTE (usando os objetos completos)
    if (equipeRepository.existsByCursoAndEsporte(curso, esporte)) {
        throw new Exception("O curso '" + curso.getNome() + "' já possui uma equipe de '" + esporte.getNome() + "'.");
    }

        // 4. VERIFICAR A QUANTIDADE DE ATLETAS (usando o minAtletas real do esporte)
    if (matriculasAtletas.size() < esporte.getMinAtletas() || matriculasAtletas.size() > esporte.getMaxAtletas()) {
        throw new Exception("A quantidade de atletas (" + matriculasAtletas.size() + ") para " + esporte.getNome() +
                " deve ser entre " + esporte.getMinAtletas() + " e " + esporte.getMaxAtletas() + ".");
    }

        // 5. CRIAR A NOVA EQUIPE COM OS DADOS CORRETOS
    Equipe novaEquipe = new Equipe();
    novaEquipe.setNome(equipeDadosRequest.getNome());
    novaEquipe.setTecnico(tecnico);
    novaEquipe.setCurso(curso); // Associando o objeto Curso completo
    novaEquipe.setEsporte(esporte); // Associando o objeto Esporte completo
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
