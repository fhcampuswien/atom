/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import java.util.List;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

public class DomainObjectPagingLoadResult implements PagingLoadResult<DomainObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5682112955779027304L;

	private List<DomainObject> domainObjects;
	private int offset, totalLength;
	
	/**
	 * 
	 */
	public DomainObjectPagingLoadResult(List<DomainObject> domainObjects, int offset, int totalLength) {
		this.domainObjects = domainObjects;
		this.offset = offset;
		this.totalLength = totalLength;
	}

	@Override
	public List<DomainObject> getData() {
		return domainObjects;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getTotalLength() {
		return totalLength;
	}

	@Override
	public void setOffset(int offset) {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "I don't know what this should be for!", this);
	}

	@Override
	public void setTotalLength(int totalLength) {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "I don't know what this should be for!", this);
	}

}
