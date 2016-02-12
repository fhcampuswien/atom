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
	 * empty string (default) means that there is no sql query for this ListBox
	 */
	String sql() default "";
	
	/**
	 * empty string (default) means that this is not a multiselect attribute
	 */
	String multiSelectSeperator() default "";
	
	public enum ViewType implements java.io.Serializable {
		DropDown, RadioButtons, RadioTable
	}
	
	ViewType viewType() default ViewType.DropDown;
}
