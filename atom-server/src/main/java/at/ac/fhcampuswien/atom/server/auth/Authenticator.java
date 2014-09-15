/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ac.fhcampuswien.atom.server.PoolableEntityManagerFactory;
import at.ac.fhcampuswien.atom.shared.ClientSession;

public class Authenticator {
	
	ClientSession session = new ClientSession(0, "sessionToken", "*", "everybody");
		
	public Authenticator(PoolableEntityManagerFactory emFactory) {
	}
	

	public ClientSession getSessionFromCookie(HttpServletRequest request) {
		return getSessionFromCookie(request, true);
	}

	public ClientSession getSessionFromCookie(HttpServletRequest request, boolean forClient) {
		return session;
	}

	public ClientSession getSessionForCredentials(String userName, String password, HttpServletRequest request, HttpServletResponse response) {
		return session;
	}

	public boolean logout(HttpServletRequest request, HttpServletResponse response) {
		return true;
	}
}
