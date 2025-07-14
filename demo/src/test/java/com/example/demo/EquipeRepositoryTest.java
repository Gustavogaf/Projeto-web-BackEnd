// src/test/java/com/example/demo/Repository/EquipeRepositoryTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.CursoRepository;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.EsporteRepository;
import com.example.demo.Repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EquipeRepositoryTest {

    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // Usaremos para salvar o tecnico e os atletas

    private Curso ads;
    private Esporte futsal;
    private Tecnico tecnico;

    @BeforeEach // Este método executa antes de cada teste
    void setUp() {
        // Limpa os repositórios para garantir que cada teste seja independente
        equipeRepository.deleteAll();
        usuarioRepository.deleteAll();
        esporteRepository.deleteAll();
        cursoRepository.deleteAll();

        // 1. Cenário pré-teste: Salva as dependências
        ads = cursoRepository.save(new Curso("Análise e Desenvolvimento de Sistemas", CategoriaCurso.SUPERIOR));
        futsal = esporteRepository.save(new Esporte("Futsal", 5, 10));

        tecnico = new Tecnico();
        tecnico.setMatricula("tec001");
        tecnico.setNome("Professor Silva");
        tecnico.setSenha("senha123");
        tecnico.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnico);
    }

    @Test
    public void deveSalvarUmaEquipeComTecnicoEAtletas() {
        // 2. Ação
        // Criando a equipe
        Equipe equipeAdsFutsal = new Equipe();
        equipeAdsFutsal.setCurso(ads);
        equipeAdsFutsal.setEsporte(futsal);
        equipeAdsFutsal.setTecnico(tecnico);
        equipeAdsFutsal.setAtletas(new ArrayList<>()); // Inicializa a lista de atletas

        // Criando e salvando o primeiro atleta
        Atleta atleta1 = new Atleta();
        atleta1.setMatricula("atl001");
        atleta1.setNome("João");
        atleta1.setApelido("Jota");
        atleta1.setTelefone("7999991");
        atleta1.setSenha("123");
        atleta1.setTipo(TipoUsuario.ATLETA);
        atleta1.setEquipe(equipeAdsFutsal);
        
        // Criando e salvando o segundo atleta
        Atleta atleta2 = new Atleta();
        atleta2.setMatricula("atl002");
        atleta2.setNome("Maria");
        atleta2.setTelefone("7999992");
        atleta2.setSenha("456");
        atleta2.setTipo(TipoUsuario.ATLETA);
        atleta2.setEquipe(equipeAdsFutsal);

        // Adicionando os atletas na lista da equipe
        equipeAdsFutsal.getAtletas().add(atleta1);
        equipeAdsFutsal.getAtletas().add(atleta2);
        
        // Salvando a equipe (o cascade deve salvar os atletas)
        equipeRepository.save(equipeAdsFutsal);
        usuarioRepository.save(atleta1); // Precisamos salvar os usuários atletas também
        usuarioRepository.save(atleta2);

        // 3. Verificação
        Equipe equipeSalva = equipeRepository.findById(equipeAdsFutsal.getId()).orElse(null);

        assertThat(equipeSalva).isNotNull();
        assertThat(equipeSalva.getCurso().getNome()).isEqualTo("Análise e Desenvolvimento de Sistemas");
        assertThat(equipeSalva.getEsporte().getNome()).isEqualTo("Futsal");
        assertThat(equipeSalva.getTecnico().getNome()).isEqualTo("Professor Silva");
        assertThat(equipeSalva.getAtletas()).hasSize(2);
        assertThat(equipeSalva.getAtletas().get(0).getNome()).isEqualTo("João");
    }
}
