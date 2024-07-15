package com.cursoalura.literalura.entity;

import com.cursoalura.literalura.model.DataAuthor;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer birthday;
    private Integer deathday;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Book> book;

    public Author() {}

    public Author(DataAuthor datosAutor){
        this.name = datosAutor.name();
        this.birthday = datosAutor.birthday();
        this.deathday = datosAutor.deathday();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public Integer getbirthday() {
        return birthday;
    }

    public void setbirthday(Integer birthday) {
        this.birthday = birthday;
    }

    public Integer getdeathday() {
        return deathday;
    }

    public void setdeathday(Integer deathday) {
        this.deathday = deathday;
    }

    public List<Book> getbook() {
        return book;
    }

    public void setbook(List<Book> book) {
        book.forEach(l -> l.setauthor(this));
        this.book = book;
    }

    public void setLibro(Book libro) {
        libro.setauthor(this);
        if (book == null)
            book = new ArrayList<>();
        book.add(libro);
    }

    @Override
    public String toString() {
        return "id=" + id +
                "\nNombre='" + name + '\'' +
                "\nNacimiento=" + birthday +
                "\nMuerte=" + deathday +
                "\nLibros=" + book.stream()
                .map(Book::getTitulo)
                .toList() +
                "\n               ************";
    }
}
