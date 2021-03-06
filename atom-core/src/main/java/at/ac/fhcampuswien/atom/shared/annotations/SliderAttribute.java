/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.annotations;

@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
public @interface SliderAttribute {
double minValue();
double maxValue();
double stepSize();
double defaultValue();

/**
 * @return
 * 0 or bigger: number of decimal places to round to (e.g. 0 = round to closest int)
 * -1 or smaller: do not round
 */
int roundTo() default -1;
}
