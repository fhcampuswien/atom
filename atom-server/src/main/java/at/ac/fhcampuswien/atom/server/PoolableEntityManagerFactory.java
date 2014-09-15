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
		emFactory = Persistence.createEntityManagerFactory("atom");
	}

	@Override
	public EntityManager makeObject() throws Exception {
		try {
			return emFactory.createEntityManager();
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
}
