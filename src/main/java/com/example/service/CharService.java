package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fight_combo_db.CharRepository;
import com.example.model.Personaje;

@Service
public class CharService {
    @Autowired
    private CharRepository charRepository;

    public List<Personaje> obtenerTodosLosPersonajes() {
        return charRepository.findAll();
    }

    public List<Personaje> obtenerPersonajePorJuego (String nombreJuego) {
        return charRepository.findByJuego(nombreJuego);
    }

    public Personaje obtenerPersonajePorNombreYJuego(String nombre, String juego) {
        return charRepository.findByNombreAndJuego(nombre, juego).orElse(null);
    }

    public Personaje obtenerPersonajePorId(Long id) {
        return charRepository.findById(id).orElse(null);
    }
}
