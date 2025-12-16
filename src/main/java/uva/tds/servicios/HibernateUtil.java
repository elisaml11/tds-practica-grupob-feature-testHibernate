package uva.tds.servicios;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    // Hibernate 5:
    private static SessionFactory buildSessionFactory(String configFile) {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(configFile) // configures settings from specified config file
                .build();
        try {
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble
            // building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }

    public static SessionFactory getSessionFactory(String configFile) {
    	if (sessionFactory == null) {
    		sessionFactory = buildSessionFactory(configFile);
    	}
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        if (sessionFactory != null) {
        	sessionFactory.close();
        	sessionFactory = null;
        }
    }

}
