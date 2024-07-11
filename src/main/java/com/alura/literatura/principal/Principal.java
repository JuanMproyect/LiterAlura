package com.alura.literatura.principal;


import com.alura.literatura.model.*;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private final ConsumoAPI apiService = new ConsumoAPI();
    private final ConvierteDatos convertidorDatos = new ConvierteDatos();
    private final Scanner entrada = new Scanner(System.in);

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;


    public Principal(AutorRepository autor, LibroRepository libro) {
        this.autorRepository = autor;
        this.libroRepository = libro;
    }

    public void muestraMenu() {

        Scanner entrada = new Scanner(System.in);
        int opcion;

        do {
            String menu = """
                    ------------------------------
                    1 - Buscar libros por titulo
                    2 - Mostrar libros registrados
                    3 - listar autores registrados
                    4 - listar autores vivos en un determinado año
                    5 - listar libros por idiomas
                    0 - Salir
                    """;
            System.out.println(menu);

            try {
                opcion = entrada.nextInt();
            } catch (Exception e) {
                System.out.println("\nOpción no válida\n");
                entrada.nextLine();  // Limpiar el buffer de entrada
                opcion = -1;  // Asignar un valor no válido para que el bucle continúe
                continue;
            }

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnUnDeterminadoAño();
                    break;
                case 5:
                    listarLibrosPorIdiomas();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("\nOpción no válida\n");
                    break;
            }
        } while (opcion != 0);
    }

    private Datos getDatosLibro() {
        System.out.println("Ingrese el titulo del libro");
        String titulo = entrada.nextLine();

        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);

        String url = URL_BASE + "?search=" + tituloCodificado.replace(" ", "+");
        String json = apiService.obtenerDatos(url);
        return convertidorDatos.obtenerDatos(json, Datos.class);


    }


    private void buscarLibroPorTitulo() {

        Datos datos = getDatosLibro();

        if (datos != null && !datos.resultados().isEmpty()) {
            DatosLibros datoslibroEcontrado = datos.resultados().get(0);

            DatosAutor datosAutor = datoslibroEcontrado.autor().get(0);
            Autor autor = autorRepository.findByNombreIgnoreCase(datosAutor.nombre()).orElseGet(() -> {
                Autor autor1 = new Autor(datosAutor);
                return autorRepository.save(autor1);
            });

            Optional<Libro> libroExiste = libroRepository.findByTituloIgnoreCase(datoslibroEcontrado.titulo());

            if (libroExiste.isPresent()) {

                System.out.println("\nEl libro ya esta registrado, pruebe con otro libro\n");
            } else {
                Libro libroEcontrado = new Libro(datoslibroEcontrado);
                libroEcontrado.setAutor(autor);
                libroRepository.save(libroEcontrado);
                System.out.println(libroEcontrado);
                System.out.println("\nLibro registrado exitosamente\n");
            }

        } else {
            System.out.println("\nLibro no encontrado, pruebe con otro libro\n");

        }

    }

    private void mostrarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("\nNo hay libros registrados");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("\nNo hay autores registrados");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnUnDeterminadoAño() {
        System.out.println("Ingrese el año en el cual desea buscar autores vivos");

        try {
            int año = entrada.nextInt();
            entrada.nextLine();
            List<Autor> autoresVivos = autorRepository.autoresVivosEnUnDeterminadoAño(año);
            if (autoresVivos.isEmpty()) {
                System.out.println("\nNo hay autores vivos en el año " + año);
            } else {
                autoresVivos.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("\nAño no válido");
            entrada.nextLine();
        }
    }

    private void listarLibrosPorIdiomas() {
        System.out.println("Ingrese el idioma en el cual desea buscar los libros");
        int opcion = -1;
        while (opcion != 0) {
            String menuIdiomas = """
                    1 - Español
                    2 - Ingles
                    3 - Frances
                    4 - Portugues
                    0 - Salir
                    """;
            System.out.println(menuIdiomas);

            try {
                opcion = entrada.nextInt();
            } catch (Exception e) {
                System.out.println("Opción no válida");
                entrada.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    List<Libro> librosEspañol = libroRepository.findByIdioma(Idioma.ES);
                    if (librosEspañol.isEmpty()) {
                        System.out.println("\nNo hay libros en español");
                    } else {
                        librosEspañol.forEach(System.out::println);
                    }
                    break;
                case 2:
                    List<Libro> librosIngles = libroRepository.findByIdioma(Idioma.EN);
                    if (librosIngles.isEmpty()) {
                        System.out.println("\nNo hay libros en inglés");
                    } else {
                        librosIngles.forEach(System.out::println);
                    }
                    break;
                case 3:
                    List<Libro> librosFrances = libroRepository.findByIdioma(Idioma.FR);
                    if (librosFrances.isEmpty()) {
                        System.out.println("\nNo hay libros en francés");
                    } else {
                        librosFrances.forEach(System.out::println);
                    }
                    break;
                case 4:
                    List<Libro> librosPortugues = libroRepository.findByIdioma(Idioma.PT);
                    if (librosPortugues.isEmpty()) {
                        System.out.println("\nNo hay libros en portugués");
                    } else {
                        librosPortugues.forEach(System.out::println);
                    }
                    break;
                default:
                    System.out.println("\nOpción no válida");
                    break;
            }

        }

    }
}