package uva.tds.ejecutores;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugada;
import uva.tds.entidades.Palo;
import uva.tds.entidades.Partida;
import uva.tds.entidades.Ronda;
import uva.tds.entidades.Turno;

/**
 * Clase de tests para EjecutarPartida.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class EjecutarPartidaTest {
    
    @Test
    void testNoValidoEjecutarPartidaPartidaNula() {
        assertThrows(IllegalArgumentException.class, () -> 
            new EjecutarPartida(null));
    }
    
    @Test
    void testNoValidoEjecutarPartidaConPartidaSinNombres() {
        Partida partida = new Partida();
        // No se añaden nombres
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertThrows(NullPointerException.class, () -> 
            ejecutor.ejecutarPartidaCompleta());
    }
    
    @Test
    void testNoValidoEjecutarPartidaConPartidaSinMesaInicial() {
        Partida partida = new Partida();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertThrows(NullPointerException.class, () -> 
            ejecutor.ejecutarPartidaCompleta());
    }
    
    @Test
    void testNoValidoEjecutarPartidaConPartidaSinManosJ1() {
        Partida partida = new Partida();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        ArrayList<Carta> mesaInicial = new ArrayList<>();
        mesaInicial.add(new Carta(Palo.OROS, 1));
        mesaInicial.add(new Carta(Palo.OROS, 2));
        mesaInicial.add(new Carta(Palo.OROS, 3));
        mesaInicial.add(new Carta(Palo.OROS, 4));
        partida.añadirMesaInicial(mesaInicial);

        // No se añaden mano Jugador1
        ArrayList<ArrayList<Carta>> manosJugador2 = new ArrayList<>();
        ArrayList<Carta> mano2 = new ArrayList<>();
        mano2.add(new Carta(Palo.BASTOS, 1));
        manosJugador2.add(mano2);
        partida.añadirManoJugador2(manosJugador2);
        
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertThrows(NullPointerException.class, () -> 
            ejecutor.ejecutarPartidaCompleta());
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoEjecutarPartidaConPartidaSinManosJ2() {
        Partida partida = new Partida();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        ArrayList<Carta> mesaInicial = new ArrayList<>();
        mesaInicial.add(new Carta(Palo.OROS, 1));
        mesaInicial.add(new Carta(Palo.OROS, 2));
        mesaInicial.add(new Carta(Palo.OROS, 3));
        mesaInicial.add(new Carta(Palo.OROS, 4));
        partida.añadirMesaInicial(mesaInicial);

        // No se añaden mano Jugador2
        ArrayList<ArrayList<Carta>> manosJugador1 = new ArrayList<>();
        ArrayList<Carta> mano1 = new ArrayList<>();
        mano1.add(new Carta(Palo.BASTOS, 1));
        manosJugador1.add(mano1);
        partida.añadirManoJugador1(manosJugador1);
        
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertThrows(NullPointerException.class, () -> 
            ejecutor.ejecutarPartidaCompleta());
    }
    
    @Test
    void testNoValidoEjecutarPartidaConPartidaSinJugadas() {
        Partida partida = crearPartidaBasicaSinJugadas();
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertThrows(NullPointerException.class, () -> 
            ejecutor.ejecutarPartidaCompleta());
    }
    
    @Test
    void testValidoConstructorConPartidaValida() {
        Partida partida = new Partida();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        assertDoesNotThrow(() -> new EjecutarPartida(partida));
    }
    
    @Test
    void testValidoGetPartidaDevuelvePartidaCorrecta() {
        Partida partida = new Partida();
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        
        assertEquals(partida, ejecutor.getPartida());
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoEjecutarPartidaConManoInicialIncorrecta() {
        Partida partida = crearPartidaBaseConManos(2);

        EjecutarPartida ejecutor = new EjecutarPartida(partida);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ejecutor.ejecutarPartidaCompleta());
        assertTrue(ex.getMessage().contains("Número incorrecto de cartas")
                || ex.getMessage().contains("Número incorrecto")
                || ex.getMessage().contains("Número incorrecto de cartas para el reparto inicial"),
                "Se esperaba excepción por número incorrecto de cartas en reparto inicial");
    }


    @Test
    @Tag("Cobertura")
    void testNoValidoEjecutarPartidaConJugadaCapturaCartaNoEnMesa() {
        Partida partida = crearPartidaBaseConManos(3);

        partida.anadirRondas(crearJugadasCapturaInvalida(partida));

        EjecutarPartida ejecutor = new EjecutarPartida(partida);

        assertThrows(IllegalArgumentException.class,
                () -> ejecutor.ejecutarPartidaCompleta());
    }


    @Test
    @Tag("Cobertura")
    void testNoValidoEjecutarPartidaConJugadasInsuficientes() {
        // partida bien formada excepto que faltan rondas (solo 5 en lugar de 6)
        Partida partida = crearPartidaBaseConManos(3);

        // añadimos solo 5 rondas para provocar IndexOutOfBounds al intentar acceder a la ronda 6
        partida.anadirRondas(crearJugadasInsuficientesPorRonda(partida));

        EjecutarPartida ejecutor = new EjecutarPartida(partida);

        // La ejecución debe lanzar IndexOutOfBoundsException (faltan rondas) o IllegalArgumentException
        Throwable t = assertThrows(Throwable.class, () -> ejecutor.ejecutarPartidaCompleta());
        assertTrue(t instanceof IndexOutOfBoundsException || t instanceof IllegalArgumentException,
                "Se esperaba IndexOutOfBoundsException o IllegalArgumentException, pero se lanzó: " + t.getClass().getName());
    }


    @Test
    @Tag("Cobertura")
    void testValidoEjecutarPartidaCompletaDesdeJSON() throws IOException {
        // Lectura de la partida de ejemplo incluida en recursos: esto debe ejecutar todas las rondas,
        // llamar a gestor.avanzarRonda(), gestor.repartoRonda(...) y gestor.finalizarPartida().
        LectorPartidaJSON lector = new LectorPartidaJSON("src/test/resources/partida_escoba1.json");
        Partida partida = lector.obtenerPartida("partida_json1");

        EjecutarPartida ejecutor = new EjecutarPartida(partida);

        assertDoesNotThrow(() -> ejecutor.ejecutarPartidaCompleta());
    }
    
    

    // ========== MÉTODOS AUXILIARES ==========
     private Partida crearPartidaBaseConManos(int mano1PrimeraSize) {
        Partida partida = new Partida();

        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);

        // mesa inicial 4 cartas válidas
        ArrayList<Carta> mesaInicial = new ArrayList<>();
        mesaInicial.add(new Carta(Palo.OROS, 5));
        mesaInicial.add(new Carta(Palo.OROS, 11));
        mesaInicial.add(new Carta(Palo.BASTOS, 10));
        mesaInicial.add(new Carta(Palo.BASTOS, 11));
        partida.añadirMesaInicial(mesaInicial);

        // crear manos para 6 rondas
        ArrayList<ArrayList<Carta>> manosJugador1 = new ArrayList<>();
        ArrayList<ArrayList<Carta>> manosJugador2 = new ArrayList<>();

        for (int ronda = 0; ronda < 6; ronda++) {
            ArrayList<Carta> mano1 = new ArrayList<>();
            // la primera ronda podemos controlar el tamaño pedido por el test
            if (ronda == 0) {
                for (int i = 0; i < mano1PrimeraSize; i++) {
                    mano1.add(new Carta(Palo.COPAS, i + 1));
                }
            } else {
                mano1.add(new Carta(Palo.COPAS, 1));
                mano1.add(new Carta(Palo.COPAS, 2));
                mano1.add(new Carta(Palo.COPAS, 3));
            }
            manosJugador1.add(mano1);

            ArrayList<Carta> mano2 = new ArrayList<>();
            mano2.add(new Carta(Palo.OROS, 7));
            mano2.add(new Carta(Palo.COPAS, 2));
            mano2.add(new Carta(Palo.OROS, 3));
            manosJugador2.add(mano2);
        }

        partida.añadirManoJugador1(manosJugador1);
        partida.añadirManoJugador2(manosJugador2);

        // Añadir rondas por defecto (6 rondas, 6 turnos cada una) para que EjecutarPartida
        // pueda validar el reparto y las jugadas incluso si el test no añade rondas explícitas.
        List<Ronda> rondas = new ArrayList<>();
        for (int r = 0; r < 6; r++) {
            List<Turno> turnos = new ArrayList<>();
            ArrayList<Carta> m1 = manosJugador1.get(r);
            ArrayList<Carta> m2 = manosJugador2.get(r);
            for (int t = 0; t < 6; t++) {
                Carta juega = (t % 2 == 0)
                        ? (m1.size() > 0 ? m1.get(0) : new Carta(Palo.OROS, 1))
                        : (m2.size() > 0 ? m2.get(0) : new Carta(Palo.COPAS, 2));
                List<Carta> captura = new ArrayList<>();
                List<Carta> mesaRes = new ArrayList<>();
                mesaRes.add(juega);
                Jugada jug = new Jugada(juega, captura, mesaRes);
                turnos.add(new Turno(t + 1, jug));
            }
            rondas.add(new Ronda(r + 1, turnos));
        }
        partida.anadirRondas(rondas);

        return partida;
    }

    private List<Ronda> crearJugadasCapturaInvalida(Partida partida) {
        List<Ronda> rondas = new ArrayList<>();
        ArrayList<ArrayList<Carta>> manos1 = partida.getManosJugador1();
        ArrayList<ArrayList<Carta>> manos2 = partida.getManosJugador2();
        for (int r = 0; r < 6; r++) {
            List<Turno> turnos = new ArrayList<>();
            // construimos 6 turnos; el primero será el problema: captura contiene carta no en mesa
            // escoger juega desde las manos si están disponibles
            Carta juega1 = !manos1.isEmpty() && manos1.get(Math.min(r, manos1.size()-1)).size() > 0
                            ? manos1.get(Math.min(r, manos1.size()-1)).get(0)
                            : new Carta(Palo.OROS, 1);
            List<Carta> capturaInvalida = new ArrayList<>();
            // elegimos una carta q NO está en la mesa inicial (7 de oros)
            capturaInvalida.add(new Carta(Palo.OROS, 7));
            List<Carta> mesaRes1 = new ArrayList<>();
            mesaRes1.add(juega1);
            Jugada jug1 = new Jugada(juega1, capturaInvalida, mesaRes1);
            turnos.add(new Turno(1, jug1));

            // rellenar los demás turnos con jugadas válidas (simples)
            for (int t = 1; t < 6; t++) {
                Carta juega = (t % 2 == 0)
                        ? ( !manos1.isEmpty() && manos1.get(Math.min(r, manos1.size()-1)).size() > 0 ? manos1.get(Math.min(r, manos1.size()-1)).get(0) : new Carta(Palo.COPAS,1) )
                        : ( !manos2.isEmpty() && manos2.get(Math.min(r, manos2.size()-1)).size() > 0 ? manos2.get(Math.min(r, manos2.size()-1)).get(0) : new Carta(Palo.ESPADAS,2) );
                List<Carta> captura = new ArrayList<>();
                List<Carta> mesaRes = new ArrayList<>();
                mesaRes.add(juega);
                Jugada jug = new Jugada(juega, captura, mesaRes);
                turnos.add(new Turno(t + 1, jug));
            }
            rondas.add(new Ronda(r + 1, turnos));
        }
        return rondas;
    }

    private List<Ronda> crearJugadasInsuficientesPorRonda(Partida partida) {
        List<Ronda> rondas = new ArrayList<>();
        // crear solo 5 rondas
        for (int r = 0; r < 5; r++) {
            List<Turno> turnos = new ArrayList<>();
            // añadir 6 turnos mínimos por ronda para que el fallo sea por falta de rondas, no de turnos
            Carta juega = new Carta(Palo.OROS, 1);
            for (int t = 0; t < 6; t++) {
                List<Carta> captura = new ArrayList<>();
                List<Carta> mesaRes = new ArrayList<>();
                mesaRes.add(juega);
                Jugada jug = new Jugada(juega, captura, mesaRes);
                turnos.add(new Turno(t + 1, jug));
            }
            rondas.add(new Ronda(r + 1, turnos));
        }
        return rondas;
    }

    private Partida crearPartidaBasicaSinJugadas() {
        Partida partida = new Partida();
        
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        
        ArrayList<Carta> mesaInicial = new ArrayList<>();
        mesaInicial.add(new Carta(Palo.OROS, 1));
        mesaInicial.add(new Carta(Palo.OROS, 2));
        mesaInicial.add(new Carta(Palo.OROS, 3));
        mesaInicial.add(new Carta(Palo.OROS, 4));
        partida.añadirMesaInicial(mesaInicial);
        
        // Crear manos para las 6 rondas
        ArrayList<ArrayList<Carta>> manosJugador1 = new ArrayList<>();
        ArrayList<ArrayList<Carta>> manosJugador2 = new ArrayList<>();
        
        for (int ronda = 0; ronda < 6; ronda++) {
            ArrayList<Carta> mano1 = new ArrayList<>();
            mano1.add(new Carta(Palo.COPAS, 1));
            mano1.add(new Carta(Palo.COPAS, 2));
            mano1.add(new Carta(Palo.COPAS, 3));
            manosJugador1.add(mano1);
            
            ArrayList<Carta> mano2 = new ArrayList<>();
            mano2.add(new Carta(Palo.BASTOS, 1));
            mano2.add(new Carta(Palo.BASTOS, 2));
            mano2.add(new Carta(Palo.BASTOS, 3));
            manosJugador2.add(mano2);
        }
        
        partida.añadirManoJugador1(manosJugador1);
        partida.añadirManoJugador2(manosJugador2);
        
        // NO se añaden jugadas
        return partida;
    }


    
}