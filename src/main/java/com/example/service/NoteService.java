package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fight_combo_db.NoteRepository;
import com.example.model.Nota;
import com.example.model.Personaje;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;
    
    public List<Nota> obtenerNotasPorPersonaje(Personaje personaje) {
        return noteRepository.findByPersonaje(personaje);
    }
}
