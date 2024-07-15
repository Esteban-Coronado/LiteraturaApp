package com.cursoalura.literalura.repository;

import com.cursoalura.literalura.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByNameContainsIgnoreCase(String name);

    List<Author> findByBirthdayLessThanEqualAndDeathdayIsGreaterThanEqual(Integer birthday,Integer deathday);

    List<Author> findByBirthdayEquals(Integer date);

    List<Author> findByDeathdayEquals(Integer date);
}
