package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import uva.tds.ejecutores.LectorPartidaJSON;
import uva.tds.entidades.Carta;
import uva.tds.entidades.Partida;
import uva.tds.entidades.ResumenPartida;
import uva.tds.interfaces.RepositorioPartida;
import uva.tds.entidades.Jugador;
import uva.tds.entidades.Palo;

/**
 * Tests para la clase ServicioPartida.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServicioPartidaHibernateTest {

    private RepositorioPartida repositorioPartida;
    private ServicioPartida servicioPartida;
    private Partida partidaCompleta;
    private Partida partidaJSON;
    private LectorPartidaJSON lectorJSON;

    private final String ID_PARTIDA = "p-test-01";
    private final LocalDate FECHA_PARTIDA = LocalDate.of(2025, 11, 25);
    private final String JUGADOR1 = "Ana";
    private final String JUGADOR2 = "Luis";

    private static final String CONFIG_FILE = "hibernate-test.cfg.xml";
    // private static final String CONFIG_FILE="hibernate.cfg.xml";

    @BeforeAll
    void setUpAll() {
        // Primero inicializamos Hibernate para que cree el esquema
        repositorioPartida = new RepositorioPartidaHibernate(CONFIG_FILE);
    }

    @BeforeEach
    void setUp() throws Exception {
        var sessionFactory = HibernateUtil.getSessionFactory(CONFIG_FILE);
        var session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            // Truncar tablas en el orden correcto para evitar violaciones de FK
            session.createNativeQuery("TRUNCATE TABLE Mano").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Turno").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Jugada").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Ronda").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Carta").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Jugador").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE ResumenPartida").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Partida").executeUpdate();

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }

        servicioPartida = new ServicioPartida(repositorioPartida);
        partidaCompleta = new Partida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        ResumenPartida resumenCompleto = new ResumenPartida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        resumenCompleto.marcarComoCompleta();
        partidaCompleta.setResumenPartida(resumenCompleto);

        lectorJSON = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        partidaJSON = lectorJSON.obtenerPartida("p-test-json1");


    }

    @AfterAll
    void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    void testObtenerPartidaValidoIdValido() {
        repositorioPartida.guardar(partidaCompleta);
        Partida resultado = servicioPartida.obtenerPartida(ID_PARTIDA);

        assertEquals(ID_PARTIDA, resultado.getId());
    }

    @Test
    void testObtenerPartidaNoValidoIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerPartida(null));
    }

    @Test
    void testObtenerPartidaNoValidoNoEncontrada() {
        assertThrows(IllegalStateException.class, () -> servicioPartida.obtenerPartida("id-inexistente"));
    }

    @Test
    void testGuardarPartidaValidoPartidaCompleta() {
        servicioPartida.guardarPartida(partidaCompleta);
        assertEquals(partidaCompleta, servicioPartida.obtenerPartida(partidaCompleta.getId()));
    }

    @Test
    void testGuardarPartidaNoValidoPartidaNula() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.guardarPartida(null));
    }

    @Test
    void testGuardarPartidaNoValidoPartidaIncompleta() {
        Partida partidaIncompleta = new Partida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        // No llamamos a establecerResultados, por lo que isCompleta() es false.
        assertThrows(IllegalStateException.class, () -> servicioPartida.guardarPartida(partidaIncompleta));
    }

    @Test
    void testActualizarPartidaValidoPartidaValida() {
        repositorioPartida.guardar(partidaCompleta);
        LocalDate fechaAct = LocalDate.of(2025, 12, 15);
        Partida partidaActualizada = new Partida(ID_PARTIDA, fechaAct, JUGADOR1, JUGADOR2);
        ResumenPartida resumenActualizado = new ResumenPartida(ID_PARTIDA, fechaAct, JUGADOR1, JUGADOR2);
        resumenActualizado.marcarComoCompleta();
        partidaActualizada.setResumenPartida(resumenActualizado);

        servicioPartida.actualizarPartida(partidaActualizada);
        assertEquals(partidaActualizada, servicioPartida.obtenerPartida(ID_PARTIDA));
        
    }

    @Test
    void testActualizarPartidaNoValidoNoEstaGuardada() {
        assertThrows(IllegalStateException.class, () -> servicioPartida.actualizarPartida(partidaCompleta));
    }

    @Test
    void testActualizarPartidaPartidaNula() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.actualizarPartida(null));
    }

    @Test
    void testEliminarPartidaValidoIdValido() {
        servicioPartida.guardarPartida(partidaCompleta);
        assertEquals(partidaCompleta, servicioPartida.obtenerPartida(ID_PARTIDA));
        servicioPartida.eliminarPartida(ID_PARTIDA);

        assertThrows(IllegalStateException.class, () -> servicioPartida.obtenerPartida(ID_PARTIDA));

    }

    @Test
    void testEliminarPartidaNoValidoIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.eliminarPartida(null));    }

    @Test
    void testEliminarPartidaNoValidoIdVacio() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.eliminarPartida(""));
    }

    @Test
    void testEliminarPartidaNoValidoNoExiste() {
        repositorioPartida.eliminar(ID_PARTIDA);

        assertThrows(IllegalStateException.class, () -> servicioPartida.eliminarPartida(ID_PARTIDA));
    }

    @Test
    void testObtenerEstadisticasEntreFechasValido() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);
        LocalDate fechaPartida2 = LocalDate.of(2025, 6, 15);

        Partida partida2 = new Partida("p-2", fechaPartida2, "Luis", "Maria");
        ResumenPartida resumen2 = new ResumenPartida("p-2", fechaPartida2, "Luis", "Maria");
        resumen2.marcarComoCompleta();
        partida2.setResumenPartida(resumen2);

        // Lista simulada
        List<Partida> partidasEntreFechas = new ArrayList<>();
        partidasEntreFechas.add(partidaCompleta);
        partidasEntreFechas.add(partida2);

        servicioPartida.guardarPartida(partidaCompleta);
        servicioPartida.guardarPartida(partida2);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin);

        assertEquals(2, resultado.size());
        assertEquals(2, resultado.get(0));
        assertEquals(3, resultado.get(1));
    }

    @Tag("Cobertura")
    @Test
    void testObtenerEstadisticasEntreFechasRepoDevuelveNull() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin);

        // Debe devolver [0, 0] (0 partidas, 0 jugadores)
        assertEquals(2, resultado.size());
        assertEquals(0, resultado.get(0));
        assertEquals(0, resultado.get(1));
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaInicioNula() {
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(null, fechaFin));
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaFinNula() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, null));
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaInicioPosteriorFechaFin() {
        LocalDate fechaInicio = LocalDate.of(2025, 12, 31);
        LocalDate fechaFin = LocalDate.of(2025, 1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin));
    }

    @Test
    void testValidoObtenerEstadisticasJugador() {
        String nombre = "Maria";
        LocalDate fecha1 = LocalDate.of(2025, 7, 9);
        Partida partida2 = new Partida("p-2", fecha1, "Luis", "Maria");
        ResumenPartida resumen2 = new ResumenPartida("p-2", fecha1, "Luis", "Maria");
        resumen2.marcarComoCompleta();

        Jugador j1 = new Jugador("Luis");
        Jugador j2 = new Jugador("Maria");
        j2.agregarCartaACartas(new Carta(Palo.OROS, 7)); // Guindis
        resumen2.establecerResultados(j1, j2, 2, 3);
        partida2.setResumenPartida(resumen2);
        servicioPartida.guardarPartida(partida2);

        LocalDate fecha2 = LocalDate.of(2025, 1, 28);
        Partida partida3 = new Partida("p-3", fecha2, "Maria", "Diego");
        ResumenPartida resumen3 = new ResumenPartida("p-3", fecha2, "Maria", "Diego");
        resumen3.marcarComoCompleta();

        Jugador j3 = new Jugador("Maria");
        Jugador j4 = new Jugador("Diego");
        resumen3.establecerResultados(j3, j4, 2, 4);
        partida3.setResumenPartida(resumen3);
        servicioPartida.guardarPartida(partida3);

        LocalDate fecha3 = LocalDate.of(2025, 7, 28);
        Partida partida4 = new Partida("p-4", fecha3, "Maria", "Ana");
        ResumenPartida resumen4 = new ResumenPartida("p-4", fecha3, "Maria", "Ana");
        resumen4.marcarComoCompleta();

        Jugador j5 = new Jugador("Maria");
        Jugador j6 = new Jugador("Ana");
        j5.agregarCartaACartas(new Carta(Palo.OROS, 7));
        resumen4.establecerResultados(j5, j6, 5, 3);
        partida4.setResumenPartida(resumen4);
        servicioPartida.guardarPartida(partida4);

        List<Partida> partidasJugador = new ArrayList<>();
        partidasJugador.add(partida2);
        partidasJugador.add(partida3);
        partidasJugador.add(partida4);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasJugador(nombre);

        assertEquals(4, resultado.size());
        assertEquals(2, resultado.get(0));// ganadas
        assertEquals(1, resultado.get(1));// perdidas
        assertEquals(3, resultado.get(2));// total
        assertEquals(2, resultado.get(3));// guindis
    }

    @Test
    @Tag("Cobertura")
    void testValidoObtenerEstadisticasJugadorSinPartidas() {
        String nombre = "Maria";

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasJugador(nombre);

        assertEquals(4, resultado.size());
        assertEquals(0, resultado.get(0));// ganadas
        assertEquals(0, resultado.get(1));// perdidas
        assertEquals(0, resultado.get(2));// total
        assertEquals(0, resultado.get(3));// guindis
    }

    @Test
    void testNoValidoObtenerEstadisticasJugadorNombreNulo() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerEstadisticasJugador(null));
    }

    @Test
    void testNoValidoObtenerEstadisticasJugadorNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerEstadisticasJugador(""));
    }

    @Test
    void testResumenPartidaValidoJSON1() {
        servicioPartida.guardarPartida(partidaCompleta);
        servicioPartida.obtenerPartida("p-test-json1");
        ArrayList<ArrayList<Integer>> resumen = servicioPartida.resumenPartida("p-test-json1");
        ArrayList<Integer> esperadoJugador1 = new ArrayList<>(List.of(0, 13, 3, 1, 0, 0));
        ArrayList<Integer> esperadoJugador2 = new ArrayList<>(List.of(1, 27, 7, 3, 1, 5));

        assertEquals(2, resumen.size());
        assertEquals(esperadoJugador1, resumen.get(0));
        assertEquals(esperadoJugador2, resumen.get(1));
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoResumenPartidaNula() {
        String idPartida = "partida-1";

        assertThrows(IllegalArgumentException.class, () -> {
            servicioPartida.resumenPartida(idPartida);
        });
    }

    @Test
    void testNoValidoResumenPartidaIdNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.resumenPartida(null));
    }

    @Test
    void testNoValidoResumenPartidaIdVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.resumenPartida(""));
    }

    @Test
    void testValidoObtenerTop10() {
        Partida partida1 = new Partida("p-1", LocalDate.of(2025, 5, 1), "Ana", "Luis");
        ResumenPartida resumen1 = new ResumenPartida("p-1", LocalDate.of(2025, 5, 1), "Ana", "Luis");
        resumen1.marcarComoCompleta();
        resumen1.establecerResultados(new Jugador("Ana"), new Jugador("Luis"), 10, 5);
        partida1.setResumenPartida(resumen1);
        servicioPartida.guardarPartida(partida1);

        Partida partida2 = new Partida("p-3", LocalDate.of(2025, 7, 1), "Carlos", "Ana");
        ResumenPartida resumen2 = new ResumenPartida("p-3", LocalDate.of(2025, 7, 1), "Carlos", "Ana");
        resumen2.marcarComoCompleta();
        resumen2.establecerResultados(new Jugador("Carlos"), new Jugador("Ana"), 2, 3);
        partida2.setResumenPartida(resumen2);
        servicioPartida.guardarPartida(partida2);

        Partida partida3 = new Partida("p-4", LocalDate.of(2025, 8, 1), "Luis", "Maria");
        ResumenPartida resumen3 = new ResumenPartida("p-4", LocalDate.of(2025, 8, 1), "Luis", "Maria");
        resumen3.marcarComoCompleta();
        resumen3.establecerResultados(new Jugador("Luis"), new Jugador("Maria"), 12, 0);
        partida3.setResumenPartida(resumen3);
        servicioPartida.guardarPartida(partida3);

        Partida partida4 = new Partida("p-5", LocalDate.of(2025, 9, 1), "Luis", "Carlos");
        ResumenPartida resumen4 = new ResumenPartida("p-5", LocalDate.of(2025, 9, 1), "Luis", "Carlos");
        resumen4.marcarComoCompleta();
        resumen4.establecerResultados(new Jugador("Luis"), new Jugador("Carlos"), 9, 8);
        partida4.setResumenPartida(resumen4);
        servicioPartida.guardarPartida(partida4);

        Partida partida5 = new Partida("p-2", LocalDate.of(2025, 6, 1), "Maria", "Diego");
        ResumenPartida resumen5 = new ResumenPartida("p-2", LocalDate.of(2025, 6, 1), "Maria", "Diego");
        resumen5.marcarComoCompleta();
        resumen5.establecerResultados(new Jugador("Maria"), new Jugador("Diego"), 8, 7);
        partida5.setResumenPartida(resumen5);
        servicioPartida.guardarPartida(partida5);

        Partida partida6 = new Partida("p-6", LocalDate.of(2025, 10, 1), "Pedro", "Sofia");
        ResumenPartida resumen6 = new ResumenPartida("p-6", LocalDate.of(2025, 10, 1), "Pedro", "Sofia");
        resumen6.marcarComoCompleta();
        resumen6.establecerResultados(new Jugador("Pedro"), new Jugador("Sofia"), 5, 5); // EMPATE
        partida6.setResumenPartida(resumen6);
        servicioPartida.guardarPartida(partida6);

        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();

        assertEquals(7, top10.size());
        assertEquals("Ana", top10.get(0).getNombre());
        assertEquals("Luis", top10.get(1).getNombre());
        assertEquals("Maria", top10.get(2).getNombre());
        assertEquals("Carlos", top10.get(3).getNombre());
        assertEquals("Diego", top10.get(4).getNombre());
        assertEquals("Pedro", top10.get(5).getNombre());
        assertEquals("Sofia", top10.get(6).getNombre());
    }

    @Tag("Cobertura")
    @Test
    void testValidoObtenerTop10ConPartidasVacio() {
        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();
        assertEquals(0, top10.size());
    }

    @Tag("Cobertura")
    @Test
    void testValidoObtenerTop10ConComparacionAlfabetica() {
        // Partida 1: Ana gana
        Partida partida1 = new Partida("p-1", LocalDate.of(2025, 5, 1), "Ana", "Pedro");
        ResumenPartida resumen1 = new ResumenPartida("p-1", LocalDate.of(2025, 5, 1), "Ana", "Pedro");
        resumen1.marcarComoCompleta();
        resumen1.establecerResultados(new Jugador("Ana"), new Jugador("Pedro"), 10, 5);
        partida1.setResumenPartida(resumen1);
        servicioPartida.guardarPartida(partida1);

        // Partida 2: Carlos gana
        Partida partida2 = new Partida("p-2", LocalDate.of(2025, 6, 1), "Carlos", "Sofia");
        ResumenPartida resumen2 = new ResumenPartida("p-2", LocalDate.of(2025, 6, 1), "Carlos", "Sofia");
        resumen2.marcarComoCompleta();
        resumen2.establecerResultados(new Jugador("Carlos"), new Jugador("Sofia"), 8, 3);
        partida2.setResumenPartida(resumen2);
        servicioPartida.guardarPartida(partida2);

        // Partida 3: Empate
        Partida partida3 = new Partida("p-3", LocalDate.of(2025, 7, 1), "Pedro", "Sofia");
        ResumenPartida resumen3 = new ResumenPartida("p-3", LocalDate.of(2025, 7, 1), "Pedro", "Sofia");
        resumen3.marcarComoCompleta();
        resumen3.establecerResultados(new Jugador("Pedro"), new Jugador("Sofia"), 5, 5);
        partida3.setResumenPartida(resumen3);
        servicioPartida.guardarPartida(partida3);

        // Partida 4: Beatriz gana
        Partida partida4 = new Partida("p-4", LocalDate.of(2025, 8, 1), "Beatriz", "Diego");
        ResumenPartida resumen4 = new ResumenPartida("p-4", LocalDate.of(2025, 8, 1), "Beatriz", "Diego");
        resumen4.marcarComoCompleta();
        resumen4.establecerResultados(new Jugador("Beatriz"), new Jugador("Diego"), 7, 6);
        partida4.setResumenPartida(resumen4);
        servicioPartida.guardarPartida(partida4);

        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();

        assertEquals(6, top10.size());
        // Jugadores con 1 victoria: Ana, Beatriz, Carlos (orden alfabético)
        assertEquals("Ana", top10.get(0).getNombre());
        assertEquals("Beatriz", top10.get(1).getNombre());
        assertEquals("Carlos", top10.get(2).getNombre());
        // Jugadores con 0 victorias: Diego, Pedro, Sofia (orden alfabético)
        assertEquals("Diego", top10.get(3).getNombre());
        assertEquals("Pedro", top10.get(4).getNombre());
        assertEquals("Sofia", top10.get(5).getNombre());
    }

    /**
     * Construye el estado final del jugador Ana (13 cartas, 3 oros, 1 siete, 0
     * escobas).
     */
    private Jugador construirAnaFinal(String nombre) {
        Jugador ana = new Jugador(nombre);

        // 3 Oros (no incluyen el 7)
        ana.agregarCartaACartas(new Carta(Palo.OROS, 1));
        ana.agregarCartaACartas(new Carta(Palo.OROS, 2));
        ana.agregarCartaACartas(new Carta(Palo.OROS, 3));

        // 1 Siete (el 7 de copas)
        ana.agregarCartaACartas(new Carta(Palo.COPAS, 7));

        // Rellenar hasta 13 cartas (4 ya añadidas, faltan 9). Usamos un set de índices
        // que no incluye el 7 para evitar conflictos de conteo de sietes.
        int[] NO_SIETE_INDICES = { 1, 2, 3, 4, 5, 6, 10, 11, 12 }; // 9 valores
        for (int i = 0; i < 9; i++) {
            ana.agregarCartaACartas(new Carta(Palo.BASTOS, NO_SIETE_INDICES[i % NO_SIETE_INDICES.length]));
        }
        return ana;
    }

    /**
     * Construye el estado final del jugador Luis (27 cartas, 7 oros, 3 sietes, 1
     * escoba, con 7 de Oros).
     */
    private Jugador construirLuisFinal(String nombre) {
        Jugador luis = new Jugador(nombre);
        luis.sumarEscoba(); // 1 escoba

        // 7 Oros
        luis.agregarCartaACartas(new Carta(Palo.OROS, 7)); // Guindis
        luis.agregarCartaACartas(new Carta(Palo.OROS, 1));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 2));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 4));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 5));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 6));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 10)); // 7 oros totales

        // Otros 2 sietes (para 3 sietes total)
        luis.agregarCartaACartas(new Carta(Palo.ESPADAS, 7));
        luis.agregarCartaACartas(new Carta(Palo.BASTOS, 7));

        // Rellenar hasta 27 cartas (9 cartas ya añadidas, faltan 18).
        // Usamos un set de índices que NO introduce más oros ni sietes.
        int[] NO_OROS_NO_SIETES_INDICES = { 1, 2, 3, 4, 5, 6, 10, 11, 12 }; // 9 valores
        for (int i = 0; i < 18; i++) {
            luis.agregarCartaACartas(
                    new Carta(Palo.COPAS, NO_OROS_NO_SIETES_INDICES[i % NO_OROS_NO_SIETES_INDICES.length]));
        }
        return luis;
    }

}