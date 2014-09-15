/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;


public class AccessForOes implements java.io.Serializable, com.google.gwt.user.client.rpc.IsSerializable {

	private static final long serialVersionUID = 7031767728429458183L;
	public Access access;
	public boolean onlyHauptrolle;
	public boolean onlyLeiter;
	public String[] oes;

	@SuppressWarnings("unused")
	private AccessForOes() {
	}

	public AccessForOes(String[] accessTypes, String[] requiredRelations, boolean onlyHauptrolle, boolean onlyLeiter, String[] oes) {
		this(new Access(accessTypes, requiredRelations), onlyHauptrolle, onlyLeiter, oes);
	}

	public AccessForOes(Access access, boolean onlyHauptrolle, boolean onlyLeiter, String[] oes) {
		this.access = access;
		this.onlyHauptrolle = onlyHauptrolle;
		this.onlyLeiter = onlyLeiter;
		this.oes = oes;
	}
}