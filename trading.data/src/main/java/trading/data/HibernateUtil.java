package trading.data;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import trading.data.model.Instrument;
import trading.data.model.Level1;
import trading.data.model.Level1New;

/**
 * Hibernate session helper
 * 
 * @author pdg
 * 
 */
public class HibernateUtil {
	/** Hibernate session factory */
	private static SessionFactory sessionFactory = buildSessionFactory();
	/** Hibernate session registry */
	private static ServiceRegistry serviceRegistry;
	/** Hibernate configuration */
	private static Configuration configuration;
	
	/**
	 * Initialization when static object created
	 * @return
	 */
	private static SessionFactory buildSessionFactory() {

		configuration = new Configuration().configure()
				.addAnnotatedClass(Instrument.class)
				.addAnnotatedClass(Level1.class)
				.addAnnotatedClass(Level1New.class);

		serviceRegistry = new ServiceRegistryBuilder().applySettings(
				configuration.getProperties()).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);

		return sessionFactory;
	}
	
	/**
	 * Get hibernate session factory
	 * @return
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Get connection string from configuration
	 * @return
	 */
	public static String getConnectionString(){
		String connectionString = configuration.getProperty("hibernate.connection.url");
		return connectionString;
	}
	
	/**
	 * Close hibernate session
	 */
	public static void shutdown() {
		getSessionFactory().close();
	}

}
