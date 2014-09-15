/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.io.Serializable;

/**
 * DataFilter class represents a filter that is applied to a data set
 * coming from the server side. It consists of a set of column and value
 * and virtually can handle any type of filtering. For example if you have
 * users and need filtering by name (e.g. Peter) and age (e.g. 20-30),
 * you will add 3 data filters:
 *   - name: Peter
 *   - minAge: 20
 *   - maxAge: 30 
 * On the server side you will modify the SQL query to apply filtering
 * by name and age.
 * 
 * (c) 2007 by Svetlin Nakov - http://www.nakov.com
 * National Academy for Software Development - http://academy.devbg.org 
 * This software is freeware. Use it at your own risk.
 */
public class DataSorter implements Serializable {

	private static final long serialVersionUID = 5777113633471171349L;

	/* for GWT to accept the class as serializable */
	@SuppressWarnings("unused")
	private DataSorter() {
	}

	private String column;
	private boolean sortOrder;
	
	public DataSorter(String column, boolean sortOrder) {
		this.column = column;
		this.sortOrder = sortOrder;
	}

	public String getColumn() {
		return column;
	}

	public boolean getSortOrder() {
		return sortOrder;
	}
}
