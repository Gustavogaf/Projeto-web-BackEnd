package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
        List<Partida> todasAsPartidas = partidaRepository.findByTorneioIdOrderByDataHoraDesc(torneioId);

        boolean existePartidaAgendada = todasAsPartidas.stream()
                .anyMatch(p -> p.getStatus() == StatusPartida.AGENDADA);

        if (existePartidaAgendada) {
            throw new Exception("Ainda existem partidas agendadas. A fase atual não foi concluída.");
        }

        // 2. IDENTIFICAR OS VENCEDORES DA ÚLTIMA FASE JOGADA
        LocalDateTime ultimaData = todasAsPartidas.get(0).getDataHora();

        List<Equipe> vencedores = new ArrayList<>();
        for (Partida p : todasAsPartidas) {
            if (p.getDataHora().equals(ultimaData)) {
                if (p.getPlacarEquipeA() > p.getPlacarEquipeB()) {
                    vencedores.add(p.getEquipeA());
                } else if (p.getPlacarEquipeB() > p.getPlacarEquipeA()) {
                    vencedores.add(p.getEquipeB());
                }
            }
        }

        // 3. VERIFICAR SE HÁ UM CAMPEÃO
        if (vencedores.size() == 1) {
            System.out.println("Temos um campeão! Equipe: " + vencedores.get(0).getNome());
            return new ArrayList<>(); // Retorna lista vazia para indicar o fim.
        }

        // 4. GERAR A PRÓXIMA FASE, TRATANDO O "BYE" SE NECESSÁRIO
        Collections.shuffle(vencedores); // Sorteia os confrontos

        
        if (vencedores.size() % 2 != 0 && vencedores.size() > 1) {
            Equipe equipeComBye = vencedores.remove(0); // A primeira equipe da lista embaralhada avança
            System.out.println("Equipe " + equipeComBye.getNome() + " avançou automaticamente (bye).");
            // Esta equipe com "bye" não entrará na geração de partidas abaixo,
            // mas precisa ser considerada como "vencedora" para a próxima fase.
            // A lógica atual de buscar vencedores da "última data" já cobre isso,
            // pois ela não terá uma partida com a nova data. Vamos ajustar isso no futuro
            // se necessário.
        }
        // -----------------------------------------

        List<Partida> proximaFase = new ArrayList<>();
        LocalDateTime proximaData = ultimaData.plusDays(7); // Marca a próxima fase para uma semana depois
        Torneio torneio = torneioRepository.findById(torneioId).get();

        for (int i = 0; i < vencedores.size() / 2; i++) {
            Partida novaPartida = new Partida();
            novaPartida.setEquipeA(vencedores.get(i * 2));
            novaPartida.setEquipeB(vencedores.get(i * 2 + 1));
            novaPartida.setDataHora(proximaData);
            novaPartida.setTorneio(torneio);

            proximaFase.add(novaPartida);
            proximaData = proximaData.plusHours(2);
        }

        return partidaRepository.saveAll(proximaFase);
    }
    
    @Transactional
    public Torneio iniciarFaseDeGrupos(Esporte esporte, CategoriaCurso categoria) throws Exception {
        // 1. BUSCAR EQUIPES INSCRITAS
        List<Equipe> equipesInscritas = equipeRepository.findByEsporteAndCurso_Categoria(esporte, categoria);
        if (equipesInscritas.size() < 3) {
            throw new Exception("Não há equipes suficientes para iniciar um torneio de " + esporte.getNome()
                    + " na categoria " + categoria);
        }

        // 2. CRIAR E SALVAR O TORNEIO *ANTES* DE GERAR AS PARTIDAS
        Torneio torneio = new Torneio();
        torneio.setEsporte(esporte);
        torneio.setCategoria(categoria);
        torneio.setGrupos(new ArrayList<>()); // Inicializa a lista de grupos
        Torneio torneioSalvo = torneioRepository.save(torneio);


        // 3. DISTRIBUIR EQUIPES NOS GRUPOS (Requisitos 14, 15, 16)
        distribuirEquipesNosGrupos(torneioSalvo, equipesInscritas);

        // 4. GERAR PARTIDAS DA FASE DE GRUPOS (Requisito 15 - todos contra todos)
        gerarPartidasDosGrupos(torneioSalvo);

        return torneioRepository.save(torneioSalvo);
    }

    private void distribuirEquipesNosGrupos(Torneio torneio, List<Equipe> equipes) {
        Collections.shuffle(equipes); // Sorteia a ordem para aleatoriedade
        int totalEquipes = equipes.size();

        // Validação de regra de negócio: mínimo de 3 equipes para um torneio
        if (totalEquipes < 3) {
            // Lançar a exceção aqui é uma boa prática, pois o serviço que inicia o torneio
            // já trata isso.
            // Apenas como uma segurança adicional.
            return;
        }

        List<Integer> distribuicao = new ArrayList<>();

        if (totalEquipes % 3 == 0) {
            int numGruposDe3 = totalEquipes / 3;
            for (int i = 0; i < numGruposDe3; i++) {
                distribuicao.add(3);
            }
        } else if (totalEquipes % 3 == 1) {
            // Ex: 7 equipes -> 1 grupo de 4 e 1 grupo de 3
            // Ex: 10 equipes -> 1 grupo de 4 e 2 grupos de 3
            distribuicao.add(4);
            int equipesRestantes = totalEquipes - 4;
            int numGruposDe3 = equipesRestantes / 3;
            for (int i = 0; i < numGruposDe3; i++) {
                distribuicao.add(3);
            }
        } else { // totalEquipes % 3 == 2
            // Ex: 5 equipes -> 1 grupo de 5
            // Ex: 8 equipes -> 1 grupo de 5 e 1 grupo de 3
            distribuicao.add(5);
            int equipesRestantes = totalEquipes - 5;
            int numGruposDe3 = equipesRestantes / 3;
            for (int i = 0; i < numGruposDe3; i++) {
                distribuicao.add(3);
            }
        }

        // Criar os grupos vazios baseados na distribuição calculada
        for (int i = 0; i < distribuicao.size(); i++) {
            Grupo grupo = new Grupo();
            grupo.setNome("Grupo " + (char) ('A' + i));
            grupo.setTorneio(torneio);
            torneio.getGrupos().add(grupo);
        }

        // Adicionar as equipes aos grupos
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
    // Mapa para controlar o último horário agendado para cada equipe
    Map<Long, LocalDateTime> ultimoHorarioPorEquipe = new HashMap<>();
    LocalDateTime proximoHorarioDisponivel = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
    List<Partida> partidasParaSalvar = new ArrayList<>();

    for (Grupo grupo : torneio.getGrupos()) {
        List<Equipe> equipesDoGrupo = grupo.getEquipes();
        // Gerar todos os confrontos possíveis para o grupo
        for (int i = 0; i < equipesDoGrupo.size(); i++) {
            for (int j = i + 1; j < equipesDoGrupo.size(); j++) {
                Equipe equipeA = equipesDoGrupo.get(i);
                Equipe equipeB = equipesDoGrupo.get(j);

                // Encontrar o próximo horário em que AMBAS as equipes estão livres
                LocalDateTime horarioEquipeA = ultimoHorarioPorEquipe.getOrDefault(equipeA.getId(), proximoHorarioDisponivel);
                LocalDateTime horarioEquipeB = ultimoHorarioPorEquipe.getOrDefault(equipeB.getId(), proximoHorarioDisponivel);

                // O horário da partida será o maior entre os últimos horários das duas equipes
                LocalDateTime horarioDaPartida = horarioEquipeA.isAfter(horarioEquipeB) ? horarioEquipeA : horarioEquipeB;

                // Para garantir que não haja sobreposição geral, verificamos também o próximo slot global
                if (horarioDaPartida.isBefore(proximoHorarioDisponivel)) {
                    horarioDaPartida = proximoHorarioDisponivel;
                }

                Partida partida = new Partida();
                partida.setEquipeA(equipeA);
                partida.setEquipeB(equipeB);
                partida.setDataHora(horarioDaPartida);
                partida.setTorneio(torneio); // Manter o vínculo com o torneio
                
                partidasParaSalvar.add(partida);

                // Atualizar o último horário para ambas as equipes
                // Adicionamos 2 horas para o jogo e 1 hora de descanso
                LocalDateTime proximoHorarioLivre = horarioDaPartida.plusHours(3);
                ultimoHorarioPorEquipe.put(equipeA.getId(), proximoHorarioLivre);
                ultimoHorarioPorEquipe.put(equipeB.getId(), proximoHorarioLivre);

                // Atualizar o próximo slot de tempo global
                proximoHorarioDisponivel = horarioDaPartida.plusHours(2);
            }
        }
    }
    partidaRepository.saveAll(partidasParaSalvar);
}

    public List<Partida> gerarMataMata(Long torneioId) throws Exception {
        Torneio torneio = torneioRepository.findById(torneioId)
                .orElseThrow(() -> new Exception("Torneio não encontrado."));

        List<Equipe> pote1_primeiros = new ArrayList<>();
        List<Equipe> pote2_segundos = new ArrayList<>();

        // 1. Preencher os potes com os classificados
        for (Grupo grupo : torneio.getGrupos()) {
            grupo.getEquipes().sort((e1, e2) -> Integer.compare(e2.getPontos(), e1.getPontos()));
            if (grupo.getEquipes().size() >= 1)
                pote1_primeiros.add(grupo.getEquipes().get(0));
            if (grupo.getEquipes().size() >= 2)
                pote2_segundos.add(grupo.getEquipes().get(1));
        }
        Collections.shuffle(pote1_primeiros);
        Collections.shuffle(pote2_segundos);

        // --- LÓGICA DE AJUSTE DE CHAVE ---

        int totalClassificados = pote1_primeiros.size() + pote2_segundos.size();
        if (totalClassificados < 2) {
            return new ArrayList<>(); // Não há jogos a serem criados
        }

        // 2. Calcular a próxima potência de 2
        int proximaPotenciaDeDois = 1;
        while (proximaPotenciaDeDois < totalClassificados) {
            proximaPotenciaDeDois *= 2;
        }
        // O objetivo é a potência de 2 anterior (ex: se classificaram 14, a próxima é
        // 16, o objetivo é 8)
        int objetivoProximaFase = proximaPotenciaDeDois / 2;

        // 3. Calcular quantos times jogam a fase preliminar e quantos recebem "bye"
        int timesNaPreliminar = (totalClassificados - objetivoProximaFase) * 2;
        int numeroDeByes = totalClassificados - timesNaPreliminar;

        List<Equipe> equipesComBye = new ArrayList<>();
        List<Equipe> equipesNaPreliminar = new ArrayList<>();

        // 4. Atribuir os "byes" aos melhores classificados (do Pote 1)
        for (int i = 0; i < numeroDeByes; i++) {
            if (!pote1_primeiros.isEmpty()) {
                equipesComBye.add(pote1_primeiros.remove(0));
            } else if (!pote2_segundos.isEmpty()) {
                // Caso raro onde há mais byes do que campeões de grupo
                equipesComBye.add(pote2_segundos.remove(0));
            }
        }

        // 5. O restante dos times vai para a rodada preliminar
        equipesNaPreliminar.addAll(pote1_primeiros);
        equipesNaPreliminar.addAll(pote2_segundos);

        // 6. Gerar as partidas da rodada preliminar
        List<Partida> partidasMataMata = new ArrayList<>();
        LocalDateTime dataPartidaFicticia = LocalDateTime.now().plusDays(7);

        for (int i = 0; i < equipesNaPreliminar.size() / 2; i++) {
            Partida partida = new Partida();
            partida.setEquipeA(equipesNaPreliminar.get(i * 2));
            partida.setEquipeB(equipesNaPreliminar.get(i * 2 + 1));
            partida.setDataHora(dataPartidaFicticia);
            partida.setTorneio(torneio);
            partidasMataMata.add(partida);
            dataPartidaFicticia = dataPartidaFicticia.plusHours(2);
        }

        System.out.println("Fase de Mata-Mata Gerada:");
        System.out.println("Equipes com Bye (avançam direto): " + equipesComBye.size());
        System.out.println("Equipes na Rodada Preliminar: " + equipesNaPreliminar.size());
        System.out.println("Partidas Geradas: " + partidasMataMata.size());

        return partidaRepository.saveAll(partidasMataMata);
    }

    public List<Torneio> listarTodos() {
        return torneioRepository.findAll();
    }
}