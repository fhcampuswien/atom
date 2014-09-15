/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface RelationDefinition {
	public static final String defaultRelation = "defaultRelation";
	public static final String noRelationRequired = "noRelationRequired";
	
	String where();
	String joins() default "";
	boolean distinct() default false;
	String relationName() default RelationDefinition.defaultRelation;
}
