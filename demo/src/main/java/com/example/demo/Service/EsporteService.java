// src/main/java/com/example/demo/Service/EsporteService.java
package com.example.demo.Service;

import com.example.demo.Model.Esporte;
import com.example.demo.Repository.EsporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsporteService {

    @Autowired
    private EsporteRepository esporteRepository;

    public Esporte criarEsporte(Esporte esporte) throws Exception {
        // Regra de Negócio: Requisito 4 - Não permitir dois esportes iguais de serem cadastrados.
        if (esporteRepository.existsByNome(esporte.getNome())) {
            throw new Exception("Já existe um esporte cadastrado com o nome: " + esporte.getNome());
        }
        // Regra de Negócio: Requisito 6 - Quantidade mínima e máxima de atletas.
        if (esporte.getMinAtletas() <= 0 || esporte.getMaxAtletas() <= 0) {
             throw new Exception("A quantidade mínima e máxima de atletas deve ser maior que zero.");
        }
        if (esporte.getMinAtletas() > esporte.getMaxAtletas()) {
            throw new Exception("A quantidade mínima de atletas não pode ser maior que a quantidade máxima.");
        }

        return esporteRepository.save(esporte);
    }
}
