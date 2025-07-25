// src/main/java/com/example/demo/Repository/EsporteRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.Esporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsporteRepository extends JpaRepository<Esporte, Long> {
    
    
    boolean existsByNome(String nome);
}
