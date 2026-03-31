package com.mifica.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "badge", indexes = {
    @Index(name = "idx_badge_user", columnList = "user_id"),
    @Index(name = "idx_badge_name", columnList = "name")
})
public class Badge {

    // ICP-TOTAL: 1
    // ICP-01: Entidade materializa conquistas de gamificação associadas ao usuário.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Long userId;

    // Construtor vazio (necessário para JPA)
    public Badge() {
    }

    // Construtor opcional para facilitar criação
    public Badge(String name, String description, Long userId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // toString opcional (útil para debug/logs)
    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                '}';
    }
}
