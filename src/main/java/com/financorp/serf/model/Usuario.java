package com.financorp.serf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String username;
    @Column(nullable = false, length = 100)
    private String passwordHash;
    @Column(nullable = false, length = 30)
    private String rol;

    protected Usuario() {}
    public Usuario(String username, String passwordHash, String rol) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public String getRol() { return rol; }
}
