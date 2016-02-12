/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

public class DomainObjectList implements java.io.Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 2950751775103224047L;

	private DomainClass domainClass;

	private String domainClassName;
	private List<DomainObject> domainObjects;
	private int fromRow, pageSize, totalSize;
	private ArrayList<DataFilter> filters;
	private ArrayList<DataSorter> sorters;
	private String searchString;
	private Date creationDate;
	private boolean onlyRelated;

	/**
	 * Needed for GWT serialization
	 */
	@SuppressWarnings("unused")
	private DomainObjectList() {
	}

	private DomainObjectList(List<DomainObject> objects, int totalSize) {
		domainObjects = objects;
		this.totalSize = totalSize;
		creationDate = new Date();
		if (totalSize == 0) {
			AtomTools.log(Level.WARNING, "DomainObjectList.totalSize = 0", this);
		}
	}

	public DomainObjectList(String theName, List<DomainObject> objects, int totalSize) {
		this(objects, totalSize);
		domainClassName = theName;
	}

	private DomainObjectList(DomainClass theDomainClass, List<DomainObject> objects, int totalSize) {
		this(objects, totalSize);
		domainClass = theDomainClass;
		AtomTools.log(Level.FINER, "DomainObjectList - DomainClass given: " + domainClass.toString(), this);
		domainClassName = domainClass.getName();
	}

	public DomainObjectList(DomainClass domainClass2, List<DomainObject> resultList, int fromRow, int pageSize, int totalSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyRelated) {
		this(domainClass2, resultList, totalSize);
		this.fromRow = fromRow;
		this.pageSize = pageSize;
		this.filters = filters;
		this.sorters = sorters;
		this.searchString = searchString;
		this.onlyRelated = onlyRelated;

	}

	public DomainClass getDomainClass() {
		AtomTools.log(Level.FINER, "DomainObjectList - DomainClass==null -> " + (domainClass == null), this);
		if (domainClass == null) {
			// generate domainClass by use of the domainClassName
			// FIXME: domainClass =
			// RPCCaller.getSinglton().getDomainTree(null).getDomainClassNamed(domainClassName);
		}
		return domainClass;
	}

	public List<DomainObject> getDomainObjects() {
		return domainObjects;
	}

	public String getDomainClassName() {
		return domainClassName;
	}

	public int getTotalSize() {
		return totalSize;
	}
	
	public int getFromRow() {
		return fromRow;
	}

	public boolean useable(String domainClassName, DomainClass domainClass, int fromRow, int pageSize, Collection<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyRelated) {
		
		if ((domainClassName != null &&  domainClassName.equals(this.domainClassName) || (domainClass != null && domainClass.equals(this.domainClass)))
				&& ((filters == null && this.filters == null) || (filters != null && filters.equals(this.filters)))
				&& ((sorters == null && this.sorters == null) || (sorters != null && sorters.equals(this.sorters)))
				&& ((searchString == null && this.searchString == null) || (searchString != null && searchString.equals(this.searchString)))
				&& (onlyRelated == this.onlyRelated)
				&& (this.pageSize >= this.totalSize || (this.pageSize >= pageSize && this.fromRow == fromRow))
				&& this.creationDate.after(new Date(new Date().getTime() - 1000*60*60))
				) {
			return true;
		}
		
		return false;
	}
		
	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof DomainObjectList) {
//			DomainObjectList other = (DomainObjectList) obj;
//			if ((other.domainClassName == this.domainClassName || (other.domainClass != null && other.domainClass.equals(this.domainClass)))
////					&& other.
//				) {
//				return true;
//			}
//			return false;
//
//		} else {
//			return super.equals(obj);
//		}
//	}

}
