// src/main/java/com/example/demo/Repository/CursoRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Métodos de busca personalizados podem ser adicionados aqui no futuro
}
