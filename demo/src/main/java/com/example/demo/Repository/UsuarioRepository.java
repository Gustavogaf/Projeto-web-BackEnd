package com.example.demo.Repository;

import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Page<Usuario> findByTipo(TipoUsuario tipo, Pageable pageable);
}


