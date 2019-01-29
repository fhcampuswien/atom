/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.exception.SQLGrammarException;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.PersistentString;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

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

	private static String[] allowedDebugHosts = {"127.0.0.1", "10.10.4.24"}; //, "localhost"
	
	public static String getServerDomain(HttpServletRequest request) {
		String referer = request.getHeader("Referer");

		// workaround for chrome not accepting cookies bound to localhost
		if(referer.contains("localhost")) {
			return "";
		}
		
		for(String s : allowedDebugHosts) {
			if(referer.contains(s)) {
				AtomTools.log(Level.INFO, "getServerDomain found " + s + " in the referer string. Using it as host for authcookie; referer = " + referer, request);
				return s;
			}
		}
		
		AtomTools.log(Level.INFO, "getServerDomain couldn't find one of the allowedDebugHosts in the referer string. Using '.fh-campuswien.ac.at' as host for authcookie; referer = " + referer, request);
		return ".fh-campuswien.ac.at";
	}

	public static String decodeAuthCookie(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			AtomTools.log(Level.SEVERE, "This servers java can't decode UTF-8!", ServerTools.class);
			throw new AtomException("This servers java can't decode UTF-8!");
		}
	}

	public static String encodeAuthCookie(String value) {
		if (value.contains(";")) {
			try {
				return URLEncoder.encode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				AtomTools.log(Level.SEVERE, "This servers java can't encode UTF-8!", ServerTools.class);
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
		AtomTools.log(Level.FINE, "Set-Cookie\", \"CAMPUSAUTH=\"******\";Path=/;Domain=" + domain + ";Secure;httponly", response);
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

	/**
	 * 
	 * @param list of DomainObjects to prepare
	 * @param dc DomainClass of the given List of DomainObjects
	 * @param em EntityManager
	 * @param cs ClientSession containing permission info
	 * @return number of instances removed from the list because access to them is denied for this user.
	 */
	public static int prepareDOListForClient(List<DomainObject> list, DomainClass dc, EntityManager em, ClientSession cs) {

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
		return denied.size();
	}

	private enum WalkTask {
		LOAD, CLEARLAZYANDDENIED;
	}

	/**
	 * 
	 * @param domainObject
	 * @param domainClass
	 * @param objectsISaw
	 * @param task
	 * @param primary
	 * @param partOfList
	 * @param accessTypes
	 * @param session
	 * @return false if permission is denied and instance must not be sent to client
	 */
	private static boolean walkInstanceWeb(DomainObject domainObject, DomainClass domainClass, HashSet<DomainObject> objectsISaw, WalkTask task,
			boolean primary, boolean partOfList, Set<String> accessTypes, ClientSession session) {

		// if (objectsISaw.contains(domainObject))
		// return true;

		if (!domainObject.getConcreteClass().equals(domainClass.getName())) {
			domainClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
		}

		// TODO: remove debugging stuff
		AtomTools.log(Level.FINER, domainClass.getName() + " - " + domainObject.getStringRepresentation() + "(Task=" + task.name() + ")",
				"ServerTools.walkInstanceWeb");
		// if(domainClass.getName().equals("at.ac.fhcampuswien.atom.shared.domain.Vortrag")) {
		// AtomTools.log(Level.FINER, "at.ac.fhcampuswien.atom.shared.domain.Vortrag instance found!", ServerTools.class);
		// }
		// if(domainClass.getName().equals("at.ac.fhcampuswien.atom.shared.domain.PortalPerson")) {
		// AtomTools.log(Level.FINER, "at.ac.fhcampuswien.atom.shared.domain.PortalPerson instance found!", ServerTools.class);
		// }

		// loading the stringRepresentation
		// AtomTools.log(Level.FINER, "InstanceWebWalker: " + domainObject.getStringRepresentation(), ServerTools.class);

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
					AtomTools.log(Level.FINER, "loading collection " + domainClassAttribute.getName() + " for instance " + domainObject.toString(),
							ServerTools.class);

					@SuppressWarnings("unchecked")
					Iterable<Object> collectionHibernate = (Iterable<Object>) getAttribute.invoke(domainObject, new Object[] {});
					Collection<Object> collectionCloned = null;
					if (WalkTask.CLEARLAZYANDDENIED.equals(task)) {
						try {
							collectionCloned = cloneCollection(collectionHibernate, objectsISaw, false, partOfList, session);
							setAttribute.invoke(domainObject, new Object[] { collectionCloned });
						}
						catch(org.hibernate.LazyInitializationException e) {
							AtomTools.log(Level.SEVERE, "huge problem here huston!", e);
						}
						
						
					} else if (collectionHibernate != null) {
						try { for (Object object : collectionHibernate)
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
						catch(javax.persistence.EntityNotFoundException e) {
							ServerTools.log(Level.SEVERE, "db messed up", domainObject, e);
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
		// AtomTools.log(Level.FINER, "DomainObject class \"" +
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
			ClientSession session) throws org.hibernate.LazyInitializationException {
		Collection<Object> destination = null;

		if (source != null) {
			if (source instanceof HashSet || source.getClass().getName().endsWith("Set")) {
				destination = new HashSet<Object>();
			} else if (source instanceof ArrayList || source.getClass().getName().endsWith("List")) {
				destination = new ArrayList<Object>();
			} else {
				AtomTools.log(Level.WARNING, "unknownCollectionType passing through:" + source, ServerTools.class);
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
			AtomTools.log(Level.WARNING, "user has no read and not even linkage permissions on this object instance! id=" + domainObject.getObjectID(),
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
							.log(Level.SEVERE,
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
//						AtomTools.log(Level.SEVERE, "multiple DataFilters for the same column recieved! Server-Logic is not designed to return meaningful results for such requests!", ServerTools.class);
//					deepSearch = f;
//				}
//				else if(AtomConfig.specialFilterQuickSearch.equals(f.getColumn())) {
//					if(quickSearch != null)
//						AtomTools.log(Level.SEVERE, "multiple DataFilters for the same column recieved! Server-Logic is not designed to return meaningful results for such requests!", ServerTools.class);
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
//					AtomTools.log(Level.SEVERE, "the result for this request will not be correct. multiple deepSearch commands recieved.", ServerTools.class);
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
//		String requiredJoins = "";
		boolean containsLetters = searchString.matches(".*[a-zA-Z]+.*");
		HashMap<String, DomainClassAttribute> attributes = domainClass.getAllAttributes();
		for (DomainClassAttribute attribute : attributes.values()) {
			// TODO: implement handling of nonFieldAttributes
			if (attribute.isField() && (!attribute.getType().startsWith("at.ac.fhcampuswien.atom.shared.domain"))
					&& (!containsLetters || attribute.getType().equals("java.lang.String"))) {
				whereClause += "obj." + attribute.getName() + " like '%" + searchString + "%' OR ";
				// AtomTools.lowerFirstChar()
			}
			else if("java.util.Set<at.ac.fhcampuswien.atom.shared.domain.PersistentString>".equals(attribute.getType())) {
//				requiredJoins += " LEFT OUTER JOIN obj." + attribute.getName();
//				whereClause += "obj." + attribute.getName() + ".value like '%" + searchString + "%' OR ";
				whereClause += "EXISTS (FROM PersistentString lt WHERE lt MEMBER OF obj." + attribute.getName() + " AND lt.value like '%" + searchString + "%' ) OR ";
				AtomTools.log(Level.INFO, "deepSearch handling PersistentString Set! -> " + attribute.getName(), ServerTools.class);
			}
			else {
				AtomTools.log(Level.INFO, "deepSearch non handled attribute: " + attribute.getName(), ServerTools.class);
			}
		}
		if (whereClause.equals("")) {
			whereClause = "0 = 1";
		} else {
			whereClause = whereClause.substring(0, whereClause.length() - 4);
		}
//		if("".equals(requiredJoins))
		return whereClause;
//		else
//			return requiredJoins + ";" + whereClause;
	}

	private static String getFilterWhereClause(DomainClass searchedClass, ArrayList<DataFilter> filters, HashSet<DataFilter> nonFieldFilters) {
		String whereClause = "";
		if (filters != null && filters.size() > 0) {
			boolean first = true;
			for (DataFilter aFilter : filters) {
				String columnName = aFilter.getColumn();
				DomainClassAttribute attribute = searchedClass.getAttributeNamed(columnName);
				if (attribute.isField() && !attribute.isTransient()) {
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
						String searchWhere = getSearchWhereClause(searchedClass, filterValue, false);
						whereClause += "(" + searchWhere + ")";
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
					

					if (DomainClassAttribute.dateType.equals(columnType)) {
						//Date date = new Date(Long.valueOf(filterValue));
						//SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
						//filterValue = "'" + formatter.format(date) + "'";
						filterValue = "'" + filterValue + "'";
					}
					else {
						String[] lbd = attribute.getListBoxDisplay();
						if(lbd != null && lbd.length > 0) {
							String[] lbk = attribute.getListBoxKeys();
							if(!lbk.equals(lbd)) {
								for(int i=0 ; i < lbd.length ; i++) {
									if(lbd[i].equals(filterValue)) {
										filterValue = lbk[i];
										break;
									}
								}
							}
						}
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
			try {
				AtomTools.validateAttribute(attribute.getDisplayName(), Class.forName(classOfObject.getName()).getMethod("get" + 
					AtomTools.upperFirstChar(attribute.getName()), (Class[]) null).invoke(domainObject, (Object[]) null), attribute.getValidators());
			} catch (ValidationError e) {
				throw e;
			} catch (Throwable t) {
				t.printStackTrace();
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
	public static void handleRelatedObjects(EntityManager em, DomainObject domainObject, DomainClass domainClass, boolean clear, ClientSession session, DomainObject dbVersion) {
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
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		for (DomainClassAttribute domainClassAttribute : domainClass.getCollectionAttributes()) {

			// keep the new state of PersistentString Sets, since
			// PersistentStrings are always created directly inside instances of
			// other Objects, and not managed separately.
			try {
				Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] {});
				Class<?> collectionClass = getAttribute.getReturnType();
				Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(domainClassAttribute.getName()), new Class[] { collectionClass });

				if (clear) {
					setAttribute.invoke(domainObject, new Object[] { null });
				} else {
					
					if(dbVersion == null)
						dbVersion = (DomainObject) em.find(domainObject.getClass(), domainObject.getObjectID());

					@SuppressWarnings("unchecked")
					Collection<? extends DomainObject> related = (Collection<? extends DomainObject>) getAttribute.invoke(domainObject, new Object[] {});
					@SuppressWarnings("unchecked")
					Collection<? extends DomainObject> dbRelated = (Collection<? extends DomainObject>) getAttribute.invoke(dbVersion, new Object[] {});
					
					String otherSidePermissionRequired = domainClassAttribute.getOtherSidePermissionRequired();
					String mappedBy = domainClassAttribute.getMappedBy();
					
					Constructor<?> ctor = null;
					if(collectionClass == java.util.Set.class)
						ctor = HashSet.class.getConstructor();
					else if(collectionClass == java.util.List.class)
						ctor = ArrayList.class.getConstructor();
					else
						ctor = collectionClass.getConstructor();
					
					@SuppressWarnings("unchecked")
					Collection<DomainObject> target = (Collection<DomainObject>) ctor.newInstance();
					
					if (related != null) for (DomainObject obj : related) {
						if(obj instanceof PersistentString) {
							//handle PersistentStrings special, since we don't want those "DomainObjects" to be handled manually by the user but simply persisted with whatever other DomainObject they are linked to.
							//those are also the only DomainObjects that can be linked to others without being saved first (intentional UI limitation by design)
							PersistentString ps = (PersistentString) obj;
							ps.setOwner(domainObject);
							ps.setOwnersAttribute(domainClassAttribute.getName());
							ps.prepareSave(session);
							if(ps.getObjectID() != null) {
								//has been saved before, check if owner is the same, to prevent pirating
								PersistentString psDB = em.find(ps.getClass(), ps.getObjectID());
								if(!psDB.getOwner().getObjectID().equals(ps.getOwner().getObjectID())
										|| psDB.getOwnersAttribute() != null && !psDB.getOwnersAttribute().equals(ps.getOwnersAttribute())) {
									throw new AtomException("User tried to hijack a PersistentString from another Object or attribute! DBowner = " + psDB.getOwner().getObjectID() + "; newOwner = " + ps.getOwner().getObjectID() + "; DBattribute = " + psDB.getOwnersAttribute() + "; newAttribute = " + ps.getOwnersAttribute());
								}
							}
//									PersistentString psDB = em.merge(ps);
//									fromDB.add(psDB);
							target.add(ps);
						}
						else {
							DomainObject dbObj = em.find(obj.getClass(), obj.getObjectID());
							if(dbRelated.contains(dbObj) || otherSidePermissionRequired == null || otherSidePermissionRequired.length() < 1 || AtomTools.isAccessAllowed(otherSidePermissionRequired, domainClassAttribute.getAccessHandler().getAccessTypes(session, dbObj))) {
								if(mappedBy != null && mappedBy.length() > 0) {
									Class<?> collectedClass = dbObj.getClass();
									try {
										Method setMappedBy = collectedClass.getMethod("set" + AtomTools.upperFirstChar(mappedBy), new Class[] { domainObject.getClass() });
										setMappedBy.invoke(dbObj, new Object[] { domainObject });
									}
									catch(NoSuchMethodException e) {
										AtomTools.log(Level.INFO, "ServerTools.handleRelatedAttributes - mappedBy reverse side couldnt find set method, save relation! - " + domainClassAttribute.getName() + " -> " + collectedClass.getName() + "." + mappedBy, null);
										// don't fuss about non writeable reverse sides.
									}
								}
								target.add(dbObj);	
							}
						}
					}
					if(domainObject.getObjectID() != null && (mappedBy != null && mappedBy.length() > 0 || otherSidePermissionRequired != null && otherSidePermissionRequired.length() > 0)) {
						//if otherSidePermissionRequired is set to something non empty, check every removed related object for permissions for the acting user.
						//if the object is pre-existing and the set is mappedBy reverse side, linked objects that might have been removed from the set need to be told of the disconnect - but only if the current user has write permissions to those disconnected objects!
						
						if(dbRelated != null) for(DomainObject linkedInDB : dbRelated)
							if(!target.contains(linkedInDB)) {
								// if user removed it
								boolean allowed = (otherSidePermissionRequired == null || otherSidePermissionRequired.length() < 1 || AtomTools.isAccessAllowed(otherSidePermissionRequired, domainClassAttribute.getAccessHandler().getAccessTypes(session, linkedInDB)));
								if(mappedBy != null && mappedBy.length() > 0 && allowed)
									try {
										Class<? extends DomainObject> linkedClass = linkedInDB.getClass();
										Method getMappedBy = linkedClass.getMethod("get" + AtomTools.upperFirstChar(mappedBy), new Class[] {});
										Class<?> mappedByClass = getMappedBy.getReturnType();
										Method setMappedBy = linkedClass.getMethod("set" + AtomTools.upperFirstChar(mappedBy), new Class[] { mappedByClass });
										
										if(Collection.class.isAssignableFrom(mappedByClass)) {
											@SuppressWarnings("unchecked")
											Collection<? extends DomainObject> mappedByCollection = (Collection<? extends DomainObject>) getMappedBy.invoke(linkedInDB, new Object[] {});
											mappedByCollection.remove(dbVersion);
											setMappedBy.invoke(linkedInDB, new Object[] { mappedByCollection });
										}
										else //not a collection, simply clear OneToMany relationship:
											setMappedBy.invoke(linkedInDB, new Object[] { null });
										
										em.merge(linkedInDB);
									}
									catch(NoSuchMethodException e) {
										AtomTools.log(Level.INFO, "ServerTools.handleRelatedAttributes - mappedBy reverse side couldnt find set method, save relation! - " + domainClassAttribute.getName() + " -> " + collectionClass.getName() + "." + mappedBy, null);
										// don't fuss about non writeable reverse sides.
									}
								if(!allowed) {
									// user has no permission to unlink this. re-add it back!
									target.add(linkedInDB);
								}
							}
					}
					setAttribute.invoke(domainObject, new Object[] { target });
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
			} catch (Throwable t) {
				t.printStackTrace();
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
	
	public static void closeDBConnection(EntityTransaction tx, EntityManager em) {
		try {
			if(tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
		catch(Throwable t) {
			ServerTools.log(Level.WARNING, "Transaction rollback failed", null, t);
		}
		if (em != null) {
			try {
				em.close();
			}
			catch(Throwable t) {
				ServerTools.log(Level.WARNING, "could not close EntityManager", null, t);
			}
		}
	}
	
	public static void log(Level logLevel, String message, Object caller, Throwable t) {
		Throwable t2 = t;
		while(t2 != null) {
			if(t2 instanceof SQLGrammarException) {
				SQLGrammarException e = (SQLGrammarException) t2;
				AtomTools.log(logLevel,"SQLGrammarException happened, sql: " + e.getSQL(),caller);
			}
//			else if(t2 instanceof RollbackException) {
//				RollbackException e = (RollbackException) t2;
//			}
//			else if(t2 instanceof javax.persistence.PersistenceException) {
//				javax.persistence.PersistenceException e = (javax.persistence.PersistenceException) t2;
//			}
			else if(t2 instanceof org.hibernate.exception.DataException) {
				org.hibernate.exception.DataException e = (org.hibernate.exception.DataException) t2;
				AtomTools.log(logLevel,"hibernate.DataException happened, sql: " + e.getSQL(),caller);
			}
			if(t2.equals(t2.getCause()))
				break;
			t2 = t2.getCause();
		}
		
		AtomTools.log(logLevel,message,caller,t);
	}
}
