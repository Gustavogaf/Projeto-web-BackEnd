// src/test/java/com/example/demo/TorneioServiceTest.java
package com.example.demo;

import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.TorneioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class TorneioServiceTest {

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EsporteRepository esporteRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private TorneioRepository torneioRepository;

    private Esporte esporte;
    private CategoriaCurso categoria;

    @BeforeEach
void setUp() {
    // Limpeza completa na ordem correta para evitar erros de constraint
    partidaRepository.deleteAll();
    torneioRepository.deleteAll();
    equipeRepository.deleteAll(); // Agora deleta as equipes e seus atletas em cascata
    usuarioRepository.deleteAll(); // Deleta os usuários restantes (Técnicos, Coordenadores, etc.)
    cursoRepository.deleteAll();
    esporteRepository.deleteAll();

    // --- CENÁRIO INICIAL ---
    // 1. Definir o esporte e a categoria do torneio
    esporte = esporteRepository.save(new Esporte("Futsal", 5, 10));
    categoria = CategoriaCurso.SUPERIOR;

    // 2. Criar 7 equipes para este esporte e categoria
    for (int i = 1; i <= 7; i++) {
        Curso curso = cursoRepository.save(new Curso("Curso " + i, categoria));
        Tecnico tecnico = new Tecnico();
        tecnico.setMatricula("tec" + i);
        tecnico.setNome("Técnico " + i);
        tecnico.setSenha("123");
        tecnico.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnico);

        criarEquipe("Equipe " + i, curso, esporte, tecnico, i);
    }
}

    @Test
    void deveGerarFaseDeGruposCorretamente() throws Exception {
        // Ação
        Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(esporte, categoria);

        // Verificação
        assertThat(torneioIniciado).isNotNull();
        assertThat(torneioIniciado.getId()).isNotNull();
        assertThat(torneioIniciado.getEsporte().getNome()).isEqualTo("Futsal");
        assertThat(torneioIniciado.getCategoria()).isEqualTo(CategoriaCurso.SUPERIOR);

        // Verificar a distribuição dos grupos (baseado na nossa lógica simplificada)
        // 7 equipes devem ser divididas em 2 grupos (um de 4, um de 3)
        assertThat(torneioIniciado.getGrupos()).hasSize(2);
        long totalDeEquipesNosGrupos = torneioIniciado.getGrupos().stream()
                .mapToLong(grupo -> grupo.getEquipes().size())
                .sum();
        assertThat(totalDeEquipesNosGrupos).isEqualTo(7);

        // Verificar se as partidas foram geradas
        // Grupo A (4 equipes) -> 6 partidas (A vs B, A vs C, A vs D, B vs C, B vs D, C vs D)
        // Grupo B (3 equipes) -> 3 partidas (A vs B, A vs C, B vs C)
        // Total = 9 partidas
        long totalPartidasGeradas = partidaRepository.count();
        assertThat(totalPartidasGeradas).isEqualTo(9);
    }

    private void criarEquipe(String nome, Curso curso, Esporte esporte, Tecnico tecnico, int idOffset) {
    // 1. CRIE A INSTÂNCIA DA EQUIPE
    Equipe equipe = new Equipe();
    equipe.setNome(nome);
    equipe.setCurso(curso);
    equipe.setEsporte(esporte);
    equipe.setTecnico(tecnico);

    // 2. CRIE E CONFIGURE OS ATLETAS
    List<Atleta> atletas = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
        Atleta atleta = new Atleta();
        atleta.setMatricula("atl" + idOffset + i);
        atleta.setNome("Atleta " + nome + " " + i);
        atleta.setSenha("123");
        atleta.setTipo(TipoUsuario.ATLETA);
        atleta.setTelefone("999" + idOffset + i);

        // Associe o atleta à equipe
        atleta.setEquipe(equipe); 
        atletas.add(atleta);
    }

    // 3. DEFINA A LISTA DE ATLETAS NA EQUIPE
    equipe.setAtletas(atletas);

    // 4. SALVE A EQUIPE APENAS UMA VEZ.
    // O CascadeType.ALL garantirá que os atletas sejam salvos juntos.
    equipeRepository.save(equipe);
}
    @Test
void deveDistribuir11EquipesEmUmGrupoDe5DoisDe3() throws Exception {
    // --- CENÁRIO ---
    // O @BeforeEach já cria 7 equipes. Vamos criar mais 4 para totalizar 11.
    for (int i = 8; i <= 11; i++) {
        Curso curso = cursoRepository.save(new Curso("Curso Extra " + i, categoria));
        Tecnico tecnico = new Tecnico();
        tecnico.setMatricula("tec" + i);
        tecnico.setNome("Técnico " + i);
        tecnico.setSenha("123");
        tecnico.setTipo(TipoUsuario.TECNICO);
        usuarioRepository.save(tecnico);
        criarEquipe("Equipe " + i, curso, esporte, tecnico, i);
    }
    
    // Garantir que temos 11 equipes no total
    assertThat(equipeRepository.count()).isEqualTo(11);

    // --- AÇÃO ---
    Torneio torneioIniciado = torneioService.iniciarFaseDeGrupos(esporte, categoria);

    // --- VERIFICAÇÃO ---
    assertThat(torneioIniciado).isNotNull();
    
    // 1. Deve haver 3 grupos no total
    assertThat(torneioIniciado.getGrupos()).hasSize(3);

    // 2. A distribuição de equipes nos grupos deve ser [5, 3, 3] (em qualquer ordem)
    List<Long> tamanhosDosGrupos = torneioIniciado.getGrupos().stream()
            .map(grupo -> (long) grupo.getEquipes().size())
            .toList();
            
    assertThat(tamanhosDosGrupos).containsExactlyInAnyOrder(5L, 3L, 3L);
    
    // 3. O número total de partidas deve estar correto
    // Grupo de 5 equipes -> 10 partidas (5*4/2)
    // Grupo de 3 equipes -> 3 partidas (3*2/2)
    // Grupo de 3 equipes -> 3 partidas (3*2/2)
    // Total = 16 partidas
    long totalPartidasGeradas = partidaRepository.count();
    assertThat(totalPartidasGeradas).isEqualTo(16);
}
}