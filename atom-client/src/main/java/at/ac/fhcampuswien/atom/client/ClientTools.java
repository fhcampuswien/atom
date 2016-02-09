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
import java.util.logging.Level;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.i18n.client.DateTimeFormat;

import at.ac.fhcampuswien.atom.client.gui.attributes.components.FilterSpecificationDialogBox;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.DomainReflectionEmulator;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

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
		return DomainReflectionEmulator.getAttributeValue(domainClass, domainClassAttribute, domainObject);
	}

	public static void setAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject, Object value) {
		DomainReflectionEmulator.setAttributeValue(domainClass, domainClassAttribute, domainObject, value);
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
			AtomTools.validateAttribute(attribute.getDisplayName(), ClientTools.getAttributeValue(classOfObject, attribute, domainObject), attribute.getValidators());
		}
	}

	public static DomainObject createInstance(DomainClass domainClass) {
		AtomTools.log(Level.FINE, "createInstance(" + domainClass.getName() + ")", null);
		return DomainReflectionEmulator.makeInstance(domainClass);
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
				AtomTools.log(Level.SEVERE, "could not load domaintree! -> " + reason, this);
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
	
	public static boolean hasEnterKeyBeenPressed(KeyPressEvent event) {
		//GWT BUG: http://code.google.com/p/google-web-toolkit/issues/detail?id=5558
		int unicode = event.getUnicodeCharCode();
		char code = event.getCharCode(); 
		NativeEvent nativeEvent = event.getNativeEvent();
		int keyCode = nativeEvent.getKeyCode();
//		AtomTools.log(Level.FINER, "login_passwordbox_keyPress; UnicodeCharCode=\""+unicode+"\" ; keyCode=\""+ keyCode +" ; CharCode=\""+String.valueOf(code)+"\"<endofline>", this);
		//if(event.getCharCode() == KeyCodes.KEY_ENTER) {
		
		if(code == '\n' || code == '\r' || (unicode == 0 && keyCode == 13)) {
			return true;
		}
		return false;
	}
}
