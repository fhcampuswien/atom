/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.exceptions;

public class ValidationError extends AtomException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2196356188183743288L;

	public ValidationError(String message) {
		super(message);
	}
	
	public ValidationError() {
	}

}
