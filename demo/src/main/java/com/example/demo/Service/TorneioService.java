package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Controller.dto.AvancoFaseResponse;
import com.example.demo.Controller.dto.EquipeResponseDTO;
import com.example.demo.Controller.dto.PartidaResponseDTO;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class TorneioService {

    @Autowired
    private TorneioRepository torneioRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private PartidaRepository partidaRepository;

    @Transactional
    public AvancoFaseResponse avancarFase(Long torneioId) throws Exception {
        // 1. Validações iniciais
        if (partidaRepository.existsByTorneioIdAndStatus(torneioId, StatusPartida.AGENDADA)) {
            throw new Exception("Ainda existem partidas agendadas. A fase atual não foi concluída.");
        }

        Torneio torneio = torneioRepository.findById(torneioId)
                .orElseThrow(() -> new Exception("Torneio não encontrado."));
        
        List<Partida> partidasConcluidas = partidaRepository.findByTorneioIdAndStatusNot(torneioId, StatusPartida.AGENDADA);

        if (partidasConcluidas.isEmpty()) {
            throw new Exception("Nenhuma partida foi concluída ainda.");
        }

        // 2. Descobrir qual foi a última fase jogada
        FaseTorneio ultimaFaseJogada = partidasConcluidas.stream()
                .max(Comparator.comparing(Partida::getFase))
                .get().getFase();

        // 3. Coletar os vencedores da última fase jogada
        List<Equipe> vencedores;
        if (ultimaFaseJogada == FaseTorneio.FASE_DE_GRUPOS) {
            vencedores = determinarClassificadosDosGrupos(torneio);
        } else {
            vencedores = partidasConcluidas.stream()
                .filter(p -> p.getFase() == ultimaFaseJogada)
                .map(p -> p.getPlacarEquipeA() > p.getPlacarEquipeB() ? p.getEquipeA() : p.getEquipeB())
                .collect(Collectors.toList());
        }

        // 4. Verificar se há um campeão
        if (ultimaFaseJogada == FaseTorneio.FINAL) {
             Equipe equipeCampeao = vencedores.get(0);
             System.out.println("Torneio finalizado! Campeão: " + equipeCampeao.getNome());
             // Retorna o DTO de resposta com os dados do campeão
             return new AvancoFaseResponse("Torneio finalizado!", new EquipeResponseDTO(equipeCampeao));
        }
        // 5. Gerar as partidas da próxima fase
        FaseTorneio proximaFase = calcularProximaFase(vencedores.size());
        
        List<Partida> novasPartidas = new ArrayList<>();
        Collections.shuffle(vencedores);
        LocalDateTime proximaData = partidasConcluidas.stream().max(Comparator.comparing(Partida::getDataHora)).get().getDataHora().plusDays(7);

        for (int i = 0; i < vencedores.size() / 2; i++) {
            Partida novaPartida = new Partida();
            novaPartida.setEquipeA(vencedores.get(i * 2));
            novaPartida.setEquipeB(vencedores.get(i * 2 + 1));
            novaPartida.setDataHora(proximaData);
            novaPartida.setTorneio(torneio);
            novaPartida.setFase(proximaFase);
            novasPartidas.add(novaPartida);
            proximaData = proximaData.plusHours(2);
        }

        List<PartidaResponseDTO> proximasPartidasDTO = novasPartidas.stream()
                .map(PartidaResponseDTO::new)
                .collect(Collectors.toList());

        // Salva no banco e retorna o DTO de resposta com as próximas partidas
        partidaRepository.saveAll(novasPartidas);
        return new AvancoFaseResponse("Próxima fase gerada com sucesso.", proximasPartidasDTO);

    }

    private List<Equipe> determinarClassificadosDosGrupos(Torneio torneio) {
        List<Equipe> classificados = new ArrayList<>();
        for (Grupo grupo : torneio.getGrupos()) {
            grupo.getEquipes().sort((e1, e2) -> Integer.compare(e2.getPontos(), e1.getPontos()));
            if (grupo.getEquipes().size() >= 1) classificados.add(grupo.getEquipes().get(0));
            if (grupo.getEquipes().size() >= 2) classificados.add(grupo.getEquipes().get(1));
        }
        return classificados;
    }
    
    private FaseTorneio calcularProximaFase(int numeroDeEquipes) {
        if (numeroDeEquipes <= 2) return FaseTorneio.FINAL;
        if (numeroDeEquipes <= 4) return FaseTorneio.SEMIFINAL;
        if (numeroDeEquipes <= 8) return FaseTorneio.QUARTAS_DE_FINAL;
        if (numeroDeEquipes <= 16) return FaseTorneio.OITAVAS_DE_FINAL;
        if (numeroDeEquipes <= 32) return FaseTorneio.DEZESSEIS_AVOS;
        return FaseTorneio.TRINTA_E_DOIS_AVOS;
    }
    
    @Transactional
    public Torneio iniciarFaseDeGrupos(Esporte esporte, CategoriaCurso categoria) throws Exception {
        List<Equipe> equipesInscritas = equipeRepository.findByEsporteAndCurso_Categoria(esporte, categoria);
        if (equipesInscritas.size() < 3) {
            throw new Exception("Não há equipes suficientes para iniciar um torneio de " + esporte.getNome()
                    + " na categoria " + categoria);
        }

        Torneio torneio = new Torneio();
        torneio.setEsporte(esporte);
        torneio.setCategoria(categoria);
        torneio.setGrupos(new ArrayList<>());
        Torneio torneioSalvo = torneioRepository.save(torneio);

        distribuirEquipesNosGrupos(torneioSalvo, equipesInscritas);
        gerarPartidasDosGrupos(torneioSalvo);

        return torneioRepository.save(torneioSalvo);
    }

    private void distribuirEquipesNosGrupos(Torneio torneio, List<Equipe> equipes) {
        Collections.shuffle(equipes);
        int totalEquipes = equipes.size();

        if (totalEquipes < 3) return;

        List<Integer> distribuicao = new ArrayList<>();

        if (totalEquipes % 3 == 0) {
            int numGruposDe3 = totalEquipes / 3;
            for (int i = 0; i < numGruposDe3; i++) distribuicao.add(3);
        } else if (totalEquipes % 3 == 1) {
            distribuicao.add(4);
            int equipesRestantes = totalEquipes - 4;
            for (int i = 0; i < equipesRestantes / 3; i++) distribuicao.add(3);
        } else {
            distribuicao.add(5);
            int equipesRestantes = totalEquipes - 5;
            for (int i = 0; i < equipesRestantes / 3; i++) distribuicao.add(3);
        }

        for (int i = 0; i < distribuicao.size(); i++) {
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + (char) ('A' + i));
            grupo.setTorneio(torneio);
            torneio.getGrupos().add(grupo);
        }

        int equipeIndex = 0;
        for (int i = 0; i < distribuicao.size(); i++) {
            Grupo grupoAtual = torneio.getGrupos().get(i);
            int equipesNoGrupo = distribuicao.get(i);
            for (int j = 0; j < equipesNoGrupo; j++) {
                if (equipeIndex < totalEquipes) {
                    grupoAtual.getEquipes().add(equipes.get(equipeIndex++));
                }
            }
        }
    }

    private void gerarPartidasDosGrupos(Torneio torneio) {
        Map<Long, LocalDateTime> ultimoHorarioPorEquipe = new HashMap<>();
        LocalDateTime proximoHorarioDisponivel = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        List<Partida> partidasParaSalvar = new ArrayList<>();

        for (Grupo grupo : torneio.getGrupos()) {
            List<Equipe> equipesDoGrupo = grupo.getEquipes();
            for (int i = 0; i < equipesDoGrupo.size(); i++) {
                for (int j = i + 1; j < equipesDoGrupo.size(); j++) {
                    Partida partida = new Partida();
                    partida.setEquipeA(equipesDoGrupo.get(i));
                    partida.setEquipeB(equipesDoGrupo.get(j));
                    partida.setTorneio(torneio);
                    partida.setFase(FaseTorneio.FASE_DE_GRUPOS);

                    LocalDateTime horarioEquipeA = ultimoHorarioPorEquipe.getOrDefault(partida.getEquipeA().getId(), proximoHorarioDisponivel);
                    LocalDateTime horarioEquipeB = ultimoHorarioPorEquipe.getOrDefault(partida.getEquipeB().getId(), proximoHorarioDisponivel);
                    LocalDateTime horarioDaPartida = horarioEquipeA.isAfter(horarioEquipeB) ? horarioEquipeA : horarioEquipeB;
                    if (horarioDaPartida.isBefore(proximoHorarioDisponivel)) horarioDaPartida = proximoHorarioDisponivel;
                    
                    partida.setDataHora(horarioDaPartida);
                    partidasParaSalvar.add(partida);

                    LocalDateTime proximoHorarioLivre = horarioDaPartida.plusHours(3);
                    ultimoHorarioPorEquipe.put(partida.getEquipeA().getId(), proximoHorarioLivre);
                    ultimoHorarioPorEquipe.put(partida.getEquipeB().getId(), proximoHorarioLivre);
                    proximoHorarioDisponivel = horarioDaPartida.plusHours(2);
                }
            }
        }
        partidaRepository.saveAll(partidasParaSalvar);
    }
    


    public List<Torneio> listarTodos() {
        return torneioRepository.findAll();
    }
}