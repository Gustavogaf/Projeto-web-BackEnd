// src/main/java/com/example/demo/Repository/EquipeRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
}
