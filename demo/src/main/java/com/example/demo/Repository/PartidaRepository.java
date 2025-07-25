package com.example.demo.Repository;

import com.example.demo.Model.Partida;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByTorneioIdOrderByDataHoraDesc(Long torneioId);
}
