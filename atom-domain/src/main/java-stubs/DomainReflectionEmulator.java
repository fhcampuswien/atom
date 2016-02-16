package at.ac.fhcampuswien.atom.shared;

import at.ac.fhcampuswien.atom.shared.domain.*;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class DomainReflectionEmulator {
	
	private DomainReflectionEmulator() {
	}

	public static String getAttributeValueAsString(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject) {
		Object value = getAttributeValue(domainClass, domainClassAttribute, domainObject);
		String stringValue = "empty";
		if (value != null) {
			stringValue = value.toString();
		}
		return stringValue;
	}

	public static Object getAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject) {
		String className = domainClass.getName();
		String attributeName = domainClassAttribute.getName();

		//throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not get value!");
	}

	/**
	 * we need the @SuppressWarnings("unchecked") annotation because we need to call setters that require specific types with a value that we get as generic @Object
	 * 
	 * @param domainClass
	 * @param domainClassAttribute
	 * @param domainObject
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public static void setAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject, Object value) {
		String className = domainClass.getName();
		String attributeName = domainClassAttribute.getName();
		
		//throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not set value!");
	}
	
	public static DomainObject makeInstance(DomainClass domainClass) {
		String className = domainClass.getName();

		//throw new AtomException("DomainClass " + domainClass.getName() + " not found. Could not create instance!");
	}
}
