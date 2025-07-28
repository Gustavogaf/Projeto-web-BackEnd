package com.example.demo.Repository;

import com.example.demo.Model.Equipe;
import com.example.demo.Model.Partida;
import com.example.demo.Model.StatusPartida; 
import com.example.demo.Model.FaseTorneio;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    
    List<Partida> findByTorneioIdOrderByDataHoraDesc(Long torneioId);
    
    boolean existsByEquipeAOrEquipeB(Equipe equipeA, Equipe equipeB);

    boolean existsByTorneioIdAndStatus(Long torneioId, StatusPartida status);

    List<Partida> findByTorneioIdAndStatusNot(Long torneioId, StatusPartida status);

    List<Partida> findByTorneioIdAndFase(Long torneioId, FaseTorneio fase);
}