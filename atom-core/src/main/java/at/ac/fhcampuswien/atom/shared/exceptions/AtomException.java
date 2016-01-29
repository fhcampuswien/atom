/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.exceptions;

import java.io.Serializable;

public class AtomException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 3656303650157138390L;

	public AtomException() {
	}

	public AtomException(String arg0) {
		super(arg0);
	}

	public AtomException(Throwable arg0) {
		super(arg0);
	}

	public AtomException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
