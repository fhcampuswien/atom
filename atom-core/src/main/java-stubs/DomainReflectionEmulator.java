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

//		throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not get value!");
	}
	
	public static void setAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject, Object value) {
		String className = domainClass.getName();
		String attributeName = domainClassAttribute.getName();
		
//		throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not set value!");
	}
}