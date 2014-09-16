/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;

import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.hibernate.jdbc.Work;

import at.ac.fhcampuswien.atom.server.auth.Authenticator;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.DomainObjectSearchResult;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.FeaturedObject;
import at.ac.fhcampuswien.atom.shared.domain.FrameVisit;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;

import com.allen_sauer.gwt.log.client.Log;

public class ServerSingleton {

	private static ServerSingleton singelton = null;

	public static ServerSingleton getInstance() {
		if (singelton == null)
			singelton = new ServerSingleton();
		return singelton;
	}

	// recommendation seems to be to use a new em per request, therefore
	// disabled
	// pooling & using the Factory directly.
	// private ObjectPool<EntityManager> emPool = new
	// SoftReferenceObjectPool<EntityManager>(new
	// PoolableEntityManagerFactory());
	//
	// private void freeEm(EntityManager em) {
	// try {
	// if (em != null)
	// emPool.returnObject(em);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void freeEMDelayed(final EntityManager em) {
	// Timer t = new Timer();
	// t.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// freeEm(em);
	// }
	// }, 100);
	// }
	private PoolableEntityManagerFactory emFactory = null;

	
	private Authenticator auth = null;
	
	public Authenticator getAuth() {
		return auth;
	}

	private ServerSingleton() {
		LocaleProxy.initialize();
		
		emFactory = new PoolableEntityManagerFactory();
		auth = new Authenticator(emFactory);
		
		if(AtomConfig.updateAllStringRepresentationsOnApplicationStartup)
			updateAllStringRepresentations();
		
		AtomTools.log(Log.LOG_LEVEL_INFO, "Atom ServerSingleton Startup Finished - " + AtomTools.getMessages().willkommen_in_app(), this);
		
//		updateStringRepresentationsOnce("at.ac.fhcampuswien.atom.shared.domain.DomainObject");
//		updateStringRepresentationsOnce("at.ac.fhcampuswien.atom.shared.domain.PortalPerson");
	}
	

	

	public DomainClass getDomainTreeForUser(HttpServletRequest request) {
		ClientSession session = auth.getSessionFromCookie(request, false);
		return DomainAnalyzer.getUsersDomainTree(session);
	}

	public DomainObject getDomainObject(HttpServletRequest request, Integer id, String nameOfClass) {

		ClientSession session = auth.getSessionFromCookie(request, false);
		return getDomainObject(session, id, nameOfClass);
	}

	public DomainObject getDomainObject(ClientSession session, Integer id, String nameOfClass) {
		if (nameOfClass == null || nameOfClass.length() < 1) {
			AtomTools.log(Log.LOG_LEVEL_WARN, "Selecting DomainObject without any class info - performance problem!!", this);
			return getDomainObject(session, id, DomainObject.class);
		}
		else
			return getDomainObject(session, id, ServerTools.getClassForName(nameOfClass));
	}

	public DomainObject getDomainObject(ClientSession session, String clue, String nameOfClass) {
		if (nameOfClass == null || nameOfClass.length() < 1)
			return getDomainObject(session, clue, DomainObject.class);
		else
			return getDomainObject(session, clue, ServerTools.getClassForName(nameOfClass));
	}

	public DomainObject getDomainObject(ClientSession session, String clue, Class<?> classOfObject) {
		if (clue != null && clue.length() > 0) {

			Double linkedID = null;
			try {
				linkedID = Double.valueOf(clue);
			} catch (NumberFormatException e) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "doesn't look like this was an objectID (couldn't parse as double; " + clue, this);
			}
			if (linkedID != null) {
				// find instance by id
				DomainObject obj = getDomainObject(session, linkedID.intValue(), classOfObject);
				if (obj != null) {
					return obj;
				}
			}

			updateStringRepresentationsOnce(classOfObject.getName());

			// find instance by string representation
			ArrayList<DataFilter> filters = new ArrayList<DataFilter>(1);
			filters.add(new DataFilter("stringRepresentation", clue, "contains", "text"));
			DomainObjectList list = getListOfDomainObjects(session, classOfObject.getName(), 0, 20, filters, null, null, false, false);
			if (list.getTotalSize() == 0) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "could not find instance of \"" + classOfObject.getName() + "\" with stringRepresentation like %" + clue
						+ "%", this);
			} else if (list.getTotalSize() == 1) {
				return list.getDomainObjects().get(0);
			} else {
				AtomTools.log(Log.LOG_LEVEL_WARN, "there is more than one instance of \"" + classOfObject.getName() + "\" with stringRepresentation like %"
						+ clue + "%", this);
				DomainObject match = null;
				for (DomainObject d : list.getDomainObjects()) {
					if (d.getStringRepresentation().equals(clue)) {
						if (match == null) {
							match = d;
						} else {
							AtomTools.log(Log.LOG_LEVEL_WARN, "there is more than one instance of \"" + classOfObject.getName()
									+ "\" with stringRepresentation equals \"" + clue + "\"; for lack of a better idea I will simply take the first one..",
									this);
						}
					}
				}
				if (match != null)
					return match;
				else
					return list.getDomainObjects().get(0);
			}
		}
		return null;
	}

	private DomainObject getDomainObject(ClientSession session, int id, Class<?> classOfObject, EntityManager em) {
		if(DomainObject.class.equals(classOfObject)) {
			AtomTools.log(Log.LOG_LEVEL_WARN, "Selecting DomainObject without any class info - performance problem!!", this);
		}
		DomainObject result = (DomainObject) em.find(classOfObject, id);
		ServerTools.fillAndDetachInstance(result, em, session, false);
		return result;
	}

	public DomainObject getDomainObject(ClientSession session, int id, Class<?> classOfObject) {

		EntityManager em = null;
		try {
			em = emFactory.makeObject();
			return getDomainObject(session, id, classOfObject, em);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
		return null;
	}

	public DomainObjectList getListOfDomainObjects(HttpServletRequest request, String nameOfClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, boolean onlyRelated) {

		ClientSession session = auth.getSessionFromCookie(request, false);
		return getListOfDomainObjects(session, nameOfClass, fromRow, pageSize, filters, sorters, searchString, onlyScanStringRepresentation, onlyRelated);
		// DomainClass requestedClass = DomainAnalyzer.getDomainClass(nameOfClass);
		//
		// Set<String> accessTypes = requestedClass.getAccessTypes(session);
		// return getListOfDomainObject(requestedClass, fromRow, pageSize, filters, sorters, searchString, accessTypes, session,
		// onlyRelated);
	}

	public DomainObjectList getListOfDomainObjects(ClientSession session, String nameOfClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, boolean onlyRelated) {
		DomainClass requestedClass = DomainAnalyzer.getDomainClass(nameOfClass);
		if (requestedClass == null) {
			AtomTools.log(Log.LOG_LEVEL_WARN, "DomainClass with name \"" + nameOfClass + "\" not found! will use DomainObject as class", this);
			requestedClass = DomainAnalyzer.getDomainTree();
		}
		return getListOfDomainObject(requestedClass, fromRow, pageSize, filters, sorters, searchString, onlyScanStringRepresentation, session, onlyRelated);
	}

	private DomainObjectList getListOfDomainObject(DomainClass domainClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, ClientSession session, boolean onlyRelated) {

		Set<String> requiredRelations = AtomTools.getRequiredRelations(AtomConfig.accessLinkage, domainClass.getAccessHandler().getAccess(session));
		Collection<RelationDefinition> reqRelDefs;
		
		if(onlyRelated) {
			reqRelDefs = domainClass.getRelationDefinitions().values();
		}
		else {
			reqRelDefs = domainClass.getRelationDefinitions(requiredRelations);
		}
		
		if(reqRelDefs.size() <= 0) {
			if(!requiredRelations.contains(RelationDefinition.noRelationRequired))
				throw new AuthenticationException("no relation definitions found and user has no unrelated-access allowance!");
			
			onlyRelated = false;
		}
		else if(!requiredRelations.contains(RelationDefinition.noRelationRequired)) {
			onlyRelated = true;
		}
		
		//AtomTools.checkPermissionMatch(AtomConfig.accessLinkage, domainClass.getAccessHandler().getAllAccessTypes(session));

		List<DomainObject> resultList = new ArrayList<DomainObject>();
		Long totalSize = null;
		EntityManager em = null;

		try {
			Class<?> specificClass = ServerTools.getClassForName(domainClass.getName());
			Class<?>[] queryParameterClasses = { DomainClass.class, int.class, int.class, ArrayList.class, ArrayList.class, String.class };
			Object[] queryParameters = { domainClass, fromRow, pageSize, filters, sorters, searchString };
			Method getListCountQuery = specificClass.getMethod("getListCountQuery", queryParameterClasses);
			String countQuery = (String) getListCountQuery.invoke(null, queryParameters);
			String whereClause = ServerTools.getWhereClause(domainClass, filters, null, searchString, onlyScanStringRepresentation, session, onlyRelated, reqRelDefs);
			String join = "";
			boolean distinct = false;
			String orderByClause = "";
			String orderByJoin = "";

			if (onlyRelated) {
				for(RelationDefinition rd : reqRelDefs) {
					if(rd != null && rd.relationName() != RelationDefinition.noRelationRequired) {
						distinct = distinct || rd.distinct();
						join += " " + rd.joins();
					}
				}
//				join = domainClass.getRelatedJoin();
//				distinct = domainClass.getRelatedDistinct();
			}
			
			join = ServerTools.removeDuplicateJoins(join);

			AtomTools.log(Log.LOG_LEVEL_DEBUG, "defined general stuff", this);

			em = emFactory.makeObject();
			Query query = null;

			AtomTools.log(Log.LOG_LEVEL_DEBUG, "counting elements", this);
			if (countQuery != null && countQuery.length() > 0)
				query = em.createNativeQuery(countQuery);
			else {
				query = em.createQuery("SELECT Count(" + (distinct ? "distinct " : "") + "obj.objectID) from " + domainClass.getName() + " obj " + join + " "
						+ whereClause);
			}
			totalSize = (Long) query.getSingleResult();

			// query =
			// em.createQuery("SELECT Count(distinct obj.objectID) from at.ac.fhcampuswien.atom.shared.domain.PortalPerson obj LEFT OUTER JOIN obj.stellen stn LEFT OUTER JOIN stn.stelle str LEFT OUTER JOIN str.orgeinheit oe WHERE Per_ID = 15237 OR oe.OrE_ID IN (-1)");
			// totalSize = (Long) query.getSingleResult();

			if (totalSize > 0) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "fetching elements", this);
				Method getListQuery = specificClass.getMethod("getListQuery", queryParameterClasses);
				String listQuery = (String) getListQuery.invoke(null, queryParameters);

				if (listQuery != null && listQuery.length() > 0)
					query = em.createNativeQuery(listQuery);
				else {
					orderByClause = ServerTools.getOrderByClause(domainClass, sorters, null);
					orderByJoin = ServerTools.getOrderByJoins(domainClass, sorters);
					String orderBySelect = "";
					if (distinct && orderByClause.length() > 0) {
						orderBySelect = orderByClause.substring(9).replace(" DESC", "").replace(" ASC", "");
						orderByClause = orderByClause.replace(orderBySelect, "distorderby");
						orderBySelect = ", " + orderBySelect + " as distorderby ";
					}
					query = em.createQuery("SELECT " + (distinct ? "distinct " : "") + "obj " + orderBySelect + "from " + domainClass.getName() + " obj "
					// fetch all properties
							+ join + orderByJoin + " " + whereClause + " " + orderByClause);
					// + " fetch all properties");
				}

				query.setMaxResults(pageSize);
				query.setFirstResult(Math.max(0, Math.min(fromRow, totalSize.intValue() - pageSize)));

				List<?> queryResult = query.getResultList();

				if (queryResult != null) {
					for (Object obj : queryResult) {
						AtomTools.log(Log.LOG_LEVEL_INFO, obj.toString(), this);
						if (obj instanceof Object[]) {
							for (Object obj1 : ((Object[]) obj)) {
								if (obj1 instanceof DomainObject) {
									DomainObject dobj1 = (DomainObject) obj1;
									resultList.add(dobj1);
								}
							}
						} else if (obj instanceof DomainObject) {
							DomainObject dobj1 = (DomainObject) obj;
							resultList.add(dobj1);
						}
					}
				}
			}

			if (resultList.size() > 0)
				ServerTools.prepareDOListForClient(resultList, domainClass, em, session);

			AtomTools.log(Log.LOG_LEVEL_TRACE, "returning fetched list", this);
			return new DomainObjectList(domainClass, resultList, fromRow, pageSize, totalSize == null ? 0 : totalSize.intValue(), filters, sorters,
					searchString, onlyRelated);

		} catch (NoResultException noResultException) {
			AtomTools.log(Log.LOG_LEVEL_WARN, "AtomServiceImpl.getListOfDomainObject - javax.persistence.NoResultException", this);
			return new DomainObjectList(domainClass, new ArrayList<DomainObject>(), fromRow, pageSize, 0, filters, sorters, searchString, onlyRelated);
		} catch (Exception e) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "getListOfDomainObject exception happened:" + e.getMessage(), this);
			e.printStackTrace();
			throw new AtomException("getListOfDomainObject exception happened:" + e.getMessage() + ";" + AtomTools.getCustomStackTrace(e));
		} finally {
			if (em != null)
				em.close();
		}
	}

	private void updateAllStringRepresentations() {
		updateStringRepresentations(DomainAnalyzer.getDomainTree());
	}
	private void updateStringRepresentations(DomainClass dc) {
		if(dc.getSubClasses() != null && dc.getSubClasses().size()>0) {
			for(DomainClass sc : dc.getSubClasses()) {
				updateStringRepresentations(sc);
			}
		}
		else {
			updateStringRepresentationsOnce(dc.getName());
		}
	}
	
	private HashSet<String> updatedStringRepresentationClasses = new HashSet<String>();
	public void updateStringRepresentationsOnce(String dc) {
		if(updatedStringRepresentationClasses.contains(dc))
			return;
		else
			updatedStringRepresentationClasses.add(dc);
		
		
		EntityManager em = null;
		try {
			em = emFactory.makeObject();
			EntityTransaction tx = em.getTransaction();
//			tx.begin();

			Query query = em.createQuery("SELECT obj from " + dc + " obj");

			query.setMaxResults(Integer.MAX_VALUE);
			query.setFirstResult(0);

			List<?> queryResult = query.getResultList();
//			tx.commit(); tx.isActive()
			
			for(Object obj : queryResult) {
				if(obj instanceof DomainObject) {
					DomainObject dobj = (DomainObject) obj;
					AtomTools.log(Log.LOG_LEVEL_INFO, "updated stringRepresentation: " + dobj.getStringRepresentation(), this);
					tx.begin();
					em.merge(dobj);
					try {
						tx.commit();
					}
					catch(RollbackException e) {
						AtomTools.log(Log.LOG_LEVEL_ERROR, "could not commit StringRepresentation" , this);
						em.detach(dobj);
					}
				}
			}

			

		} catch (Throwable t) {
			if (t instanceof AtomException) {
				throw (AtomException) t;
			} else {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "ServerTools.updateStringRepresentationsOnce exception: " + t.getClass().getSimpleName() + " - " + t.getMessage(), this);
				AtomTools.logStackTrace(Log.LOG_LEVEL_ERROR, t, this);
			}
		} finally {
			if (em != null)
				em.close();
		}
	}

	public DomainObject saveDomainObject(HttpServletRequest request, DomainObject instance) {

		ClientSession session = auth.getSessionFromCookie(request, false);
		return saveDomainObject(session, instance);
	}

	public DomainObject saveDomainObject(ClientSession session, DomainObject domainObject) {

		DomainObject origBak = domainObject;
		DomainClass requestedClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());

		if (domainObject.getObjectID() == null) {
			AtomTools.checkPermissionMatch(AtomConfig.accessCreateNew, requestedClass.getAccessHandler().getAccessTypes(session, domainObject));
		} else {
			DomainObject dbVersion = getDomainObject(session, domainObject.getObjectID(), domainObject.getClass());
			if (dbVersion == null) {
				if (dbVersion instanceof FrameVisit || dbVersion instanceof ClipBoardEntry) {
					throw new AtomException(AtomTools.getMessages().save_deleted(String.valueOf(domainObject.getObjectID())));
				}
			} else {
				AtomTools.checkPermissionMatch(AtomConfig.accessReadWrite, requestedClass.getAccessHandler().getAccessTypes(session, dbVersion));

				if (dbVersion instanceof FeaturedObject) {
					FeaturedObject fromDB = (FeaturedObject) dbVersion;
					FeaturedObject toSave = (FeaturedObject) domainObject;
					if (fromDB.getLastModifiedDate() != null && fromDB.getLastModifiedDate().after(toSave.getLastModifiedDate())) {
						if (fromDB instanceof FrameVisit)
							return fromDB;
						else
							throw new AtomException(AtomTools.getMessages().outdated());
					}
					if (fromDB.getCreationUser() != null && toSave.getCreationUser() != null && !fromDB.getCreationUser().equals(toSave.getCreationUser())) {
						throw new AtomException(AtomTools.getMessages().illegalChange());
					}
				}
			}
		}

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			domainObject.prepareSave(session);
			ServerTools.validateDomainObject(domainObject);

			em = emFactory.makeObject();
			tx = em.getTransaction();
			tx.begin();

			ServerTools.replaceRelatedObjects(em, domainObject, requestedClass, false);
			domainObject = em.merge(domainObject);

			tx.commit();

		} catch (Throwable t) {
			if(tx != null && tx.isActive())
				tx.rollback();
			if (t instanceof AtomException) {
				throw (AtomException) t;
			} else {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "ServerTools.saveDomainobject exception: " + t.getClass().getSimpleName() + " - " + t.getMessage(), this);
				AtomTools.logStackTrace(Log.LOG_LEVEL_ERROR, t, this);
			}
		} finally {
			if (em != null)
				em.close();
		}

		if(domainObject != null && domainObject.getObjectID() != null)
			return getDomainObject(session, domainObject.getObjectID(), domainObject.getClass());
		else if(origBak.getObjectID() != null)
			return getDomainObject(session, origBak.getObjectID(), origBak.getClass());
		else
			return null;
	}

	public boolean deleteDomainObject(HttpServletRequest request, DomainObject domainObject) {

		if (domainObject == null)
			return false;

		ClientSession session = auth.getSessionFromCookie(request, false);
		return deleteDomainObject(session, domainObject);
	}

	public boolean deleteDomainObject(ClientSession session, DomainObject domainObject) {

		DomainClass requestedClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());

		if (domainObject.getObjectID() != null) {
			
			EntityManager em = null;
			try {
				em = emFactory.makeObject();
				
				DomainObject dbVersion = (DomainObject) em.find(domainObject.getClass(), domainObject.getObjectID());
				//getDomainObject(session, domainObject.getObjectID(), domainObject.getClass(), em);
				//DomainObject dbVersion = getDomainObject(session, domainObject.getObjectID(), domainObject.getClass());
				if (dbVersion == null) {
	
					// this will happen quite often, through the "on delete cascade"
					// options set on those two collections.
					if (domainObject instanceof FrameVisit || domainObject instanceof ClipBoardEntry)
						return true;
					else
						throw new AtomException(AtomTools.getMessages().save_deleted(String.valueOf(domainObject.getObjectID())));
				} else {
					AtomTools.checkPermissionMatch(AtomConfig.accessReadWrite, requestedClass.getAccessHandler().getAccessTypes(session, dbVersion));
	
					if (dbVersion instanceof FeaturedObject && !(dbVersion instanceof FrameVisit || dbVersion instanceof ClipBoardEntry)) {
						FeaturedObject fromDB = (FeaturedObject) dbVersion;
						FeaturedObject toSave = (FeaturedObject) domainObject;
						if (fromDB.getLastModifiedDate() != null && fromDB.getLastModifiedDate().after(toSave.getLastModifiedDate())) {
							throw new AtomException(AtomTools.getMessages().outdated());
						}
					}
				}
				
				EntityTransaction tx = em.getTransaction();
				tx.begin();
				em.remove(dbVersion);
				tx.commit();
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				em.close();
			}
		}

		return false;
	}

	public LinkedHashMap<String, String> getAttributeChoiceList(HttpServletRequest request, String nameOfClass, String nameOfAttribute) {

		ClientSession session = auth.getSessionFromCookie(request, false);
		return getAttributeChoiceList(session, nameOfClass, nameOfAttribute);
	}

	private LinkedHashMap<String, String> getAttributeChoiceList(ClientSession session, String nameOfClass, String nameOfAttribute) {

		DomainClass requestedClass = DomainAnalyzer.getDomainClass(nameOfClass);
		AtomTools.checkPermissionMatch(AtomConfig.accessLinkage, requestedClass.getAccessHandler().getAllAccessTypes(session));

		DomainClassAttribute attr = requestedClass.getAttributeNamed(nameOfAttribute);
		final String sql = attr.getListBoxSql();
		if (sql == null || sql.length() < 1) {

			AtomTools.log(Log.LOG_LEVEL_WARN,
					"you should not ask server for static ListBoxValues that the client can read out of the DomainTree metadata himself..", this);
			return attr.getListBoxMapped();
		} else {

			try {
				EntityManager em = emFactory.makeObject();
				EntityTransaction tx = em.getTransaction();
				tx.begin();

				final LinkedHashMap<String, String> returnValue = new LinkedHashMap<String, String>();

				((org.hibernate.jpa.internal.EntityManagerImpl) em).getSession().doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {

						java.sql.Statement stmt = connection.createStatement();
						stmt.execute(sql);
						java.sql.ResultSet rs = stmt.getResultSet();

						while (rs.next()) {
							if (rs.getMetaData().getColumnCount() == 1)
								returnValue.put(rs.getString(1), rs.getString(1));
							else if (rs.getMetaData().getColumnCount() == 2)
								returnValue.put(rs.getString(1), rs.getString(2));
							else
								throw new AtomException("ListBoxSql selects an invalid number of columns! --> " + sql);
						}
					}
				});
				return returnValue;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public DomainObjectSearchResult searchDomainObjects(HttpServletRequest request, String searchString, int pageSize, boolean onlyRelated, boolean onlyScanStringRepresentation, String onlyScanClassWithName)
			throws AtomException {

		ClientSession session = auth.getSessionFromCookie(request, false);
		DomainClass domainClass = DomainAnalyzer.getUsersDomainTree(session);
		DomainObjectSearchResult result = new DomainObjectSearchResult(searchString, onlyRelated, onlyScanStringRepresentation, onlyScanClassWithName);
		if(onlyScanClassWithName != null && onlyScanClassWithName.length() > 0 && !onlyScanClassWithName.equals(" ")) {
			domainClass = domainClass.getDomainClassNamed(onlyScanClassWithName);
		}
		processClassSearch(result, domainClass, 0, pageSize, session);
		return result;
	}

	private void processClassSearch(DomainObjectSearchResult result, DomainClass domainClass, int fromRow, int pageSize,
			ClientSession session) {

		if (domainClass.isSearchable()) { // info: permission gets checked by the getList method // && AtomTools.isAccessAllowed(AtomConfig.accessLinkage, access)) {
			DomainObjectList subResult = getListOfDomainObject(domainClass, fromRow, pageSize, null, null, result.getSearchTerm(), result.isOnlyScanStringRepresentation(), session, result.isOnlyRelated());
			if (subResult != null & subResult.getTotalSize() > 0) {
				result.addList(subResult);
			}
		}
		for (DomainClass subClass : domainClass.getSubClasses()) {
			processClassSearch(result, subClass, fromRow, pageSize, session);
		}
	}
}