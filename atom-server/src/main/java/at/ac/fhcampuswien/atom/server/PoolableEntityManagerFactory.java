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

import com.allen_sauer.gwt.log.client.Log;

public class PoolableEntityManagerFactory extends BasePoolableObjectFactory<EntityManager> {
	
	private EntityManagerFactory emFactory;

	public PoolableEntityManagerFactory() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "creating new EntityManagerFactory", this);
		emFactory = Persistence.createEntityManagerFactory("atom");
		AtomTools.log(Log.LOG_LEVEL_TRACE, "created new EntityManagerFactory", this);
	}

	@Override
	public EntityManager makeObject() {
		try {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "calling EntityManagerFactory.createEntityManager()", this);
			EntityManager val = emFactory.createEntityManager();
			AtomTools.log(Log.LOG_LEVEL_TRACE, "successfully created EntityManager, returning it to caller", this);
			return val;
		} catch (Throwable t) {
			// I think this will happen when a db-connection-timeout happens (or
			// something like that..)
			AtomTools.log(Log.LOG_LEVEL_ERROR, "emFactory.createEntityManager() failed! will try to close the factory and create a new one", this);
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
