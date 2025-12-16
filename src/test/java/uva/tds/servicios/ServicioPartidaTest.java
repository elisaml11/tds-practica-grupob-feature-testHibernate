package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
public class ServicioPartidaTest {

    @TestSubject
    private ServicioPartida servicioPartida;

    @Mock
    private RepositorioPartida repositorioPartida;

    private Partida partidaCompleta;
    private final String ID_PARTIDA = "p-test-01";
    private final LocalDate FECHA_PARTIDA = LocalDate.of(2025, 11, 25);
    private final String JUGADOR1 = "Ana";
    private final String JUGADOR2 = "Luis";

    @BeforeEach
    void setUp() {
        repositorioPartida = EasyMock.mock(RepositorioPartida.class);
        servicioPartida = new ServicioPartida(repositorioPartida);
        partidaCompleta = new Partida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        ResumenPartida resumenCompleto = new ResumenPartida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        resumenCompleto.marcarComoCompleta();
        partidaCompleta.setResumenPartida(resumenCompleto);
    }

    @Test
    public void testConstructorValido() {
        ServicioPartida servicio = new ServicioPartida(repositorioPartida);
        assertEquals(repositorioPartida, servicio.getRepositorioPartida());
    }

    @Test
    public void testConstructorNoValidoRepositorioNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ServicioPartida(null);
        });
    }

    @Test
    void testObtenerPartidaValidoIdValido() {
        expect(repositorioPartida.cargar(ID_PARTIDA)).andReturn(partidaCompleta);
        replay(repositorioPartida);

        Partida resultado = servicioPartida.obtenerPartida(ID_PARTIDA);

        assertEquals(ID_PARTIDA, resultado.getId());
        verify(repositorioPartida);
    }

    @Test
    void testObtenerPartidaNoValidoIdNulo() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerPartida(null));
        verify(repositorioPartida);
    }

    @Test
    void testObtenerPartidaNoValidoNoEncontrada() {
        expect(repositorioPartida.cargar(ID_PARTIDA)).andThrow(new IllegalStateException("Partida no encontrada"));
        replay(repositorioPartida);

        assertThrows(IllegalStateException.class, () -> servicioPartida.obtenerPartida(ID_PARTIDA));
        verify(repositorioPartida);
    }

    @Test
    void testGuardarPartidaValidoPartidaCompleta() {
        repositorioPartida.guardar(partidaCompleta);
        EasyMock.expectLastCall();
        replay(repositorioPartida);
        servicioPartida.guardarPartida(partidaCompleta);
        verify(repositorioPartida);
    }

    @Test
    void testGuardarPartidaNoValidoPartidaNula() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.guardarPartida(null));
        verify(repositorioPartida);
    }

    @Test
    void testGuardarPartidaNoValidoPartidaIncompleta() {
        Partida partidaIncompleta = new Partida(ID_PARTIDA, FECHA_PARTIDA, JUGADOR1, JUGADOR2);
        // No llamamos a establecerResultados, por lo que isCompleta() es false.

        replay(repositorioPartida);
        assertThrows(IllegalStateException.class, () -> servicioPartida.guardarPartida(partidaIncompleta));
        verify(repositorioPartida);
    }

    @Test
    void testActualizarPartidaValidoPartidaValida() {
        repositorioPartida.actualizar(partidaCompleta);
        EasyMock.expectLastCall();
        replay(repositorioPartida);
        servicioPartida.actualizarPartida(partidaCompleta);
        verify(repositorioPartida);
    }

    @Test
    void testActualizarPartidaNoValidoNoEstaGuardada() {
        repositorioPartida.actualizar(partidaCompleta);
        expectLastCall().andThrow(new IllegalStateException("Partida a actualizar no existe"));
        replay(repositorioPartida);

        assertThrows(IllegalStateException.class, () -> servicioPartida.actualizarPartida(partidaCompleta));
        verify(repositorioPartida);
    }

    @Test
    void testActualizarPartidaPartidaNula() {
        replay(repositorioPartida);

        assertThrows(IllegalArgumentException.class, () -> servicioPartida.actualizarPartida(null),
                "Debe lanzar IllegalArgumentException si la partida es nula");

        verify(repositorioPartida);
    }

    @Test
    void testEliminarPartidaValidoIdValido() {
        repositorioPartida.eliminar(ID_PARTIDA);
        EasyMock.expectLastCall();
        replay(repositorioPartida);
        servicioPartida.eliminarPartida(ID_PARTIDA);
        verify(repositorioPartida);
    }

    @Test
    void testEliminarPartidaNoValidoIdNulo() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.eliminarPartida(null));
        verify(repositorioPartida);
    }

    @Test
    void testEliminarPartidaNoValidoIdVacio() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.eliminarPartida(""));
        verify(repositorioPartida);
    }

    @Test
    void testEliminarPartidaNoValidoNoExiste() {
        repositorioPartida.eliminar(ID_PARTIDA);
        expectLastCall().andThrow(new IllegalStateException("Partida a eliminar no existe"));
        replay(repositorioPartida);

        assertThrows(IllegalStateException.class, () -> servicioPartida.eliminarPartida(ID_PARTIDA));
        verify(repositorioPartida);
    }

    @Test
    public void testValidarPartidaValidoCreaResumenSiNoExiste() throws IOException {
        // Cargamos la partida desde el JSON (no contiene ResumenPartida en el objeto)
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partida = lector.obtenerPartida();

        assertNull(partida.getResumenPartida(), "Antes de validar, la partida no debe contener resumen");

        boolean valido = servicioPartida.validarPartida(partida);

        assertTrue(valido, "La reproducción debe considerarse válida y generar el resumen");
        ResumenPartida resumen = partida.getResumenPartida();
        // Comprobar algunos campos esperados según el JSON de referencia
        assertEquals(0, resumen.getPuntosJugador1(), "Puntos esperados jugador1 (Ana)");
        assertEquals(5, resumen.getPuntosJugador2(), "Puntos esperados jugador2 (Luis)");
        assertEquals(3, resumen.getOrosJugador1(), "Oros Ana");
        assertEquals(7, resumen.getOrosJugador2(), "Oros Luis");
        assertEquals(13, resumen.getCartasCapturadasJugador1(), "Cartas capturadas Ana");
        assertEquals(27, resumen.getCartasCapturadasJugador2(), "Cartas capturadas Luis");
    }

    @Test
    void testValidarPartidaValidoConDatosJSON() {
        Partida partida = crearPartidaDeDatos1();

        ResumenPartida esperado = partida.getResumenPartida();
        assertEquals(0, esperado.getPuntosJugador1());
        assertEquals(5, esperado.getPuntosJugador2());

        assertTrue(servicioPartida.validarPartida(partida),
                "La partida ejecutada debería ser consistente con el resumen esperado.");
    }

    @Test
    void testValidarPartidaValidoCoincideConResumen() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partidaSinMeta = lector.obtenerPartida();

        Partida partida = new Partida("p-1", LocalDate.now(),
                partidaSinMeta.getNombres().get(0), partidaSinMeta.getNombres().get(1));

        partida.añadirMesaInicial(partidaSinMeta.getMesaInicial());
        partida.añadirManoJugador1(partidaSinMeta.getManosJugador1());
        partida.añadirManoJugador2(partidaSinMeta.getManosJugador2());
        partida.anadirRondas(partidaSinMeta.getRondas());

        // --- ESTADO FINAL ANA (13 cartas, 3 oros, 1 siete) ---
        Jugador anaFinal = construirAnaFinal("Ana");

        // --- ESTADO FINAL LUIS (27 cartas, 7 oros, 3 sietes, 1 escoba) ---
        Jugador luisFinal = construirLuisFinal("Luis");

        // establecer resultados (crea ResumenPartida interno en la Partida)
        partida.establecerResultados(anaFinal, luisFinal, /* puntosAna */ 0, /* puntosLuis */ 5);

        assertTrue(servicioPartida.validarPartida(partida));
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaValidoDetectaDiferenciaPuntosJ1() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partidaSinMeta = lector.obtenerPartida();

        Partida partida = new Partida("p-2", LocalDate.now(),
                partidaSinMeta.getNombres().get(0), partidaSinMeta.getNombres().get(1));
        partida.añadirMesaInicial(partidaSinMeta.getMesaInicial());
        partida.añadirManoJugador1(partidaSinMeta.getManosJugador1());
        partida.añadirManoJugador2(partidaSinMeta.getManosJugador2());
        partida.anadirRondas(partidaSinMeta.getRondas());
        Jugador anaFinal = new Jugador("Ana");
        agregarCartasCiclicas(anaFinal, Palo.COPAS, 13);

        Jugador luisFinal = new Jugador("Luis");
        agregarCartasCiclicas(luisFinal, Palo.BASTOS, 26);
        luisFinal.agregarCartaACartas(new Carta(Palo.OROS, 7));
        luisFinal.sumarEscoba();

        partida.establecerResultados(anaFinal, luisFinal, 1, 99);

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar al detectar puntos distintos a los calculados");
    }

    @Test
    void testValidarPartidaValidoDetectaDiferenciaPuntosJ2() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partidaSinMeta = lector.obtenerPartida();

        Partida partida = new Partida("p-2", LocalDate.now(),
                partidaSinMeta.getNombres().get(0), partidaSinMeta.getNombres().get(1));
        partida.añadirMesaInicial(partidaSinMeta.getMesaInicial());
        partida.añadirManoJugador1(partidaSinMeta.getManosJugador1());
        partida.añadirManoJugador2(partidaSinMeta.getManosJugador2());
        partida.anadirRondas(partidaSinMeta.getRondas());
        // Crear jugadores con cartas válidas (no deben coincidir con la ejecución)
        Jugador anaFinal = new Jugador("Ana");
        agregarCartasCiclicas(anaFinal, Palo.COPAS, 13);

        Jugador luisFinal = new Jugador("Luis");
        agregarCartasCiclicas(luisFinal, Palo.BASTOS, 26);
        luisFinal.agregarCartaACartas(new Carta(Palo.OROS, 7)); // aseguramos guindis
        luisFinal.sumarEscoba();

        // establecer resultados con puntos erróneos (por ejemplo 99 para Luis)
        // La reproducción real da (0, 5), esto debe fallar con (0, 99)
        partida.establecerResultados(anaFinal, luisFinal, 0, 99);

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar al detectar puntos distintos a los calculados");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaEscobasJ1() {
        Partida partida = crearPartidaDeDatos1();

        ResumenPartida esperado = partida.getResumenPartida();
        // Construimos jugadores esperados alterando escobas
        Jugador anaAlterada = construirAnaFinal(esperado.getJugador1());
        anaAlterada.sumarEscoba(); // añadir 1 escoba extra
        Jugador luis = construirLuisFinal(esperado.getJugador2());

        partida.establecerResultados(
                anaAlterada, luis,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si las escobas esperadas no coinciden con las calculadas");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaEscobasJ2() {
        Partida partida = crearPartidaDeDatos1();

        ResumenPartida esperado = partida.getResumenPartida();
        // Construimos jugadores esperados alterando escobas
        Jugador ana = construirAnaFinal(esperado.getJugador1());
        Jugador luisAlterado = construirLuisFinal(esperado.getJugador2());
        luisAlterado.sumarEscoba(); // añadir 1 escoba extra

        partida.establecerResultados(
                ana, luisAlterado,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si las escobas esperadas no coinciden con las calculadas");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaOrosJ1() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador anaAlterada = construirAnaFinal(esperado.getJugador1());
        anaAlterada.agregarCartaACartas(new Carta(Palo.OROS, 1));

        Jugador luis = construirLuisFinal(esperado.getJugador2());

        partida.establecerResultados(
                anaAlterada, luis,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si los oros esperados no coinciden con los calculados");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaOrosJ2() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador luisAlterado = construirLuisFinal(esperado.getJugador2());
        luisAlterado.agregarCartaACartas(new Carta(Palo.OROS, 1));

        Jugador ana = construirAnaFinal(esperado.getJugador1());

        partida.establecerResultados(
                ana, luisAlterado,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si los oros esperados no coinciden con los calculados");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaSietesJ2() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador ana = construirAnaFinal(esperado.getJugador1());
        Jugador luisAlterado = construirLuisFinal(esperado.getJugador2());
        luisAlterado.agregarCartaACartas(new Carta(Palo.COPAS, 7));

        partida.establecerResultados(
                ana, luisAlterado,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si los sietes esperados no coinciden con los calculados");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaSietesJ1() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador anaAlterado = construirAnaFinal(esperado.getJugador1());
        Jugador luis = construirLuisFinal(esperado.getJugador2());
        anaAlterado.agregarCartaACartas(new Carta(Palo.COPAS, 7));

        partida.establecerResultados(
                anaAlterado, luis,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si los sietes esperados no coinciden con los calculados");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaGuindisJ1() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador luisAlterado = construirLuisFinal(esperado.getJugador1());
        luisAlterado.agregarCartaACartas(new Carta(Palo.OROS, 7));

        Jugador ana = construirAnaFinal(esperado.getJugador2());

        partida.establecerResultados(
                ana, luisAlterado,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si la bandera de guindis difiere entre esperado y calculado");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaGuindisJ2() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador anaAlterada = construirAnaFinal(esperado.getJugador1());
        anaAlterada.agregarCartaACartas(new Carta(Palo.OROS, 7));

        Jugador luis = construirLuisFinal(esperado.getJugador2());

        partida.establecerResultados(
                anaAlterada, luis,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si la bandera de guindis difiere entre esperado y calculado");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaCartasCapturadas() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();

        Jugador ana = construirAnaFinal(esperado.getJugador1());
        Jugador luisAlterado = construirLuisFinal(esperado.getJugador2());
        luisAlterado.agregarCartaACartas(new Carta(Palo.COPAS, 1));
        luisAlterado.agregarCartaACartas(new Carta(Palo.COPAS, 2));

        partida.establecerResultados(
                ana, luisAlterado,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si el número de cartas capturadas difiere entre esperado y calculado");
    }

    @Tag("Cobertura")
    @Test
    void testValidarPartidaDetectaDiferenciaCartasCapturadasJ2() {
        Partida partida = crearPartidaDeDatos1();
        ResumenPartida esperado = partida.getResumenPartida();
        Jugador luis = construirLuisFinal(esperado.getJugador1());
        Jugador anaAlterada = construirAnaFinal(esperado.getJugador2());
        anaAlterada.agregarCartaACartas(new Carta(Palo.COPAS, 1));
        anaAlterada.agregarCartaACartas(new Carta(Palo.COPAS, 2));

        partida.establecerResultados(
                anaAlterada, luis,
                esperado.getPuntosJugador1(),
                esperado.getPuntosJugador2());

        assertFalse(servicioPartida.validarPartida(partida),
                "La validación debe fallar si el número de cartas capturadas difiere entre esperado y calculado");
    }

    @Test
    void testCalcularPuntosJugadorValido() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partida = lector.obtenerPartida();

        int puntosLuis = servicioPartida.calcularPuntos(partida, "Luis");
        int puntosAna = servicioPartida.calcularPuntos(partida, "Ana");

        assertEquals(5, puntosLuis);
        assertEquals(0, puntosAna);
    }

    @Test
    void testValidarPartidaNoValidoNull() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.validarPartida(null));
    }

    @Test
    void testCalcularPuntosNoValidoNullPartida() {
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.calcularPuntos(null, "Ana"));
    }

    @Test
    void testCalcularPuntosNoValidoNullJugador() {
        Partida p = new Partida();
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.calcularPuntos(p, null));
    }

    @Test
    void testCalcularPuntosEmptyJugadorNoValido() {
        Partida p = new Partida();
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.calcularPuntos(p, ""));
    }

    @Test
    void testCalcularPuntosJugadorNoParticipaNoValido() throws IOException {
        // jugador que no participa en JSON
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partida = lector.obtenerPartida();
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.calcularPuntos(partida, "Pepito"));
    }

    @Test
    void testValidarPartidaNoValidoNoCompleta() {
        Partida partidaIncompleta = new Partida();
        assertThrows(NullPointerException.class, () -> servicioPartida.validarPartida(partidaIncompleta));
    }

    @Test
    void testObtenerEstadisticasEntreFechasValido() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        String inicio = fechaInicio.toString();
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);
        String fin = fechaFin.toString();
        LocalDate fechaPartida2 = LocalDate.of(2025, 6, 15);

        Partida partida2 = new Partida("p-2", fechaPartida2, "Luis", "Maria");

        // Lista simulada
        List<Partida> partidasEntreFechas = new ArrayList<>();
        partidasEntreFechas.add(partidaCompleta);
        partidasEntreFechas.add(partida2);

        expect(repositorioPartida.obtenerPartidasPorFecha(inicio, fin)).andReturn(partidasEntreFechas);
        replay(repositorioPartida);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin);

        assertEquals(2, resultado.size());
        assertEquals(2, resultado.get(0));
        assertEquals(3, resultado.get(1));
        verify(repositorioPartida);
    }

    @Tag("Cobertura")
    @Test
    void testObtenerEstadisticasEntreFechasRepoDevuelveNull() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);
        String inicio = fechaInicio.toString();
        String fin = fechaFin.toString();

        // El repositorio devuelve null -> el método debe crear una lista vacía
        // internamente
        expect(repositorioPartida.obtenerPartidasPorFecha(inicio, fin)).andReturn(null);
        replay(repositorioPartida);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin);

        // Debe devolver [0, 0] (0 partidas, 0 jugadores)
        assertEquals(2, resultado.size());
        assertEquals(0, resultado.get(0));
        assertEquals(0, resultado.get(1));

        verify(repositorioPartida);
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaInicioNula() {
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);

        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(null, fechaFin));
        verify(repositorioPartida);
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaFinNula() {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);

        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, null));
        verify(repositorioPartida);
    }

    @Test
    void testNoValidoObtenerEstadisticasEntreFechasFechaInicioPosteriorFechaFin() {
        LocalDate fechaInicio = LocalDate.of(2025, 12, 31);
        LocalDate fechaFin = LocalDate.of(2025, 1, 1);

        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class,
                () -> servicioPartida.obtenerEstadisticasEntreFechas(fechaInicio, fechaFin));
        verify(repositorioPartida);
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

        LocalDate fecha2 = LocalDate.of(2025, 1, 28);
        Partida partida3 = new Partida("p-3", fecha2, "Maria", "Diego");
        ResumenPartida resumen3 = new ResumenPartida("p-3", fecha2, "Maria", "Diego");
        resumen3.marcarComoCompleta();

        Jugador j3 = new Jugador("Maria");
        Jugador j4 = new Jugador("Diego");
        resumen3.establecerResultados(j3, j4, 2, 4);
        partida3.setResumenPartida(resumen3);

        LocalDate fecha3 = LocalDate.of(2025, 7, 28);
        Partida partida4 = new Partida("p-4", fecha3, "Maria", "Ana");
        ResumenPartida resumen4 = new ResumenPartida("p-4", fecha3, "Maria", "Ana");
        resumen4.marcarComoCompleta();

        Jugador j5 = new Jugador("Maria");
        Jugador j6 = new Jugador("Ana");
        j5.agregarCartaACartas(new Carta(Palo.OROS, 7));
        resumen4.establecerResultados(j5, j6, 5, 3);
        partida4.setResumenPartida(resumen4);

        List<Partida> partidasJugador = new ArrayList<>();
        partidasJugador.add(partida2);
        partidasJugador.add(partida3);
        partidasJugador.add(partida4);

        expect(repositorioPartida.obtenerPartidasPorJugador(nombre)).andReturn(partidasJugador);
        replay(repositorioPartida);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasJugador(nombre);

        assertEquals(4, resultado.size());
        assertEquals(2, resultado.get(0));// ganadas
        assertEquals(1, resultado.get(1));// perdidas
        assertEquals(3, resultado.get(2));// total
        assertEquals(2, resultado.get(3));// guindis
        verify(repositorioPartida);
    }

    @Test
    @Tag("Cobertura")
    void testValidoObtenerEstadisticasJugadorPartidaNoCompletada() {
        String nombre = "Maria";

        Partida partidaIncompleta = new Partida("p-2", LocalDate.of(2025, 6, 15), "Luis", "Maria");
        ResumenPartida resumenIncompleto = new ResumenPartida("p-2", LocalDate.of(2025, 6, 15), "Luis", "Maria");
        partidaIncompleta.setResumenPartida(resumenIncompleto);
        // No se establece resumen, por lo que queda como no completada

        LocalDate fecha1 = LocalDate.of(2025, 7, 9);
        Partida partida2 = new Partida("p-2", fecha1, "Luis", "Maria");
        ResumenPartida resumen2 = new ResumenPartida("p-2", fecha1, "Luis", "Maria");
        resumen2.marcarComoCompleta();

        Jugador j1 = new Jugador("Luis");
        Jugador j2 = new Jugador("Maria");
        j2.agregarCartaACartas(new Carta(Palo.OROS, 7)); // Guindis
        resumen2.establecerResultados(j1, j2, 2, 3);
        partida2.setResumenPartida(resumen2);

        LocalDate fecha2 = LocalDate.of(2025, 1, 28);
        Partida partida3 = new Partida("p-3", fecha2, "Maria", "Diego");
        ResumenPartida resumen3 = new ResumenPartida("p-3", fecha2, "Maria", "Diego");
        resumen3.marcarComoCompleta();

        Jugador j3 = new Jugador("Maria");
        Jugador j4 = new Jugador("Diego");
        resumen3.establecerResultados(j3, j4, 2, 4);
        partida3.setResumenPartida(resumen3);

        List<Partida> partidasJugador = new ArrayList<>();
        partidasJugador.add(partidaIncompleta);
        partidasJugador.add(partida2);
        partidasJugador.add(partida3);

        expect(repositorioPartida.obtenerPartidasPorJugador(nombre)).andReturn(partidasJugador);
        replay(repositorioPartida);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasJugador(nombre);

        assertEquals(4, resultado.size());
        assertEquals(1, resultado.get(0));// ganadas
        assertEquals(1, resultado.get(1));// perdidas
        assertEquals(3, resultado.get(2));// total
        assertEquals(1, resultado.get(3));// guindis
        verify(repositorioPartida);
    }

    @Test
    @Tag("Cobertura")
    void testValidoObtenerEstadisticasJugadorSinPartidas() {
        String nombre = "Maria";

        List<Partida> partidasJugador = new ArrayList<>();

        expect(repositorioPartida.obtenerPartidasPorJugador(nombre)).andReturn(partidasJugador);
        replay(repositorioPartida);

        ArrayList<Integer> resultado = servicioPartida.obtenerEstadisticasJugador(nombre);

        assertEquals(4, resultado.size());
        assertEquals(0, resultado.get(0));// ganadas
        assertEquals(0, resultado.get(1));// perdidas
        assertEquals(0, resultado.get(2));// total
        assertEquals(0, resultado.get(3));// guindis
        verify(repositorioPartida);
    }

    @Test
    void testNoValidoObtenerEstadisticasJugadorNombreNulo() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerEstadisticasJugador(null));
        verify(repositorioPartida);
    }

    @Test
    void testNoValidoObtenerEstadisticasJugadorNombreVacio() {
        replay(repositorioPartida);
        assertThrows(IllegalArgumentException.class, () -> servicioPartida.obtenerEstadisticasJugador(""));
        verify(repositorioPartida);
    }

    @Test
    void testResumenPartidaValidoJSON1() {
        expect(repositorioPartida.cargar("p-test-json1")).andReturn(crearPartidaDeDatos1()).times(2);
        replay(repositorioPartida);
        servicioPartida.obtenerPartida("p-test-json1");
        ArrayList<ArrayList<Integer>> resumen = servicioPartida.resumenPartida("p-test-json1");
        ArrayList<Integer> esperadoJugador1 = new ArrayList<>(List.of(0, 13, 3, 1, 0, 0));
        ArrayList<Integer> esperadoJugador2 = new ArrayList<>(List.of(1, 27, 7, 3, 1, 5));
        assertEquals(2, resumen.size());
        assertEquals(esperadoJugador1, resumen.get(0));
        assertEquals(esperadoJugador2, resumen.get(1));
        verify(repositorioPartida);
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoResumenPartidaNula() {
        String idPartida = "partida-1";

        expect(repositorioPartida.cargar(idPartida)).andReturn(null);
        replay(repositorioPartida);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioPartida.resumenPartida(idPartida);
        });

        verify(repositorioPartida);
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

        Partida partida2 = new Partida("p-3", LocalDate.of(2025, 7, 1), "Carlos", "Ana");
        ResumenPartida resumen2 = new ResumenPartida("p-3", LocalDate.of(2025, 7, 1), "Carlos", "Ana");
        resumen2.marcarComoCompleta();
        resumen2.establecerResultados(new Jugador("Carlos"), new Jugador("Ana"), 2, 3);
        partida2.setResumenPartida(resumen2);

        Partida partida3 = new Partida("p-4", LocalDate.of(2025, 8, 1), "Luis", "Maria");
        ResumenPartida resumen3 = new ResumenPartida("p-4", LocalDate.of(2025, 8, 1), "Luis", "Maria");
        resumen3.marcarComoCompleta();
        resumen3.establecerResultados(new Jugador("Luis"), new Jugador("Maria"), 12, 0);
        partida3.setResumenPartida(resumen3);

        Partida partida4 = new Partida("p-5", LocalDate.of(2025, 9, 1), "Luis", "Carlos");
        ResumenPartida resumen4 = new ResumenPartida("p-5", LocalDate.of(2025, 9, 1), "Luis", "Carlos");
        resumen4.marcarComoCompleta();
        resumen4.establecerResultados(new Jugador("Luis"), new Jugador("Carlos"), 9, 8);
        partida4.setResumenPartida(resumen4);

        Partida partida5 = new Partida("p-2", LocalDate.of(2025, 6, 1), "Maria", "Diego");
        ResumenPartida resumen5 = new ResumenPartida("p-2", LocalDate.of(2025, 6, 1), "Maria", "Diego");
        resumen5.marcarComoCompleta();
        resumen5.establecerResultados(new Jugador("Maria"), new Jugador("Diego"), 8, 7);
        partida5.setResumenPartida(resumen5);

        Partida partida6 = new Partida("p-6", LocalDate.of(2025, 10, 1), "Pedro", "Sofia");
        ResumenPartida resumen6 = new ResumenPartida("p-6", LocalDate.of(2025, 10, 1), "Pedro", "Sofia");
        resumen6.marcarComoCompleta();
        resumen6.establecerResultados(new Jugador("Pedro"), new Jugador("Sofia"), 5, 5); // EMPATE
        partida6.setResumenPartida(resumen6);

        List<Partida> partidas = new ArrayList<>(List.of(
                partida1, partida2, partida3, partida4, partida5, partida6));

        expect(repositorioPartida.obtenerPartidasPorFecha("0001-01-01", LocalDate.now().toString()))
                .andReturn(partidas);
        replay(repositorioPartida);

        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();
        assertEquals(7, top10.size());
        assertEquals("Ana", top10.get(0).getNombre());
        assertEquals("Luis", top10.get(1).getNombre());
        assertEquals("Maria", top10.get(2).getNombre());
        assertEquals("Carlos", top10.get(3).getNombre());
        assertEquals("Diego", top10.get(4).getNombre());
        assertEquals("Pedro", top10.get(5).getNombre());
        assertEquals("Sofia", top10.get(6).getNombre());

        verify(repositorioPartida);
    }

    @Tag("Cobertura")
    @Test
    void testValidoObtenerTop10ConPartidasNull() {
        expect(repositorioPartida.obtenerPartidasPorFecha("0001-01-01", LocalDate.now().toString())).andReturn(null);
        replay(repositorioPartida);
        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();
        assertEquals(0, top10.size());
        verify(repositorioPartida);
    }

    @Tag("Cobertura")
    @Test
    void testValidoObtenerTop10ConListaVacia() {
        expect(repositorioPartida.obtenerPartidasPorFecha("0001-01-01", LocalDate.now().toString()))
                .andReturn(new ArrayList<>());
        replay(repositorioPartida);
        ArrayList<Jugador> top10 = servicioPartida.obtenerTop10();
        assertEquals(0, top10.size());
        verify(repositorioPartida);
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

        // Partida 2: Carlos gana
        Partida partida2 = new Partida("p-2", LocalDate.of(2025, 6, 1), "Carlos", "Sofia");
        ResumenPartida resumen2 = new ResumenPartida("p-2", LocalDate.of(2025, 6, 1), "Carlos", "Sofia");
        resumen2.marcarComoCompleta();
        resumen2.establecerResultados(new Jugador("Carlos"), new Jugador("Sofia"), 8, 3);
        partida2.setResumenPartida(resumen2);

        // Partida 3: Empate
        Partida partida3 = new Partida("p-3", LocalDate.of(2025, 7, 1), "Pedro", "Sofia");
        ResumenPartida resumen3 = new ResumenPartida("p-3", LocalDate.of(2025, 7, 1), "Pedro", "Sofia");
        resumen3.marcarComoCompleta();
        resumen3.establecerResultados(new Jugador("Pedro"), new Jugador("Sofia"), 5, 5);
        partida3.setResumenPartida(resumen3);

        // Partida 4: Beatriz gana
        Partida partida4 = new Partida("p-4", LocalDate.of(2025, 8, 1), "Beatriz", "Diego");
        ResumenPartida resumen4 = new ResumenPartida("p-4", LocalDate.of(2025, 8, 1), "Beatriz", "Diego");
        resumen4.marcarComoCompleta();
        resumen4.establecerResultados(new Jugador("Beatriz"), new Jugador("Diego"), 7, 6);
        partida4.setResumenPartida(resumen4);

        List<Partida> partidas = new ArrayList<>(List.of(partida1, partida2, partida3, partida4));

        expect(repositorioPartida.obtenerPartidasPorFecha("0001-01-01", LocalDate.now().toString()))
                .andReturn(partidas);
        replay(repositorioPartida);

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
        verify(repositorioPartida);
    }


        private static final int[] INDICES_VALIDOS = { 1, 2, 3, 4, 5, 6, 7, 10, 11, 12 }; // 10 valores


    // Asegura que solo se usen índices del array INDICES_VALIDOS
    private void agregarCartasCiclicas(Jugador jugador, Palo palo, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            int idx = INDICES_VALIDOS[i % INDICES_VALIDOS.length];
            jugador.agregarCartaACartas(new Carta(palo, idx));
        }
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

    /**
     * Recrea el objeto Partida a partir de los datos del json partida_escoba1.json,
     * incluyendo el resumen esperado basado en el estado final de las cartas
     * y el estado final de los jugadores.
     */
    private Partida crearPartidaDeDatos1() {
        try {
            // Leemos la partida real desde el JSON de recursos para garantizar consistencia
            LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
            Partida partida = lector.obtenerPartida();

            // Construimos los jugadores finales esperados (igual que antes)
            String jugador1Nombre = partida.getNombres().get(0);
            String jugador2Nombre = partida.getNombres().get(1);

            Jugador anaFinal = construirAnaFinal(jugador1Nombre);
            Jugador luisFinal = construirLuisFinal(jugador2Nombre);

            partida.establecerResultados(anaFinal, luisFinal, 0, 5);

            return partida;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el JSON de prueba para crear la partida", e);
        }
    }

}