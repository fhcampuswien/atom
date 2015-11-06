/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.pool.BasePoolableObjectFactory;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import java.util.logging.Level;

public class PoolableEntityManagerFactory extends BasePoolableObjectFactory<EntityManager> {
	
	private EntityManagerFactory emFactory;

	public PoolableEntityManagerFactory() {
		AtomTools.log(Level.FINER, "creating new EntityManagerFactory", this);
		emFactory = Persistence.createEntityManagerFactory("atom");
		AtomTools.log(Level.FINER, "created new EntityManagerFactory", this);
	}

	@Override
	public EntityManager makeObject() {
		try {
			AtomTools.log(Level.FINER, "calling EntityManagerFactory.createEntityManager()", this);
			EntityManager val = emFactory.createEntityManager();
			AtomTools.log(Level.FINER, "successfully created EntityManager, returning it to caller", this);
			return val;
		} catch (Throwable t) {
			// I think this will happen when a db-connection-timeout happens (or
			// something like that..)
			AtomTools.log(Level.SEVERE, "emFactory.createEntityManager() failed! will try to close the factory and create a new one", this);
			synchronized (emFactory) {
				try {
					emFactory.close();
				} catch (Throwable t1) {
					// i don't care!
				}
				emFactory = Persistence.createEntityManagerFactory("atom");
			}
			return emFactory.createEntityManager();
		}
	}
	
	protected String getConnectionInfo() {
		return emFactory.getProperties().get("hibernate.connection.url").toString().replace("jdbc:sqlserver://", "");
	}
}
