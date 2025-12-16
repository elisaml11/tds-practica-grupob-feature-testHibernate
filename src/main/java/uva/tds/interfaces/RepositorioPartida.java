package uva.tds.interfaces;
import java.util.List;

import uva.tds.entidades.Partida;

/**
 * Interfaz que define el contrato para la persistencia de partidas.
 * Sigue el patrón Repository para abstraer el mecanismo de almacenamiento
 * y facilitar testing con diferentes implementaciones (JSON, base de datos,
 * memoria, etc.).
 */
public interface RepositorioPartida {

    /**
     * Guarda una partida en el repositorio.
     * 
     * @param partida La partida a guardar
     * @throws IllegalArgumentException si partida es null
     * @throws IllegalStateException    si ya existe una partida con el mismo
     *                                  identificador
     */
    public void guardar(Partida partida);

    /**
     * Actualiza una partida en el repositorio.
     * 
     * @param partida La partida a actualizar
     * @throws IllegalArgumentException si partida es null
     * @throws IllegalStateException    si no existe una partida con el mismo
     *                                  identificador
     */
    public void actualizar(Partida partida);

    /**
     * Carga una partida desde el repositorio.
     * 
     * @param identificador Identificador único de la partida
     * @return La partida con el identificador dado
     * @throws IllegalArgumentException si identificador es null o vacío
     * @throws IllegalStateException    si no existe una partida con el
     *                                  identificador dado
     */
    public Partida cargar(String identificador);

    /**
     * Elimina una partida del repositorio.
     * 
     * @param identificador Identificador único de la partida a eliminar
     * @throws IllegalArgumentException si identificador es null o vacío
     * @throws IllegalStateException    si no existe una partida con el
     *                                  identificador dado
     */
    public void eliminar(String identificador);

    /**
     * Obtiene una lista de partidas asociadas a un jugador. Si no existen partidas, se devuelve una lista vacía.
     * 
     * @param nombreJugador Nombre del jugador
     * @return Lista de partidas del jugador
     * @throws IllegalArgumentException si nombreJugador es null o vacío
     */
    public List<Partida> obtenerPartidasPorJugador(String nombreJugador);

    /**
     * Obtiene una lista de partidas entre la fecha inicial y la fecha final. Si no existen partidas en ese rango, se devuelve una lista vacía.
     * 
     * @param fechaInicial Fecha inicial del rango. Formato: "yyyy-MM-dd"
     * @param fechaFinal   Fecha final del rango. Formato: "yyyy-MM-dd"
     * @return Lista de partidas en el rango de fechas
     * @throws IllegalArgumentException si fechaInicial o fechaFinal son null,
     *                                  vacías o no
     *                                  siguen el formato correcto
     */
    public List<Partida> obtenerPartidasPorFecha(String fechaInicial, String fechaFinal);

}
