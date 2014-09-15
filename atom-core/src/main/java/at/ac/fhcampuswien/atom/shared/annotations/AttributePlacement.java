/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface AttributePlacement {
    public static final String defaultType = "defaultType";
    public static final String overallType = "overallType";
    public static final String inGroupType = "inGroupType";

    double value() default Double.MAX_VALUE;
    double inGroup() default Double.MAX_VALUE;
    double overall() default Double.MAX_VALUE;

    String type() default defaultType;
}
