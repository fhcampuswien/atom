/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthenticationException extends AtomException implements IsSerializable {

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
    
    private String reason;
    
    @Override
    public String getMessage() {
    	return reason;
    }
}
