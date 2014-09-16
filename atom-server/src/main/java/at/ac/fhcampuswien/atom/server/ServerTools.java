/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

import com.allen_sauer.gwt.log.client.Log;

public class ServerTools {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	public static String getAuthCookie(HttpServletRequest request) {
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("CAMPUSAUTH".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static String getDecodedAuthCookie(HttpServletRequest request) {
		String cookie = getAuthCookie(request);
		if (cookie != null)
			return decodeAuthCookie(cookie);
		else
			return null;
	}

	public static String getServerDomain(HttpServletRequest request) {
		String referer = request.getHeader("Referer");

		if (referer.contains("127.0.0.1")) {
			AtomTools.log(Log.LOG_LEVEL_INFO, "getServerDomain found 127.0.0.1, using this as host for authcookie", request);
			return "127.0.0.1";
		}	
		else if (referer.contains("localhost")) {
			AtomTools.log(Log.LOG_LEVEL_INFO, "getServerDomain found localhost, using this as host for authcookie", request);
			return "localhost";
		}
		AtomTools.log(Log.LOG_LEVEL_INFO, "getServerDomain couldn't find local-access, using '.fh-campuswien.ac.at' as host for authcookie", request);
		return ".fh-campuswien.ac.at";
	}

	public static String decodeAuthCookie(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AtomTools.log(Log.LOG_LEVEL_FATAL, "This servers java can't decode UTF-8!", ServerTools.class);
			throw new AtomException("This servers java can't decode UTF-8!");
		}
	}

	public static String encodeAuthCookie(String value) {
		if (value.contains(";")) {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				AtomTools.log(Log.LOG_LEVEL_FATAL, "This servers java can't encode UTF-8!", ServerTools.class);
				throw new AtomException("This servers java can't encode UTF-8!");
			}
		} else
			return value;
	}

	public static void setAuthCookie(HttpServletRequest request, HttpServletResponse response, String cookieValue) {
		setAuthCookie(response, getServerDomain(request), cookieValue);
	}

	public static void setAuthCookie(HttpServletResponse response, String domain, String cookieValue) {
		response.addHeader("Set-Cookie", "CAMPUSAUTH=" + encodeAuthCookie(cookieValue) + ";Path=/;Domain=" + domain + ";Secure;httponly");
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "Set-Cookie\", \"CAMPUSAUTH=\"******\";Path=/;Domain=" + domain + ";Secure;httponly", response);
	}

	public static void clearAuthCookie(HttpServletResponse response, String domain) {
		response.addHeader("Set-Cookie", "CAMPUSAUTH=invalid;Path=/;Domain=" + domain + ";Max-Age=0;Secure;httponly");
	}

	public static boolean fillAndDetachInstance(DomainObject instance, EntityManager em, ClientSession cs, boolean partOfList) {

		if (instance == null)
			return false;

		DomainClass dc = DomainAnalyzer.getDomainClass(instance.getConcreteClass());
		return prepareDOInstanceForClient(instance, dc, em, cs, partOfList);
	}

	public static boolean prepareDOInstanceForClient(DomainObject instance, DomainClass dc, EntityManager em, ClientSession cs, boolean partOfList) {

		Set<String> accessTypes = dc.getAccessHandler().getAccessTypes(cs, instance);
		return prepareDOInstanceForClient(instance, dc, em, cs, accessTypes, partOfList);
	}

	public static boolean prepareDOInstanceForClient(DomainObject instance, DomainClass dc, EntityManager em, ClientSession cs, Set<String> accessTypes,
			boolean partOfList) {

		walkInstanceWeb(instance, dc, new HashSet<DomainObject>(), WalkTask.LOAD, true, partOfList, accessTypes, cs);
		em.detach(instance);
		return walkInstanceWeb(instance, dc, new HashSet<DomainObject>(), WalkTask.CLEARLAZYANDDENIED, true, partOfList, accessTypes, cs);
	}

	public static void prepareDOListForClient(List<DomainObject> list, DomainClass dc, EntityManager em, ClientSession cs) {

		HashSet<DomainObject> seen = new HashSet<DomainObject>();
		for (DomainObject o : list) {
			walkInstanceWeb(o, dc, seen, WalkTask.LOAD, true, true, dc.getAccessHandler().getAccessTypes(cs, o), cs);
		}
		for (DomainObject o : list) {
			em.detach(o);
		}
		seen = new HashSet<DomainObject>();
		HashSet<DomainObject> denied = new HashSet<DomainObject>();

		for (DomainObject o : list) {
			if (!walkInstanceWeb(o, dc, seen, WalkTask.CLEARLAZYANDDENIED, true, true, dc.getAccessHandler().getAccessTypes(cs, o), cs)) {
				denied.add(o);
			}
		}
		for (DomainObject d : denied) {
			list.remove(d);
		}
	}

	private enum WalkTask {
		LOAD, CLEARLAZYANDDENIED;
	}

	private static boolean walkInstanceWeb(DomainObject domainObject, DomainClass domainClass, HashSet<DomainObject> objectsISaw, WalkTask task,
			boolean primary, boolean partOfList, Set<String> accessTypes, ClientSession session) {

		// if (objectsISaw.contains(domainObject))
		// return true;

		if (!domainObject.getConcreteClass().equals(domainClass.getName())) {
			domainClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
		}

		// TODO: remove debugging stuff
		AtomTools.log(Log.LOG_LEVEL_TRACE, domainClass.getName() + " - " + domainObject.getStringRepresentation() + "(Task=" + task.name() + ")",
				"ServerTools.walkInstanceWeb");
		// if(domainClass.getName().equals("at.ac.fhcampuswien.atom.shared.domain.Vortrag")) {
		// AtomTools.log(Log.LOG_LEVEL_TRACE, "at.ac.fhcampuswien.atom.shared.domain.Vortrag instance found!", ServerTools.class);
		// }
		// if(domainClass.getName().equals("at.ac.fhcampuswien.atom.shared.domain.PortalPerson")) {
		// AtomTools.log(Log.LOG_LEVEL_TRACE, "at.ac.fhcampuswien.atom.shared.domain.PortalPerson instance found!", ServerTools.class);
		// }

		// loading the stringRepresentation
		// AtomTools.log(Log.LOG_LEVEL_TRACE, "InstanceWebWalker: " + domainObject.getStringRepresentation(), ServerTools.class);

		boolean containsLazyStuff = false;

		boolean permission = checkPermissionsAndClearDeniedProperties(domainClass, domainObject, accessTypes, session, task);
		if (!permission) {
			return false;
		}
		objectsISaw.add(domainObject);

		// DomainClass domainClass =
		// DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
		Class<?> reflClass = domainObject.getClass();
		for (DomainClassAttribute domainClassAttribute : domainClass.getCollectionAttributes()) {
			if (domainObject.getNullReasons().containsKey(domainClassAttribute.getName())) {
				continue; // this attribute has already been cleared before (probably because of only-linkage permission & it's non-relation-essential)
			}
			try {
				Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] {});
				Class<?> collectionClass = getAttribute.getReturnType();
				Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] { collectionClass });

				if (AtomConfig.loadEverythingRelated || domainClassAttribute.isLoadedWhenNotPrimary()
						|| (primary && (!partOfList || domainClassAttribute.isLoadedWithLists()))) {
					AtomTools.log(Log.LOG_LEVEL_TRACE, "loading collection " + domainClassAttribute.getName() + " for instance " + domainObject.toString(),
							ServerTools.class);

					@SuppressWarnings("unchecked")
					Iterable<Object> collectionHibernate = (Iterable<Object>) getAttribute.invoke(domainObject, new Object[] {});
					Collection<Object> collectionCloned = null;
					if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
						collectionCloned = cloneCollection(collectionHibernate, objectsISaw, false, partOfList, session);
						setAttribute.invoke(domainObject, new Object[] { collectionCloned });
					} else if (collectionHibernate != null) {
						for (Object object : collectionHibernate) {
							if (object != null && object instanceof DomainObject && !objectsISaw.contains(object)) {

								DomainObject domainObject2 = (DomainObject) object;
								DomainClass domainClass2 = DomainAnalyzer.getDomainClass(domainObject2.getConcreteClass());

								//if (WalkTask.LOAD.equals(task)) // do this only once = the
									// first time = with
									// activeConnection
									// domainObject2.calculateUserPermissions(session, domainClass2);
								Set<String> accessTypes2 = domainClass2.getAccessHandler().getAccessTypes(session, domainObject2);

								if (AtomTools.isAccessAllowed(AtomConfig.accessLinkage, accessTypes2)) {
									if (!walkInstanceWeb(domainObject2, domainClass2, objectsISaw, task, false, partOfList, accessTypes2, session))
										domainObject.addNullReasons(domainClassAttribute.getName(), AtomConfig.nullReasonNotRelationEssential);
								}
							}
						}
					}
				} else {
					containsLazyStuff = true;

					if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
						// only remove stuff when there is no active database
						// connection (else we would delete the contents in the
						// db!)
						// we won't load the real content, but make it null and
						// call it "lazy loading"
						setAttribute.invoke(domainObject, new Object[] { null });
						domainObject.addNullReasons(domainClassAttribute.getName(), AtomConfig.nullReasonLazyLoading);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (DomainClassAttribute domainClassAttribute : domainClass.getAttributesOfTypeDomainObject()) {
			if (domainObject.getNullReasons().containsKey(domainClassAttribute.getName())) {
				continue; // this attribute has already been cleared before (probably because of only-linkage permission & it's non-relation-essential)
			}
			try {
				Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] {});
				Class<?> collectionClass = getAttribute.getReturnType();
				Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] { collectionClass });

				boolean clear = false;

				if (AtomConfig.loadEverythingRelated || domainClassAttribute.isLoadedWhenNotPrimary()
						|| (primary && (!partOfList || domainClassAttribute.isLoadedWithLists()))) {

					DomainObject relatedDomainObject = (DomainObject) getAttribute.invoke(domainObject, new Object[] {});
					if (relatedDomainObject != null && !objectsISaw.contains(relatedDomainObject)) {

						DomainClass relatedDomainClass = DomainAnalyzer.getDomainClass(relatedDomainObject.getConcreteClass());

						Set<String> accessTypes2 = relatedDomainClass.getAccessHandler().getAccessTypes(session, relatedDomainObject);

						if (AtomTools.isAccessAllowed(AtomConfig.accessLinkage, accessTypes2)) {
							if (!walkInstanceWeb(relatedDomainObject, relatedDomainClass, objectsISaw, task, false, partOfList, accessTypes2, session)) {
								objectsISaw.remove(relatedDomainObject);
								setAttribute.invoke(domainObject, new Object[] { null });
								domainObject.addNullReasons(domainClassAttribute.getName(), AtomConfig.nullReasonLazyLoading);
							}
						} else {
							clear = true;
						}
					}
				} else {
					clear = true;
				}

				if (clear) {
					containsLazyStuff = true;
					if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
						// müssen wir das wirklich löschen?? da inhalt lazy sein
						// könnte: ja!
						setAttribute.invoke(domainObject, new Object[] { null });
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// I don't think this is necessary or even helpful in any way. Was
		// implemented
		// for testing whether it helped with lazy domainObject and it did not.
		// for (DomainClassAttribute domainClassAttribute :
		// domainClass.getSimpleAttributes()) {
		// try {
		// Method getAttribute = reflClass.getMethod("get" +
		// domainClassAttribute.getName(), new Class[] {});
		// Object value = getAttribute.invoke(domainObject, new Object[] {});
		// String stringValue = value!=null ? value.toString() : "null";
		// AtomTools.log(Log.LOG_LEVEL_TRACE, "DomainObject class \"" +
		// domainClass.getSimpleName() +
		// "\" instance \"" + domainObject.getStringRepresentation() +
		// "\" attribute \"" + domainClassAttribute.getName() +
		// "\" value \"" + stringValue + "\"", this);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
			if (!containsLazyStuff) {
				domainObject.setCompletelyLoaded(true);
			} else {
				domainObject.setCompletelyLoaded(false);
			}
		}

		return true;
	}

	private static Collection<Object> cloneCollection(Iterable<Object> source, HashSet<DomainObject> objectsDone, boolean primary, boolean partOfList,
			ClientSession session) {
		Collection<Object> destination = null;

		if (source != null) {
			if (source instanceof HashSet || source.getClass().getName().endsWith("Set")) {
				destination = new HashSet<Object>();
			} else if (source instanceof ArrayList || source.getClass().getName().endsWith("List")) {
				destination = new ArrayList<Object>();
			} else {
				AtomTools.log(Log.LOG_LEVEL_WARN, "unknownCollectionType passing through:" + source, ServerTools.class);
				// FeaturedObject has special Lists which must be allowed to be
				// sent to the client!
				// throw new AtomException("unknown collection type!");
			}
			if (destination != null) {
				for (Object object : source) {
					boolean permission = true;
					if (object != null && object instanceof DomainObject && !objectsDone.contains(object)) {
						DomainObject domainObject = (DomainObject) object;
						DomainClass domainClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
						Set<String> accessTypes = domainClass.getAccessHandler().getAccessTypes(session, domainObject);
						if (AtomTools.isAccessAllowed(AtomConfig.accessLinkage, accessTypes)) {
							// Set<String> accessTypes =
							// AtomTools.checkPermissions(session,
							// AtomConfig.accessLinkage, domainClass,
							// domainObject);
							permission = walkInstanceWeb(domainObject, domainClass, objectsDone, WalkTask.CLEARLAZYANDDENIED, primary, partOfList, accessTypes,
									session);
						} else
							permission = false;
					}
					if (permission)
						destination.add(object);
				}
			}
		}
		return destination;
	}

	/**
	 * Deletes content of non-relation-essential properties, if the user has only linkage permissions
	 * 
	 * @param domainClass
	 * @param domainObject
	 * @param accessTypes
	 * @param session
	 * @param activeConnection
	 * @return false, if user has no permission -> object must not be sent to client
	 */
	private static boolean checkPermissionsAndClearDeniedProperties(DomainClass domainClass, DomainObject domainObject, Set<String> accessTypes,
			ClientSession session, WalkTask task) {
		boolean linkage = false;
		boolean fullRead = false;
		if (accessTypes != null)
			for (String accessType : accessTypes) {
				if (AtomConfig.accessLinkage.equals(accessType))
					linkage = true;
				if (AtomConfig.accessReadOnly.equals(accessType) || AtomConfig.accessReadWrite.equals(accessType))
					fullRead = true;
			}
		if (fullRead || linkage) {
			domainObject.prepareForClient();
		}
		if (fullRead)
			return true;
		else if (!linkage) {
			AtomTools.log(Log.LOG_LEVEL_WARN, "user has no read and not even linkage permissions on this object instance! id=" + domainObject.getObjectID(),
					ServerTools.class);
			return false;
		} else {
			// We don't have full-read-access (e.g. only-linkage) access --> clear all attributes that the user doesn't have specific access to
			if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
				Class<?> reflClass = domainObject.getClass();
				for (DomainClassAttribute attribute : domainClass.getAllAttributes().values()) {
					
					if(attribute.isRelationEssential())
						continue;

					Set<String> ati = attribute.getAccessHandler().getAccessTypes(session, domainObject);
					if (!AtomTools.isAccessAllowed(AtomConfig.accessReadOnly, ati)) {
						try {
							Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(attribute.getName()), new Class[] {});
							Class<?> collectionClass = getAttribute.getReturnType();
							Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(attribute.getName()), new Class[] { collectionClass });
							setAttribute.invoke(domainObject, new Object[] { null });
							domainObject.addNullReasons(attribute.getName(), AtomConfig.nullReasonNotRelationEssential);
						} catch (NoSuchMethodException e) {
							AtomTools
							.log(Log.LOG_LEVEL_ERROR,
									"Could not find get and/or set method for attribute "
											+ attribute.getName()
											+ ", therefore could not clear this attribute although it is not relationEssential and the user has only linkagePermission --> will not send this object to the client for safty reasons.",
									ServerTools.class);
							return false;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			return true;
		}
	}

	public static Class<? extends DomainObject> getClassForName(String nameOfClass) {
		Class<? extends DomainObject> searchedClass;
		try {
			searchedClass = Class.forName(nameOfClass).asSubclass(DomainObject.class);
			return searchedClass;
		} catch (ClassNotFoundException e) {
			throw new AtomException("\"" + nameOfClass + "\" is no valid DomainObject");
		}
	}
	
	public static String fillWherePlaceholders(ClientSession session, String where) {
	
	if(where == null || where.length() <= 0) {
			return null;
	}
	
	Integer perID = null, objectID = null, oreID = null;
	CharSequence oreIDList = "(-1)";
	
	if(session.getUser() != null) {
		perID = session.getUser().getPer_ID();
		objectID = session.getUser().getObjectID();
		oreID = session.getUser().getHaupt_OrE_ID();
		oreIDList = session.getUser().getOrE_ID_List();
	}
	
	return where
			.replace("{$Per_ID}", String.valueOf(perID))
			.replace("{$objectID}", String.valueOf(objectID))
			.replace("{$haupt_OrE_ID}", String.valueOf(oreID))
			.replace("{$OrE_ID_List}", oreIDList);
}

	public static String getWhereClause(DomainClass searchedClass, ArrayList<DataFilter> filters, HashSet<DataFilter> nonFieldFilters, String searchString, boolean onlyScanStringRepresentation,
			ClientSession session, boolean onlyRelated, Collection<RelationDefinition> reqRelDefs) {

//		DataFilter deepSearch = null, quickSearch = null;
//		if(filters != null) {
//			for(DataFilter f : filters) {
//				if(AtomConfig.specialFilterDeepSearch.equals(f.getColumn())) {
//					if(deepSearch != null)
//						AtomTools.log(Log.LOG_LEVEL_ERROR, "multiple DataFilters for the same column recieved! Server-Logic is not designed to return meaningful results for such requests!", ServerTools.class);
//					deepSearch = f;
//				}
//				else if(AtomConfig.specialFilterQuickSearch.equals(f.getColumn())) {
//					if(quickSearch != null)
//						AtomTools.log(Log.LOG_LEVEL_ERROR, "multiple DataFilters for the same column recieved! Server-Logic is not designed to return meaningful results for such requests!", ServerTools.class);
//					quickSearch = f;
//				}
//			}
//			if(deepSearch != null) {
//				filters.remove(deepSearch);
//				if(searchString == null || searchString.equals("")) {
//					//search operation is empty, can replace it with this
//					searchString = deepSearch.getValue();
//					onlyScanStringRepresentation = false;
//				}
//				else if(searchString.equals(deepSearch.getValue()) && onlyScanStringRepresentation == false) {
//					//all good, the search operation is equal to the deepSearch Filter. 
//				}
//				else if(onlyScanStringRepresentation == true) {
//					if(!(quickSearch != null && searchString.equals(quickSearch.getValue()))) {
//						filters.add(new DataFilter(AtomConfig.specialFilterQuickSearch, searchString, null, null));
//					}
//					searchString = deepSearch.getValue();
//					onlyScanStringRepresentation = false;
//				}
//				else {
//					AtomTools.log(Log.LOG_LEVEL_ERROR, "the result for this request will not be correct. multiple deepSearch commands recieved.", ServerTools.class);
//				}
//			}
//			if(quickSearch != null) {
//				if(searchString != null && searchString.equals(quickSearch.getValue())) {
//					filters.remove(quickSearch);
//				}
//			}
//		}
		String filterWhere = getFilterWhereClause(searchedClass, filters, nonFieldFilters);
		
		String relatedWhere = "(";
		if (onlyRelated) {
			for(RelationDefinition rd : reqRelDefs) {
				if(rd != null && rd.relationName() != RelationDefinition.noRelationRequired)
					relatedWhere += fillWherePlaceholders(session, rd.where()) + ") OR (";
			}
			relatedWhere = relatedWhere.substring(0, relatedWhere.length() - 5);
			//relatedWhere = searchedClass.getRelatedWhere(session);
		}	

		if (searchString == null || "".equals(searchString)) {
			if (!onlyRelated)
				if ("".equals(filterWhere))
					return "";
				else
					return "WHERE " + filterWhere;
			else if ("".equals(filterWhere))
				return "WHERE " + relatedWhere;
			else
				return "WHERE " + "(" + filterWhere + ") AND (" + relatedWhere + ")";
		} else {
			String searchWhere = getSearchWhereClause(searchedClass, searchString, onlyScanStringRepresentation);
			if (!onlyRelated)
				if ("".equals(filterWhere))
					return "WHERE " + searchWhere;
				else
					return "WHERE (" + filterWhere + ") AND (" + searchWhere + ")";
			else if ("".equals(filterWhere))
				return "WHERE (" + searchWhere + ") AND (" + relatedWhere + ")";
			else
				return "WHERE (" + filterWhere + ") AND (" + searchWhere + ") AND (" + relatedWhere + ")";
		}
	}

	private static String getSearchWhereClause(DomainClass domainClass, String searchString, boolean onlyScanStringRepresentation) {
		
		if(onlyScanStringRepresentation) {
			return "obj.stringRepresentation like '%" + searchString + "%'"; 
		}
		
		String whereClause = "";
		boolean containsLetters = searchString.matches(".*[a-zA-Z]+.*");
		HashMap<String, DomainClassAttribute> attributes = domainClass.getAllAttributes();
		for (DomainClassAttribute attribute : attributes.values()) {
			// TODO: implement handling of nonFieldAttributes
			if (attribute.isField() && (!attribute.getType().startsWith("at.ac.fhcampuswien.atom.shared.domain"))
					&& (!containsLetters || attribute.getType().equals("java.lang.String"))) {
				whereClause += "obj." + attribute.getName() + " like '%" + searchString + "%' OR ";
				// AtomTools.lowerFirstChar()
			}
		}
		if (whereClause.equals("")) {
			whereClause = "0 = 1";
		} else {
			whereClause = whereClause.substring(0, whereClause.length() - 4);
		}
		return whereClause;
	}

	private static String getFilterWhereClause(DomainClass searchedClass, ArrayList<DataFilter> filters, HashSet<DataFilter> nonFieldFilters) {
		String whereClause = "";
		if (filters != null && filters.size() > 0) {
			boolean first = true;
			for (DataFilter aFilter : filters) {
				String columnName = aFilter.getColumn();
				DomainClassAttribute attribute = searchedClass.getAttributeNamed(columnName);
				if (attribute.isField()) {
					if (!first) {
						whereClause += " AND ";
					} else {
						first = false;
					}
					String filterType = aFilter.getFilterType();
					String columnType = aFilter.getColumnType();
					String filterValue = aFilter.getValue();
					
					String dbColumn;
					if(AtomConfig.specialFilterDeepSearch.equals(columnName)) {
						whereClause += "(" + getSearchWhereClause(searchedClass, filterValue, false) + ")";
						continue;
					}
					else {
						dbColumn = AtomTools.lowerFirstChar(columnName);
						if(attribute.getType().startsWith("at.ac.fhcampuswien.atom.shared.domain.")) {
							dbColumn += ".stringRepresentation";
						}
						else if(attribute.getType().contains("<at.ac.fhcampuswien.atom.shared.domain.")) {
							//handle set of domainobjects
							String listedType = AtomTools.getListedType(attribute.getType());
							whereClause += "EXISTS (FROM " + listedType + " lt WHERE lt MEMBER OF obj." + dbColumn + " AND lt.stringRepresentation" + getFilterRightPart(filterType, "String", filterValue) + ")";
							// JOIN not neccesary? -->  lt IN obj." + dbColumn + " AND 
							continue;
						}
					}
					 

					if ("date".equals(columnType)) {
						Date date = new Date(Long.valueOf(filterValue));
						SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
						filterValue = formatter.format(date);
					}

					// if (Date.class.equals((filterValue.getClass()))) {
					// SimpleDateFormat formatter = new
					// SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
					// filterValueAsString = formatter.format(filterValue);
					// } else {
					// filterValueAsString = filterValue.toString();
					// }
					
					//if ("numeric".equals(columnType) || "boolean".equals(columnType)) {

					whereClause += dbColumn + getFilterRightPart(filterType, columnType, filterValue);

				} else if (nonFieldFilters != null) {
					nonFieldFilters.add(aFilter);
				}
			}
		}
		return whereClause;
	}
	
	private static String getFilterRightPart(String filterType, String columnType, String filterValue) {
		if ("eq".equals(filterType) || "=".equals(filterType)) {
			if ("numeric".equals(columnType) || "boolean".equals(columnType) || "int".equals(columnType) || (columnType != null && columnType.contains("Integer"))) {
				return " = " + filterValue;
			} else {
				return " = '" + filterValue + "'";
			}
		} else if("!=".equals(filterType) || "<>".equals(filterType) || "≠".equals(filterType)) {
			return " <> " + filterValue;
		} else if("∋".equals(filterType) || "contains".equals(filterType)) {
			return " like '%" + filterValue + "%'";
		} else if("∌".equals(filterType)) {
			return " not like '%" + filterValue + "%'";
		} else if ("lt".equals(filterType) || "before".equals(filterType) || "<".equals(filterType)) {
			return " < " + filterValue;
		} else if ("gt".equals(filterType) || "after".equals(filterType) || ">".equals(filterType)) {
			return " > " + filterValue;
		} else if ("le".equals(filterType) || "≤".equals(filterType)) {
			return " <= " + filterValue;
		} else if ("ge".equals(filterType) || "≥".equals(filterType)) {
			return " >= " + filterValue;
		}
		else {
			if ("numeric".equals(columnType) || "boolean".equals(columnType) || "int".equals(columnType) || (columnType != null && columnType.contains("Integer"))) {
				return " = " + filterValue;
			} else {
				return " like '%" + filterValue + "%'";
			}
		}
	}

	public static String getOrderByClause(DomainClass searchedClass, Collection<DataSorter> sorters, HashSet<DataSorter> nonFieldSorters) {
		String orderByClause = "";
		if (sorters != null && sorters.size() > 0) {
			boolean first = true;
			for (DataSorter aSorter : sorters) {
				String columnName = aSorter.getColumn();
				DomainClassAttribute attribute = searchedClass.getAttributeNamed(columnName);
				if (attribute.isField()) {
					if (!first) {
						orderByClause = ", " + orderByClause;
					} else {
						first = false;
					}

					String clausePart = "";
					String type = attribute.getType();
					if (type.contains("<")) {
						clausePart = "obj." + columnName + ".size";
					} else if (type.contains("at.ac.fhcampuswien.atom.shared.domain.")) {

						DomainClass columnType = DomainAnalyzer.getDomainTree().getDomainClassNamed(type);
						String sortColumn = columnType.getSortColumn();
						if (sortColumn == null || sortColumn.length() == 0)
							sortColumn = "objectID";

//						clausePart = "obj." + AtomTools.lowerFirstChar(columnName);
						clausePart = columnName + "." + sortColumn;
						
//						if (type.contains("<")) {
//							clausePart = "minelement(" + clausePart + ")." + sortColumn;
//						} else
//							clausePart += ".";
					} else {
						clausePart = "obj." + columnName;
					}

					if (aSorter.getSortOrder()) {
						clausePart += " ASC";
					} else {
						clausePart += " DESC";
					}
					orderByClause = clausePart + orderByClause;
				} else if (nonFieldSorters != null) {
					nonFieldSorters.add(aSorter);
				}
			}
		}
		if (orderByClause.length() == 0) {
			orderByClause = searchedClass.getSortColumn();
			if (orderByClause.length() > 0)
				orderByClause = "obj." + orderByClause;
		}
		if (orderByClause.length() > 0) {
			orderByClause = "ORDER BY " + orderByClause;
		}
		return orderByClause;
	}

	public static String getOrderByJoins(DomainClass searchedClass, Collection<DataSorter> sorters) {
		String orderJoins = "";
		if (sorters != null && sorters.size() > 0)
			for (DataSorter aSorter : sorters) {
				String columnName = AtomTools.lowerFirstChar(aSorter.getColumn());
				DomainClassAttribute attribute = searchedClass.getAttributeNamed(columnName);
				if (attribute.isField()) {
					String type = attribute.getType();

					if (type.contains("at.ac.fhcampuswien.atom.shared.domain.") && !type.contains("<")) {

						DomainClass columnType = DomainAnalyzer.getDomainTree().getDomainClassNamed(type);

						orderJoins += " LEFT OUTER JOIN obj." + columnName + " " + columnName;
						String sortColumn = columnType.getSortColumn();
						if (sortColumn == null || sortColumn.length() == 0)
							sortColumn = "objectID";
					}
				}
			}
		return orderJoins;
	}

	public static void validateDomainObject(DomainObject domainObject) throws ValidationError {
		DomainClass classOfObject = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
		HashMap<String, DomainClassAttribute> allAttributes = classOfObject.getAllAttributes();
		for (DomainClassAttribute attribute : allAttributes.values()) {
			String attributeValidator = attribute.getAnnotation("AttributeValidator");
			if (attributeValidator != null && attributeValidator != "") {
				try {
					AtomTools.validateAttribute(
							Class.forName(classOfObject.getName()).getMethod("get" + AtomTools.upperFirstChar(attribute.getName()), (Class[]) null)
									.invoke(domainObject, (Object[]) null), attributeValidator);
				} catch (ValidationError e) {
					throw e;
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * we need to replace objects related to the object the user wants to save with the versions, that are in the database right now. This is necessary,
	 * because: 1.) We only check if the user has the rights to edit the first object, not all related objects 2.) The related object might contain nulls that
	 * are not real nulls (lazy loading or permission denied nulls) Hibernate saves the full related object, not only the ID, therefore we provide the whole
	 * object --> the current state inside the database, not the object that the user provided (could be manipulated)
	 * 
	 * @param em
	 *            in case we should not just clear the relations, we need a connected entitymanager.
	 * @param domainObject
	 *            = instance
	 * @param domainClass
	 *            = class of the instance of interest
	 * @param clear
	 *            true = clear relations, false = replace with uptodate copys from the database.
	 */
	public static void replaceRelatedObjects(EntityManager em, DomainObject domainObject, DomainClass domainClass, boolean clear) {
		Class<?> reflClass = domainObject.getClass();

		for (DomainClassAttribute domainClassAttribute : domainClass.getAttributesOfTypeDomainObject()) {

			try {
				Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] {});
				Class<?> collectionClass = getAttribute.getReturnType();
				Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] { collectionClass });

				DomainObject related = (DomainObject) getAttribute.invoke(domainObject, new Object[] {});

				if (related != null) {
					if (clear) {
						setAttribute.invoke(domainObject, new Object[] { null });
					} else {
						DomainObject fromDB = related.getObjectID() != null ? (DomainObject) em.find(related.getClass(), related.getObjectID()) : null;
						setAttribute.invoke(domainObject, new Object[] { fromDB });
					}
				}

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		for (DomainClassAttribute domainClassAttribute : domainClass.getCollectionAttributes()) {

			try {
				Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] {});
				Class<?> collectionClass = getAttribute.getReturnType();
				Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] { collectionClass });

				@SuppressWarnings("unchecked")
				Iterable<Object> related = (Iterable<Object>) getAttribute.invoke(domainObject, new Object[] {});

				if (related != null) {

					if (clear) {
						setAttribute.invoke(domainObject, new Object[] { null });
					} else {
						HashSet<DomainObject> fromDB = new HashSet<DomainObject>();
						for (Object relObj : related) {
							if (relObj instanceof DomainObject) {
								DomainObject obj = (DomainObject) relObj;
								fromDB.add(em.find(obj.getClass(), obj.getObjectID()));
							} else {
								AtomTools.log(Log.LOG_LEVEL_FATAL, "this cannot happen", ServerTools.class);
							}
						}
						setAttribute.invoke(domainObject, new Object[] { fromDB });
					}
				}

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static MessageDigest hasher;
	static {
		try {
			hasher = MessageDigest.getInstance( "SHA" );
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static String getHash(String string) {
		byte[] bytes = hasher.digest( string.getBytes() );
		StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}

	public static String removeDuplicateJoins(String join) {
		
		if(join == null || join.length() <= 1)
			return join;
		
		join = join.trim();
		
		while(join.contains("  "))
			join = join.replace("  ", " ");
		
		String[] joins = join.split("LEFT OUTER JOIN ");
		String rv = "";
		HashSet<String> uniqJoins = new HashSet<String>();
		for(String j : joins) {
			if(j != null) {
				j = j.trim();
				if(j.length() > 0) {
					if(uniqJoins.add(j)) {
						rv += " LEFT OUTER JOIN " + j;
					}
				}
			}
		}
		
		return rv;
		//return "LEFT OUTER JOIN " + Joiner.on(" LEFT OUTER JOIN ").join(uniqJoins);
	}
}