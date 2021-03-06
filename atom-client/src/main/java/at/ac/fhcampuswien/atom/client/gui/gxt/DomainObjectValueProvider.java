/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import java.util.LinkedHashMap;

import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import java.util.logging.Level;
import com.sencha.gxt.core.client.ValueProvider;

final class DomainObjectValueProvider<T> implements ValueProvider<DomainObject, T> {

	public enum Special {
		objectID, stringRepresentation
	}

	private DomainClass domainClass;
	private DomainClassAttribute attribute;
	private Special special = null;
	private boolean beauty = false;

	private LinkedHashMap<String, String> listBoxMap = null;

	public DomainObjectValueProvider(Special special) {
		this.special = special;
	}

	public DomainObjectValueProvider(DomainClass domainClass, DomainClassAttribute attribute) {
		this.domainClass = domainClass;
		this.attribute = attribute;
	}

	public DomainObjectValueProvider(DomainClass domainClass, DomainClassAttribute attribute, boolean beauty) {
		this.domainClass = domainClass;
		this.attribute = attribute;
		this.beauty = beauty;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue(DomainObject object) {
//		AtomTools.log(Level.FINER, "DomainObjectValueProvider.getValue(" + (object == null ? "null" : object.toString()) + ")", this);

		if (special == Special.objectID)
			return (T) object.getObjectID();

		else if (special == Special.stringRepresentation)
			return (T) object.getStringRepresentation();

		else if (domainClass != null && attribute != null) {
			
			Object val = ClientTools.getAttributeValue(domainClass, attribute, object);
			
			if(val == null) {
				return null;
			}
			
			if(beauty && (val instanceof java.util.Date || val instanceof java.sql.Date || val instanceof java.sql.Timestamp)) {
				 return (T) AtomTools.getDateStringIntelligent((java.util.Date) val);
			}
			

			
			String listBoxSql = attribute.getListBoxSql();
			if (listBoxMap == null) {
				if(listBoxSql != null && listBoxSql.length() > 0) {
			
					RPCCaller.getSinglton().loadListBoxChoices(domainClass, attribute.getName(), new WaitingFor<LinkedHashMap<String, String>>() {
	
						@Override
						public void requestFailed(String reason) {
							AtomTools.log(Level.SEVERE, "getListBoxChoices failed -> " + reason, this);
						}
	
						@Override
						public void recieve(LinkedHashMap<String, String> result) {
							DomainObjectValueProvider.this.listBoxMap = result;
						}
					});
				}
				else {
					listBoxMap = attribute.getListBoxMapped();
				}
			}
			
			if (listBoxMap != null && listBoxMap.size() > 0) {
				String sep = attribute.getListBoxMSSeperator();
				String sval = val.toString();
				if(sep != null && sep.length() > 0 && sval.contains(sep)) {
					String[] parts = sval.split(sep);
					sval = null;
					for(String part : parts) {
						String disp = listBoxMap.get(part);
						if(disp != null && disp.length() > 0) {
							if(sval == null)
								sval = disp;
							else
								sval = sval + sep + disp;
						}
					}
				}
				else
					sval = listBoxMap.get(val.toString());
				
				//if value not contained in list, display value as is
				if(sval != null && sval.length() > 0)
					val = sval;
			}
			
			if(val instanceof String) {
				val = AtomTools.removeHtmlTags((String)val);
			}

			return (T) val;
		}

		else
			AtomTools.log(Level.SEVERE, "this is wrong!", this);
		return null;
	}

	@Override
	public void setValue(DomainObject object, T value) {
		AtomTools.log(Level.FINER, "DomainObjectValueProvider.setValue(" + (object == null ? "null" : object.toString()) + ", "
				+ (value == null ? "null" : value.toString()) + ")", this);
		//		if(special != Special.objectID && special != Special.stringRepresentation)
		if (domainClass != null && attribute != null)
			ClientTools.setAttributeValue(domainClass, attribute, object, value);
		else
			AtomTools.log(Level.SEVERE, "this is wrong!", this);
	}

	@Override
	public String getPath() {
		String returnValue = null;

		if (attribute != null)
			returnValue = attribute.getName();

		else if (special == Special.stringRepresentation)
			returnValue = "stringRepresentation";

		else if (special == Special.objectID)
			returnValue = "objectID";

		//		else if(domainClass != null && attribute != null)
		//			returnValue = domainClass.getName() + "." + attribute.getName();

		else
			AtomTools.log(Level.SEVERE, "this is wrong!", this);

//		AtomTools.log(Level.FINER, "DomainObjectValueProvider.getPath() --> " + returnValue, this);
		return returnValue;
	}
}