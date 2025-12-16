package uva.tds.servicios;

import uva.tds.entidades.Partida;
import uva.tds.interfaces.RepositorioPartida;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Implementación de RepositorioPartida usando Hibernate con la base de datos HSQLDB
 */

public class RepositorioPartidaHibernate implements RepositorioPartida {

    private String configFile;

    public RepositorioPartidaHibernate(String configFile) {
        this.configFile = configFile;
    }

    /**
     * Guarda una partida en el repositorio.
     * 
     * @param partida La partida a guardar
     * @throws IllegalArgumentException si partida es null
     * @throws IllegalStateException    si ya existe una partida con el mismo
     *                                  identificador
     */
    @Override
    public void guardar(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser null");
        }

        Session session = getSession();
        if (session != null) {
            try {
                session.beginTransaction();

                Partida existente = session.get(Partida.class, partida.getId());
                if (existente != null) {
                    throw new IllegalStateException("Ya existe una partida con el mismo identificador");
                }

                session.persist(partida);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
    }

    /**
     * Actualiza una partida en el repositorio.
     * 
     * @param partida La partida a actualizar
     * @throws IllegalArgumentException si partida es null
     * @throws IllegalStateException    si no existe una partida con el mismo
     *                                  identificador
     */
    @Override
    public void actualizar(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser null");
        }

        Session session = getSession();
        if (session != null) {
            try {
                session.beginTransaction();

                Partida existente = session.get(Partida.class, partida.getId());
                if (existente == null) {
                    throw new IllegalStateException("No existe una partida con el mismo identificador");
                }

                session.merge(partida);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
    }

    /**
     * Carga una partida desde el repositorio.
     * 
     * @param identificador Identificador único de la partida
     * @return La partida con el identificador dado
     * @throws IllegalArgumentException si identificador es null o vacío
     * @throws IllegalStateException    si no existe una partida con el
     *                                  identificador dado
     */
    @Override
    public Partida cargar(String identificador) {
        if (identificador == null || identificador.isEmpty()) {
            throw new IllegalArgumentException("El identificador no puede ser null o vacío");
        }

        Session session = getSession();
        Partida partida = null;

        if (session != null) {
            try {
                session.beginTransaction();

                partida = session.get(Partida.class, identificador);

                if (partida == null) {
                    throw new IllegalStateException("No existe una partida con el identificador dado");
                }

                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return partida;
    }

    /**
     * Elimina una partida del repositorio.
     * 
     * @param identificador Identificador único de la partida a eliminar
     * @throws IllegalArgumentException si identificador es null o vacío
     * @throws IllegalStateException    si no existe una partida con el
     *                                  identificador dado
     */
    @Override
    public void eliminar(String identificador) {
        if (identificador == null || identificador.isEmpty()) {
            throw new IllegalArgumentException("El identificador no puede ser null o vacío");
        }

        Session session = getSession();

        if (session != null) {
            try {
                session.beginTransaction();

                Partida partida = session.get(Partida.class, identificador);
                if (partida == null) {
                    throw new IllegalStateException("No existe una partida con el identificador dado");
                }

                session.delete(partida);
                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
    }

    /**
     * Obtiene una lista de partidas asociadas a un jugador. Si no existen partidas,
     * se devuelve una lista vacía.
     * 
     * @param nombreJugador Nombre del jugador
     * @return Lista de partidas del jugador
     * @throws IllegalArgumentException si nombreJugador es null o vacío
     */
    @Override
    public List<Partida> obtenerPartidasPorJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede ser null o vacío");
        }

        Session session = getSession();
        List<Partida> partidas = List.of();

        if (session != null) {
            try {
                session.beginTransaction();

                String hql = "FROM Partida P WHERE P.nombreJugador = :nombreJugador";
                Query<Partida> query = session.createQuery(hql, Partida.class);
                query.setParameter("nombreJugador", nombreJugador);
                partidas = query.list();

                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return partidas;
    }

    /**
     * Obtiene una lista de partidas entre la fecha inicial y la fecha final. Si no
     * existen partidas en ese rango, se devuelve una lista vacía.
     * 
     * @param fechaInicial Fecha inicial del rango. Formato: "yyyy-MM-dd"
     * @param fechaFinal   Fecha final del rango. Formato: "yyyy-MM-dd"
     * @return Lista de partidas en el rango de fechas
     * @throws IllegalArgumentException si fechaInicial o fechaFinal son null,
     *                                  vacías o no
     *                                  siguen el formato correcto
     */
    @Override
    public List<Partida> obtenerPartidasPorFecha(String fechaInicial, String fechaFinal) {
        if (fechaInicial == null || fechaInicial.isEmpty() || fechaFinal == null || fechaFinal.isEmpty()) {
            throw new IllegalArgumentException("Las fechas no pueden ser null o vacías");
        }

        fechaValida(fechaFinal);
        fechaValida(fechaInicial);

        Session session = getSession();
        List<Partida> partidas = List.of();
        if (session != null) {
            try {
                session.beginTransaction();

                String hql = "FROM Partida P WHERE P.fecha BETWEEN :fechaInicial AND :fechaFinal";
                Query<Partida> query = session.createQuery(hql, Partida.class);
                query.setParameter("fechaInicial", fechaInicial);
                query.setParameter("fechaFinal", fechaFinal);
                partidas = query.list();

                session.getTransaction().commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
            } finally {
                session.close();
            }
        }
        return partidas;
    }

    private void fechaValida(String fecha) {
        if(!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("La " + fecha + " debe seguir el formato 'yyyy-MM-dd'");
        }
    }
    private Session getSession() {
        SessionFactory factory = HibernateUtil.getSessionFactory(this.configFile);
        Session session;
        try {
            session = factory.getCurrentSession();
            return session;
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
