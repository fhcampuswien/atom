/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface AttributeValidators {
	String[] value();
	
	public final static String email = "email";
	public final static String telephone = "telephone";
	public final static String postalAdress = "postalAdress";
	public final static String socialsecurity = "socialsecurity";
	public final static String money = "money";
	public final static String notEmpty = "notempty";
	public final static String url = "url";
}
