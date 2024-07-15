package com.cursoalura.literalura.repository;

import com.cursoalura.literalura.entity.Book;
import com.cursoalura.literalura.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleContainsIgnoreCase(String title);

    List<Book> findByLenguaje(Language lenguaje);

    @Query("SELECT l FROM Libro l ORDER BY l.descarga DESC LIMIT 10")
    List<Book> top10Books() ;
}
