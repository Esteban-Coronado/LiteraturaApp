package com.cursoalura.literalura.main;

import com.cursoalura.literalura.entity.Author;
import com.cursoalura.literalura.entity.Book;
import com.cursoalura.literalura.model.Data;
import com.cursoalura.literalura.model.DataBook;
import com.cursoalura.literalura.model.Language;
import com.cursoalura.literalura.repository.AuthorRepository;
import com.cursoalura.literalura.repository.BookRepository;
import com.cursoalura.literalura.service.ConexionApi;
import com.cursoalura.literalura.service.ConvertData;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner input = new Scanner(System.in);
    private ConexionApi api = new ConexionApi();
    private ConvertData convert = new ConvertData();

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public Main(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public void startMenu() {
        int opcion = -1;
        String menu = """
                ==============================
                       MENÚ DE LITERATURA
                ==============================
                1. Buscar libro por título
                2. Listar libros registrados
                3. Listar autores registrados
                4. Listar autores vivos en un determinado año
                5. Listar libros por idioma
                6. Generar estadísticas
                7. Top 10 libros
                8. Buscar autor por nombre
                9. Listar autores con otras consultas
                0. Salir
                ==============================
                Por favor, elija una opción:
                """;


        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.parseInt(input.nextLine());
                switch (opcion) {
                    case 1 -> searchBookByTitle();
                    case 2 -> listRegisteredBooks();
                    case 3 -> listRegisteredAuthors();
                    case 4 -> listLivingAuthors();
                    case 5 -> listBooksByLanguage();
                    case 6 -> generateStatistics();
                    case 7 -> top10Books();
                    case 8 -> searchAuthorByName();
                    case 9 -> listAuthorsWithOtherQueries();
                    case 0 -> System.out.println("Cerrando la aplicación...");
                    default -> System.out.println("¡Opción no válida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida: " + e.getMessage());
            }
        }
    }

    public void searchBookByTitle() {
        System.out.print("""
                ==============================
                   BUSCAR LIBROS POR TÍTULO
                ==============================
                """);

        System.out.println("Introduzca el nombre del libro que desea buscar:");
        String nombre = input.nextLine();
        String json = api.getData(URL_BASE + "?search=" + nombre.replace(" ", "%20"));
        var datos = convert.getData(json, Data.class);
        Optional<DataBook> libroAPI = datos.books().stream()
                .filter(l -> l.title()
                        .toUpperCase()
                        .contains(nombre.toUpperCase()))
                .findFirst();
        if (libroAPI.isPresent()) {
            Author autor = new Author(libroAPI.get().authors().get(0));
            Book libro = new Book(libroAPI.get());
            try {
                Optional<Book> libroDB = bookRepository.findByTitleContainsIgnoreCase(libro.getTitulo());
                if (libroDB.isPresent()) {
                    System.out.println("El libro ya está guardado en la base de datos.");
                    System.out.println(libroDB.get());
                } else {
                    Optional<Author> autorDB = authorRepository.findByNameContainsIgnoreCase(autor.getname());
                    if (autorDB.isPresent()) {
                        autor = autorDB.get();
                        autor.setLibro(libro);
                        System.out.println("El autor ya está guardado en la BD!");
                    } else {
                        autor.setbook(Collections.singletonList(libro));
                    }
                    authorRepository.save(autor);
                    System.out.println(libro);
                }
            } catch (Exception e) {
                System.out.println("Advertencia! " + e.getMessage());
            }
        } else
            System.out.println("Libro no encontrado!");
    }

    public void listRegisteredBooks() {
        System.out.println("""
                ==============================
                   LIBROS REGISTRADOS
                ==============================
                """);

        List<Book> libros = bookRepository.findAll();
        libros.forEach(System.out::println);
    }

    public void listRegisteredAuthors() {
        System.out.print("""
                ==============================
                   AUTORES REGISTRADOS
                ==============================
                """);

        List<Author> autores = authorRepository.findAll();
        autores.forEach(System.out::println);
    }

    public void listLivingAuthors() {
        System.out.println("""
                ==============================
                   LISTAR AUTORES VIVOS
                ==============================
                """);

        System.out.println("Introduzca el año que desea buscar:");
        try {
            var fecha = Integer.valueOf(input.nextLine());
            List<Author> autores = authorRepository.findByBirthdayLessThanEqualAndDeathdayIsGreaterThanEqual(
                    fecha,
                    fecha
            );
            if (!autores.isEmpty()) {
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getname() +
                                "\nFecha de nacimiento: " + a.getbirthday() +
                                "\nFecha de fallecimiento: " + a.getdeathday()
                ));
            } else
                System.out.println("No hay autores vivos en ese año registrado en la BD!");
        } catch (NumberFormatException e) {
            System.out.println("Introduce un año válido " + e.getMessage());
        }
    }

    public void listBooksByLanguage() {
        System.out.println("""
                ==============================
                    LISTAR LIBROS POR IDIOMA
                ==============================
                Ingrese el idioma para buscar libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """);
        String idioma = input.nextLine();
        if (List.of("es", "en", "fr", "pt").contains(idioma.toLowerCase())) {
            Language lenguaje = Language.fromString(idioma);
            List<Book> libros = bookRepository.findByLenguaje(lenguaje);
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma.");
            } else {
                libros.forEach(System.out::println);
            }
        } else {
            System.out.println("Introduzca un idioma en el formato válido.");
        }
    }

    public void generateStatistics() {
        System.out.println("""
                ==============================
                    ESTADÍSTICAS DE DESCARGAS
                ==============================
                """);

        var json = api.getData(URL_BASE);
        var datos = convert.getData(json, Data.class);
        IntSummaryStatistics est = datos.books().stream()
                .filter(l -> l.download() > 0)
                .collect(Collectors.summarizingInt(DataBook::download));
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadísticas: " +
                est.getCount());
    }

    public void top10Books() {
        System.out.println("""
                ==============================
                        TOP 10 LIBROS
                ==============================
                """);

        List<Book> libros = bookRepository.top10Books();
        libros.forEach(System.out::println);
    }

    public void searchAuthorByName() {
        System.out.println("""
                ==============================
                    BUSCAR AUTOR POR NOMBRE
                ==============================
                """);

        System.out.println("Ingrese el nombre del autor que desea buscar:");
        String nombre = input.nextLine();
        Optional<Author> autor = authorRepository.findByNameContainsIgnoreCase(nombre);
        if (autor.isPresent())
            System.out.println(autor.get());
        else
            System.out.println("El autor no existe en la BD!");
    }

    public void listAuthorsWithOtherQueries() {
        System.out.println("""
                ==================================
                    LISTAR AUTORES POR AÑO
                ==================================
                1 - Listar autores por año de nacimiento
                2 - Listar autores por año de fallecimiento
                Ingrese la opción por la cual desea listar los autores:
                """);

        try {
            var opcion = Integer.valueOf(input.nextLine());
            switch (opcion) {
                case 1:
                    listAuthorsByBirth();
                    break;
                case 2:
                    listAuthorsByDeath();
                    break;
                default:
                    System.out.println("Opción inválida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }

    public void listAuthorsByBirth() {
        System.out.println("""
                ==============================
                BUSCAR AUTOR POR NACIMIENTO
                ==============================
                """);

        System.out.println("Introduce el año de nacimiento que deseas buscar:");
        try {
            var fecha = Integer.valueOf(input.nextLine());
            List<Author> autores = authorRepository.findByBirthdayEquals(fecha);
            if (autores.isEmpty())
                System.out.println("No existen autores con año de nacimiento igual a " + fecha);
            else
                autores.forEach(System.out::println);
        } catch (NumberFormatException e){
            System.out.println("Año no válido: " + e.getMessage());
        }
    }

    public void listAuthorsByDeath() {
        System.out.println("""
                ==============================
                BUSCAR AUTOR POR FALLECIMIENTO
                ==============================
                """);

        System.out.println("Introduce el año de fallecimiento que deseas buscar:");
        try {
            var fallecimiento = Integer.valueOf(input.nextLine());
            List<Author> autores = authorRepository.findByDeathdayEquals(fallecimiento);
            if (autores.isEmpty())
                System.out.println("No existen autores con año de fallecimiento igual a " + fallecimiento);
            else
                autores.forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida: " + e.getMessage());
        }
    }
}
