package uva.tds.ejecutores;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugada;
import uva.tds.entidades.Partida;
import uva.tds.entidades.Ronda;
import uva.tds.entidades.Turno;

/**
 * Clase para leer una partida desde un archivo JSON.
 * Proporciona métodos para extraer información de la partida.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */

public class LectorPartidaJSON {

    private JSONObject partidaJSON;

    /**
     * Constructor de LectorPartidaJSON que lee el archivo JSON de la ruta dada.
     * 
     * @param rutaArchivo Ruta del archivo JSON que contiene la partida.
     * @throws IOException Si ocurre un error al leer el archivo.
     * @throws IllegalArgumentException Si la ruta es nula o vacía.
     * @throws FileNotFoundException Si el archivo no se encuentra.
     */
    public LectorPartidaJSON(String rutaArchivo) throws IOException {
         if (rutaArchivo == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula");
        }/*  */
        if (rutaArchivo.isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }

        try {
            this.partidaJSON = leerArchivoJSON(rutaArchivo);
        } catch (IOException e) {
            FileNotFoundException fnf = new FileNotFoundException();
            fnf.initCause(e);
            throw fnf;
        }
    }

    /**
     * Obtiene el objeto JSON que representa la partida.
     * 
     * @return Objeto JSONObject de la partida.
     */
    public JSONObject getPartidaJSON(){
        return this.partidaJSON;
    }

    /**
     * Obtiene una instancia de Partida con los datos extraídos del JSON.
     * 
     * @param idPartida Identificador de la partida.
     * @return Instancia de Partida con los datos cargados.
     */
    public Partida obtenerPartida(String idPartida){
        String j1 = extraerNombresJugadores().get(0);
        String j2 = extraerNombresJugadores().get(1);

        Partida partida = new Partida(idPartida,LocalDate.now(),j1,j2);
        partida.añadirJugadores(extraerNombresJugadores());
        partida.añadirMesaInicial(extraerMesaInicial());

        ArrayList<ArrayList<Carta>> manos1 = new ArrayList<>();
        ArrayList<ArrayList<Carta>> manos2 = new ArrayList<>();
        for(int ronda = 1; ronda <= 6; ronda++){
            manos1.add(extraerManoJugador(ronda, j1));
            manos2.add(extraerManoJugador(ronda, j2));
        }
        partida.añadirManoJugador1(manos1);
        partida.añadirManoJugador2(manos2);

        List<Ronda> rondas = new ArrayList<>();
        for(int numeroRonda = 1; numeroRonda <= 6; numeroRonda++){
            List<Turno> turnos = new ArrayList<>();
            for(int numeroTurno = 1; numeroTurno <=6; numeroTurno++){
                Jugada jug = extraerJugada(numeroRonda, numeroTurno);
                Turno t = new Turno(numeroTurno, jug);
                turnos.add(t);
            }
            rondas.add(new Ronda(numeroRonda, turnos));
        }
        partida.anadirRondas(rondas);

        return partida;
    }

    /**
     * Convierte una cadena de texto que representa una carta en un objeto Carta.
     * 
     * @param strCarta String que representa la carta.
     * @return Objeto Carta correspondiente a la cadena.
     */
    public Carta convertirStringACarta(String strCarta) {
        if (strCarta == null || strCarta.isEmpty()) {
            throw new IllegalArgumentException("La cadena de entrada no puede ser nula o vacía.");
        }
        return AdaptadorCartaJson.parse(strCarta);
    }

    /**
     * Convierte un array JSON de cadenas que representan cartas en una lista de objetos Carta.
     * 
     * @param jsonArrayStr Array JSON en forma de cadena.
     * @return Lista de objetos Carta.
     */
    public ArrayList<Carta> convertirArrayJSONaCartas(String jsonArrayStr) {
        if (jsonArrayStr == null) {
            throw new IllegalArgumentException("El array JSON no puede ser nulo.");
        }
        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        ArrayList<Carta> cartas = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String cartaString = jsonArray.getString(i);
            Carta carta = AdaptadorCartaJson.parse(cartaString);
            cartas.add(carta);
        }
        return cartas;
    }

    /**
     * Lee un archivo JSON desde la ruta especificada y devuelve un objeto JSONObject.
     * 
     * @param rutaArchivo Ruta del archivo JSON.
     * @return Objeto JSONObject que representa el contenido del archivo.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public JSONObject leerArchivoJSON(String rutaArchivo) throws IOException {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(rutaArchivo)));
            return new JSONObject(contenido);
        } catch (IOException ex) {
            String candidate = rutaArchivo;
            if (candidate.startsWith("src/test/resources/")) {
                candidate = candidate.substring("src/test/resources/".length());
            } else if (candidate.startsWith("src/test/java/")) {
                candidate = candidate.substring("src/test/java/".length());
            } else if (candidate.startsWith("src/")) {
                int idx = candidate.indexOf('/', 4);
                if (idx != -1) candidate = candidate.substring(idx + 1);
            }
            candidate = candidate.replace('\\', '/');

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(candidate);

            if (is == null) {
                String filename = candidate;
                int lastSlash = candidate.lastIndexOf('/');
                if (lastSlash >= 0) filename = candidate.substring(lastSlash + 1);
                is = cl.getResourceAsStream("uva/tds/resources/" + filename);
            }

            if (is == null) {
                throw ex;
            }

            try (InputStream in = is) {
                byte[] bytes = in.readAllBytes();
                String contenido = new String(bytes);
                return new JSONObject(contenido);
            }
        }
    }


    /**
     * Extrae los nombres de los jugadores del JSON de la partida.
     * 
     * @return Lista de nombres de los jugadores.
     */
    public ArrayList<String> extraerNombresJugadores() {

        ArrayList<String> nombres = new ArrayList<>();
        JSONObject manos = partidaJSON.getJSONObject("partida")
                       .getJSONArray("rondas")
                       .getJSONObject(0)
                       .getJSONObject("inicio")
                       .getJSONObject("manos");   
        for(String nombre : manos.keySet()){
            nombres.add(nombre);
        }

         if (nombres.size() != 2) {
            throw new IllegalArgumentException(
                    "El JSON debe contener exactamente 2 jugadores en 'manos' de la primera ronda");
        }
        
        return nombres;
    }

    /**
     * Extrae la mano de un jugador en una ronda específica.
     * 
     * @param numeroRonda Número de la ronda.
     * @param nombreJugador Nombre del jugador.
     * @return Lista de cartas que representa la mano del jugador en la ronda especificada.
     */
    public ArrayList<Carta> extraerManoJugador(int numeroRonda, String nombreJugador) {
        if (numeroRonda < 1 || numeroRonda > 6) {
            throw new IllegalArgumentException("Ronda fuera de rango, tiene que estar entre 1 y 6.");
        }
        if (nombreJugador == null) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo.");
        }

        JSONObject manos = partidaJSON.getJSONObject("partida")
                    .getJSONArray("rondas")
                    .getJSONObject(numeroRonda - 1)  
                    .getJSONObject("inicio")
                    .getJSONObject("manos");  

        if (!manos.has(nombreJugador)) {
            throw new IllegalArgumentException("El jugador no existe en la partida.");
        }

        ArrayList<Carta> mano = new ArrayList<>();
        JSONArray cartasJSON = manos.getJSONArray(nombreJugador);
        for (int i = 0; i < cartasJSON.length(); i++) {
            String cartaStr = cartasJSON.getString(i);
            Carta carta = convertirStringACarta(cartaStr);
            mano.add(carta);
        }
        return mano;
    }

    /**
     * Extrae la mesa inicial de la partida desde el JSON.
     * 
     * @return Lista de cartas que representa la mesa inicial.
     */
    public ArrayList<Carta> extraerMesaInicial(){
        ArrayList<Carta> mano = new ArrayList<>();
        JSONArray cartasJSON = partidaJSON.getJSONObject("partida")
                       .getJSONArray("rondas")
                       .getJSONObject(0)
                       .getJSONObject("inicio")
                       .getJSONArray("mesa_inicial");  


        for(int i = 0; i < cartasJSON.length(); i++){
            String cartaStr = cartasJSON.getString(i);
            Carta carta = convertirStringACarta(cartaStr);
            mano.add(carta);
        }
        return mano;
    }

    /**
     * Extrae la jugada de una ronda y turno específicos desde el JSON.
     * 
     * @param ronda Número de la ronda.
     * @param turno Número del turno.
     * @return Lista que contiene la carta jugada, capturadas y la mesa resultante.
     */
    public Jugada extraerJugada(int ronda, int turno){
        if(ronda < 1 || ronda > 6){
            throw new IllegalArgumentException("Ronda fuera de rango, tiene que estar entre 1 y 6.");
        }
        if(turno < 1 || turno > 6){
            throw new IllegalArgumentException("Turno fuera de rango,  tiene que estar entre 1 y 6.");
        }

        JSONObject jugadaObj = partidaJSON.getJSONObject("partida")
                                .getJSONArray("rondas")
                                .getJSONObject(ronda - 1)
                                .getJSONArray("jugadas")
                                .getJSONObject(turno - 1);

        String juegaStr = jugadaObj.getString("juega");
        Carta juega = convertirStringACarta(juegaStr);

        ArrayList<Carta> captura = new ArrayList<>();
        JSONArray capturaArr = jugadaObj.getJSONArray("captura");
        for (int i = 0; i < capturaArr.length(); i++) {
            String cStr = capturaArr.getString(i);
            captura.add(convertirStringACarta(cStr));
        }

        ArrayList<Carta> resultante = new ArrayList<>();
        JSONArray resulArr = jugadaObj.getJSONArray("mesa_resultante");
        for (int i = 0; i < resulArr.length(); i++) {
            String resulStr = resulArr.getString(i);
            resultante.add(convertirStringACarta(resulStr));
        }
        return new Jugada(juega, captura, resultante);
    }

}
