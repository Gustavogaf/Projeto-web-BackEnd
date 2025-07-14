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

    public List<Partida> avancarFase(Long torneioId) throws Exception {
        // 1. VERIFICAR SE A FASE ATUAL REALMENTE TERMINOU
        // Buscamos todas as partidas do torneio, ordenadas pela data
        List<Partida> todasAsPartidas = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneioId);
        
        boolean existePartidaAgendada = todasAsPartidas.stream()
                .anyMatch(p -> p.getStatus() == StatusPartida.AGENDADA);

        if (existePartidaAgendada) {
            throw new Exception("Ainda existem partidas agendadas. A fase atual não foi concluída.");
        }

        // 2. IDENTIFICAR A ÚLTIMA FASE JOGADA E OS VENCEDORES
        // Pegamos a data da última partida jogada
        LocalDateTime ultimaData = todasAsPartidas.get(0).getDataHora();
        
        List<Equipe> vencedores = new ArrayList<>();
        for (Partida p : todasAsPartidas) {
            if (p.getDataHora().equals(ultimaData)) { // Filtra apenas as partidas da última rodada
                if (p.getPlacarEquipeA() > p.getPlacarEquipeB()) {
                    vencedores.add(p.getEquipeA());
                } else if (p.getPlacarEquipeB() > p.getPlacarEquipeA()) {
                    vencedores.add(p.getEquipeB());
                }
                // Em caso de empate no mata-mata, a lógica precisaria ser mais complexa (pênaltis, etc.)
                // Por simplicidade, assumimos que não haverá empates no mata-mata.
            }
        }

        // 3. VERIFICAR SE HÁ UM CAMPEÃO
        if (vencedores.size() == 1) {
            // Lógica para declarar o campeão pode ser adicionada aqui
            System.out.println("Temos um campeão! Equipe: " + vencedores.get(0).getNome());
            return new ArrayList<>(); // Retorna uma lista vazia, pois não há novas partidas
        }

        // 4. GERAR A PRÓXIMA FASE
        Collections.shuffle(vencedores); // Sorteia os confrontos
        List<Partida> proximaFase = new ArrayList<>();
        LocalDateTime proximaData = ultimaData.plusDays(7); // Marca a próxima fase para uma semana depois

        for (int i = 0; i < vencedores.size() / 2; i++) {
            Partida novaPartida = new Partida();
            novaPartida.setEquipeA(vencedores.get(i * 2));
            novaPartida.setEquipeB(vencedores.get(i * 2 + 1));
            novaPartida.setDataHora(proximaData);
            // Associar a partida ao torneio
            Torneio torneio = torneioRepository.findById(torneioId).get();
            novaPartida.setTorneio(torneio); // Precisaremos adicionar o campo 'torneio' na entidade Partida
            
            proximaFase.add(novaPartida);
            proximaData = proximaData.plusHours(2);
        }

        return partidaRepository.saveAll(proximaFase);
    }

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
    public List<Partida> gerarMataMata(Long torneioId) throws Exception {
        Torneio torneio = torneioRepository.findById(torneioId)
                .orElseThrow(() -> new Exception("Torneio não encontrado."));

        List<Equipe> classificados = new ArrayList<>();
        // 1. SELECIONAR OS 2 MELHORES DE CADA GRUPO
        for (Grupo grupo : torneio.getGrupos()) {
            grupo.getEquipes().sort((e1, e2) -> Integer.compare(e2.getPontos(), e1.getPontos())); // Ordena por pontos
            
            if (grupo.getEquipes().size() >= 2) {
                classificados.add(grupo.getEquipes().get(0));
                classificados.add(grupo.getEquipes().get(1));
            } else if (grupo.getEquipes().size() == 1) {
                classificados.add(grupo.getEquipes().get(0));
            }
        }

        // 2. EMBARALHAR OS CLASSIFICADOS PARA O SORTEIO DAS CHAVES
        Collections.shuffle(classificados);

        // 3. GERAR AS PARTIDAS DO MATA-MATA
        List<Partida> partidasMataMata = new ArrayList<>();
        LocalDateTime dataPartidaFicticia = LocalDateTime.now().plusDays(7); // Marca para uma semana depois

        for (int i = 0; i < classificados.size() / 2; i++) {
            Partida partida = new Partida();
            partida.setEquipeA(classificados.get(i*2));
            partida.setEquipeB(classificados.get(i*2 + 1));
            partida.setDataHora(dataPartidaFicticia);
            
            partidasMataMata.add(partida);
            dataPartidaFicticia = dataPartidaFicticia.plusHours(2);
        }

        return partidaRepository.saveAll(partidasMataMata);
    }
}
