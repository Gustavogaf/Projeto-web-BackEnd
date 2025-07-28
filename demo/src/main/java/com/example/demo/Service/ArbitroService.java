package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArbitroService {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

     @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public Partida registrarResultado(String matriculaArbitro, Long partidaId, int placarA, int placarB) throws Exception {
        // 1. VERIFICAR SE O SOLICITANTE É UM ÁRBITRO
        Optional<Usuario> arbitroOpt = usuarioRepository.findById(matriculaArbitro);
        if (arbitroOpt.isEmpty() || arbitroOpt.get().getTipo() != TipoUsuario.ARBITRO) {
            throw new Exception("Apenas usuários do tipo ARBITRO podem registrar resultados.");
        }

        // 2. BUSCAR A PARTIDA E VERIFICAR SEU STATUS
        Optional<Partida> partidaOpt = partidaRepository.findById(partidaId);
        if (partidaOpt.isEmpty()) {
            throw new Exception("Partida com o ID " + partidaId + " não encontrada.");
        }
        Partida partida = partidaOpt.get();
        if (partida.getStatus() != StatusPartida.AGENDADA) {
            throw new Exception("O resultado desta partida já foi registrado.");
        }

        // 3. ATUALIZAR PLACAR E STATUS
        partida.setPlacarEquipeA(placarA);
        partida.setPlacarEquipeB(placarB);
        partida.setStatus(StatusPartida.FINALIZADA);

        Equipe equipeA = partida.getEquipeA();
        Equipe equipeB = partida.getEquipeB();

        if (placarA > placarB) { // Vitória da Equipe A
            equipeA.setPontos(equipeA.getPontos() + 3);
        } else if (placarB > placarA) { // Vitória da Equipe B
            equipeB.setPontos(equipeB.getPontos() + 3);
        } else { // Empate
            equipeA.setPontos(equipeA.getPontos() + 1);
            equipeB.setPontos(equipeB.getPontos() + 1);
        }
        equipeRepository.save(equipeA);
        equipeRepository.save(equipeB);

        return partidaRepository.save(partida);
    }

    public Partida registrarWO(String matriculaArbitro, Long partidaId, Long equipeVencedoraId) throws Exception {
    // 1. VERIFICAR SE O SOLICITANTE É UM ÁRBITRO
    usuarioRepository.findById(matriculaArbitro)
            .filter(u -> u.getTipo() == TipoUsuario.ARBITRO)
            .orElseThrow(() -> new Exception("Apenas usuários do tipo ARBITRO podem registrar um W.O."));

    // 2. BUSCAR A PARTIDA E VERIFICAR SEU STATUS
    Partida partida = partidaRepository.findById(partidaId)
            .orElseThrow(() -> new Exception("Partida com o ID " + partidaId + " não encontrada."));

    if (partida.getStatus() != StatusPartida.AGENDADA) {
        throw new Exception("Esta partida não está mais agendada e não pode ser marcada como W.O.");
    }

    // 3. IDENTIFICAR EQUIPES E ATUALIZAR STATUS E PLACAR
    Equipe equipeA = partida.getEquipeA();
    Equipe equipeB = partida.getEquipeB();
    
    if (equipeVencedoraId.equals(equipeA.getId())) {
        partida.setStatus(StatusPartida.WO_EQUIPE_B); // Equipe B desistiu
        partida.setPlacarEquipeA(3); // Placar simbólico
        partida.setPlacarEquipeB(0);
        equipeA.setPontos(equipeA.getPontos() + 3); // 3 pontos para o vencedor
        equipeRepository.save(equipeA);

    } else if (equipeVencedoraId.equals(equipeB.getId())) {
        partida.setStatus(StatusPartida.WO_EQUIPE_A); // Equipe A desistiu
        partida.setPlacarEquipeA(0);
        partida.setPlacarEquipeB(3); // Placar simbólico
        equipeB.setPontos(equipeB.getPontos() + 3); // 3 pontos para o vencedor
        equipeRepository.save(equipeB);

    } else {
        throw new Exception("A equipe vencedora informada não participa desta partida.");
    }

    return partidaRepository.save(partida);
    }

    @Transactional
    public Partida reverterResultado(String matriculaArbitro, Long partidaId) throws Exception {
        // 1. Validar se o usuário é um árbitro
        usuarioRepository.findById(matriculaArbitro)
                .filter(u -> u.getTipo() == TipoUsuario.ARBITRO)
                .orElseThrow(() -> new Exception("Apenas usuários do tipo ARBITRO podem reverter resultados."));
    
        // 2. Buscar a partida
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new Exception("Partida com o ID " + partidaId + " não encontrada."));
    
        StatusPartida statusAtual = partida.getStatus();
        if (statusAtual == StatusPartida.AGENDADA) {
            throw new Exception("Esta partida ainda está agendada e não pode ser revertida.");
        }
    
        // 3. Reverter os pontos das equipes
        Equipe equipeA = partida.getEquipeA();
        Equipe equipeB = partida.getEquipeB();
    
        if (statusAtual == StatusPartida.FINALIZADA) {
            if (partida.getPlacarEquipeA() > partida.getPlacarEquipeB()) {
                equipeA.setPontos(equipeA.getPontos() - 3);
            } else if (partida.getPlacarEquipeB() > partida.getPlacarEquipeA()) {
                equipeB.setPontos(equipeB.getPontos() - 3);
            } else {
                equipeA.setPontos(equipeA.getPontos() - 1);
                equipeB.setPontos(equipeB.getPontos() - 1);
            }
        } else if (statusAtual == StatusPartida.WO_EQUIPE_B) {
            equipeA.setPontos(equipeA.getPontos() - 3);
        } else if (statusAtual == StatusPartida.WO_EQUIPE_A) {
            equipeB.setPontos(equipeB.getPontos() - 3);
        }
    
        equipeRepository.save(equipeA);
        equipeRepository.save(equipeB);
    
        // 4. Resetar o status e o placar da partida
        partida.setStatus(StatusPartida.AGENDADA);
        partida.setPlacarEquipeA(null);
        partida.setPlacarEquipeB(null);
    
        return partidaRepository.save(partida);
    }
}
