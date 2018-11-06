/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;


@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface ListBoxDefinition {
		
	String[] keys() default {};
	String[] display() default {};
	
	/**
	 * empty string (default) means that there is no sql query for this ListBox, instead it is a static definition
	 * the sql query can have 2 columns (key & value) or 1 column which is then used for both key and display value.
	 */
	String sql() default "";
	boolean useHql() default false;
	
	/**
	 * empty string (default) means that this is not a multiselect attribute
	 */
	String multiSelectSeperator() default "";
	
	/**
	 * when set to true, values outside the defined list can be entered.
	 * This only works with ViewType = FilterAbleDropDown
	 * (but ViewType = FilterAbleDropDown also works with this set to false!)
	 */
	boolean allowOtherValues() default false;
	
	/**
	 * any value that any instance of the containing object has in this attribute is used for the list
	 */
	boolean anyExistingValue() default false;
	
	boolean hideNonSelectedInReadMode() default false;
	
	public enum ViewType implements java.io.Serializable {
		DropDown, RadioButtons, RadioTable, FilterAbleDropDown
	}
	
	ViewType viewType() default ViewType.DropDown;
}
