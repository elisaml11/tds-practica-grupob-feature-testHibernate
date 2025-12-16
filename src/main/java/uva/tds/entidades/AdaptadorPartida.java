package uva.tds.entidades;

import java.time.LocalDate;


import uva.tds.servicios.GestorPartida;

/**
 * Convierte el estado de un GestorPartida a la entidad Partida.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class AdaptadorPartida {

    /**
     * Constructor por defecto.
     */
    public AdaptadorPartida() {
    }

    /**
     * Convierte el estado del GestorPartida en una Partida.
     *
     * @param id      Identificador único de la partida.
     * @param fecha   Fecha en la que se jugó la partida.
     * @param gestor  GestorPartida que contiene el estado de la partida.
     * @return        Objeto Partida con los datos convertidos.
     * @throws NullPointerException     Si algún parámetro es nulo.
     * @throws IllegalArgumentException Si el id es vacío o si el gestor no contiene jugadores válidos.
     */
    public ResumenPartida convertir(String id, LocalDate fecha, GestorPartida gestor) {
        if (id == null || fecha == null || gestor == null)
            throw new IllegalArgumentException("El id, la fecha y el gestor no pueden ser nulos");
        if (id.isEmpty())
            throw new IllegalArgumentException("El id no puede ser nulo o vacío");

        Jugador j1 = gestor.getJugador1();
        Jugador j2 = gestor.getJugador2();
       
        ResumenPartida partida = new ResumenPartida(id, fecha, j1.getNombre(), j2.getNombre());

        int puntos1 = gestor.calcularPuntuacionFinal(j1);
        int puntos2 = gestor.calcularPuntuacionFinal(j2);

        partida.establecerResultados(j1, j2, puntos1, puntos2);
        return partida;
    }
}