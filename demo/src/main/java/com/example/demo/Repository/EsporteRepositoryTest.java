
package com.example.demo.Repository;

import com.example.demo.Model.Esporte;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EsporteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Um helper para manipular entidades no teste

    @Autowired
    private EsporteRepository esporteRepository;

    @Test
    public void quandoSalvarUmEsporte_entaoDeveEncontraLoPeloNome() {
        // 1. Cenário
        Esporte novoEsporte = new Esporte("Futsal", 5, 10);

        // 2. Ação
        entityManager.persist(novoEsporte); // Salva o esporte no banco de dados de teste
        entityManager.flush(); // Garante que a operação foi executada

        // 3. Verificação
        // Busca o esporte que acabamos de salvar
        Esporte esporteEncontrado = esporteRepository.findById(novoEsporte.getId()).orElse(null);

        // Afirma que o esporte encontrado não é nulo e que o nome é o esperado
        assertThat(esporteEncontrado).isNotNull();
        assertThat(esporteEncontrado.getNome()).isEqualTo(novoEsporte.getNome());
    }

    @Test
    public void quandoExistsByNome_entaoDeveRetornarVerdadeiro() {
        // Cenário
        Esporte esporte = new Esporte("Voleibol", 6, 12);
        entityManager.persist(esporte);
        entityManager.flush();

        // Ação
        boolean existe = esporteRepository.existsByNome("Voleibol");

        // Verificação
        assertThat(existe).isTrue();
    }
    
    @Test
    public void quandoExistsByNome_entaoDeveRetornarFalso() {
        // Ação
        boolean existe = esporteRepository.existsByNome("Basquete");

        // Verificação
        assertThat(existe).isFalse();
    }
}