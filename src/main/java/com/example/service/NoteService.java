package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fight_combo_db.CharRepository;
import com.example.fight_combo_db.NoteRepository;
import com.example.model.Nota;
import com.example.model.Personaje;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CharRepository charRepository;

    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Nota> obtenerNotasPorPersonaje(Personaje personaje) {
        return noteRepository.findByPersonaje(personaje);
    }

    public void saveNote(String titulo, String contenido, Long personajeId) {
    
        Personaje personaje = charRepository.findById(personajeId).orElse(null);
        Nota nota = new Nota();
        nota.setTitulo(titulo);
        nota.setContenido(contenido);
        nota.setPersonaje(personaje);

        noteRepository.save(nota);
    }
}
