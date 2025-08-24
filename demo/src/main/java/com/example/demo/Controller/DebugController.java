package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI() {
        // Pega a autenticação do contexto de segurança do Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Nenhum usuário autenticado.");
        }

        // Prepara uma resposta com os dados do usuário
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", authentication.getName());
        userDetails.put("authorities", authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userDetails);
    }
}