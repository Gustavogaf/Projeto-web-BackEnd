package com.example.demo.Service;

import com.example.demo.Model.Usuario;
import com.example.demo.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Injetamos o PasswordEncoder para que a senha do admin seja consistente
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
        // --- INÍCIO DA MODIFICAÇÃO: Lógica do Super Usuário ---
        if ("admin".equals(matricula)) {
            return User.builder()
                    .username("admin")
                    // A senha deve ser a mesma que usaremos no login do teste
                    .password(passwordEncoder.encode("admin")) 
                    .roles("ADMIN") // Define o papel (ROLE)
                    .build();
        }
        // --- FIM DA MODIFICAÇÃO ---

        Usuario usuario = usuarioRepository.findById(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com a matrícula: " + matricula));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name());

        return new User(usuario.getMatricula(), usuario.getSenha(), Collections.singletonList(authority));
    }

    
}