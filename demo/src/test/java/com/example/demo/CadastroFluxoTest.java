// src/test/java/com/example/demo/CadastroFluxoTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.CursoRepository;
import com.example.demo.Repository.EsporteRepository;
import com.example.demo.Repository.UsuarioRepository;
import com.example.demo.Service.CoordenadorService;
import com.example.demo.Service.TecnicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // Garante que as operações do teste não afetem o banco permanentemente
public class CadastroFluxoTest {

    @Autowired
    private CoordenadorService coordenadorService;
    @Autowired
    private TecnicoService tecnicoService;
    
    // Repositórios para setup inicial dos dados
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;

    private Coordenador coordenador;
    private Curso curso;
    private Esporte esporte;
    private List<Atleta> atletas = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Limpando dados para garantir a independência dos testes
        usuarioRepository.deleteAll();
        cursoRepository.deleteAll();
        esporteRepository.deleteAll();

        // --- CENÁRIO INICIAL ---
        // 1. Criar e salvar um curso
        curso = new Curso("Licenciatura em Física", CategoriaCurso.SUPERIOR);
        cursoRepository.save(curso);
        
        // 2. Criar e salvar um esporte
        esporte = new Esporte("Handebol", 7, 14);
        esporteRepository.save(esporte);

        // 3. Criar e salvar o coordenador que fará a operação
        coordenador = new Coordenador();
        coordenador.setMatricula("coord001");
        coordenador.setNome("Coordenador Chefe");
        coordenador.setSenha("senhaForte");
        coordenador.setTipo(TipoUsuario.COORDENADOR);
        usuarioRepository.save(coordenador);

        // 4. Criar e salvar atletas que serão usados para montar a equipe
        for (int i = 1; i <= 7; i++) {
            Atleta atleta = new Atleta();
            atleta.setMatricula("atl" + i);
            atleta.setNome("Atleta " + i);
            atleta.setSenha("123");
            atleta.setTipo(TipoUsuario.ATLETA);
            atleta.setTelefone("123456" + i);
            atletas.add(usuarioRepository.save(atleta));
        }
    }

    @Test
    void deveExecutarFluxoCompletoDeCadastroDeEquipe() throws Exception {
        // --- PARTE 1: COORDENADOR CADASTRA TÉCNICO ---
        Tecnico dadosNovoTecnico = new Tecnico();
        dadosNovoTecnico.setMatricula("tec001");
        dadosNovoTecnico.setNome("Professor Tite");
        dadosNovoTecnico.setSenha("secreta");
        
        // Ação
        Tecnico tecnicoSalvo = coordenadorService.cadastrarTecnico(coordenador.getMatricula(), dadosNovoTecnico);

        // Verificação
        assertThat(tecnicoSalvo).isNotNull();
        assertThat(tecnicoSalvo.getTipo()).isEqualTo(TipoUsuario.TECNICO);
        assertThat(usuarioRepository.existsById("tec001")).isTrue();

        // --- PARTE 2: TÉCNICO CADASTRA EQUIPE ---
        Equipe novaEquipe = new Equipe();
        novaEquipe.setCurso(curso);
        novaEquipe.setEsporte(esporte);

        List<String> matriculasAtletas = atletas.stream().map(Usuario::getMatricula).toList();

        // Ação
        Equipe equipeSalva = tecnicoService.cadastrarEquipe(tecnicoSalvo.getMatricula(), novaEquipe, matriculasAtletas);

        // Verificação
        assertThat(equipeSalva).isNotNull();
        assertThat(equipeSalva.getId()).isNotNull();
        assertThat(equipeSalva.getTecnico().getNome()).isEqualTo("Professor Tite");
        assertThat(equipeSalva.getAtletas()).hasSize(7);

        // Verifica se o atleta foi atualizado com a referência da equipe
        Atleta atletaVerificado = (Atleta) usuarioRepository.findById("atl3").orElseThrow();
        assertThat(atletaVerificado.getEquipe()).isNotNull();
        assertThat(atletaVerificado.getEquipe().getId()).isEqualTo(equipeSalva.getId());
    }
}
