package com.alura.literalura.dto;

public class LibroDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private AutorDTO autor;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() {
        return titulo; }
    public void setTitulo(String titulo) {
        this.titulo = titulo; }

    public String getDescripcion() {
        return descripcion; }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion; }

    public AutorDTO getAutor() {
        return autor; }
    public void setAutor(AutorDTO autor) {
        this.autor = autor; }
}
