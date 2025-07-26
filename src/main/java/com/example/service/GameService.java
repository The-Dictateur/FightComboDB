package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fight_combo_db.GameRepository;
import com.example.model.Juego;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public List<Juego> obtenerTodosLosJuegos() {
        return gameRepository.findAll();
    }
}
