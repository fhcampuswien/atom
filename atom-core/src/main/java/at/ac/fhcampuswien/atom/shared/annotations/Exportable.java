/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

/**
 * 
 * @author kaefert
 *
 *         If set to false, the export is disabled for users that have read
 *         access, and both import & export capabilities are disabled for users
 *         with write access.
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD,
		java.lang.annotation.ElementType.FIELD })
public @interface Exportable {
	boolean value() default true;
}
