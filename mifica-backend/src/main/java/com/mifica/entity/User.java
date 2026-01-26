package com.mifica.entity;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int points;   // <-- campo que faltava

    private int level;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {   // <-- agora existe
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}
