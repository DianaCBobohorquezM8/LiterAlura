package com.alura.literalura.model;
import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;


@Entity
@Table(name ="Libros")
public class Libro {

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;


    @Column(name = "titulo", updatable = true)
    private String titulo;

    @Column(name = "idiomas")
    private String idiomasStr;

    private  Double numeroDescargas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;


    public Libro(){
    }

    public Libro(DatosLibro datosLibros, Autor autor) {
        this.titulo = datosLibros.titulo();
        this.setIdiomas(datosLibros.idiomas());
        this.numeroDescargas = datosLibros.numeroDescargas();
        this.autor = autor;

    }


    public List<String> getIdiomas() {
        return Arrays.asList(idiomasStr.split(","));
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomasStr = String.join(",", idiomas);
    }
    public String getIdiomasStr() { return idiomasStr;
    }


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    // Métodos para 'titulo'

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    // Métodos para 'autor'
    public Autor getAutor() {
        return autor;
    }
    public void setAutor(Autor autor) {
        this.autor = autor;
    }
       // Métodos para 'numeroDescargas'
    public Double getNumeroDescargas() {
        return numeroDescargas;
    }
    public void setNumeroDescargas(Double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString() {
        return
                "Id=" + Id +
                ", titulo='" + titulo + '\'' +
                ", idiomasStr='" + idiomasStr + '\'' +
                ", numeroDescargas=" + numeroDescargas +
                ", autor=" + autor ;
    }
}


