package com.example.fight_combo_db;

import com.example.model.Juego;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Juego, Long> {
}
