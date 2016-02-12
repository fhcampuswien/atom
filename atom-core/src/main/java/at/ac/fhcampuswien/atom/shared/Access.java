/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

public class Access implements java.io.Serializable {

	private static final long serialVersionUID = 8629279266876299942L;

	private String[] accessTypes;
	private String[] requiredRelations;

	@SuppressWarnings("unused")
	private Access() {
	}

	public Access(String[] accessTypes, String[] requiredRelations) {
		this.accessTypes = accessTypes;
		this.requiredRelations = requiredRelations;
	}
	
	public String[] getAccessTypes() {
		return accessTypes;
	}

	public String[] getRequiredRelations() {
		return requiredRelations;
	}
}