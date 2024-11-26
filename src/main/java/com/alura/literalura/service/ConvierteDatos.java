package com.alura.literalura.service;
import com.alura.literalura.dto.AutorDTO;
import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos implements IConvierteDatos {

    private ObjectMapper objectMapper = new ObjectMapper();

    //convertir JSON a objeto//
    public <T> T obtenerDatos(String json, Class<T> clase){
        try {
            return objectMapper.readValue(json,clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir JSON a objeto", e);
        }
    }

    //convertir entidad Autor a DTO
    public AutorDTO convertirAutorADto(Autor autor){
        AutorDTO dto = new AutorDTO();
        dto.setId(autor.getId());
        dto.setNombre(autor.getNombre());
        return dto;
    }

    //convertir DTO Autor a entidad
    public LibroDTO convertirLibroDTO(Libro libro){
        LibroDTO dto = new LibroDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(convertirAutorADto(libro.getAutor()));
        return dto;
    }

    // convertir DTO Autor a entidad
    public Autor convertirDtoAAutor(AutorDTO dto){
        Autor autor = new Autor();
        autor.setId(dto.getId());
        autor.setNombre(dto.getNombre());
        return autor;
    }
    //convertir DTO Libro a entidad
    public Libro convertirDtoALibro(LibroDTO dto){
        Libro libro = new Libro();
        libro.setId(dto.getId());
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(convertirDtoAAutor(dto.getAutor()));
        return libro;
    }
}
