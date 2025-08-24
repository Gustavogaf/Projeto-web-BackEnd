package com.example.demo.Service;

import com.example.demo.Model.Esporte;
import com.example.demo.Repository.EsporteRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class EsporteService {

    @Autowired
    private EsporteRepository esporteRepository;

    public Esporte criarEsporte(Esporte esporte) throws Exception {
        // Regra de Negócio: Não permitir dois esportes iguais de serem cadastrados.
        if (esporteRepository.existsByNome(esporte.getNome())) {
            throw new Exception("Já existe um esporte cadastrado com o nome: " + esporte.getNome());
        }
        // Regra de Negócio: minAtletas não pode ser maior que maxAtletas
        if (esporte.getMinAtletas() > esporte.getMaxAtletas()) {
            throw new Exception("A quantidade mínima de atletas não pode ser maior que a quantidade máxima.");
        }

        return esporteRepository.save(esporte);
    }

    public Page<Esporte> listarTodos(Pageable paginacao) {
    return esporteRepository.findAll(paginacao);
}

    public Esporte atualizarEsporte(Long id, Esporte esporteDetails) throws Exception {
        Esporte esporte = esporteRepository.findById(id)
                .orElseThrow(() -> new Exception("Esporte com o ID " + id + " não encontrado."));

        if (esporteDetails.getNome() != null && !esporteDetails.getNome().isBlank()) {
            esporte.setNome(esporteDetails.getNome());
        }
        if (esporteDetails.getMinAtletas() > 0) {
            esporte.setMinAtletas(esporteDetails.getMinAtletas());
        }
        if (esporteDetails.getMaxAtletas() > 0) {
            esporte.setMaxAtletas(esporteDetails.getMaxAtletas());
        }

        // Revalida a regra de min/max
        if (esporte.getMinAtletas() > esporte.getMaxAtletas()) {
            throw new Exception("A quantidade mínima de atletas não pode ser maior que a quantidade máxima.");
        }

        return esporteRepository.save(esporte);
    }

    public void deletarEsporte(Long id) throws Exception {
        if (!esporteRepository.existsById(id)) {
            throw new Exception("Esporte com o ID " + id + " não encontrado.");
        }
        // Adicionar verificação de dependência com equipes se necessário
        esporteRepository.deleteById(id);
    }
}
