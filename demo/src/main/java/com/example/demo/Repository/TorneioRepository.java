// src/main/java/com/example/demo/Repository/TorneioRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Torneio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneioRepository extends JpaRepository<Torneio, Long> {
}
