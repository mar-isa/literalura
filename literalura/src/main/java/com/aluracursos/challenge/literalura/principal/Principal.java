package com.aluracursos.challenge.literalura.principal;

import com.aluracursos.challenge.literalura.model.Autor;
import com.aluracursos.challenge.literalura.model.DatosLibro;
import com.aluracursos.challenge.literalura.model.Libro;
import com.aluracursos.challenge.literalura.repository.AutorRepository;
import com.aluracursos.challenge.literalura.repository.LibroRepository;
import com.aluracursos.challenge.literalura.service.ConsumoApi;
import com.aluracursos.challenge.literalura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private final ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor> autores;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ------------------------------------------------
                    \t\t  ¡Bienvenido a LiterAlura!
                    \tIngrese la opción de lo que desea hacer
                    ------------------------------------------------
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Buscar autor por nombre
                    4 - Listar autores registrados
                    5 - Listar autores vivos en un determinado año
                    6 - Listar libros por idioma
                    7 - Mostrar top 10 de los libros más descargados
                    8 - Mostrar estadísticas
                    
                    0 - SALIR
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    buscarAutorPorNombre();
                    break;
                case 4:
                    mostrarAutoresBuscados();
                    break;
                case 5:
                    filtrarAutoresPorFecha();
                    break;
                case 6:
                    mostrarLibrosPorIdioma();
                    break;
                case 7:
                    buscarTop10LibrosMasDescargados();
                    break;
                case 8:
                    mostrarEstadisticas();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación... Gracias por usar LiterAlura!");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        String nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        var libroEncontrado = conversor.obtenerListaDatos(json, DatosLibro.class).stream()
                .filter(l -> l.titulo().equalsIgnoreCase(nombreLibro))
                .findFirst();
        libroEncontrado.ifPresentOrElse(libro -> {
            if (libroRepository.findByTitulo(libro.titulo()) == null) {
                libroRepository.save(new Libro(libro));
                System.out.println(new Libro(libro));
            } else {
                System.out.println("El libro ya se encuentra en la base de datos");
            }
        }, () -> System.out.println("No se pudo encontrar el libro"));
    }

    private void mostrarLibrosBuscados() {
        libros = libroRepository.findAll();
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Escribe el nombre autor");
        var nombreAutor = teclado.nextLine();
        List<Autor> autorEncontrado = autorRepository.findByNombre(nombreAutor);

        if (!autorEncontrado.isEmpty()) {
            autorEncontrado.forEach(System.out::println);
        }
        else {
            System.out.println("Autor no encontrado");
        }
    }

    private void mostrarAutoresBuscados() {
        autores = autorRepository.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
    }

    private void filtrarAutoresPorFecha() {
        System.out.println("Escribe el año por el que deseas filtrar:");
        var fecha = teclado.nextInt();
        teclado.nextLine();
        List<Autor> filtroAutores = autorRepository.autoresPorFecha(fecha);
        filtroAutores.forEach(System.out::println);
        if (filtroAutores.isEmpty()) {
            System.out.println("No se encontró ningún autor vivo en esa fecha");
        }
    }

    private void mostrarLibrosPorIdioma() {
        System.out.println("""
                \nEscribe el idioma por el que deseas buscar los libros:
                • en - inglés
                • es - español
                • fr - francés
                • pt - portugués
                """);
        var idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma.toLowerCase());
        if (!librosPorIdioma.isEmpty()) {
            librosPorIdioma.forEach(System.out::println);
        } else {
            System.out.println("No se encontraron libros con el idioma " + idioma);
        }
    }

    private void buscarTop10LibrosMasDescargados() {
        List<Libro> topLibros = libroRepository.findTop10ByOrderByNumeroDescargasDesc();
        System.out.println("""
                \n--------------------------------------------
                    \tTOP 10 LIBROS MAS DESCARGADOS
                --------------------------------------------
                """);
        topLibros.forEach(System.out::println);
    }

    private void mostrarEstadisticas() {
        System.out.println("""
                \n-----------------------------
                    \tESTADISTICAS
                -----------------------------
                """);
        DoubleSummaryStatistics est = libroRepository.buscarNumeroDescargas().stream()
                .collect(Collectors.summarizingDouble(Libro::getNumeroDescargas));
        System.out.printf("Media de descargas: %.2f\n", est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados: " + est.getCount() + "\n");
    }
}