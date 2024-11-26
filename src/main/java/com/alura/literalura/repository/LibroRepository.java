package com.alura.literalura.repository;
import com.alura.literalura.model.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface LibroRepository  extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloAndAutorId(String titulo, Long autorId);

    @Query("SELECT l FROM Libro l ORDER BY l.titulo")
    List<Libro> findAllOrderedByTitulo();

    @Query("SELECT l FROM Libro l WHERE l.idiomasStr LIKE %:idioma%")
    List<Libro> findByIdioma(@Param("idioma") String idioma);

    @Query("SELECT l FROM Libro l ORDER BY l.numeroDescargas DESC")
    Page<Libro> findTopLibrosPorDescarga(Pageable pageable);
}
