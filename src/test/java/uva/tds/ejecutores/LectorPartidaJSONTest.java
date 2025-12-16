package uva.tds.ejecutores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugada;
import uva.tds.entidades.Palo;
import uva.tds.entidades.Partida;
import uva.tds.entidades.Ronda;
import uva.tds.entidades.Turno;

public class LectorPartidaJSONTest {

    private String rutaArchivo = "src/test/resources/partida_escoba1.json";
    private LectorPartidaJSON lector;
    private JSONObject partidaJSON;

    @BeforeEach
    void setUp() throws IOException {
        lector = new LectorPartidaJSON(rutaArchivo);
        partidaJSON = lector.getPartidaJSON();
    }

    @Test
    void testConstructorValido() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        assertNotNull(lector);
    }

    @Test
    void testConstructorArchivoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new LectorPartidaJSON(null));
    }

    @Test
    void testConstructorNoValidoArchivoNoExiste() {
        String rutaInexistente = "src/test/resources/archivo_inexistente.json";
        assertThrows(FileNotFoundException.class,
                () -> new LectorPartidaJSON(rutaInexistente));
    }

    @Test
    void testConstructorNoValidoArchivoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new LectorPartidaJSON(""));
    }

    @Test
    void testConvertirStringACartaValido() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        String entrada = "5-oros";
        Carta carta = lector.convertirStringACarta(entrada);
        assertEquals(5, carta.getIndice());
        assertEquals(Palo.OROS, carta.getPalo());
    }

    @Test
    void testConvertirStringACartaNoValidoStringNulo() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        assertThrows(IllegalArgumentException.class,
                () -> lector.convertirStringACarta(null));
    }

    @Test
    void testConvertirStringACartaNoValidoFormatoIncorrecto() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        String entrada = "5oros";
        assertThrows(IllegalArgumentException.class,
                () -> lector.convertirStringACarta(entrada));
    }

    @Test
    public void testConvertirStringVacioLanzaExcepcion() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        assertThrows(IllegalArgumentException.class,
                () -> lector.convertirStringACarta(""));
    }

    @Test
    public void testConvertirArrayJSONaCartasValidoVacio() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        String jsonArray = "[]";
        ArrayList<Carta> cartas = lector.convertirArrayJSONaCartas(jsonArray);
        assertEquals(0, cartas.size());
    }

    @Test
    public void testConvertirArrayJSONaCartasValidoConUna() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        String jsonArray = "[\"5-oros\"]";
        ArrayList<Carta> cartas = lector.convertirArrayJSONaCartas(jsonArray);

        assertEquals(1, cartas.size());
        assertEquals(new Carta(Palo.OROS, 5), cartas.get(0));
    }

    @Test
    public void testConvertirArrayJSONaCartasValidoConVarias() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        String jsonArray = "[\"5-oros\",\"11-oros\",\"10-bastos\"]";
        ArrayList<Carta> cartas = lector.convertirArrayJSONaCartas(jsonArray);

        assertEquals(3, cartas.size());
        assertEquals(new Carta(Palo.OROS, 5), cartas.get(0));
        assertEquals(new Carta(Palo.OROS, 11), cartas.get(1));
        assertEquals(new Carta(Palo.BASTOS, 10), cartas.get(2));
    }

    @Test
    public void testConvertirArrayJSONNoValidoNulo() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON(rutaArchivo);
        assertThrows(IllegalArgumentException.class, () -> {
            lector.convertirArrayJSONaCartas(null);
        });
    }

    @Test
    void testLeerArchivoJSONValido() throws IOException {
        JSONObject json = lector.leerArchivoJSON(rutaArchivo);
        assertEquals(partidaJSON.toString(), json.toString());
    }

    @Test
    void testLeerArchivoJSONDesdeSrcGenerico() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/otraCarpeta/partida_escoba1.json");

        JSONObject json = lector.getPartidaJSON();

        assertNotNull(json);
    }

    @Test
    void testLeerArchivoJSONDesdeTestJava() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/java/partida_escoba1.json");

        JSONObject json = lector.getPartidaJSON();

        assertNotNull(json);
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoLeerArchivoJSONSinEntrarIfSrc() {
        // Ruta que NO comienza con "src/"
        String ruta = "archivo_inexistente_fuera.json";

        assertThrows(FileNotFoundException.class, () -> {
            new LectorPartidaJSON(ruta);
        });
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoLeerArchivoJSONNoEntraEnIdx() {
        // Ruta que entra en if (candidate.startsWith("src/"))
        // y tiene un '/' después de la posición 4
        String ruta = "src/archivo_inexistente.json";

        assertThrows(FileNotFoundException.class, () -> {
            new LectorPartidaJSON(ruta);
        });
    }

    @Test
    void testExtraerNombresJugadoresValido() {
        ArrayList<String> nombres = lector.extraerNombresJugadores();

        ArrayList<String> esperados = new ArrayList<>();
        esperados.add("Ana");
        esperados.add("Luis");

        assertEquals(esperados, nombres);
    }

    @Test
    void testExtraerNombresJugadoresNoValidoMasDeDosJugadores() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escobaENombres.json");
        partidaJSON = lector.getPartidaJSON();

        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerNombresJugadores());
    }

    @Test
    void testExtraerNombresJugadoresNoValidoJugadoresNulo() throws IOException {
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escobaNombresNull.json");
        partidaJSON = lector.getPartidaJSON();

        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerNombresJugadores());
    }

    @Test
    void testExtraerManoInicialJugadorValido() {
        ArrayList<Carta> cartas = lector.extraerManoJugador(1, "Ana");

        ArrayList<Carta> esperadas = new ArrayList<>();
        esperadas.add(new Carta(Palo.COPAS, 1));
        esperadas.add(new Carta(Palo.COPAS, 4));
        esperadas.add(new Carta(Palo.ESPADAS, 11));

        assertEquals(esperadas, cartas);
    }

    @Test
    void testExtraerMesaInicialValido() {
        ArrayList<Carta> cartas = lector.extraerMesaInicial();

        ArrayList<Carta> esperadas = new ArrayList<>();
        esperadas.add(new Carta(Palo.OROS, 5));
        esperadas.add(new Carta(Palo.OROS, 11));
        esperadas.add(new Carta(Palo.BASTOS, 10));
        esperadas.add(new Carta(Palo.BASTOS, 11));

        assertEquals(esperadas, cartas);
    }

    @Test
    void testExtraerJugadaValido() {
        Jugada jugada = lector.extraerJugada(5, 3);

        // en el JSON de ejemplo, 5ª ronda, 3er turno: juega 6-copas, sin captura,
        // mesa_resultante contiene 6-copas
        assertEquals(new Carta(Palo.COPAS, 6), jugada.getJuega());
        assertTrue(jugada.getCaptura().isEmpty());
        assertEquals(1, jugada.getMesaResultante().size());
        assertEquals(new Carta(Palo.COPAS, 6), jugada.getMesaResultante().get(0));
    }

    @Test
    void testExtraerJugadaValidoLimiteInferior() {
        Jugada jugada = lector.extraerJugada(1, 1);

        assertEquals(new Carta(Palo.COPAS, 1), jugada.getJuega());
        assertEquals(2, jugada.getCaptura().size());
        assertTrue(jugada.getCaptura().contains(new Carta(Palo.OROS, 5)));
        assertTrue(jugada.getCaptura().contains(new Carta(Palo.OROS, 11)));
        assertEquals(2, jugada.getMesaResultante().size());
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.BASTOS, 10)));
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.BASTOS, 11)));
    }

    @Test
    void testExtraerJugadaValidoRondaLimiteSuperior() {
        Jugada jugada = lector.extraerJugada(6, 6);

        assertEquals(new Carta(Palo.BASTOS, 7), jugada.getJuega());
        assertTrue(jugada.getCaptura().isEmpty());
        assertEquals(5, jugada.getMesaResultante().size(), "El JSON de ejemplo contiene 5 cartas en mesa_resultante para la ronda 6 turno 6");
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.ESPADAS, 12)));
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.OROS, 12)));
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.BASTOS, 12)));
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.ESPADAS, 3)));
        assertTrue(jugada.getMesaResultante().contains(new Carta(Palo.BASTOS, 7)));
    }

    @Test
    void testExtraerJugadaNoValidoRondaLimiteInferior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerJugada(0, 3));
    }

    @Test
    void testExtraerJugadaNoValidoTurnoLimiteInferior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerJugada(3, 0));
    }

    @Test
    void testExtraerJugadaNoValidoRondaLimiteSuperior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerJugada(7, 3));
    }

    @Test
    void testExtraerJugadaNoValidoTurnoLimiteSuperior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerJugada(3, 7));
    }

    @Test
    void testExtraerManoJugadorRonda2() {
        ArrayList<Carta> cartas = lector.extraerManoJugador(2, "Ana");

        ArrayList<Carta> esperadas = new ArrayList<>();
        esperadas.add(new Carta(Palo.ESPADAS, 2));
        esperadas.add(new Carta(Palo.ESPADAS, 5));
        esperadas.add(new Carta(Palo.ESPADAS, 10));

        assertEquals(esperadas, cartas);
    }

    @Test
    void testExtraerManoJugadorRondaValidoLimiteInferior() {
        ArrayList<Carta> cartas = lector.extraerManoJugador(1, "Luis");
        assertNotNull(cartas);
        assertEquals(3, cartas.size());
    }

    @Test
    void testExtraerManoJugadorRondaValidoLimiteSuperior() {
        ArrayList<Carta> cartas = lector.extraerManoJugador(6, "Luis");
        assertNotNull(cartas);
        assertEquals(3, cartas.size());
    }

    @Test
    void testExtraerManoInicialJugadorNoValidoNombreNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerManoJugador(1, null));
    }

    @Test
    void testExtraerManoInicialJugadorNoValidoNombreIncorrecto() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerManoJugador(1, "Pedro"));
    }

    @Test
    void testExtraerManoJugadorRondaNoValidoLimiteInferior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerManoJugador(0, "Ana"));
    }

    @Test
    void testExtraerManoJugadorRondaNoValidoLimiteSuperior() {
        assertThrows(IllegalArgumentException.class,
                () -> lector.extraerManoJugador(7, "Ana"));
    }

    @Test
    @Tag("Cobertura")
    void testValidoObtenerPartida() throws IOException {
        Partida partida = lector.obtenerPartida("partidaJSON");
        assertEquals("Ana", partida.getNombres().get(0));
        assertEquals("Luis", partida.getNombres().get(1));
        assertEquals(new Carta(Palo.OROS, 11), partida.getMesaInicial().get(1));
        assertEquals(new Carta(Palo.COPAS, 4), partida.getManosJugador1().get(0).get(1));
        assertEquals(new Carta(Palo.ESPADAS, 2), partida.getManosJugador1().get(1).get(0));
        assertEquals(new Carta(Palo.BASTOS, 7), partida.getManosJugador2().get(5).get(2));

        // Assertions migrated to use getRondas() / Ronda / Turno / Jugada API
        List<Ronda> rondas = partida.getRondas();
        assertEquals(6, rondas.size(), "Deben leerse 6 rondas");

        // 5ª ronda (índice 4), 3er turno (índice 2), carta jugada
        Turno turno5_3 = rondas.get(4).getTurnos().get(2);
        assertEquals(new Carta(Palo.COPAS, 6), turno5_3.getJugada().getJuega());

        // 6ª ronda (índice 5), 6º turno (índice 5), mesa_resultante índice 4
        Turno turno6_6 = rondas.get(5).getTurnos().get(5);
        assertEquals(new Carta(Palo.BASTOS, 7), turno6_6.getJugada().getMesaResultante().get(4));
    }

}
