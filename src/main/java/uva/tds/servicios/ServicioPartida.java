package uva.tds.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

import uva.tds.ejecutores.EjecutarPartida;
import uva.tds.entidades.AdaptadorPartida;
import uva.tds.entidades.Jugador;
import uva.tds.entidades.Partida;
import uva.tds.entidades.ResumenPartida;
import uva.tds.interfaces.RepositorioPartida;

/**
 * Clase ServicioPartida que gestiona la lógica de negocio, persistencia, validación,
 * y obtención de resultados y estadísticas de las partidas de la Escoba.
 *
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class ServicioPartida {

    private final RepositorioPartida repositorioPartida;

    /**
     * Constructor de ServicioPartida.
     * 
     * @param repositorioPartida repositorio de partidas
     * @throws IllegalArgumentException si repositorioPartida es null
     */
    public ServicioPartida(RepositorioPartida repositorioPartida) {
        if (repositorioPartida == null) {
            throw new IllegalArgumentException("El repositorio de partida no puede ser nulo");
        }
        this.repositorioPartida = repositorioPartida;
    }

    public RepositorioPartida getRepositorioPartida() {
        return repositorioPartida;
    }
    
    /**
     * Obtiene una partida del repositorio por su id.
     * 
     * @param id id de la partida 
     * @return partida con el id especificado
     * @throws IllegalArgumentException si id es null o vacío
     */
    public Partida obtenerPartida(String id) {
        validarIdPartida(id);
        return repositorioPartida.cargar(id);
    }

    /**
     * Guarda una partida en el repositorio.
     * 
     * @param partida partida a guardar
     * @throws IllegalArgumentException si partida es null
     * @throws IllegalStateException si la partida no está completa
     */
    public void guardarPartida(Partida partida) {
        validarPartidaNoNula(partida);
        if (!partida.isCompleta()) {
            throw new IllegalStateException("Solo se pueden guardar partidas completas");
        }
        repositorioPartida.guardar(partida);
    }

    /**
     * Actualiza una partida en el repositorio.
     * 
     * @param partida partida a actualizar
     * @throws IllegalArgumentException si partida es null
     */
    public void actualizarPartida(Partida partida) {
        validarPartidaNoNula(partida);
        repositorioPartida.actualizar(partida);
    }
    
    /**
     * Elimina una partida del repositorio por su id.
     * 
     * @param id id de la partida a eliminar
     * @throws IllegalArgumentException si id es null o vacío
     */
    public void eliminarPartida(String id) {
        validarIdPartida(id);
        repositorioPartida.eliminar(id);
    }

    /**
     * Valida una partida: la reproduce y, si la partida contiene un resumen compara los resultados.
     * Si la partida no contiene resumen, crea y asigna el resumen calculado.
     *
     * @param partida partida a validar (no nula)
     * @return true si la ejecución es consistente con el resumen (o si se ha creado el resumen),
     *         false si existe un resumen y no coincide con la reproducción
     * @throws IllegalArgumentException si partida es null
     */
    public boolean validarPartida(Partida partida) {
        validarPartidaNoNula(partida);
        GestorPartida gestor = validarYObtenerGestor(partida);
        AdaptadorPartida adaptador = new AdaptadorPartida();

        String id;
        LocalDate fecha;
        id = partida.getId();
        fecha = partida.getFecha();


        ResumenPartida calculado = adaptador.convertir(id, fecha, gestor);
        ResumenPartida esperado = partida.getResumenPartida();

        // Si no hay resumen en la partida, asignamos el calculado y consideramos válido
        if (esperado == null) {
            partida.setResumenPartida(calculado);
            return true;
        }

        boolean metricasCoinciden = compararResultados(calculado, esperado);
        if (!metricasCoinciden) {
            return false;
        }

        String gCalc = calculado.getGanador();
        String gExp = esperado.getGanador();
        return Objects.equals(gCalc, gExp);
    }

    /**
     * Reproduce la partida y devuelve la puntuación final de un jugador.
     *
     * @param partida partida a reproducir (no nula)
     * @param nombreJugador nombre del jugador cuya puntuación se desea obtener (no nulo ni vacío)
     * @return puntuación final del jugador
     * @throws IllegalArgumentException si partida es null, o si nombreJugador es null o vacío,
     * or si el jugador no participa en la partida
     */
    public int calcularPuntos(Partida partida, String nombreJugador) {
        validarPartidaNoNula(partida);
        if (nombreJugador == null || nombreJugador.isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo o vacío");
        }

        GestorPartida gestor = validarYObtenerGestor(partida);

        if (gestor.getJugador1().getNombre().equals(nombreJugador)) {
            return gestor.calcularPuntuacionFinal(gestor.getJugador1());
        } else if (gestor.getJugador2().getNombre().equals(nombreJugador)) {
            return gestor.calcularPuntuacionFinal(gestor.getJugador2());
        } else {
            throw new IllegalArgumentException("El jugador " + nombreJugador + " no participa en la partida");
        }
    }

    /**
     * Obtiene las estadísticas de partidas jugadas entre dos fechas.
     * 
     * @param fechaInicio Fecha de inicio de la búsqueda
     * @param fechaFin Fecha de fin de la búsqueda
     * @return ArrayList con las estadísticas: [total partidas, total jugadores participantes]
     * @throws IllegalArgumentException Si alguna de las fechas es null 
     * @throws IllegalArgumentException Si fechaInicio es posterior a fechaFin
     */
    public ArrayList<Integer> obtenerEstadisticasEntreFechas(LocalDate fechaInicio, LocalDate fechaFin){
        if(fechaInicio == null || fechaFin == null){
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if(fechaInicio.isAfter(fechaFin)){
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        String inicio = fechaInicio.toString();
        String fin = fechaFin.toString();
        ArrayList<Partida> partidas = (ArrayList<Partida>) repositorioPartida.obtenerPartidasPorFecha(inicio, fin);
        if (partidas == null) partidas = new ArrayList<>();
        
        ArrayList<Integer> resultado = new ArrayList<>();
        ArrayList<String> jugadores = obtenerTodosJugadores(partidas);

        int totalPartidas = partidas.size();
        resultado.add(totalPartidas);
        resultado.add(jugadores.size());

        return resultado;
    }

    
    /**
     * Obtiene las estadísticas de un jugador específico.
     * 
     * @param nombreJugador Nombre del jugador del que se desean obtener las estadísticas
     * @return ArrayList con las estadísticas: [partidas ganadas, partidas perdidas, total partidas, guindis]
     * @throws IllegalArgumentException si nombreJugador es null o vacío
     */
    public ArrayList<Integer> obtenerEstadisticasJugador(String nombreJugador){
        if(nombreJugador == null || nombreJugador.isEmpty()){
            throw new IllegalArgumentException("El nombre del jugador no puede ser nulo o vacío");
        }
        ArrayList<Partida> partidas = (ArrayList<Partida>) repositorioPartida.obtenerPartidasPorJugador(nombreJugador);
        ArrayList<Integer> resultado = new ArrayList<>();
        int totalPartidas = partidas.size();
        int ganadas = 0;
        int perdidas = 0;
        int guindis = 0;

        for(int i=0; i<totalPartidas; i++){
            Partida partida = partidas.get(i);
            ResumenPartida resumen = partida.getResumenPartida();
            if(resumen != null && resumen.isCompleta()){
                if(resumen.getGanador().equals(nombreJugador)){ganadas++;} 
                else {perdidas++;}

                if(nombreJugador.equals(resumen.getJugador1())){
                    if(resumen.isGuindisJugador1()){guindis++;}
                } else {
                    if(resumen.isGuindisJugador2()){guindis++;}
                }
            }
        }

        resultado.add(ganadas);
        resultado.add(perdidas);
        resultado.add(totalPartidas);
        resultado.add(guindis);

        return resultado;
    }

    /**
     * Obtiene un resumen de la partida con las estadísticas de ambos jugadores.
     * 
     * @param idPartida id de la partida
     * @return ArrayList con dos ArrayLists internos, uno por cada jugador, con las estadísticas:
     *         [escobas, cartas capturadas, oros, sietes, guindis (1 si tiene, 0 si no), puntos]
     * @throws IllegalArgumentException si idPartida es null o vacío
     * @throws IllegalArgumentException si no existe una partida con el id proporcionado
     */
    public ArrayList<ArrayList<Integer>> resumenPartida(String idPartida) {
        validarIdPartida(idPartida);
        Partida partida = repositorioPartida.cargar(idPartida);
        if (partida == null) {
            throw new IllegalArgumentException("No existe una partida con el id proporcionado");
        }

        ArrayList<ArrayList<Integer>> resumen = new ArrayList<>();
        ArrayList<Integer> jugador1Stats = new ArrayList<>();
        ArrayList<Integer> jugador2Stats = new ArrayList<>();

        jugador1Stats.add(partida.getEscobasJugador1());
        jugador1Stats.add(partida.getCartasCapturadasJugador1());
        jugador1Stats.add(partida.getOrosJugador1());
        jugador1Stats.add(partida.getSietesJugador1());
        jugador1Stats.add(partida.isGuindisJugador1() ? 1 : 0);
        jugador1Stats.add(partida.getPuntosJugador1());

        jugador2Stats.add(partida.getEscobasJugador2());
        jugador2Stats.add(partida.getCartasCapturadasJugador2());
        jugador2Stats.add(partida.getOrosJugador2());
        jugador2Stats.add(partida.getSietesJugador2());
        jugador2Stats.add(partida.isGuindisJugador2() ? 1 : 0);
        jugador2Stats.add(partida.getPuntosJugador2());

        resumen.add(jugador1Stats);
        resumen.add(jugador2Stats);
        return resumen;
    }


    /**
     * Obtiene el top 10 de jugadores con más victorias en todas las partidas almacenadas.
     * 
     * @return ArrayList con los 10 jugadores con más victorias
     */
    public ArrayList<Jugador> obtenerTop10(){
        LocalDate fechaActual = LocalDate.now();
        String actual = fechaActual.toString();
        String inicio = "0001-01-01";

        ArrayList<Partida> partidas = (ArrayList<Partida>) repositorioPartida.obtenerPartidasPorFecha(inicio, actual);
        if (partidas == null) partidas = new ArrayList<>();
        ArrayList<String> jugadores = obtenerTodosJugadores(partidas);
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<Integer> victorias = new ArrayList<>();

        for (String jugador : jugadores) {
            int ganadas = 0;
            for (Partida p : partidas) {
                ResumenPartida resumen = p.getResumenPartida();
                if (resumen != null && resumen.isCompleta()) {
                    String ganador = null;
                    ganador = resumen.getGanador();
                    if (ganador != null && ganador.equals(jugador)) {
                        ganadas++;
                    }
                }
            }
            nombres.add(jugador);
            victorias.add(ganadas);
        }

        ordenarPorVictorias(nombres, victorias);

        ArrayList<Jugador> top10 = new ArrayList<>();
        int limite = Math.min(10, nombres.size());
        for (int i = 0; i < limite; i++) {
            top10.add(new Jugador(nombres.get(i)));
        }

        return top10;

    }

    /**
     * Ordena dos listas paralelas: una de nombres y otra de victorias, 
     * en orden descendente por victorias y alfabéticamente por nombre en caso de empate.
     * 
     * @param nombres Lista de nombres de jugadores
     * @param victorias Lista de victorias correspondientes a los jugadores
     */
    private void ordenarPorVictorias(ArrayList<String> nombres, ArrayList<Integer> victorias) {
        if (nombres == null || victorias == null) return;
        int n = nombres.size();
        for (int i = 0; i < n - 1; i++) {
            int indiceMejor = i;
            for (int j = i + 1; j < n; j++) {
                int vj = victorias.get(j).intValue();
                int vMejor = victorias.get(indiceMejor).intValue();
                if (vj > vMejor) {
                    indiceMejor = j;
                } else if (vj == vMejor) {
                    String nombreJ = nombres.get(j);
                    String nombreMejor = nombres.get(indiceMejor);
                    if (nombreJ.compareTo(nombreMejor) < 0) {
                        indiceMejor = j;
                    }
                }
            }
            if (indiceMejor != i) {
                String tmpNombre = nombres.get(i);
                nombres.set(i, nombres.get(indiceMejor));
                nombres.set(indiceMejor, tmpNombre);
                int tmpVict = victorias.get(i);
                victorias.set(i, victorias.get(indiceMejor));
                victorias.set(indiceMejor, tmpVict);
            }
        }
    }


    /**
     * Reproduce la partida y devuelve el GestorPartida resultante.
     * Metodo auxiliar para evitar repetir lógica entre validarPartida y calcularPuntos.
     * @param partida partida a reproducir
     * @return gestor de la partida reproducida
     */
    private GestorPartida validarYObtenerGestor(Partida partida) {
        EjecutarPartida ejecutor = new EjecutarPartida(partida);
        return ejecutor.ejecutarPartidaCompleta();
    }

    /**
     * Valida que el id de la partida no sea nulo ni vacío.
     * 
     * @param id id de la partida
     * @throws IllegalArgumentException si id es null o vacío
     */
    private void validarIdPartida(String id) {
       if (id == null || id.isEmpty()) {
           throw new IllegalArgumentException("El id no puede ser nulo o vacío");
       }
    }

    /**
     * Valida que la partida no sea nula.
     * 
     * @param partida partida a validar
     * @throws IllegalArgumentException si partida es null
     */
    private void validarPartidaNoNula(Partida partida) {
       if (partida == null) {
           throw new IllegalArgumentException("La partida no puede ser nula");
       }
    }

    /**
     * Compara dos resúmenes de partida para verificar si sus métricas coinciden.
     *
     * @param calculado resumen calculado a partir de la reproducción
     * @param esperado resumen esperado almacenado en la partida
     * @return true si las métricas coinciden, false en caso contrario
     */
    private boolean compararResultados(ResumenPartida calculado, ResumenPartida esperado) {
        // Comparar campos relevantes entre 'calculado' y 'esperado'
        if (calculado.getPuntosJugador1() != esperado.getPuntosJugador1()) return false;
        if (calculado.getPuntosJugador2() != esperado.getPuntosJugador2()) return false;

        if (calculado.getEscobasJugador1() != esperado.getEscobasJugador1()) return false;
        if (calculado.getEscobasJugador2() != esperado.getEscobasJugador2()) return false;

        if (calculado.getOrosJugador1() != esperado.getOrosJugador1()) return false;
        if (calculado.getOrosJugador2() != esperado.getOrosJugador2()) return false;

        if (calculado.getSietesJugador1() != esperado.getSietesJugador1()) return false;
        if (calculado.getSietesJugador2() != esperado.getSietesJugador2()) return false;

        if (calculado.isGuindisJugador1() != esperado.isGuindisJugador1()) return false;
        if (calculado.isGuindisJugador2() != esperado.isGuindisJugador2()) return false;

        if (calculado.getCartasCapturadasJugador1() != esperado.getCartasCapturadasJugador1()) return false;
        if (calculado.getCartasCapturadasJugador2() != esperado.getCartasCapturadasJugador2()) return false;
        return true;
    }

    
    /**
     * Obtiene una lista con todos los jugadores únicos de una lista de partidas.
     * 
     * @param partidas Lista de partidas para extraer todos los jugadores
     * @return ArrayList con los nombres de los jugadores únicos
     */
    private ArrayList<String> obtenerTodosJugadores(ArrayList<Partida> partidas){
        ArrayList<String> jugadores = new ArrayList<>();
        for(int i = 0; i < partidas.size(); i++){
            Partida partida = partidas.get(i);
            String jugador1 = partida.getJugador1();
            String jugador2 = partida.getJugador2();

            if(!jugadores.contains(jugador1)){
                jugadores.add(jugador1);
            }
            if(!jugadores.contains(jugador2)){
                jugadores.add(jugador2);
            }
        }
        return jugadores;
    }
}