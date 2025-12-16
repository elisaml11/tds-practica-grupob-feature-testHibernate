package uva.tds.servicios;

import java.util.ArrayList;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;

/**
 * Clase que gestiona una ronda del juego, incluyendo el número de ronda y las cartas en la mesa.
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorRonda {
    private int numero;
    private final ArrayList<Carta> cartasMesa;

   /**
    * Constructor de GestorRonda.
    * @param numeroInicial Número de ronda inicial.
    */
    public GestorRonda(int numeroInicial) {
        this.numero = numeroInicial;
        this.cartasMesa = new ArrayList<>();
    }

    /**
     * Devuelve el número de la ronda actual.
     * @return Número de ronda.
     */
    public int getNumero() {
        return this.numero;
    }

    /**
     * Incrementa el número de la ronda en uno.
     */
    public void incrementarNumero() {
        this.numero += 1;
    }

    /**
     * Devuelve una copia de las cartas en la mesa.
     * @return Lista de cartas en la mesa.
     */
    public ArrayList<Carta> getCartasMesa() {
        return new ArrayList<>(this.cartasMesa);
    }

    /**
     * Establece las cartas en la mesa a una copia de la lista proporcionada.
     * @param cartas Lista de cartas a establecer en la mesa.
     */
    public void setCartasMesa(ArrayList<Carta> cartas) {
        this.cartasMesa.clear();
        if (cartas != null) {
            this.cartasMesa.addAll(cartas);
        }
    }

    /**
     * Añade una carta a la mesa.
     * @param c Carta a añadir.
     */
    public void annadeCartaMesa(Carta c) {
        if (c == null) throw new IllegalArgumentException("La carta no puede ser nula");
        this.cartasMesa.add(c);
    }

    /**
     * Elimina las cartas especificadas de la mesa.
     * @param aRemover Lista de cartas a eliminar.
     */
    public void eliminaCartasMesa(ArrayList<Carta> aRemover) {
        if (aRemover == null) return;
        this.cartasMesa.removeAll(aRemover);
    }

    /**
     * Indica si la mesa está vacía.
     * @return true si la mesa está vacía, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.cartasMesa.isEmpty();
    }

    /**
     * Calcula la suma de los valores de las cartas en la mesa según las reglas del juego.
     * @return Suma de los valores de las cartas en la mesa.
     */
    public int sumaCartas() {
        int suma = 0;
        for (Carta c : this.cartasMesa) {
            int idx = c.getIndice();
            if (idx == 12) suma += 10;
            else if (idx == 11) suma += 9;
            else if (idx == 10) suma += 8;
            else suma += idx;
        }
        return suma;
    }

    /**
     * Asigna todas las cartas de la mesa a un jugador y limpia la mesa.
     * @param receptor Jugador que recibirá las cartas.
     */
    public void asignarCartasAJugador(Jugador receptor) {
        if (receptor == null) throw new IllegalArgumentException("Receptor no puede ser nulo");
        for (Carta c : new ArrayList<>(this.cartasMesa)) {
            receptor.agregarCartaACartas(c);
        }
        this.cartasMesa.clear();
    }
}