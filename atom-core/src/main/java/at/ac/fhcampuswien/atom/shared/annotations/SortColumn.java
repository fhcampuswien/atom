/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;


@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
/**
 * When sorting a List of Objects with a column of the annotated type, then the column with the given name is used as sort criterion instead of the ObjectID
 * Also, we will use this as the default Sorting Column (check if implemented)
 * 
 * @author thomas
 *
 */
public @interface SortColumn {
	String value();
}
