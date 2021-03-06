/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import at.ac.fhcampuswien.atom.shared.annotations.AttributePlacement;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition.ViewType;

public class DomainClassAttribute implements java.io.Serializable {

	private static final long serialVersionUID = 3965088824795861114L;
	
	public static final String[] ordinalTypes = new String[] {"Integer", "int", "Double", "double", "Date", "long"};
	public static final String dateType = "java.util.Date";

	private DomainClass domainClass;
	
	private String name;
	private String displayName = null;
	private String tooltip = null;
	private String type;
	private ArrayList<String> annotations = new ArrayList<String>();
	private boolean isField = false;
	private boolean isTransient = false;
	private boolean readAble = false;
	private boolean writeAble = false;
	private boolean loadedWithLists = true;
	private boolean loadedWhenNotPrimary = false;
	private boolean requiredForStringRepresentation = false;
	private boolean relationEssential = false;
	private Double position = null;
	private Double positionOverall = null;
	private Double positionInGroup = null;
	private String attributeGroup = null;
	private String[] validators = null;
	private String mappedBy = null;
	private String where = null;
	private String otherSidePermissionRequired = null;

	private Boolean hideFromListGui = null;
	private Boolean hideFromDetailGui = null;

	private String[] listBoxKeys = null;
	private String[] listBoxDisplay = null;
	private String listBoxSql = null;
	private boolean listBoxUseHql = false;
	private ListBoxDefinition.ViewType listBoxViewType = null;
	private String listBoxMSSeperator = null;
	private int listBoxMSSize;
	private boolean listBoxAllowOtherValues = false;
	private boolean listBoxAnyExistingValue = false;
	private boolean listBoxHideNonSelectedInReadMode = false;

	private String linkPrefix = null;
	private String linkSuffix = null;
	
	//slider-attribute-view config
	private boolean useSlider = false;
	private double sliderMinValue, sliderMaxValue, sliderDefaultValue, sliderStepSize;
	private int sliderRoundTo;
	
	private AccessHandler accessHandler;
	
	private HashMap<Boolean, String> booleanMeanings;

	private static final HashMap<Boolean, String> defaultMeanings;
	static {
		defaultMeanings = new HashMap<Boolean, String>();
		defaultMeanings.put(true, "Ja");
		defaultMeanings.put(false, "Nein");
		defaultMeanings.put(null, "k.A.");
	}

	/**
	 * Needed for GWT serialization
	 */
	@SuppressWarnings("unused")
	private DomainClassAttribute() {
	}

	public DomainClassAttribute(DomainClass domainClass, String theName, String theType, boolean isField) {
		name = theName;
		this.domainClass = domainClass;
		
		if (theType != null && theType.startsWith("class "))
			type = theType.substring(6);
		else
			type = theType;
		
		this.isField = isField;
	}

	/**
	 * 
	 * @param theName
	 * @param theType
	 * @param isField
	 * @param write
	 *            true=writeAble, false=readAble, if neither applies, use Constructor without write Parameter
	 */
	public DomainClassAttribute(DomainClass domainClass, String theName, String theType, boolean isField, boolean write) {
		this(domainClass, theName, theType, isField);
		if (write)
			writeAble = true;
		else
			readAble = true;
	}

	public DomainClassAttribute(DomainClass domainClass, DomainClassAttribute superAttribute) {
		this.domainClass = domainClass;
//		this.domainClass = superAttribute.domainClass;
		this.name = superAttribute.name;
		this.displayName = superAttribute.displayName;
		this.type = superAttribute.type;
		this.annotations = new ArrayList<String>(superAttribute.annotations);
		this.isField = superAttribute.isField;
		this.isTransient = superAttribute.isTransient;
		this.readAble = superAttribute.readAble;
		this.writeAble = superAttribute.writeAble;
		this.loadedWithLists = superAttribute.loadedWithLists;
		this.loadedWhenNotPrimary = superAttribute.loadedWhenNotPrimary;
		this.requiredForStringRepresentation = superAttribute.requiredForStringRepresentation;
		this.relationEssential = superAttribute.relationEssential;
		this.position = superAttribute.position;
		this.positionOverall = superAttribute.positionOverall;
		this.positionInGroup = superAttribute.positionInGroup;
		this.attributeGroup = superAttribute.attributeGroup;
//		this.hideFromListGui = superAttribute.hideFromListGui;
//		this.hideFromDetailGui = superAttribute.hideFromDetailGui;
		this.listBoxKeys = superAttribute.listBoxKeys;
		this.listBoxDisplay = superAttribute.listBoxDisplay;
		this.listBoxSql = superAttribute.listBoxSql;
		this.listBoxViewType = superAttribute.listBoxViewType;
		this.useSlider = superAttribute.useSlider;
		this.sliderMinValue = superAttribute.sliderMinValue;
		this.sliderMaxValue = superAttribute.sliderMaxValue;
		this.sliderDefaultValue = superAttribute.sliderDefaultValue;
		this.sliderStepSize = superAttribute.sliderStepSize;
		this.sliderRoundTo = superAttribute.sliderRoundTo;
		this.booleanMeanings = superAttribute.booleanMeanings;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isReadAble() {
		return readAble;
	}

	public boolean isWriteAble() {
		return writeAble;
	}

	public boolean isField() {
		return isField;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void makeReadAble() {
		readAble = true;
	}

	public void makeWriteAble() {
		writeAble = true;
	}

	public void addAnnotation(String annotationName) {
		annotations.add(annotationName);
	}

	public String getAnnotation(String contains) {
		for (String anAnnotation : annotations) {
			if (anAnnotation.contains(contains))
				return anAnnotation;
		}
		return null;
	}

	public Collection<String> getAnnotations() {
		return annotations;
	}
	
	public void setPosition(double value, String type) {
		if (AttributePlacement.defaultType.equals(type)) {
			setPosition(value);
		} else if (AttributePlacement.overallType.equals(type)) {
			setPositionOverall(value);
		} else if (AttributePlacement.inGroupType.equals(type)) {
			setPositionInGroup(value);
		} else {
			AtomTools.log(Level.SEVERE, "unknown AttributePlacement Type", this);
			setPosition(value);
		}
	}
	

	public void setPosition(double value) {
		position = Double.valueOf(value);
	}

	public Double getPosition() {
		return position;
	}

	public void setPositionOverall(Double positionOverall) {
		this.positionOverall = positionOverall;
	}

	public Double getPositionOverall() {
		return (positionOverall != null && positionOverall != Double.MAX_VALUE ? positionOverall : position);
	}

	public void setPositionInGroup(Double positionInGroup) {
		this.positionInGroup = positionInGroup;
	}

	public Double getPositionInGroup() {
		return (positionInGroup != null && positionInGroup != Double.MAX_VALUE ? positionInGroup : position);
	}

	public void setAttributeGroup(String value) {
		attributeGroup = value;
	}

	public String getAttributeGroup() {
		return attributeGroup;
	}

	public void setTransient() {
		isTransient = true;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	public String getDisplayName() {
		if (displayName != null && !"".equals(displayName))
			return displayName;
		else
			return name;
	}
	
	public String getTooltip() {
		return tooltip;
	}

	public void setBooleanMeaning(Boolean value, String meaning) {
		if (booleanMeanings == null)
			booleanMeanings = new HashMap<Boolean, String>();
		booleanMeanings.put(value, meaning);
	}

	public String getBooleanMeaning(Boolean value) {
		if (booleanMeanings != null)
			return booleanMeanings.get(value) != null ? booleanMeanings.get(value) : defaultMeanings.get(value);
		else
			return defaultMeanings.get(value);
	}
	
	public HashMap<Boolean, String> getBooleanMeanings() {
		if (booleanMeanings != null) {
			for(Boolean value : defaultMeanings.keySet()) {
				if(booleanMeanings.get(value) == null)
					booleanMeanings.put(value, defaultMeanings.get(value));
			}
			return booleanMeanings;
		}
		else
			return defaultMeanings;
	}

	public boolean isLoadedWithLists() {
		return loadedWithLists;
	}

	public void setLoadedWithLists(boolean loadedWithLists) {
		this.loadedWithLists = loadedWithLists;
	}

	public boolean isLoadedWhenNotPrimary() {
		return loadedWhenNotPrimary;
	}

	public void setLoadedWhenNotPrimary(boolean loadedWhenNotPrimary) {
		this.loadedWhenNotPrimary = loadedWhenNotPrimary;
	}

	public boolean isRequiredForStringRepresentation() {
		return requiredForStringRepresentation;
	}

	public void setRequiredForStringRepresentation(boolean requiredForStringRepresentation) {
		this.requiredForStringRepresentation = requiredForStringRepresentation;
	}

	public boolean isRelationEssential() {
		if(!relationEssential && domainClass != null) {
			DomainClass superClass = domainClass.getSuperClass();
			if(superClass != null) {
				DomainClassAttribute superAttribute = superClass.getAttributeNamed(this.name);
				if(superAttribute != null) {
					return superAttribute.isRelationEssential();
				}
			}
		}
		return relationEssential;
	}

	public void setRelationEssential(boolean relationEssential) {
		this.relationEssential = relationEssential;
	}

	public void setListBox(String[] keys, String[] display, String sql, boolean useHql, ViewType viewType, String multiSelectSeperator, int multiSelectSize, boolean allowOtherValues, boolean anyExistingValue, boolean hideNonSelectedInReadMode) {
		listBoxKeys = keys;
		listBoxDisplay = display;
		listBoxSql = sql;
		listBoxUseHql = useHql;
		listBoxViewType = viewType;
		listBoxMSSeperator = multiSelectSeperator;
		listBoxMSSize = multiSelectSize;
		listBoxAllowOtherValues = allowOtherValues;
		listBoxAnyExistingValue = anyExistingValue;
		listBoxHideNonSelectedInReadMode = hideNonSelectedInReadMode;
	}

	public String[] getListBoxKeys() {
		return listBoxKeys;
	}

	public String[] getListBoxDisplay() {
		if(listBoxDisplay == null || listBoxDisplay.length < 1)
			return listBoxKeys;
		else
			return listBoxDisplay;
	}
	
	public LinkedHashMap<String, String> getListBoxMapped() {
		if(getListBoxKeys() == null)
			return null;
		
		LinkedHashMap<String, String> returnValue = new LinkedHashMap<String, String>(getListBoxKeys().length);
		for(int i=0 ; i < getListBoxKeys().length ; i++) {
			returnValue.put(getListBoxKeys()[i], getListBoxDisplay()[i]);
		}
		return returnValue;
	}
	
	public LinkedHashMap<String, String> getListBoxMappedReverse() {
		if(getListBoxKeys() == null)
			return null;
		
		LinkedHashMap<String, String> returnValue = new LinkedHashMap<String, String>(getListBoxKeys().length);
		for(int i=0 ; i < getListBoxKeys().length ; i++) {
			returnValue.put(getListBoxDisplay()[i], getListBoxKeys()[i]);
		}
		return returnValue;
	}
	
	public String getListBoxSql() {
		return listBoxSql;
	}

	public boolean getListBoxUseHql() {
		return listBoxUseHql;
	}

	public ViewType getListBoxViewType() {
		return listBoxViewType;
	}
	
	public String getListBoxMSSeperator() {
		return listBoxMSSeperator;
	}
	
	public int getListBoxMSSize() {
		return listBoxMSSize;
	}
	
	public boolean getListBoxAllowOtherValues() {
		return listBoxAllowOtherValues;
	}

	public boolean getListBoxAnyExistingValue() {
		return listBoxAnyExistingValue;
	}

	public boolean getListBoxHideNonSelectedInReadMode() {
		return listBoxHideNonSelectedInReadMode;
	}

	public Boolean getHideFromListGui() {
		return hideFromListGui;
	}

	public void setHideFromListGui(Boolean hideFromListGui) {
		this.hideFromListGui = hideFromListGui;
	}

	public Boolean getHideFromDetailGui() {
		return hideFromDetailGui;
	}

	public void setHideFromDetailGui(Boolean hideFromDetailGui) {
		this.hideFromDetailGui = hideFromDetailGui;
	}
	
	@Override
	public String toString() {
		if(domainClass != null)
			return domainClass + "." + this.getName();
		return this.getName();
	}

	public void setSlider(double minValue, double maxValue, double defaultValue, double stepSize, int roundTo) {
		useSlider = true;
		sliderMinValue = minValue;
		sliderMaxValue = maxValue;
		sliderDefaultValue = defaultValue;
		sliderStepSize = stepSize;
		sliderRoundTo = roundTo;
	}
	
	public boolean useSlider() {
		return useSlider;
	}

	public double getSliderMinValue() {
		return sliderMinValue;
	}

	public double getSliderMaxValue() {
		return sliderMaxValue;
	}

	public double getSliderDefaultValue() {
		return sliderDefaultValue;
	}

	public double getSliderStepSize() {
		return sliderStepSize;
	}

	public int getSliderRoundTo() {
		return sliderRoundTo;
	}

	public String[] getValidators() {
		return validators;
	}

	public void setValidators(String[] validators) {
		this.validators = validators;
	}
	
	public AccessHandler getAccessHandler() {
		if(accessHandler == null)
			accessHandler = new AccessHandler();
		return accessHandler;
	}

	public void setLinkPrefix(String prefix) {
		this.linkPrefix = prefix;
	}
	
	public String getLinkPrefix() {
		return linkPrefix;
	}

	public void setLinkSuffix(String suffix) {
		this.linkSuffix = suffix;
	}

	public String getLinkSuffix() {
		return linkSuffix;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getOtherSidePermissionRequired() {
		return otherSidePermissionRequired;
	}

	public void setOtherSidePermissionRequired(String otherSidePermissionRequired) {
		this.otherSidePermissionRequired = otherSidePermissionRequired;
	}

//	/**
//	 * this will only get called if the user has only linkage permission to the instance.
//	 * 
//	 * @param session
//	 * @return
//	 */
//	public boolean hasUserPermissions(ClientSession session) {
//		if(isRelationEssential())
//			return true;
//		
//		Set<Access> accesses = accessHandler.getAccess(session);
//		for(Access a : accesses) {
//			a.
//		}
//		
//		Map<Integer, String> roles = session.getRoles();
//		for (String accessType : accessRoles.keySet()) {
//			for (String role : accessRoles.get(accessType)) {
//				if (roles.containsValue(role) || roles.containsKey(role) || role.equals("*")) {
//					return AtomConfig.accessDenied.equals(accessType);
//				}
//			}
//		}
//		if(session.getUser() != null) {
//			HashSet<Access> accessTypes = session.getUser().accessForMatchingOes(accessOes);
//			if(accessTypes.contains(AtomConfig.accessDenied))
//				return false;
//			else if(accessTypes.size() > 0)
//				return true;
//		}
//		
//		return false;
//	}
}
