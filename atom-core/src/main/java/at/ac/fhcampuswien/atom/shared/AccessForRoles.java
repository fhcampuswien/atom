/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;


public class AccessForRoles implements java.io.Serializable, com.google.gwt.user.client.rpc.IsSerializable {

	private static final long serialVersionUID = 499417338058916874L;
	
	public Access access;
	public String[] roles;

	@SuppressWarnings("unused")
	private AccessForRoles() {
	}

	public AccessForRoles(String[] accessTypes, String[] requiredRelations, String[] roles) {
		this(new Access(accessTypes, requiredRelations), roles);
	}

	public AccessForRoles(Access access, String[] roles) {
		this.access = access;
		this.roles = roles;
	}
}