package com.example.fight_combo_db;

import com.example.model.Personaje;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharRepository extends JpaRepository<Personaje, Long> {
    List<Personaje> findByJuego(String juego);
    Optional<Personaje> findByNombreAndJuego(String nombre, String juego);
    List<Personaje> findByFavorito(int favorito);
}
