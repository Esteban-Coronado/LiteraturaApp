package com.cursoalura.literalura.entity;

import com.cursoalura.literalura.model.DataBook;
import com.cursoalura.literalura.model.Language;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Language languaje;
    private String copyright;
    private Integer download;
    @ManyToOne
    private Author author;

    public Book() {}

    public Book(DataBook libro) {
        this.titulo = libro.title();
        this.languaje = Language.fromString(libro.languages().stream()
                .findFirst()
                .orElse(""));
        this.copyright = libro.copyright();
        this.download = libro.download();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Language getlanguaje() {
        return languaje;
    }

    public void setlanguaje(Language languaje) {
        this.languaje = languaje;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getdownload() {
        return download;
    }

    public void setdownload(Integer download) {
        this.download = download;
    }

    public Author getauthor() {
        return author;
    }

    public void setauthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "\ntitulo='" + titulo + '\'' +
                "\nautor=" + author.getname() +
                "\nlenguaje=" + languaje +
                "\ncopyright='" + copyright + '\'' +
                "\ndescarga=" + download +
                "\n               ************";
    }
}
