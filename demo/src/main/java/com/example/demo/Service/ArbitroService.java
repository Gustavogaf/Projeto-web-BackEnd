// src/main/java/com/example/demo/Service/ArbitroService.java
package com.example.demo.Service;

import com.example.demo.Model.*;
import com.example.demo.Repository.EquipeRepository;
import com.example.demo.Repository.PartidaRepository;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArbitroService {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

     @Autowired
    private EquipeRepository equipeRepository;

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
}
