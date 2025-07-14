// src/main/java/com/example/demo/Repository/PartidaRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long> {
}
