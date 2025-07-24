package com.example.fight_combo_db;

import com.example.model.Personaje;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonajeRepository extends JpaRepository<Personaje, Long> {
    
}
