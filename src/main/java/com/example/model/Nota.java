package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String contenido;

    @ManyToOne
    @JoinColumn(name = "personaje_id", nullable = false)
    private Personaje personaje;

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Personaje getPersonaje() { return personaje; }
    public void setPersonaje(Personaje personaje) { this.personaje = personaje; }
}
