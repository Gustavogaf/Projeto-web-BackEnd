// src/test/java/com/example/demo/Repository/EsporteRepositoryTest.java
package com.example.demo;

import com.example.demo.Model.Esporte;
import com.example.demo.Repository.EsporteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase; // <-- IMPORTE AQUI
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // <-- ADICIONE ESTA LINHA
public class EsporteRepositoryTest {

    @Autowired
    private EsporteRepository esporteRepository;

    @Test
    public void quandoSalvarUmEsporte_entaoDeveEncontraLoPeloNome() {
        // Cenário
        Esporte novoEsporte = new Esporte("Futsal", 5, 10);

        // Ação
        esporteRepository.save(novoEsporte);

        // Verificação
        Esporte esporteEncontrado = esporteRepository.findById(novoEsporte.getId()).orElse(null);
        assertThat(esporteEncontrado).isNotNull();
        assertThat(esporteEncontrado.getNome()).isEqualTo("Futsal");
    }

    @Test
    public void quandoExistsByNome_entaoDeveRetornarVerdadeiro() {
        // Cenário
        esporteRepository.save(new Esporte("Voleibol", 6, 12));

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