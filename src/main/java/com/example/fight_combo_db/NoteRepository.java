package com.example.fight_combo_db;

import com.example.model.Nota;
import com.example.model.Personaje;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByPersonaje(Personaje personaje);
}