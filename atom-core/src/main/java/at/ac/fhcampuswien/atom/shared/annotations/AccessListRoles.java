/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

import at.ac.fhcampuswien.atom.shared.AtomConfig;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD})
public @interface AccessListRoles {
	
    String[] accessTypes() default AtomConfig.accessReadOnly;

	/**
	 * interpreted as OR relation - one of the given relationTypes will
	 * suffice to make the specified accessTypes available to the user.
	 */
	String[] requiredRelations() default RelationDefinition.noRelationRequired;
	
    //list of roles that get the specified permissions for the annotated target
    String[] value();
}
