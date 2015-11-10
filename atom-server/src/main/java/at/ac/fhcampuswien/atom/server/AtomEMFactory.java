package at.ac.fhcampuswien.atom.server;

import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import at.ac.fhcampuswien.atom.shared.AtomTools;


/**
 * recommendation seems to be to use a new em per request, therefore doing just that
 * @author kaefert
 *
 */
public class AtomEMFactory {

	private static EntityManagerFactory emFactory = null;
	
	private static void createEntityManagerFactory() {
		try {
			if(emFactory != null && emFactory.isOpen()) {
				emFactory.close();
			}
		} catch (Throwable t) {
			AtomTools.log(Level.WARNING, "EntityManagerFactory.close() failed. should not matter to much, creating a new one anyway. might leak memory or leave broken db connections tangling..", null, t);
		}
		emFactory = Persistence.createEntityManagerFactory("atom");
	}
	
	public static EntityManager getEntityManager() {
		try {
			if(emFactory == null || !emFactory.isOpen()) {
				createEntityManagerFactory();
			}
			return emFactory.createEntityManager();
		} catch (Throwable t) {
			AtomTools.log(Level.WARNING, "createEntityManager failed, trying to start with fresh factory!", null, t);
			createEntityManagerFactory();
			return getEntityManager();
		}
	}
	
	protected static String getConnectionInfo() {
		if(emFactory == null) {
			createEntityManagerFactory();
		}
		return emFactory.getProperties().get("hibernate.connection.url").toString().replace("jdbc:sqlserver://", "");
	}
}
