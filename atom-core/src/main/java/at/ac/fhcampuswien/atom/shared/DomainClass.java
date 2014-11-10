/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import at.ac.fhcampuswien.atom.shared.annotations.OrderedAttributesList;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class DomainClass implements java.io.Serializable, com.google.gwt.user.client.rpc.IsSerializable {

	private static final long serialVersionUID = -5815890350506114250L;

	/**
	 * Needed for GWT serialization
	 */
	@SuppressWarnings("unused")
	private DomainClass() {
	}

	public DomainClass(String theSimpleName, String thePackageName, boolean pIsAbstract) {
		simpleName = theSimpleName;
		packageName = thePackageName;
		isAbstract = pIsAbstract;
	}

	public DomainClass(String theSimpleName, String thePackageName, boolean pIsAbstract, DomainClass superClass) {
		this(theSimpleName, thePackageName, pIsAbstract);
		this.superClass = superClass;
		this.accessHandler = new AccessHandler(superClass == null ? null : superClass.getAccessHandler());
		if (superClass != null) {
			superClass.getSubClasses().add(this);
		}
	}

	private String simpleName;
	private String packageName;
	private String pluralName = null;
	private String singularName = null;
	
	private boolean hideFromGui = false;
	
	private boolean searchable = true;

//	private transient String relatedWhere = null;
	
//	private transient String relatedJoin = null;
//	
//	private transient boolean relatedDistinct = false;
	private String sortColumn = "";

	private String objectLogoImageData = null;

	private String defaultAttributeGroup = null;
	private String[] orderedAttributeGroups = null;
	private String[][] orderedGroupFields = null;
	//private OrderedAttributesList[] orderedGroupAttributesLists = null;
	private DomainClass superClass;
	private ArrayList<DomainClass> subClasses = new ArrayList<DomainClass>();
	private ArrayList<String> annotations = new ArrayList<String>();

	private String[] orderedAttributesListViewStrings = null;
	private ArrayList<DomainClassAttribute> orderedAttributesListView = null;

	private AccessHandler accessHandler;

	private HashMap<String, DomainClassAttribute> attributes = new HashMap<String, DomainClassAttribute>();
	private boolean isAbstract;

	public DomainClass getSuperClass() {
		return superClass;
	}

	public void setSuperClass(DomainClass superClass) {
		this.superClass = superClass;
	}

	public ArrayList<DomainClass> getSubClasses() {
		return subClasses;
	}

	public ArrayList<String> getAnnotations() {
		return annotations;
	}

	public String getAnnotation(String contains) {
		for (String anAnnotation : annotations) {
			if (anAnnotation.contains(contains))
				return anAnnotation;
		}
		return null;
	}

	private transient String tableName = null;

	public String getTableName() {
		if (tableName == null) {
			String tableAnnotation = getAnnotation("javax.persistence.Table");
			if (tableAnnotation != null && tableAnnotation.contains("name=")) {
				int start = tableAnnotation.indexOf("name=") + "name=".length();
				int end = tableAnnotation.indexOf(",", start);
				if (end == -1)
					end = tableAnnotation.indexOf(")", start);
				tableName = tableAnnotation.substring(start, end);
			} else {
				tableName = getSimpleName();
			}
		}
		return tableName;

	}

	public String getSimpleName() {
		return simpleName;
	}

	public String getPluralName() {
		return pluralName == null ? simpleName : pluralName;
	}

	public String getSingularName() {
		return singularName == null ? simpleName : singularName;
	}

	public String getName() {
		return packageName + "." + simpleName;
	}

	public String getPackageName() {
		return packageName;
	}

	public boolean getIsAbstract() {
		return isAbstract;
	}

	public DomainClassAttribute createAttribute(String name, String type, boolean isField) {
		DomainClassAttribute attribute = attributes.get(name);
		if (attribute == null) {
			attributes.put(name, new DomainClassAttribute(this, name, type, isField));
		}
		return attributes.get(name);
	}

	public DomainClassAttribute updateAttribute(String name, String type, boolean isField, boolean write) {
		
		//this will not only get an already existing attribute of this class, but if not existing here, will fetch, duplicate and store it from superclasses. 
		DomainClassAttribute attribute = getAttributeNamed(name, true);
		
		if (attribute != null) {
			if (write)
				attribute.makeWriteAble();
			else
				attribute.makeReadAble();
		} else {
			attribute = new DomainClassAttribute(this, name, type, isField, write);
			attributes.put(name, attribute);
		}
		return attribute;
	}

	public void addClassAnnotation(String annotationAsString) {
		annotations.add(annotationAsString);
	}

	public Collection<DomainClassAttribute> getAttributes() {
		return attributes.values();
	}

	// private HashMap<String, DomainClassAttribute> allAttributes = new
	// HashMap<String, DomainClassAttribute>();

	@SuppressWarnings("unchecked")
	public HashMap<String, DomainClassAttribute> getAllAttributes() {
		// if(allAttributes.size() == 0) {
		HashMap<String, DomainClassAttribute> allAttributes = (HashMap<String, DomainClassAttribute>) attributes.clone();

		if (getSuperClass() != null) {
			HashMap<String, DomainClassAttribute> supersAttributes = getSuperClass().getAllAttributes();
			for (String attributeName : supersAttributes.keySet()) {
				if (allAttributes.containsKey(attributeName)) {
					DomainClassAttribute myAttribute = allAttributes.get(attributeName);
					DomainClassAttribute superAttribute = supersAttributes.get(attributeName);
					if (superAttribute.isReadAble())
						myAttribute.makeReadAble();
					if (superAttribute.isWriteAble())
						myAttribute.makeWriteAble();
					for (String annotation : superAttribute.getAnnotations()) {
						myAttribute.addAnnotation(annotation);
					}
				} else {
					allAttributes.put(attributeName, supersAttributes.get(attributeName));
				}
			}
		}
		// }

		return allAttributes;
	}
	
	public HashSet<DomainClassAttribute> getAllFileAttributes() {
		HashSet<DomainClassAttribute> returnValue = new HashSet<DomainClassAttribute>();
		for(DomainClassAttribute a : getAllAttributes().values()) {
			if(a.getAnnotation("FileAttribute") != null)
				returnValue.add(a);
		}
		return returnValue;
	}
	
	private static final DomainClassAttribute deepSearch = new DomainClassAttribute(null, AtomConfig.specialFilterDeepSearch, AtomConfig.specialFilterDeepSearch, true, false);
	static {
		deepSearch.setDisplayName(AtomTools.getMessages().specialFilterDeepSearch());
	}
			
	public ArrayList<DomainClassAttribute> getSortedAttributesForFilterDefinition() {
		ArrayList<DomainClassAttribute> listViewAttributes = getSortedAttributesListView();
		ArrayList<DomainClassAttribute> returnValue = new ArrayList<DomainClassAttribute>(listViewAttributes.size()+2);
		
		DomainClassAttribute qs = this.getAttributeNamed(AtomConfig.specialFilterQuickSearch);
		listViewAttributes.remove(qs);
		returnValue.add(qs);
		returnValue.add(deepSearch);
		
		returnValue.addAll(listViewAttributes);
		return returnValue;
	}

	public ArrayList<DomainClassAttribute> getSortedAttributesListView() {
		return getSortedAttributesListView(true);
	}
	
	public ArrayList<DomainClassAttribute> getSortedAttributesListView(boolean omitHidden) {

		if (orderedAttributesListViewStrings != null) {
			if (orderedAttributesListView == null) {
				orderedAttributesListView = new ArrayList<DomainClassAttribute>(orderedAttributesListViewStrings.length);
				for (String s : orderedAttributesListViewStrings) {
					orderedAttributesListView.add(this.getAttributeNamed(AtomTools.upperFirstChar(s)));
				}
			}
			if (orderedAttributesListView.size() > 0)
				return orderedAttributesListView;
		}

		HashMap<Double, DomainClassAttribute> attributesWithPosition = new HashMap<Double, DomainClassAttribute>();
		HashSet<DomainClassAttribute> attributesWithoutPosition = new HashSet<DomainClassAttribute>();

		for (DomainClassAttribute oneAttribute : DomainClass.this.getAllAttributes().values()) {

			Boolean truu = new Boolean(true);
			if (omitHidden && truu.equals(oneAttribute.getHideFromListGui()) && truu.equals(oneAttribute.getHideFromDetailGui())) {
				// Don't mind those attributes anyhow, they are to be hidden from the GUI completely
				continue;
			}

			Double position = oneAttribute.getPositionOverall();
			if (position == null || position == Double.MAX_VALUE) {
				attributesWithoutPosition.add(oneAttribute);
			} else {
				while (attributesWithPosition.get(position) != null) {
					// add the smallest possible value that won't cause
					// precision overflow
					position += position / 100000;

					// can't do the real thing since the whole doubletobit thing
					// is not supported by javascript
					// we will just add the 10^-7th part of the number, this
					// leaves 9 of the available precision bits to

					// position = AtomTools.nextAfter(position,
					// Double.POSITIVE_INFINITY);

					// general, but incomplete, since 111111 (all bits) + 1
					// would give an error/mistake
					// long bits = Double.doubleToLongBits(position);
					// bits++;
					// position = Double.longBitsToDouble(bits);

					// in der GWT-Implementation von java.math nicht unterstüzt,
					// wäre aber die sauberere Lösung:
					// position = Math.nextAfter(position,
					// Double.POSITIVE_INFINITY);
				}
				attributesWithPosition.put(position, oneAttribute);
			}
		}
		ArrayList<DomainClassAttribute> list = new ArrayList<DomainClassAttribute>(attributesWithPosition.size()
				+ attributesWithoutPosition.size());
		ArrayList<Double> positions = new ArrayList<Double>(attributesWithPosition.keySet());
		Collections.sort(positions);
		for (Double index : positions) {
			list.add(attributesWithPosition.get(index));
		}
		list.addAll(attributesWithoutPosition);
		return list;
	}

	private String getAttributeGroupName(String attributeName) {
		DomainClassAttribute attribute = this.getAttributeNamed(attributeName);
		if (attribute != null)
			return getAttributeGroupName(attribute);
		else
			return null;
	}

	private String getAttributeGroupName(DomainClassAttribute attribute) {
		String foundGroup = attribute.getAttributeGroup();
		if (foundGroup == null && superClass != null) {
			String superGroup = superClass.getAttributeGroupName(attribute.getName());
			if(superGroup != null)
				return superGroup;
			else
				return getDefaultAttributeGroup();
		}
		else
			return foundGroup;
	}
	
	private LinkedHashMap<String, ArrayList<DomainClassAttribute>> getPredefinedGroups() {
//		orderedGroupFields = null;
		
		LinkedHashMap<String, ArrayList<DomainClassAttribute>> r = new LinkedHashMap<String, ArrayList<DomainClassAttribute>>(orderedAttributeGroups.length);
		for(int i = 0 ; i <= orderedAttributeGroups.length ; i++) {
			ArrayList<DomainClassAttribute> attributes = new ArrayList<DomainClassAttribute>();
			for(String attributeName : orderedGroupFields[i]) {
				attributes.add(getAttributeNamed(attributeName));
			}
			r.put(orderedAttributeGroups[i], attributes);
		}
		return r;
	}

	public LinkedHashMap<String, ArrayList<DomainClassAttribute>> getGroupedAttributes() {
		
		if(orderedGroupFields != null)
			return getPredefinedGroups();

		HashMap<String, ArrayList<DomainClassAttribute>> sortedGroups = new HashMap<String, ArrayList<DomainClassAttribute>>();
		HashMap<String, HashSet<DomainClassAttribute>> unsortedGroups = new HashMap<String, HashSet<DomainClassAttribute>>();

		for (DomainClassAttribute oneAttribute : DomainClass.this.getAllAttributes().values()) {
			String groupName = getAttributeGroupName(oneAttribute);
			HashSet<DomainClassAttribute> group = unsortedGroups.get(groupName);
			if (group == null) {
				group = new HashSet<DomainClassAttribute>();
				unsortedGroups.put(groupName, group);
			}
			group.add(oneAttribute);
		}

		for (String groupName : unsortedGroups.keySet()) {
			TreeMap<Double, DomainClassAttribute> attributesWithPosition = new TreeMap<Double, DomainClassAttribute>();
			HashSet<DomainClassAttribute> attributesWithoutPosition = new HashSet<DomainClassAttribute>();

			for (DomainClassAttribute oneAttribute : unsortedGroups.get(groupName)) {
				Double position = oneAttribute.getPositionInGroup();
				if (position == null) {
					attributesWithoutPosition.add(oneAttribute);
				} else {
					while (attributesWithPosition.containsKey(position)) {
						position += position / 100000;
					}
					attributesWithPosition.put(position, oneAttribute);
				}
			}

			ArrayList<DomainClassAttribute> sortedGroup = new ArrayList<DomainClassAttribute>(attributesWithPosition.size()
					+ attributesWithoutPosition.size());
			sortedGroup.addAll(attributesWithPosition.values());
			sortedGroup.addAll(attributesWithoutPosition);
			sortedGroups.put(groupName, sortedGroup);
		}
		
		String[] orderedGroupNames = getOrderedAttributeGroups();
		LinkedHashMap<String, ArrayList<DomainClassAttribute>> orderedGroups = new LinkedHashMap<String, ArrayList<DomainClassAttribute>>(sortedGroups.size());
		for(String groupName : orderedGroupNames) {
			orderedGroups.put(groupName, sortedGroups.get(groupName));
		}
		for(String groupName : sortedGroups.keySet()) {
			if(!orderedGroups.containsKey(groupName)) {
				orderedGroups.put(groupName, sortedGroups.get(groupName));
			}
		}
		return orderedGroups;
	}

	public Collection<DomainClassAttribute> getAttributesOfTypeDomainObject() {
		HashSet<DomainClassAttribute> returnSet = new HashSet<DomainClassAttribute>();

		if (superClass != null)
			returnSet.addAll(superClass.getAttributesOfTypeDomainObject());

		for (DomainClassAttribute domainClassAttribute : getAttributes()) {
			if (domainClassAttribute.getType().startsWith("at.ac.fhcampuswien.atom.shared.domain")) {
				returnSet.add(domainClassAttribute);
			}
		}

		return returnSet;
	}

	public Collection<DomainClassAttribute> getCollectionAttributes() {
		HashSet<DomainClassAttribute> returnSet = new HashSet<DomainClassAttribute>();
		if (superClass != null)
			returnSet.addAll(superClass.getCollectionAttributes());

		for (DomainClassAttribute domainClassAttribute : getAttributes()) {
			if (domainClassAttribute.getType().startsWith("java.util.Set")
					|| domainClassAttribute.getType().startsWith("java.util.List")) {
				returnSet.add(domainClassAttribute);
			}
		}
		return returnSet;
	}

	public Collection<DomainClassAttribute> getSimpleAttributes() {
		Collection<DomainClassAttribute> simpleAttributes = this.getAllAttributes().values();
		simpleAttributes.removeAll(this.getCollectionAttributes());
		simpleAttributes.removeAll(this.getAttributesOfTypeDomainObject());

		return simpleAttributes;
	}

	public DomainClassAttribute getAttributeNamed(String attributeName) {
		if(AtomConfig.specialFilterDeepSearch.equals(attributeName))
			return deepSearch;
		return getAttributeNamed(attributeName, false);
	}

	public DomainClassAttribute getAttributeNamed(String attributeName, boolean duplicateSuper) {
		// HashMap<String, DomainClassAttribute> allAttributes =
		// getAllAttributes();
		
		DomainClassAttribute attribute = attributes.get(AtomTools.lowerFirstChar(attributeName));
		if(attribute != null)
			return attribute;
		
		attribute = attributes.get(AtomTools.upperFirstChar(attributeName));
		if(attribute != null)
			return attribute;
		
		if(superClass != null) {
			attribute = superClass.getAttributeNamed(attributeName);
			if(duplicateSuper && attribute != null) {
				attribute = new DomainClassAttribute(this, attribute);
				attributes.put(attribute.getName(), attribute);
				return attribute;
			}	
			else
				return attribute;
		}
		else
			return null;
			
//		if (attributes.get(attributeName) == null && superClass != null)
//			return superClass.getAttributeNamed(attributeName);
//		else
//			return attributes.get(attributeName);
		// return allAttributes.get(attributeName);
	}

	public DomainClass getDomainClassNamed(String concreteClass) {
		if (concreteClass.equals(this.getName()))
			return this;
		else
			for (DomainClass aDomainClass : getSubClasses()) {
				DomainClass returnValue = aDomainClass.getDomainClassNamed(concreteClass);
				if (returnValue != null)
					return returnValue;
			}
		return null;
	}

	public boolean isClassOfInstance(DomainObject o) {
		if(o == null)
			return false;
		if (o.getConcreteClass().equals(this.getName()))
			return true;
		for (DomainClass dc : this.subClasses) {
			if (dc.isClassOfInstance(o))
				return true;
		}
		return false;
	}
	
	public boolean classOrSuperClassNamed(String name) {
		if(getName().equals(name))
			return true;
		else if(superClass == null)
			return false;
		else
			return superClass.classOrSuperClassNamed(name);
	}

	public void setPluralName(String value) {
		pluralName = value;
	}

	public void setSingularName(String value) {
		singularName = value;
	}

	public void setDefaultAttributeGroup(String value) {
		defaultAttributeGroup = value;
	}

	public String getDefaultAttributeGroup() {
		if (defaultAttributeGroup == null && this.superClass != null)
			return superClass.getDefaultAttributeGroup();
		return defaultAttributeGroup;
	}

	public void setOrderedAttributeGroups(String[] value, OrderedAttributesList[] oal) {
		orderedAttributeGroups = value;
		
		if(oal != null && oal.length > 0) {
			if(oal.length != orderedAttributeGroups.length)
				throw new AtomException("If a DomainObject has the Annotation [OrderedAttributeGroups] the field [attributes] needs to either be empty, or have the exact same amout of elements as groups!");
			
			int maxAttr = 0;
			for(OrderedAttributesList list : oal) {
				if(list != null)
					maxAttr = Math.max(maxAttr, list.value().length);
			}
			orderedGroupFields = new String[oal.length][maxAttr];
			for(int i=0 ; i<= oal.length ; i++) {
				orderedGroupFields[i] = oal[i].value();
			}
		}
	}

	public String[] getOrderedAttributeGroups() {
		if (orderedAttributeGroups == null && superClass != null)
			return superClass.getOrderedAttributeGroups();
		return orderedAttributeGroups;
	}

	// public boolean isAccessAllowed(ClientSession session, String accessType)
	// {
	// Set<String> foundTypes = getAccessTypes(session);
	// return (foundTypes.contains(accessType) // allow exact match of
	// accesstype
	// || foundTypes.contains(AtomConfig.accessReadWrite) // allow any access if
	// write access would be allowable
	// || (accessType != AtomConfig.accessReadWrite &&
	// foundTypes.contains(AtomConfig.accessReadOnly)) // allow any access but
	// write access, if
	// // readaccess is allowable
	// || (accessType.equals(AtomConfig.accessLinkage) &&
	// AtomTools.isLinkageAccessAllowed(foundTypes, session, null)) ||
	// (superClass != null && superClass
	// .isAccessAllowed(session, accessType)) // ask superclass for access
	// );
	// }

	// public boolean isAccessAllowed(Map<Integer, String> roles, boolean
	// writeAccess, boolean scanHierarchy) {
	// if (accessRoles.containsKey(AccessListRoles.accessReadWrite)) {
	// for (String role : accessRoles.get(AccessListRoles.accessReadWrite)) {
	// if (roles.containsValue(role) || roles.containsKey(role)) {
	// return true;
	// }
	// }
	// }
	// if (!writeAccess &&
	// accessRoles.containsKey(AccessListRoles.accessReadOnly)) {
	// for (String role : accessRoles.get(AccessListRoles.accessReadOnly)) {
	// if (roles.containsValue(role) || roles.containsKey(role)) {
	// return true;
	// }
	// }
	// }
	// if (accessRoles.containsKey(AccessListRoles.accessDenied)) {
	// for (String role : accessRoles.get(AccessListRoles.accessDenied)) {
	// if (roles.containsValue(role) || roles.containsKey(role)) {
	// return false;
	// }
	// }
	// }
	// if (scanHierarchy && superClass != null) {
	// return superClass.isAccessAllowed(roles, writeAccess, true);
	// } else {
	// // DISCUSS: default accessibility? if none of the users roles is neither
	// allowed, nor denied anywhere in domain tree above the requested class?!
	// return false;
	// }
	// }

	public DomainClass clone() {
		DomainClass clone = new DomainClass(simpleName, packageName, isAbstract);
		clone.pluralName = this.pluralName;
		clone.defaultAttributeGroup = this.defaultAttributeGroup;
		clone.orderedAttributeGroups = this.orderedAttributeGroups;
		clone.annotations = this.annotations;
		clone.attributes = this.attributes;
		clone.accessHandler = this.accessHandler;

		for (DomainClass subClass : subClasses) {
			DomainClass subClassClone = subClass.clone();
			subClass.setSuperClass(clone);
			clone.getSubClasses().add(subClassClone);
		}

		return clone;
	}

//	public String getRelatedWhere(ClientSession session) {
//		
//		if(relatedWhere == null) {
//			if(superClass != null)
//				return superClass.getRelatedWhere(session);
//			else
//				return null;
//		}
//		
//		Integer perID = null, objectID = null, oreID = null;
//		CharSequence oreIDList = "(-1)";
//		
//		if(session.getUser() != null) {
//			perID = session.getUser().getPer_ID();
//			objectID = session.getUser().getObjectID();
//			oreID = session.getUser().getHaupt_OrE_ID();
//			oreIDList = session.getUser().getOrE_ID_List();
//		}
//		
//		return relatedWhere
//				.replace("{$Per_ID}", String.valueOf(perID))
//				.replace("{$objectID}", String.valueOf(objectID))
//				.replace("{$haupt_OrE_ID}", String.valueOf(oreID))
//				.replace("{$OrE_ID_List}", oreIDList);
//	}
	
	
	private transient Map<String, RelationDefinition> relationDefinitions = new HashMap<String, RelationDefinition>();;

	public void addRelationDefinition(RelationDefinition relDef) {
		relationDefinitions.put(relDef.relationName(), relDef);
	}
	
	public RelationDefinition getRelationDefinition(String name) {
		RelationDefinition found = relationDefinitions.get(name);
		if(found != null)
			return found;
		else if(superClass != null)
			return superClass.getRelationDefinition(name);
		
		return null;
	}
	
	public Set<RelationDefinition> getRelationDefinitions(Set<String> names) {
		Set<RelationDefinition> ret = new HashSet<RelationDefinition>();
		for(String name : names) {
			ret.add(getRelationDefinition(name));
		}
		return ret;
	}
	
	public Map<String, RelationDefinition> getRelationDefinitions() {
		if(superClass != null) {
			Map<String, RelationDefinition> superRD = superClass.getRelationDefinitions();
			Map<String, RelationDefinition> rv = new HashMap<String, RelationDefinition>();
			rv.putAll(superRD);
			rv.putAll(relationDefinitions);
			return rv;
		}
		else
			return relationDefinitions;
	}

//	public void setRelatedWhere(String relatedWhere) {
//		this.relatedWhere = relatedWhere;
//	}

//	public String getRelatedJoin() {
//		if(superClass != null && relatedWhere == null)
//			return superClass.getRelatedJoin();
//		return relatedJoin;
//	}
//
//	public void setRelatedJoin(String relatedJoin) {
//		this.relatedJoin = relatedJoin;
//	}
//
//	public boolean getRelatedDistinct() {
//		if(superClass != null && relatedWhere == null)
//			return superClass.getRelatedDistinct();
//		return relatedDistinct;
//	}
//
//	public void setRelatedDistinct(boolean distinct) {
//		this.relatedDistinct = distinct;
//	}

	// public String getImageData() {
	// String imageData = this.getAnnotation("ObjectImage");
	// if(imageData == null)
	// return this.superClass.getImageData();
	// return
	// imageData.substring("@at.ac.fhcampuswien.atom.shared.annotations.ObjectImage(value=".length(),
	// imageData.length()-1);
	// }

	public String getObjectLogoImageData() {
		if ((objectLogoImageData == null || objectLogoImageData.length() == 0) && superClass != null)
			return superClass.getObjectLogoImageData();
		return objectLogoImageData;
	}

	public void setObjectLogoImageData(String objectLogoImageData) {
		this.objectLogoImageData = objectLogoImageData;
	}

	public void setSortColumn(String value) {
		this.sortColumn = value;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void setOrderedAttributesListView(String[] orderedAttributesListViewStrings) {
		this.orderedAttributesListViewStrings = orderedAttributesListViewStrings;
	}

	public static Comparator<DomainClass> nameComparator = new Comparator<DomainClass>() {

		@Override
		public int compare(DomainClass o1, DomainClass o2) {
			return o1.getPluralName().compareTo(o2.getPluralName());
		}
	};

	public void setHideFromGui(boolean hide) {
		hideFromGui = hide;
	}
	
	public boolean hideFromGui() {
		return hideFromGui;
	}

	public void setSearchable(boolean value) {
		this.searchable = value;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public AccessHandler getAccessHandler() {
		return accessHandler;
	}

	/**
	 * we don't need attribute access on the client side.
	 * If you do, please uncomment the two lines at the end:
	 */
	public void fillUsersAccessRecursive(ClientSession session) {
		accessHandler.fillUsersAccess(session);
		if(subClasses != null) for(DomainClass c : subClasses)
			c.fillUsersAccessRecursive(session);
//		if(attributes != null) for(DomainClassAttribute d : attributes.values())
//			d.getAccessHandler().fillUsersAccess(session);
	}
}
