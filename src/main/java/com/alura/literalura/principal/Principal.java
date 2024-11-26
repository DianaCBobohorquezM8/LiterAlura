package com.alura.literalura.principal;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
;


import java.util.*;
import java.util.stream.Collectors;

@Controller
public class    Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();
    private Datos datosBusqueda;


    @Autowired
    private final LibroRepository libroRepository;
    @Autowired
    private final AutorRepository autorRepository;


    @Autowired
    public Principal(LibroRepository libroRepository,AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu(){
        var opcion = -1;
        while (opcion != 0){
            System.out.println("-------------------------------------------");
            System.out.println(" ~_______________________________________~ ");
            System.out.println(" | B | I | E | N | V | E | N | I | D | @ | ");
            System.out.println(" |___|___|___|___|___|___|___|___|___|___| ");
            var menu = """
                     |  ~~ ¡LiterAlura - Challenge Java! ~~  |
                     |___|___|___|___|___|___|___|___|___|___|
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    1 - Buscar libro por titulo
                    2 - Lista de libros registrados
                    3 - Lista de autores registrados
                    4 - Buscar libros nombre del autor
                    5 - Lista de autores vivos en determinado tiempo
                    6 - Lista de libros por idioma
                    7 - Top 3 de Libros mas Descargados
                    8 - Estadística de Descargas
                    0 - Salir
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~
                    Elija una Opción Valida:
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~
                    """;
            System.out.println(menu);
        if (teclado.hasNextInt()){
                opcion = teclado.nextInt();
                teclado.nextLine();
            switch (opcion){
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    librosRegistrados();
                    break;
                case 3:
                    autoresRegistrados();
                    break;
                case 4:
                    buscarPorNombreAutor();
                    break;
                case 5:
                    consultaAutoresVivos();
                  break;
                case 6:
                    consultaLibrosPorIdioma();
                    break;
                case 7:
                    consultaTop3LibrosDescargados();
                    break;
                case 8:
                    estadisticaDeDescargas();
                    break;
                case 0:
                    System.out.println("|__ Cerrando Aplicación... ___|");
                    break;
                default:
                    System.out.println("|*** Opción Invalida ***|");
                }
            } else {
            System.out.println("|*** Entrada inválida. Por favor, ingrese un número entero. ***|");
            teclado.nextLine();
            }
        }
    }

    private List<DatosLibro> getDatosLibro() {
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ","+"));
        System.out.println(json);
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        return datosBusqueda.resultados();
    }


    // Case 1
    private void buscarLibroPorTitulo() {
                System.out.println(""" 
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Ingrese el titulo del libro que desea buscar:
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                """);
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ","%20"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l->l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            DatosLibro datosLibro = libroBuscado.get();
            System.out.println(" |^^^^^^^^^^^^^^^^^|");
            System.out.println(" |___Encontrando___|");
            System.out.println("------  LIBRO  ------");
            System.out.println("Título: " + datosLibro.titulo());

            String autores = datosLibro.autor().stream()
                    .map(DatosAutor::nombre)
                    .collect(Collectors.joining(", "));
            System.out.println("Autor(es): " + autores);

            String idiomas = String.join(", ",datosLibro.idiomas());
            System.out.println("Idioma(s): " + idiomas);
            System.out.println("Número de descargas: " + datosLibro.numeroDescargas());
            System.out.println("-----------------");

            datosLibros.add(datosLibro);// Añadir libro encontrado a la lista

            List<DatosAutor> datosAutores = libroBuscado.get().autor();
            for (DatosAutor datosAutor : datosAutores){
                Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.nombre());
                Autor autor;
                if (autorExistente.isPresent()){
                    autor=autorExistente.get();
                }else {
                    List<Libro>libros = new ArrayList<>();
                    autor = new Autor(datosAutor, libros);
                    autor =autorRepository.save(autor);
                }

                Optional<Libro> libroExistente = libroRepository.findByTituloAndAutorId(libroBuscado.get().titulo(), autor.getId());
                if (libroExistente.isPresent()) {
                    System.out.println("|~~~ El libro ya existe en la base de datos.~~~|");
                } else {
                    Libro nuevoLibro = new Libro(datosLibro, autor);
                    nuevoLibro.setIdiomas(datosLibro.idiomas());
                    autor.getLibros().add(nuevoLibro);
                    libroRepository.save(nuevoLibro);
                    }
                }
            } else { System.out.println("|*** Libro No encontrado. ***|");
        }
    }
    // Case 2
    private void librosRegistrados() {
        System.out.println(""" 
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Libros registrados en la base de datos:
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                """);
        List<Libro> libros = libroRepository.findAllOrderedByTitulo();
        if (libros.isEmpty()) {
            System.out.println("|*** No hay libros registrados. ***|");
            return;
        }
        libros.forEach(libro -> {
            System.out.println("----- LIBRO -----");
            System.out.println("Título: " + libro.getTitulo());

            String autores = libro.getAutor().getNombre();
            System.out.println("Autor: " + autores);

            String idiomas = String.join(", ", libro.getIdiomas());
            System.out.println("Idioma(s): " + idiomas);

            System.out.println("Número de descargas: " + libro.getNumeroDescargas());
            System.out.println("-----------------");
        });
    }

     //Case 3
    private void autoresRegistrados() {
        System.out.println(""" 
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Autores registrados en la base de datos:
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                """);
        List<Autor> autores = autorRepository.findAllOrderedByNombre();
        if (autores.isEmpty()){
            System.out.println("|*** No hay autores registrados. ***|");
            return;
        }
            autores.forEach(autor -> {
            System.out.println("----- AUTOR -----");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Fecha de Nacimiento: " + autor.getFechaDeNacimiento());
            System.out.println("Fecha de Muerte: " + (autor.getFechaDeMuerte()
                                != null ? autor.getFechaDeMuerte() : "N/A"));
            System.out.println("Libros: ");
            List<Libro> libros = autor.getLibros();
            if (libros.isEmpty()) { System.out.println(" No tiene libros registrados.");
            } else {
                libros.forEach(libro -> System.out.println(" - " + libro.getTitulo())); }
            System.out.println("-----------------");
        });
    }

    //case 4
    private void buscarPorNombreAutor() {
        System.out.println("""
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Ingrese el nombre el autor que desea buscar:
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                """);
        String nombreAutor = teclado.nextLine();

        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreAutor.replace(" ", "%20"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        List<DatosLibro> librosPorAutor = datosBusqueda.resultados().stream()
                .filter(l -> l.autor().stream().anyMatch(autor -> autor.nombre()
                        .toUpperCase().contains(nombreAutor.toUpperCase())))
                        .collect(Collectors.toList());

        if (librosPorAutor.isEmpty()) {
            System.out.println("|*** No se encontraron libros para el autor especificado.***|");
            return;
        }
        librosPorAutor.forEach(l -> {
            System.out.println("----- LIBRO -----");
            System.out.println("Título: " + l.titulo());
            System.out.println("Autor: " + l.autor().stream()
                    .map(DatosAutor::nombre)
                    .collect(Collectors.joining(", ")));
            System.out.println("----------------------------------------------");
        });
    }

    //case 5
     private void consultaAutoresVivos() {
        System.out.println("""
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Ingrese el año vivo del autor que desea buscar:
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        """);
         if (teclado.hasNextInt()) {
        int anioBusqueda = teclado.nextInt();
        teclado.nextLine();

         String urlConParametros = URL_BASE + "?author_year_start=" + anioBusqueda + "&author_year_end=" + anioBusqueda;
         var json = consumoAPI.obtenerDatos(urlConParametros);
         var datosBusqueda = conversor.obtenerDatos(json, Datos.class);

         List<DatosAutor> autoresVivos = datosBusqueda.resultados().stream()
                 .flatMap(libro -> libro.autor().stream()) .distinct() .toList();
         if (autoresVivos.isEmpty()) { System.out.println("|*** No se encontraron autores vivos en el año especificado.***|");
         } else { autoresVivos.forEach(autor ->

                 System.out.println(
                         "~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
                         "\n Autor: " + autor.nombre() +
                         "\n Nacimiento: " + autor.fechaDeNacimiento() + "\n " +
                         "Muerte: " + (autor.fechaDeMuerte() != null ? autor.fechaDeMuerte() : "N/A")));
                 }
         }else {System.out.println("|*** Entrada inválida. Por favor, ingrese un número entero válido para el año. ***|");
             teclado.nextLine();
         }
     }

    // Case 6
    private void consultaLibrosPorIdioma() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Ingrese el idioma para buscar los libros:");
        System.out.println("~~~~~~~~~~~~~");
        System.out.println("para español: >   es ");
        System.out.println("para inglés: >    en ");
        System.out.println("para francés: >   fr ");
        System.out.println("para portugués: > pt ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        String idioma = teclado.nextLine();

        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("|*** No se encontraron libros en el idioma especificado. ***|");
        } else { librosPorIdioma.forEach(libro ->
                System.out.println(
                        "------LIBRO------" +
                        "\n Título: " + libro.getTitulo() +
                        "\n Autor: " + libro.getAutor().getNombre() +
                        "\n Idiomas: " + libro.getIdiomasStr())) ;
        }
    }
    // Case 7
    private void consultaTop3LibrosDescargados(){
        System.out.println("""
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Top 3 de Libros mas Descargados
                ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                """);
        Pageable top3 = PageRequest.of(0, 3);
        Page<Libro> top3LibrosPage = libroRepository.findTopLibrosPorDescarga(top3);
        List<Libro> top3Libros = top3LibrosPage.getContent();

        if (top3Libros.isEmpty()){
            System.out.println("No se encontraron libros en la base de datos.");
            return;
        }
        top3Libros.forEach(l->{
           System.out.println("----- LIBRO -----");
            System.out.println("Título: " + l.getTitulo() +
                    "\n Autor: " + l.getAutor().getNombre() +
                    "\n Descargas: " + l.getNumeroDescargas());
            System.out.println("----------------------------");
        });

    }
    // Case 8
    private void estadisticaDeDescargas(){
        System.out.println("""
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Ingrese el título del libro para consultar estadísticas de descargas:
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        """);
        String palabraClave = teclado.nextLine();

         var json = consumoAPI.obtenerDatos(URL_BASE+ "?search=" + palabraClave.replace(" ", "%20"));
         var datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        List<DatosLibro> librosFiltrados = datosBusqueda.resultados().stream()
                .filter(libro->libro.titulo().toUpperCase().contains(palabraClave.toUpperCase()))
                .sorted(Comparator.comparingInt((DatosLibro libro) -> libro.titulo().toUpperCase().indexOf(palabraClave.toUpperCase())))
                .collect(Collectors.toList());
        if (librosFiltrados.isEmpty()) {
            System.out.println("|*** No se encontraron libros con el título especificado. ***|");
            return;
        }

        DoubleSummaryStatistics est = librosFiltrados.stream()
        .filter(libro -> libro.numeroDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibro::numeroDescargas));

        DatosLibro libro = librosFiltrados.get(0);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Título: " + libro.titulo());
        System.out.println("Autor: " + libro.autor().stream().map(DatosAutor::nombre).collect(Collectors.joining(", ")));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("|~~~~~ Datos Estadísticos del Libro ~~~~~|");
        System.out.println("Cantidad Media de descargas: " + est.getAverage());
        System.out.println("Cantidad Maxima de descargas: " +  est.getMax());
        System.out.println("Cantidad Minima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " + est.getCount());
    }




}


