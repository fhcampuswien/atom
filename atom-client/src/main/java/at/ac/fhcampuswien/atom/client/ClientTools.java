/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import at.ac.fhcampuswien.atom.client.gui.attributes.components.FilterSpecificationDialogBox;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.gwtent.reflection.client.ClassType;
import com.gwtent.reflection.client.NotFoundException;
import com.gwtent.reflection.client.TypeOracle;

public class ClientTools {
	private ClientTools() {
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
		
//		AtomTools.log(Log.LOG_LEVEL_TRACE,
//				"AtomTools.getAttributeValue(" + domainClass.getName() + ", " + domainClassAttribute.getName() + ", " + domainObject.getStringRepresentation()
//						+ ") started", null);

		try {
			ClassType<?> classType = TypeOracle.Instance.getClassType(domainClass.getName());
			Object returnValue = classType.invoke(domainObject, "get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), (Object[]) null);
			// AtomTools.log(Log.LOG_LEVEL_TRACE,
			// "AtomTools.getAttributeValue successful", null);
//			String stringValue;
//			if (returnValue != null)
//				stringValue = returnValue.toString();
//			else
//				stringValue = "null";
//			AtomTools.log(Log.LOG_LEVEL_TRACE, "AtomTools.getAttributeValue(" + domainClass.getName() + ", " + domainClassAttribute.getName() + ", "
//					+ domainObject.getStringRepresentation() + ") found value '" + stringValue + "', returning", null);
			return returnValue;
		} catch (NotFoundException e) {
			if (domainClass.getSuperClass() != null) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "AtomTools.getAttributeValue(" + domainClass.getName() + ", " + domainClassAttribute.getName() + ", "
						+ domainObject.getStringRepresentation() + ") did not find getMethod in this class, will try superclass", null);
				return getAttributeValue(domainClass.getSuperClass(), domainClassAttribute, domainObject);
			} else {
				AtomTools.log(Log.LOG_LEVEL_FATAL, "AtomTools.getAttributeValue(" + domainClass.getName() + ", " + domainClassAttribute.getName() + ", "
						+ domainObject.getStringRepresentation() + ") could not find method 'get" + AtomTools.upperFirstChar(domainClassAttribute.getName())
						+ "' anywhere in the domaintree on the client-side!", null);
			}
		} catch (Throwable t) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "AtomTools.getAttributeValue - throwable: {" + t.getClass().getName() + "}" + t.getMessage(), null);
			StackTraceElement[] st = t.getStackTrace();
			for (StackTraceElement e : st) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "AtomTools.getAttributeValue - stacktrace: " + e.toString(), null);
			}
			// t.printStackTrace();
			// throw t;
		}
		return null;
	}

	public static void setAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject, Object value) {
		// AtomTools.log(
		// Log.LOG_LEVEL_TRACE,
		// "setAttributeValue(" + domainClass.getName() + ", "
		// + domainClassAttribute.getName() + ", "
		// + domainObject.getStringRepresentation() + ")", null);

		try {
			ClassType<?> classType = TypeOracle.Instance.getClassType(domainClass.getName());
			classType.invoke(domainObject, "set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Object[] { value });
		} catch (NotFoundException e) {
			if (domainClass.getSuperClass() != null) {
				setAttributeValue(domainClass.getSuperClass(), domainClassAttribute, domainObject, value);
			} else {
				AtomTools.log(Log.LOG_LEVEL_FATAL, "could not find method 'set" + AtomTools.upperFirstChar(domainClassAttribute.getName())
						+ "' anywhere in the domaintree on the client-side!", null);
			}
		} catch (Throwable t) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "AtomTools.setAttributeValue - throwable: {" + t.getClass().getName() + "}" + t.getMessage(), null);
			StackTraceElement[] st = t.getStackTrace();
			for (StackTraceElement e : st) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "AtomTools.setAttributeValue - stacktrace: " + e.toString(), null);
			}
		}

		// classType.invoke(domainObject, "set" +
		// domainClassAttribute.getName(), new Object[] { value });
		// AtomTools.log(Log.LOG_LEVEL_DEBUG,
		// "END-OF AtomTools.setAttributeValue", null);
	}

	public static HashSet<DomainObject> getDirectlyRelatedObjects(DomainClass domainClass, DomainObject domainObject) {
		HashSet<DomainObject> foundObjects = new HashSet<DomainObject>();

		for (DomainClassAttribute domainClassAttribute : domainClass.getAttributesOfTypeDomainObject()) {
			DomainObject relatedObject = (DomainObject) ClientTools.getAttributeValue(domainClass, domainClassAttribute, domainObject);
			if (relatedObject != null && relatedObject != domainObject) {
				foundObjects.add(relatedObject);
			}
		}
		for (DomainClassAttribute domainClassAttribute : domainClass.getCollectionAttributes()) {

			@SuppressWarnings("unchecked")
			Collection<DomainObject> collection = (Collection<DomainObject>) ClientTools.getAttributeValue(domainClass, domainClassAttribute, domainObject);
			if (collection != null)
				for (Object relatedObject : collection)
					if (relatedObject != null && relatedObject instanceof DomainObject && relatedObject != domainObject)
						foundObjects.add((DomainObject) relatedObject);
		}

		return foundObjects;
	}

	/**
	 * ClientTools.getAttributeValue only works on client, because GWT.create(TypeOracle) only works on client
	 * 
	 * @param domainObject
	 * @param classOfObject
	 */
	public static void validateDomainObject(DomainObject domainObject, DomainClass classOfObject) {
		HashMap<String, DomainClassAttribute> allAttributes = classOfObject.getAllAttributes();
		for (DomainClassAttribute attribute : allAttributes.values()) {
			String attributeValidator = attribute.getAnnotation("AttributeValidator");
			if (attributeValidator != null && attributeValidator != "")
				AtomTools.validateAttribute(ClientTools.getAttributeValue(classOfObject, attribute, domainObject), attributeValidator);
		}
	}

	public static DomainObject createInstance(DomainClass domainClass) {
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "createInstance(" + domainClass.getName() + ")", null);
		ClassType<?> classType = TypeOracle.Instance.getClassType(domainClass.getName());
		String[] params = new String[] {};
		return (DomainObject) classType.findConstructor(params).newInstance();
	}

	private static Map<Object, Date> recordedCalls = new HashMap<Object, Date>();

	/**
	 * 
	 * @param clicker
	 * @return false when called for the first time, and true if called a second time within ClientConfig.doubleClickTime
	 */
	public static boolean isClickDoubleClick(Object clicker) {
		Date lastClicked = recordedCalls.get(clicker);
		if (lastClicked != null && lastClicked.after(new Date(new Date().getTime() - ClientConfig.doubleClickTime))) {
			recordedCalls.put(clicker, null);
			return true;
		} else {
			recordedCalls.put(clicker, new Date());
			return false;
		}
	}

	private static DateTimeFormat dateWithTimeSec = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm:ss.S");
	private static DateTimeFormat dateWithTimeMin = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm");
	private static DateTimeFormat dateWithoutTime = DateTimeFormat.getFormat("dd.MM.yyyy");

	public static String getDateStringWithoutTime(Date date) {
		if (date != null)
			return dateWithoutTime.format(date);
		return "";
	}

	public static String getDateStringWithTimeMin(Date date) {
		if (date != null)
			return dateWithTimeMin.format(date);
		return "";
	}

	public static String getDateStringWithTimeSec(Date date) {
		if (date != null)
			return dateWithTimeSec.format(date);
		return "";
	}
	
	public static void getFilterConfigFromUser(final String className, final boolean showShowListButton, final WaitingFor<Collection<DataFilter>> reciever) {
		RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "could not load domaintree! -> " + reason, this);
			}
			
			@Override
			public void recieve(DomainClass result) {
				DomainClass dc = result.getDomainClassNamed(className);
//				LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
//
//				attributes.put(AtomConfig.specialFilterQuickSearch, AtomTools.getMessages().specialFilterQuickSearch());
//				attributes.put(AtomConfig.specialFilterDeepSearch, AtomTools.getMessages().specialFilterDeepSearch());
//				
//				for(DomainClassAttribute a : dc.getSortedAttributesListView()) {
//					if(!AtomConfig.specialFilterQuickSearch.equals(a.getName()))
//						attributes.put(a.getName(), a.getDisplayName());
//				}
				
				new FilterSpecificationDialogBox("Bitte Suchbegriff(e) eingeben:", dc.getSortedAttributesForFilterDefinition(), showShowListButton, reciever);
			}
		});
	}
}