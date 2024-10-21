package com.aluracursos.challenge.literalura.repository;

import com.aluracursos.challenge.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByNombre(String nombreAutor);
    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :fecha AND a.fechaDefuncion >= :fecha")
    List<Autor> autoresPorFecha(int fecha);
}
