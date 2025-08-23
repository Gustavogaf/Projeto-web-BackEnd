package com.example.demo.Controller.dto;

public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    // Getter e Setter
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}