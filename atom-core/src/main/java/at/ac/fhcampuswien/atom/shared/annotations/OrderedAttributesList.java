/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

/**
 * 
 * When used directly on a Class, 
 * it defines the Attributes Displayed in ListViews.
 * WARNING: this will completely hide attributes not listed in the 
 *          annotation from the list, not only set them to hidden
 * 
 * When used inside the Annotation OrderedAttributesInGroups, 
 * it defines the Attributes displayed in the corresponding group in DetailViews.
 * 
 * @author kaefert
 *
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface OrderedAttributesList {
	String[] value();
}
