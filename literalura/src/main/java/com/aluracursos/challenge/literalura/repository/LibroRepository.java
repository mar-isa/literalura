package com.aluracursos.challenge.literalura.repository;

import com.aluracursos.challenge.literalura.model.Autor;
import com.aluracursos.challenge.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findByTitulo(String titulo);
    List<Libro> findByIdioma(String idioma);
    List<Libro> findTop10ByOrderByNumeroDescargasDesc();
    @Query("SELECT l FROM Libro l WHERE l.numeroDescargas > 0")
    List<Libro> buscarNumeroDescargas();
}