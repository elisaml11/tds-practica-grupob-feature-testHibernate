package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import uva.tds.ejecutores.AdaptadorCartaJson;
import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;
import uva.tds.entidades.Palo;

/**
 * Clase de test para GestorPartida.
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorPartidaTest {

    private Jugador j1;
    private Jugador j2;
    private GestorPartida gestor;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("j1");
        j2 = new Jugador("j2");
        gestor = new GestorPartida(j1, j2);
    }

    private ArrayList<Carta> cartas(String... strs) {
        ArrayList<Carta> res = new ArrayList<>();
        for (String s : strs) res.add(AdaptadorCartaJson.parse(s));
        return res;
    }

    @Test
    void testConstructorValidoInicializaCorrectamente() {
        assertEquals(j1, gestor.getJugador1());
        assertEquals(j2, gestor.getJugador2());
        assertEquals(1, gestor.getRondaActual());
        assertEquals(0, gestor.getTurnosJugados());
        assertEquals(j1, gestor.getJugadorActual());
        assertNotNull(gestor.getBaraja());
        assertEquals(40, gestor.getBaraja().getNumeroDeCartas());
        assertTrue(gestor.getCartasMesa().isEmpty());
        assertNull(gestor.getUltimoQueHizoBaza());
    }

    @Test
    void testRepartoInicialValidoDejaManosYMesaYReduceBaraja() {
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas","5-bastos");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros");
        ArrayList<Carta> mesa = cartas("7-bastos","10-espadas","11-copas","12-oros");

        gestor.repartoInicial(mano1, mano2, mesa);

        assertEquals(mesa, gestor.getCartasMesa());
        assertEquals(mano1, j1.getMano());
        assertEquals(mano2, j2.getMano());
        assertEquals(30, gestor.getBaraja().getNumeroDeCartas());
    }

    @Test
    void testRepartoInicialNoValidoManoNoTresCartas(){
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros");
        ArrayList<Carta> mesa = cartas("7-bastos","10-espadas","11-copas","12-oros");

        assertThrows(IllegalArgumentException.class, () -> 
            gestor.repartoInicial(mano1, mano2, mesa));
    }

    @Test
    void testRepartoInicialNoValidoMano2NoTresCartas(){
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas","5-bastos");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros","7-oros");
        ArrayList<Carta> mesa = cartas("7-bastos","10-espadas","11-copas","12-oros");

        assertThrows(IllegalArgumentException.class, () -> 
            gestor.repartoInicial(mano1, mano2, mesa));
    }

    @Test
    void testRepartoInicialNoValidoMesaNoCuatroCartas(){
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas","5-bastos");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros");
        ArrayList<Carta> mesa = cartas("7-bastos","10-espadas","11-copas");

        assertThrows(IllegalArgumentException.class, () -> 
            gestor.repartoInicial(mano1, mano2, mesa));
    }

    @Test
    void testJugarCartaValidoSinCapturaColocaEnMesaYCambiaTurnoYCuentaTurno() {
        Carta c = AdaptadorCartaJson.parse("1-oros");
        j1.agregarCartaAMano(c);

        assertEquals(0, gestor.getTurnosJugados());
        gestor.jugarCarta(c, new ArrayList<>()); // j1 juega
        assertEquals(1, gestor.getTurnosJugados());
        assertTrue(gestor.getCartasMesa().contains(c));
        assertFalse(j1.getMano().contains(c));
        assertEquals(j2, gestor.getJugadorActual());
    }

    @Test
    void testCapturarCartasValidoMovimientoYCambiaUltimoQueHizoBazaYEscoba() {
        // reparto inicial con configuración que permita captura 5 + 10 = 15
        ArrayList<Carta> mano1 = cartas("5-bastos","2-oros","3-copas");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros");
        ArrayList<Carta> mesa = cartas("12-oros","7-bastos","3-espadas","1-copas");

        gestor.repartoInicial(mano1, mano2, mesa);

        Carta cartaJugada = j1.getMano().stream().filter(c -> c.getIndice() == 5 && c.getPalo() == Palo.BASTOS).findFirst().orElse(null);
        assertNotNull(cartaJugada);

        ArrayList<Carta> aCapturar = new ArrayList<>();
        for (Carta cm : gestor.getCartasMesa()) {
            if (cm.getIndice() == 12 && cm.getPalo() == Palo.OROS) {
                aCapturar.add(cm);
                break;
            }
        }
        assertFalse(aCapturar.isEmpty());

        gestor.capturarCartas(cartaJugada, aCapturar);
        assertTrue(j1.getCartas().contains(cartaJugada));
        assertTrue(j1.getCartas().contains(aCapturar.get(0)));
        assertEquals(j1, gestor.getUltimoQueHizoBaza());
    }

    @Test
    void testCapturarCartasInvalidaSiNoSumanQuince() {
        gestor.repartoInicial(cartas("1-oros","3-copas","5-bastos"),
                              cartas("2-espadas","4-copas","6-oros"),
                              cartas("7-bastos","10-espadas","11-copas","12-oros"));

        Carta cartaJ = new Carta(Palo.BASTOS, 2); // valor 2
        j1.agregarCartaAMano(cartaJ);

        ArrayList<Carta> capturar = new ArrayList<>();
        capturar.add(gestor.getCartasMesa().get(0)); 
        capturar.add(gestor.getCartasMesa().get(1));

        assertThrows(IllegalArgumentException.class, () -> gestor.capturarCartas(cartaJ, capturar));
    }

    @Test
    void testAvanzarRondaValidoFinalizaSiBarajaVaciaYRondaSeis() {
        // Avanzamos hasta la ronda 6
        for (int i = 0; i < 5; i++) {
            gestor.avanzarRonda();
        }
        assertEquals(6, gestor.getRondaActual());

        // Ahora vaciamos la baraja
        while (gestor.getBaraja().getNumeroDeCartas() > 0) {
            Carta c = gestor.getBaraja().getCartas().get(0);
            gestor.getBaraja().robarCarta(c);
        }
        assertEquals(0, gestor.getBaraja().getNumeroDeCartas());

        // manos y mesa están vacías por defecto, avanzarRonda debe finalizar la partida
        gestor.avanzarRonda();
        assertEquals(-1, gestor.getRondaActual());
    }

    @Test
    @Tag("Cobertura")
    public void testAvanzarRondaFinalizaBarajaVaciaRonda6() {
        Jugador j1 = new Jugador("P1");
        Jugador j2 = new Jugador("P2");
        GestorPartida gp = new GestorPartida(j1, j2);

        // llevar la ronda a 6
        while (gp.getRondaActual() < 6) gp.avanzarRonda();
        assertTrue(gp.getRondaActual() >= 6);

        while (gp.getBaraja().getNumeroDeCartas() > 0) {
            gp.getBaraja().robarCarta(gp.getBaraja().getCartas().get(0));
        }
        assertEquals(0, gp.getBaraja().getNumeroDeCartas());

        // debe finalizar la partida al avanzar
        gp.avanzarRonda();
        assertEquals(-1, gp.getRondaActual());
    }

    @Test
    @Tag("Cobertura")
    public void testAvanzarRondaNoFinalizaBarajaVaciaRondaTemprana() {
        Jugador j1 = new Jugador("P1");
        Jugador j2 = new Jugador("P2");
        GestorPartida gp = new GestorPartida(j1, j2);

        while (gp.getBaraja().getNumeroDeCartas() > 0) {
            gp.getBaraja().robarCarta(gp.getBaraja().getCartas().get(0));
        }
        assertEquals(0, gp.getBaraja().getNumeroDeCartas());
        assertTrue(gp.getRondaActual() < 6);

        gp.avanzarRonda();
        assertNotEquals(-1, gp.getRondaActual());
        assertEquals(2, gp.getRondaActual());
    }

    @Test
    @Tag("Cobertura")
    public void testAvanzarRondaNoFinalizaBarajaNoVaciaRonda6() {
        Jugador j1 = new Jugador("P1");
        Jugador j2 = new Jugador("P2");
        GestorPartida gp = new GestorPartida(j1, j2);

        // llevar la ronda a 6 sin vaciar la baraja
        while (gp.getRondaActual() < 6) gp.avanzarRonda();
        assertTrue(gp.getRondaActual() >= 6);
        assertTrue(gp.getBaraja().getNumeroDeCartas() > 0);

        int antes = gp.getRondaActual();
        gp.avanzarRonda();
        // no debe finalizar porque la baraja no está vacía
        assertNotEquals(-1, gp.getRondaActual());
        assertEquals(antes + 1, gp.getRondaActual());
    }

    @Test
    @Tag("Cobertura")
    public void testAvanzarRondaNoFinalizaBarajaNoVaciaRondaMenor() {
        Jugador j1 = new Jugador("P1");
        Jugador j2 = new Jugador("P2");
        GestorPartida gp = new GestorPartida(j1, j2);

        assertTrue(gp.getBaraja().getNumeroDeCartas() > 0);
        assertTrue(gp.getRondaActual() < 6);

        gp.avanzarRonda();
        assertEquals(2, gp.getRondaActual());
        assertNotEquals(-1, gp.getRondaActual());
    }

    @Test
    void testFinalizarPartidaNoValidoManosNoVacias() {
        // añadimos carta a mano de j1
        j1.agregarCartaAMano(new Carta(Palo.OROS, 1));
        assertThrows(IllegalStateException.class, () -> gestor.finalizarPartida());
    }

    @Test
    void testFinalizarPartidaNoValidoSumaMesaInvalida() {
        // crear reparto inicial con mesa cuya suma (p. ej. cuatro 1s -> valor=4) es inválida
        ArrayList<Carta> mano1 = cartas("2-bastos","3-copas","4-oros");
        ArrayList<Carta> mano2 = cartas("2-espadas","3-oros","4-copas");
        ArrayList<Carta> mesa = cartas("1-oros","1-copas","1-espadas","1-bastos"); // sum=4 -> inválida

        gestor.repartoInicial(mano1, mano2, mesa);

        // vaciamos manos para simular final real, hay que tirar las cartas individualmente
        for (Carta c : new ArrayList<>(j1.getMano())) j1.tirarCarta(c);
        for (Carta c : new ArrayList<>(j2.getMano())) j2.tirarCarta(c);

        assertThrows(IllegalStateException.class, () -> gestor.finalizarPartida());
    }

    @Test
    void testFinalizarPartidaValidaAsignaCartasAlReceptor() {
        ArrayList<Carta> mano1 = cartas("2-bastos","3-copas","4-oros");
        ArrayList<Carta> mano2 = cartas("2-espadas","3-oros","4-copas");
        ArrayList<Carta> mesa = cartas("1-oros","1-copas","1-espadas","7-bastos"); 

        gestor.repartoInicial(mano1, mano2, mesa);

        // getMano() devuelve una copia, así que hay que tirar las cartas individualmente
        for (Carta c : new ArrayList<>(j1.getMano())) j1.tirarCarta(c);
        for (Carta c : new ArrayList<>(j2.getMano())) j2.tirarCarta(c);

        gestor.finalizarPartida();
        assertEquals(-1, gestor.getRondaActual());
        assertTrue(j1.getCartas().containsAll(mesa));
    }

    @Test
    void testFinalizarPartidaNoValidaPartidaYaFinalizada() {
        gestor.finalizarPartida();
        assertThrows(IllegalStateException.class, () -> gestor.finalizarPartida());
    }

    @Test
    void testFinalizarPartidaNoValidaMano1NoVacia(){
        j1.agregarCartaAMano(new Carta(Palo.OROS, 1));
        assertThrows(IllegalStateException.class, () -> gestor.finalizarPartida());

    }

    @Test
    void testFinalizarPartidaNoValidaManos2NoVacia(){
        j2.agregarCartaAMano(new Carta(Palo.OROS, 1));
        assertThrows(IllegalStateException.class, () -> gestor.finalizarPartida());

    }

    @Test
    void testJugarCartaNoValidoRondaTerminada() {
        gestor.repartoInicial(cartas("1-oros","3-copas","5-bastos"),
                              cartas("2-espadas","4-copas","6-oros"),
                              cartas("7-bastos","10-espadas","11-copas","12-oros"));

        for (int i = 0; i < 6; i++) gestor.cambiarTurno();

        Carta carta = new Carta(Palo.OROS, 1);
        j1.agregarCartaAMano(carta);
        assertThrows(IllegalStateException.class, () -> gestor.jugarCarta(carta, new ArrayList<>()));
    }

    @Test
    void testConstructorNoValidoJugadoresNulos() {
        assertThrows(IllegalArgumentException.class, () -> new GestorPartida(null, j2));
        assertThrows(IllegalArgumentException.class, () -> new GestorPartida(j1, null));
    }

    @Test
    void testRondaTerminadaValida() {
        GestorPartida g = new GestorPartida(j1, j2);
        assertFalse(g.rondaTerminada());
        for (int i = 0; i < 6; i++) g.cambiarTurno();
        assertTrue(g.rondaTerminada());
    }

    @Test
    void testRepartoInicialNoValidoJugador1Null() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> c2 = new ArrayList<>(); c2.add(AdaptadorCartaJson.parse("2-espadas"));
        ArrayList<Carta> mesa = new ArrayList<>(); mesa.add(AdaptadorCartaJson.parse("7-bastos"));

        assertThrows(IllegalArgumentException.class, () -> g.repartoInicial(null, c2, mesa));
    }

    @Test
    void testRepartoInicialNoValidoJugador2Null() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> c1 = new ArrayList<>(); c1.add(AdaptadorCartaJson.parse("1-oros"));
        ArrayList<Carta> mesa = new ArrayList<>(); mesa.add(AdaptadorCartaJson.parse("7-bastos"));

        assertThrows(IllegalArgumentException.class, () -> g.repartoInicial(c1, null, mesa));
    }

    @Test
    void testRepartoInicialNoValidoMesaNull() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> c1 = new ArrayList<>(); c1.add(AdaptadorCartaJson.parse("1-oros"));
        ArrayList<Carta> c2 = new ArrayList<>(); c2.add(AdaptadorCartaJson.parse("2-espadas"));

        assertThrows(IllegalArgumentException.class, () -> g.repartoInicial(c1, c2, null));
    }

    @Test
    void testRepartoInicialNoValidoDuranteRondaLanzaIllegalState() {
        GestorPartida g = new GestorPartida(j1, j2);
        // usar listas con tamaños válidos (3/3/4) para que el primer reparto no falle
        ArrayList<Carta> c1 = cartas("1-oros", "2-copas", "3-bastos");
        ArrayList<Carta> c2 = cartas("4-oros", "5-copas", "6-bastos");
        ArrayList<Carta> mesa = cartas("7-bastos", "10-espadas", "11-copas", "12-oros");

        g.repartoInicial(c1, c2, mesa);
        g.avanzarRonda();
        assertThrows(IllegalStateException.class, () -> g.repartoInicial(
                cartas("1-oros", "2-copas", "3-bastos"),
                cartas("4-oros", "5-copas", "6-bastos"),
                cartas("7-bastos", "10-espadas", "11-copas", "12-oros")
        ));
    }

    @Test
    void testRepartoRondaValidacionesParamsNulosLanzan() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano = new ArrayList<>();
        mano.add(AdaptadorCartaJson.parse("1-oros")); mano.add(AdaptadorCartaJson.parse("3-copas")); mano.add(AdaptadorCartaJson.parse("5-bastos"));

        g.avanzarRonda();
        assertThrows(IllegalArgumentException.class, () -> g.repartoRonda(null, mano));
        assertThrows(IllegalArgumentException.class, () -> g.repartoRonda(mano, null));
    }

    @Test
    void testRepartoRondaValidacionesTamanioIncorrectoLanza() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano = new ArrayList<>();
        mano.add(AdaptadorCartaJson.parse("1-oros")); mano.add(AdaptadorCartaJson.parse("3-copas")); mano.add(AdaptadorCartaJson.parse("5-bastos"));

        g.avanzarRonda();
        // tamaños incorrectos
        ArrayList<Carta> shortList = new ArrayList<>(); shortList.add(AdaptadorCartaJson.parse("1-oros"));
        assertThrows(IllegalArgumentException.class, () -> g.repartoRonda(shortList, mano));
    }

    @Test
    void testRepartoRondaValidacionesCasoValidoEstableceMano() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano = new ArrayList<>();
        mano.add(AdaptadorCartaJson.parse("1-oros")); mano.add(AdaptadorCartaJson.parse("3-copas")); mano.add(AdaptadorCartaJson.parse("5-bastos"));

        g.avanzarRonda();
        ArrayList<Carta> mano2 = new ArrayList<>();
        mano2.add(AdaptadorCartaJson.parse("2-espadas")); mano2.add(AdaptadorCartaJson.parse("4-copas")); mano2.add(AdaptadorCartaJson.parse("6-oros"));
        g.repartoRonda(mano, mano2);
        assertEquals(mano, j1.getMano());
    }

    @Test
    void testRepartoRondaNoValidoMano1NoTresCartas(){
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros");

        g.avanzarRonda();
        assertThrows(IllegalArgumentException.class, () -> 
            g.repartoRonda(mano1, mano2));
    }

    @Test
    void testRepartoRondaNoValidoMano2NoTresCartas(){
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano1 = cartas("1-oros","3-copas","5-bastos");
        ArrayList<Carta> mano2 = cartas("2-espadas","4-copas","6-oros","7-oros");

        g.avanzarRonda();
        assertThrows(IllegalArgumentException.class, () -> 
            g.repartoRonda(mano1, mano2));
    }

    @Test
    void testJugarCartaValidaciones() {
        GestorPartida g = new GestorPartida(j1, j2);
        assertThrows(IllegalArgumentException.class, () -> g.jugarCarta(null, new ArrayList<>()));

        Carta c = AdaptadorCartaJson.parse("1-oros"); j1.agregarCartaAMano(c);
        assertThrows(IllegalArgumentException.class, () -> g.jugarCarta(c, null));

        Carta otra = AdaptadorCartaJson.parse("2-espadas");
        assertThrows(IllegalArgumentException.class, () -> g.jugarCarta(otra, new ArrayList<>()));
    }

    @Test
    void testCapturarCartasValidaciones() {
        GestorPartida g = new GestorPartida(j1, j2);
        ArrayList<Carta> mano1 = new ArrayList<>();
        mano1.add(AdaptadorCartaJson.parse("5-bastos")); mano1.add(AdaptadorCartaJson.parse("2-oros")); mano1.add(AdaptadorCartaJson.parse("3-copas"));
        ArrayList<Carta> mano2 = new ArrayList<>();
        mano2.add(AdaptadorCartaJson.parse("2-espadas")); mano2.add(AdaptadorCartaJson.parse("4-copas")); mano2.add(AdaptadorCartaJson.parse("6-oros"));
        ArrayList<Carta> mesa = new ArrayList<>();
        mesa.add(AdaptadorCartaJson.parse("12-oros")); mesa.add(AdaptadorCartaJson.parse("7-bastos")); mesa.add(AdaptadorCartaJson.parse("3-espadas")); mesa.add(AdaptadorCartaJson.parse("1-copas"));
        g.repartoInicial(mano1, mano2, mesa);

        assertThrows(IllegalArgumentException.class, () -> g.capturarCartas(null, new ArrayList<>()));
        Carta cartaJ = j1.getMano().get(0);
        assertThrows(IllegalArgumentException.class, () -> g.capturarCartas(cartaJ, null));

        ArrayList<Carta> notInMesa = new ArrayList<>(); notInMesa.add(AdaptadorCartaJson.parse("2-oros"));
        assertThrows(IllegalArgumentException.class, () -> g.capturarCartas(cartaJ, notInMesa));

        for (int i = 0; i < 6; i++) g.cambiarTurno();
        ArrayList<Carta> vacio = new ArrayList<>();
        Carta posible = j1.getMano().isEmpty() ? AdaptadorCartaJson.parse("1-oros") : j1.getMano().get(0);
        assertThrows(IllegalStateException.class, () -> g.capturarCartas(posible, vacio));
    }

    @Test
    void testAvanzarRondaFinalNormal() {
        GestorPartida gFinished = new GestorPartida(j1, j2);
        gFinished.finalizarPartida();
        assertThrows(IllegalStateException.class, () -> gFinished.avanzarRonda());

        GestorPartida g2 = new GestorPartida(j1, j2);
        int r = g2.getRondaActual();
        g2.avanzarRonda();
        assertEquals(r + 1, g2.getRondaActual());
    }

    @Test
    public void testRepartoRondaTamanioIncorrectoLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        gp.avanzarRonda();

        ArrayList<Carta> a1 = new ArrayList<>();
        a1.add(AdaptadorCartaJson.parse("1-oros"));
        ArrayList<Carta> a2 = new ArrayList<>();
        a2.add(AdaptadorCartaJson.parse("2-copas"));

        assertThrows(IllegalArgumentException.class, () -> gp.repartoRonda(a1, a2));
    }

    @Test
    public void testRepartoRondaEnRonda1Lanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        ArrayList<Carta> a1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> a2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));

        assertThrows(IllegalStateException.class, () -> gp.repartoRonda(a1, a2));
    }

    @Test
    public void testRepartoRondaPartidaFinalizadaLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        // finalizar partida (manos y mesa vacías permiten finalizar)
        gp.finalizarPartida();

        ArrayList<Carta> a1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> a2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));

        assertThrows(IllegalStateException.class, () -> gp.repartoRonda(a1, a2));
    }

    @Test
    public void testJugarCartaPartidaFinalizadaLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        gp.finalizarPartida();
        assertThrows(IllegalStateException.class, () -> gp.jugarCarta(AdaptadorCartaJson.parse("1-oros"), new ArrayList<>()));
    }

    @Test
    public void testJugarCartaRondaTerminadaLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        ArrayList<Carta> mano1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> mano2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));
        ArrayList<Carta> mesa = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("7-oros"), AdaptadorCartaJson.parse("10-bastos"), AdaptadorCartaJson.parse("11-copas"), AdaptadorCartaJson.parse("12-espadas")));
        gp.repartoInicial(mano1, mano2, mesa);

        for (int i = 0; i < 6; i++) gp.cambiarTurno();

        // intentar jugar cualquier carta existente 
        Carta carta = AdaptadorCartaJson.parse("1-oros");
        assertThrows(IllegalStateException.class, () -> gp.jugarCarta(carta, new ArrayList<>()));
    }

    @Test
    public void testCapturarCartasNullsLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        assertThrows(IllegalArgumentException.class, () -> gp.capturarCartas(null, new ArrayList<>()));
        assertThrows(IllegalArgumentException.class, () -> gp.capturarCartas(AdaptadorCartaJson.parse("1-oros"), null));
    }

    @Test
    public void testCapturarJugadorNoTieneCartaLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        // jugador no tiene cartas en mano
        Carta carta = AdaptadorCartaJson.parse("1-oros");
        ArrayList<Carta> capt = new ArrayList<>();
        capt.add(AdaptadorCartaJson.parse("2-copas"));
        assertThrows(IllegalArgumentException.class, () -> gp.capturarCartas(carta, capt));
    }

    @Test
    public void testCapturarCartasNoEnMesaLanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        Carta cartaMano = AdaptadorCartaJson.parse("1-oros");
        gp.getJugadorActual().agregarCartaAMano(cartaMano);

        ArrayList<Carta> capt = new ArrayList<>();
        capt.add(AdaptadorCartaJson.parse("2-copas")); // no está en la mesa
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> gp.capturarCartas(cartaMano, capt));
        assertTrue(ex.getMessage().contains("no está en la mesa"));
    }

    @Test
    public void testCapturarSumaNoSuma15Lanza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        Carta cartaMesa = AdaptadorCartaJson.parse("2-copas");
        Carta cartaA = AdaptadorCartaJson.parse("3-oros");
        gp.getJugadorActual().agregarCartaAMano(cartaMesa);
        gp.jugarCarta(cartaMesa, new ArrayList<>());

        //  intentar capturar con otra carta que no suma 15
        gp.getJugadorActual().agregarCartaAMano(cartaA);
        ArrayList<Carta> capt = new ArrayList<>();
        capt.add(cartaMesa);

        assertThrows(IllegalArgumentException.class, () -> gp.capturarCartas(cartaA, capt));
    }

    @Test
    public void testCapturarEscobaActualizaEscobaYUltimoQueHizoBaza() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        ArrayList<Carta> mano1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("7-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> mano2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));
        ArrayList<Carta> mesa = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("5-bastos"), AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("1-copas"), AdaptadorCartaJson.parse("1-espadas")));
        gp.repartoInicial(mano1, mano2, mesa);

        ArrayList<Carta> capturar = new ArrayList<>(gp.getCartasMesa());
        gp.jugarCarta(AdaptadorCartaJson.parse("7-oros"), capturar);

        // comprobar que P1 ha sumado escoba y que fue el último en hacer baza
        assertEquals(1, gp.getJugador1().getEscobas());
        assertEquals(gp.getJugador1(), gp.getUltimoQueHizoBaza());
    }

    @Test
    public void testFinalizarPartidaInalidoManosNoVacias() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        ArrayList<Carta> mano1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> mano2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));
        ArrayList<Carta> mesa = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("7-oros"), AdaptadorCartaJson.parse("10-bastos"), AdaptadorCartaJson.parse("11-copas"), AdaptadorCartaJson.parse("12-espadas")));
        gp.repartoInicial(mano1, mano2, mesa);

        assertThrows(IllegalStateException.class, () -> gp.finalizarPartida());
    }

    @Test
    public void testFinalizarPartidaMesaInvalida() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        // reparto con mesa que suma 7 (por ejemplo 2+5)
        ArrayList<Carta> mano1 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("2-copas"), AdaptadorCartaJson.parse("3-bastos")));
        ArrayList<Carta> mano2 = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("4-oros"), AdaptadorCartaJson.parse("5-copas"), AdaptadorCartaJson.parse("6-bastos")));
        ArrayList<Carta> mesaInvalid = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("2-espadas"), AdaptadorCartaJson.parse("5-bastos"), AdaptadorCartaJson.parse("1-copas"), AdaptadorCartaJson.parse("3-oros")));
        gp.repartoInicial(mano1, mano2, mesaInvalid);

        // vaciar las manos para permitir finalizar
        for (Carta c : new ArrayList<>(gp.getJugador1().getMano())) gp.getJugador1().tirarCarta(c);
        for (Carta c : new ArrayList<>(gp.getJugador2().getMano())) gp.getJugador2().tirarCarta(c);

        // suma inválida → debería lanzar
        assertThrows(IllegalStateException.class, () -> gp.finalizarPartida());
    }

    @Test
    public void testFinalizarPartidaMesaValidaAsignaAlReceptor() {
        GestorPartida gp2 = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        ArrayList<Carta> manoA = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("2-bastos"), AdaptadorCartaJson.parse("3-copas"), AdaptadorCartaJson.parse("4-oros")));
        ArrayList<Carta> manoB = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("5-espadas"), AdaptadorCartaJson.parse("6-copas"), AdaptadorCartaJson.parse("10-bastos")));
        ArrayList<Carta> mesaValid = new ArrayList<>(java.util.List.of(AdaptadorCartaJson.parse("1-oros"), AdaptadorCartaJson.parse("1-copas"), AdaptadorCartaJson.parse("1-espadas"), AdaptadorCartaJson.parse("7-bastos")));
        gp2.repartoInicial(manoA, manoB, mesaValid);

        for (Carta c : new ArrayList<>(gp2.getJugador1().getMano())) gp2.getJugador1().tirarCarta(c);
        for (Carta c : new ArrayList<>(gp2.getJugador2().getMano())) gp2.getJugador2().tirarCarta(c);
        // antes de finalizar, comprobar número de cartas capturadas
        int before = gp2.getJugador1().getNumeroCartasCapturadas();
        gp2.finalizarPartida();
        // receptor debe ser jugador actual (jugador1) y habrá recibido las cartas de la mesa
        assertTrue(gp2.getJugador1().getNumeroCartasCapturadas() > before);
    }

    @Test
    public void testCalcularPuntuacionFinalRamas() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        Jugador p1 = gp.getJugador1();
        Jugador p2 = gp.getJugador2();
        p1.sumarEscoba(); // +1
        p1.sumarEscoba(); // +1 -> total 2
        // guindis (7 de oros)
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-oros")); // tiene guindis +1
        // sietes (4 sietes -> +2)
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-copas"));
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-bastos"));
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-espadas"));
        for (int i = 0; i < 9; i++) {
            p1.agregarCartaACartas(AdaptadorCartaJson.parse("1-oros"));
        }
        p2.agregarCartaACartas(AdaptadorCartaJson.parse("1-copas"));
        int puntos = gp.calcularPuntuacionFinal(p1);
        // 2escobas -> 2 + sietes==4 -> +3 + oros==10 -> +2 + mayor número cartas capturadas -> +1   + mayoria sietes -> +1 = 2+3+2+1+1=9
        assertEquals(9, puntos);
    }

    @Test
    public void testCalcularPuntuacionFinalSietesMayorDaUnPunto() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        Jugador p1 = gp.getJugador1();
        Jugador p2 = gp.getJugador2();

        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-copas"));
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("7-bastos"));
        p1.agregarCartaACartas(AdaptadorCartaJson.parse("1-copas"));

        p2.agregarCartaACartas(AdaptadorCartaJson.parse("7-espadas"));
        p2.agregarCartaACartas(AdaptadorCartaJson.parse("1-bastos"));
        p2.agregarCartaACartas(AdaptadorCartaJson.parse("2-bastos"));
        int puntos = gp.calcularPuntuacionFinal(p1);
        assertEquals(1, puntos);
    }

    @Test
    public void testReiniciarTurnosNoValidos() {
        GestorPartida gp = new GestorPartida(new Jugador("P1"), new Jugador("P2"));
        assertThrows(IllegalStateException.class, () -> gp.reiniciarTurnos());

        gp.finalizarPartida();
        assertThrows(IllegalStateException.class, () -> gp.reiniciarTurnos());
    }

    @Test
    public void testReiniciarTurnosValidoReseteaContadorYJugadorActual() {
        GestorPartida gp = new GestorPartida(j1, j2);
        for (int i = 0; i < 6; i++) gp.cambiarTurno();

        assertTrue(gp.rondaTerminada());
        assertEquals(6, gp.getTurnosJugados());

        gp.reiniciarTurnos();
        assertEquals(0, gp.getTurnosJugados());
        assertEquals(gp.getJugador1(), gp.getJugadorActual());
    }

    @Test
    public void testCambiarTurnoPartidaFinalizadaLanza() {
        GestorPartida gp = new GestorPartida(j1, j2);
        gp.finalizarPartida();
        assertThrows(IllegalStateException.class, () -> gp.cambiarTurno());
    }

    @Test
    public void testJugarCartaAlCompletarRondaEstableceJugador1ComoActual() {
        GestorPartida gp = new GestorPartida(j1, j2);
        for (int i = 0; i < 5; i++) gp.cambiarTurno();

        assertEquals(5, gp.getTurnosJugados());
        // jugador actual debe ser jugador2
        assertEquals(gp.getJugador2(), gp.getJugadorActual());

        // darle una carta al jugador actual y que la juegue
        Carta carta = AdaptadorCartaJson.parse("1-oros");
        gp.getJugadorActual().agregarCartaAMano(carta);
        gp.jugarCarta(carta, new ArrayList<>());

        // tras jugar la carta la ronda se completa y debe establecer jugador actual a jugador1
        assertEquals(6, gp.getTurnosJugados());
        assertEquals(gp.getJugador1(), gp.getJugadorActual());
        assertTrue(gp.getCartasMesa().contains(carta));
    }
}