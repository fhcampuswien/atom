/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;


//import com.google.common.annotations.GwtCompatible;
//
//@GwtCompatible(emulated=true)
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
public class DataFilter implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5016092327888851502L;

	// for serializability
	@SuppressWarnings("unused")
	private DataFilter() {
	}

	private String column;
	private String value;

	/**
	 * if ("lt".equals(name)) return FilterType.LOWERTHEN; else if
	 * ("gt".equals(name) || "after".equals(name)) return
	 * FilterType.GREATERTHEN; else if ("eq".equals(name)) return
	 * FilterType.EQUALS; else
	 */
	private String filterType;

	/**
	 * if ("string".equals(name)) return ColumnType.STRING; else if
	 * ("date".equals(name)) return ColumnType.DATE; else if
	 * ("boolean".equals(name)) return ColumnType.BOOLEAN; else if
	 * ("list".equals(name)) return ColumnType.LIST; else if
	 * ("numeric".equals(name)) return ColumnType.NUMERIC;
	 */
	private String columnType;

	public DataFilter(String column, String value, String filterType,
			String columnType) {
		this.column = column;
		this.value = value;
		this.filterType = filterType;
		this.columnType = columnType;
	}

	public String getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public String getFilterType() {
		return filterType;
	}

	public String getColumnType() {
		return columnType;
	}
	
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		
		if(column != null)
			hash += column.hashCode();
		
		if(value != null)
			hash += value.hashCode();
		
		if(filterType != null)
			hash += filterType.hashCode();
		
		if(columnType != null)
			hash += columnType.hashCode();
		
		if(hash != 0)
			return hash;
		else
			return super.hashCode();
	}

//	@GwtIncompatible(value = "java.io.ObjectOutputStream")
//	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
//		out.writeUTF(column + ";" + value + ";" + filterType + ";" + columnType);
//	}
//	
//	@GwtIncompatible(value = "java.io.ObjectInputStream")
//	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException {
//		String[] all = in.readUTF().split(";");
//		column = all[0];
//		value = all[1];
//		filterType = all[2];
//		columnType = all[3];
//	}
}
