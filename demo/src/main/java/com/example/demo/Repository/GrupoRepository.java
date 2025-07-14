// src/main/java/com/example/demo/Repository/GrupoRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
}
