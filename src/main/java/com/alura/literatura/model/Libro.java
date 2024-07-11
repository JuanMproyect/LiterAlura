package com.alura.literatura.model;

import jakarta.persistence.*;


@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;
//    private String autor;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private double numeroDescargas;

    @ManyToOne
    private Autor autor;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        this.idioma = Idioma.fromString(datosLibros.idiomas().toString().split(",")[0]);
        this.numeroDescargas = datosLibros.numeroDescargas();
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


    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public double getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor author) {
        this.autor = author;
    }

    @Override
    public String toString() {
        return "----------LIBRO--------------\n" +
                "titulo: " + titulo + "\n" +
                "idioma: " + idioma + "\n" +
                "autor: " + autor.getNombre() + "\n" +
                "numeroDescargas: " + numeroDescargas + "\n" +
                "---------------------------------";
    }
}