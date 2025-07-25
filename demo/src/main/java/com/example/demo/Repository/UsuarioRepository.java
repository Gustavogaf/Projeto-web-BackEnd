package com.example.demo.Repository;

import com.example.demo.Model.TipoUsuario;
import com.example.demo.Model.Usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    List<Usuario> findByTipo(TipoUsuario tipo);
}
