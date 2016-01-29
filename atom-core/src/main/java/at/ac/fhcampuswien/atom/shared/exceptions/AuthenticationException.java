/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.exceptions;

import java.io.Serializable;

public class AuthenticationException extends AtomException implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4035170085639513676L;
    
 // for serializability
	@SuppressWarnings("unused")
    private AuthenticationException() {
    }
    
    public AuthenticationException(String reason) {
    	this.reason = reason;
    }
    
    public AuthenticationException(String reason, Throwable cause) {
    	this(reason);
    	this.initCause(cause);
    }
    
    private String reason;
    
    @Override
    public String getMessage() {
    	return reason;
    }
}
