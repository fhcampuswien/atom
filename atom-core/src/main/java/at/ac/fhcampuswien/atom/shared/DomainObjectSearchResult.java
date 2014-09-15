/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DomainObjectSearchResult implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -5564728300292787847L;

	private String searchTerm;
	private String onlyScanClassWithName;

	private boolean onlyRelated;
	private boolean onlyScanStringRepresentation;

	private List<DomainObjectList> result;

	/**
	 * Needed for GWT serialization
	 */
	@SuppressWarnings("unused")
	private DomainObjectSearchResult() {
	}

	public DomainObjectSearchResult(String searchTerm, boolean onlyRelated, boolean onlyScanStringRepresentation, String onlyScanClassWithName) {
		this.searchTerm = searchTerm;
		this.onlyScanClassWithName = onlyScanClassWithName;
		this.onlyRelated = onlyRelated;
		this.onlyScanStringRepresentation = onlyScanStringRepresentation;
		result = new ArrayList<DomainObjectList>();
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String getOnlyScanClassWithName() {
		return onlyScanClassWithName;
	}

	public boolean isOnlyRelated() {
		return onlyRelated;
	}

	public boolean isOnlyScanStringRepresentation() {
		return onlyScanStringRepresentation;
	}

	public List<DomainObjectList> getResult() {
		return result;
	}

	public void addList(DomainObjectList domainObjectList) {
		result.add(domainObjectList);
	}

}
