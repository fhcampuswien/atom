/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

/**
 * 
 * @author Thomas Käfer
 *
 * Annotation to mark an String Attribute to represent an URI
 * And should be displayed as a clickable link
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface LinkAttribute {
	String prefix() default "";
	String suffix() default "";
}
