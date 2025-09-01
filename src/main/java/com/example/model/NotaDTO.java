package com.example.model;

public class NotaDTO {
    private String titulo;
    private String contenido;
    private Long idPersonaje;

    // 👇 Jackson necesita este constructor vacío
    public NotaDTO() {}

    public NotaDTO(String titulo, String contenido, Long idPersonaje) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.idPersonaje = idPersonaje;
    }

    // 👇 Getters y setters públicos (muy importante para Jackson)
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Long getIdPersonaje() {
        return idPersonaje;
    }
    public void setIdPersonaje(Long idPersonaje) {
        this.idPersonaje = idPersonaje;
    }
}
