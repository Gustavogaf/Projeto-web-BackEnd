// src/main/java/com/example/demo/Service/TorneioService.java
package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TorneioService {

    @Autowired
    private TorneioRepository torneioRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    public Torneio iniciarFaseDeGrupos(Esporte esporte, CategoriaCurso categoria) throws Exception {
        // 1. BUSCAR EQUIPES INSCRITAS
        List<Equipe> equipesInscritas = equipeRepository.findByEsporteAndCurso_Categoria(esporte, categoria);
        if (equipesInscritas.size() < 3) {
            throw new Exception("Não há equipes suficientes para iniciar um torneio de " + esporte.getNome() + " na categoria " + categoria);
        }

        // 2. CRIAR E SALVAR O TORNEIO
        Torneio torneio = new Torneio();
        torneio.setEsporte(esporte);
        torneio.setCategoria(categoria);

        // 3. DISTRIBUIR EQUIPES NOS GRUPOS (Requisitos 14, 15, 16)
        distribuirEquipesNosGrupos(torneio, equipesInscritas);

        // 4. GERAR PARTIDAS DA FASE DE GRUPOS (Requisito 15 - todos contra todos)
        gerarPartidasDosGrupos(torneio);

        return torneioRepository.save(torneio);
    }

    private void distribuirEquipesNosGrupos(Torneio torneio, List<Equipe> equipes) {
        Collections.shuffle(equipes); // Sorteia a ordem das equipes
        int totalEquipes = equipes.size();
        
        // Lógica simplificada para distribuição de grupos.
        // A lógica da tabela do Requisito 14 pode ser complexa e implementada aqui.
        // Por simplicidade inicial, vamos criar grupos de 3 ou 4.
        int numGrupos = (int) Math.ceil(totalEquipes / 4.0);
        for (int i = 0; i < numGrupos; i++) {
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + (char)('A' + i));
            grupo.setTorneio(torneio);
            torneio.getGrupos().add(grupo);
        }

        for (int i = 0; i < totalEquipes; i++) {
            torneio.getGrupos().get(i % numGrupos).getEquipes().add(equipes.get(i));
        }
    }

    private void gerarPartidasDosGrupos(Torneio torneio) {
        LocalDateTime dataPartidaFicticia = LocalDateTime.now();

        for (Grupo grupo : torneio.getGrupos()) {
            List<Equipe> equipesDoGrupo = grupo.getEquipes();
            for (int i = 0; i < equipesDoGrupo.size(); i++) {
                for (int j = i + 1; j < equipesDoGrupo.size(); j++) {
                    Partida partida = new Partida();
                    partida.setEquipeA(equipesDoGrupo.get(i));
                    partida.setEquipeB(equipesDoGrupo.get(j));
                    // Sorteia data e horário fictícios (Requisito 7)
                    partida.setDataHora(dataPartidaFicticia);
                    dataPartidaFicticia = dataPartidaFicticia.plusHours(2); // Avança o horário para a próxima partida
                    
                    partidaRepository.save(partida);
                }
            }
        }
    }
}
