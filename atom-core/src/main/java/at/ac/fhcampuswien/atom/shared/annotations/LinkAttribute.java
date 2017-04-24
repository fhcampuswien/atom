/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

/**
 * 
 * @author Thomas Käfer
 *
 * Annotation to mark an String Attribute to represent a Binary File.
 * Such Files are persisted in the Database in the Entity @PersistedFileAttribute
 * 
 * We do not want to use a byte array (every byte takes an extra byte of java heap)
 * So we use java.sql.Blob - therefore we cannot transfer instances of it to the client
 * because java.sql.Blob cannot be translated to javascript.
 * 
 * Also it does not make sense to send huge binaries to the client, unless he really 
 * wants that binary.
 * 
 * Therefore Those files are only loosely coupled by their string representation:
 * Name [ID]
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface LinkAttribute {
	String prefix() default "";
	String suffix() default "";
}
