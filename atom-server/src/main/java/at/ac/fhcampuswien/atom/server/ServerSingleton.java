/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
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
import at.ac.fhcampuswien.atom.shared.FileAttributeRepresentation;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.FeaturedObject;
import at.ac.fhcampuswien.atom.shared.domain.FrameVisit;
import at.ac.fhcampuswien.atom.shared.domain.PersistentString;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;
import net.lightoze.gwt.i18n.server.LocaleProxy;

public class ServerSingleton {

	private static ServerSingleton singelton = null;

	public static ServerSingleton getInstance() {
		if (singelton == null)
			singelton = new ServerSingleton();
		return singelton;
	}
	
	private Authenticator auth = null;
	
	public Authenticator getAuth() {
		return auth;
	}

	private ServerSingleton() {
		LocaleProxy.initialize();
		
		auth = new Authenticator();
		
		if(AtomConfig.updateAllStringRepresentationsOnApplicationStartup)
			updateAllStringRepresentations();
		
		//updateStringRepresentationsOnce("at.ac.fhcampuswien.atom.shared.domain.Publikation");
		
		AtomTools.log(Level.INFO, "Atom ServerSingleton Startup Finished - " + AtomTools.getMessages().willkommen_in_app(), this);
		
//		updateStringRepresentationsOnce("at.ac.fhcampuswien.atom.shared.domain.DomainObject");
//		updateStringRepresentationsOnce("at.ac.fhcampuswien.atom.shared.domain.PortalPerson");
	}
	
	protected String getDatabaseInfo(HttpServletRequest request) {
		//auth.getSessionFromCookie(request, false); //giving out this info without authentication?
		return AtomEMFactory.getConnectionInfo();
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
			AtomTools.log(Level.WARNING, "Selecting DomainObject without any class info - performance problem!!", this);
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
				AtomTools.log(Level.INFO, "doesn't look like this was an objectID (couldn't parse as double; " + clue, this);
			}
			if (linkedID != null) {
				// find instance by id
				DomainObject obj = getDomainObject(session, linkedID.intValue(), classOfObject);
				if (obj != null) {
					return obj;
				}
			}

			//don't do that! this takes forever!
			//updateStringRepresentationsOnce(classOfObject.getName());

			// find instance by string representation
			ArrayList<DataFilter> filters = new ArrayList<DataFilter>(1);
			filters.add(new DataFilter("stringRepresentation", clue, "contains", "text"));
			DomainObjectList list = getListOfDomainObjects(session, classOfObject.getName(), 0, 20, filters, null, null, false, false);
			if (list.getTotalSize() == 0) {
				AtomTools.log(Level.WARNING, "could not find instance of \"" + classOfObject.getName() + "\" with stringRepresentation like %" + clue
						+ "%", this);
			} else if (list.getTotalSize() == 1) {
				return list.getDomainObjects().get(0);
			} else {
				AtomTools.log(Level.WARNING, "there is more than one instance of \"" + classOfObject.getName() + "\" with stringRepresentation like %"
						+ clue + "%", this);
				DomainObject match = null;
				for (DomainObject d : list.getDomainObjects()) {
					if (d.getStringRepresentation().equals(clue)) {
						if (match == null) {
							match = d;
						} else {
							AtomTools.log(Level.WARNING, "there is more than one instance of \"" + classOfObject.getName()
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
			AtomTools.log(Level.WARNING, "Selecting DomainObject without any class info - performance problem!!", this);
			Thread.dumpStack();
		}
		AtomTools.log(Level.FINEST, "ServerSingelton.getDomainObject - getting entity from entityManager now", this);
		DomainObject result = (DomainObject) em.find(classOfObject, id);
		AtomTools.log(Level.FINEST, "ServerSingelton.getDomainObject - got entity, preparing for client", this);
		ServerTools.fillAndDetachInstance(result, em, session, false);
		AtomTools.log(Level.FINEST, "ServerSingelton.getDomainObject - prepared for client, returning", this);
		return result;
	}

	public DomainObject getDomainObject(ClientSession session, int id, Class<?> classOfObject) {

		EntityManager em = null;
		try {
			AtomTools.log(Level.FINER, "ServerSingelton.getDomainObject - creating entityManager for query", this);
			em = AtomEMFactory.getEntityManager();
			return getDomainObject(session, id, classOfObject, em);
		} catch (Exception e) {
			ServerTools.log(Level.SEVERE, "ServerSingelton.getDomainObject - Exception happened! ", this, e);
		} finally {
			ServerTools.closeDBConnection(null, em);
			AtomTools.log(Level.FINER, "ServerSingelton.getDomainObject finally - closed entityManager", this);
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
			AtomTools.log(Level.WARNING, "DomainClass with name \"" + nameOfClass + "\" not found! will use DomainObject as class", this);
			requestedClass = DomainAnalyzer.getDomainTree();
		}
		return getListOfDomainObject(requestedClass, fromRow, pageSize, filters, sorters, searchString, onlyScanStringRepresentation, session, onlyRelated);
	}

	private DomainObjectList getListOfDomainObject(DomainClass domainClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, ClientSession session, boolean onlyRelated) {

		AtomTools.log(Level.FINER, "ServerSingelton.getListOfDomainObject start - user " + session.getUsername() + " requesting list of " + domainClass.getName(), this);
		
		Set<String> requiredRelations = AtomTools.getRequiredRelations(AtomConfig.accessLinkage, domainClass.getAccessHandler().getAccess(session));
		Collection<RelationDefinition> reqRelDefs = null;
		
		if(requiredRelations.contains(RelationDefinition.noRelationRequired)) {
			if(onlyRelated) {
				//user wishes too see related instances, but he has rights to see anything. use any and all relations that we know of.
				reqRelDefs = domainClass.getRelationDefinitions().values();
			}
		}
		else {
			//user does not have the permission to see unrelated, but only those related with one of the listed relations: 
			reqRelDefs = domainClass.getRelationDefinitions(requiredRelations);
			onlyRelated = true;
			
			if(reqRelDefs.size() <= 0) {
				throw new AuthenticationException("ServerSingelton.getListOfDomainObject - no relation definitions found and user has no unrelated-access allowance!");
			}
		}
		
		//AtomTools.checkPermissionMatch(AtomConfig.accessLinkage, domainClass.getAccessHandler().getAllAccessTypes(session));

		List<DomainObject> resultList = new ArrayList<DomainObject>();
		Long totalSize = null;
		EntityManager em = null;
		Query query = null;

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

			AtomTools.log(Level.FINE, "ServerSingelton.getListOfDomainObject - defined general stuff (built HQL query segments) - getting EntityManager now", this);

			em = AtomEMFactory.getEntityManager();

			AtomTools.log(Level.FINE, "ServerSingelton.getListOfDomainObject - got my EntityManger, selecting count", this);
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
				AtomTools.log(Level.FINE, "ServerSingelton.getListOfDomainObject - got count bigger 0, fetching elements now", this);
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
				
				// WARNING: the Microsoft JDBC implementation seems to deter from the javax.persistence interface definition:
				// parameter startPosition seems to start with 1 (and 0 is considered equal to it) and only 2 or higher will change the results startPosition
				// therefore we added 1+ here
				if(fromRow > 0)
					query.setFirstResult(fromRow+1);
				//query.setFirstResult(Math.max(0, Math.min(fromRow, totalSize.intValue() - pageSize +1)));

				List<?> queryResult = query.getResultList();
				AtomTools.log(Level.FINE, "ServerSingelton.getListOfDomainObject - got resultList, processing & preparing for client", this);

				if (queryResult != null) {
					for (Object obj : queryResult) {
						AtomTools.log(Level.FINE, "getListOfDomainObject processing object: " + obj.toString(), this);
						if (obj instanceof Object[]) {
							AtomTools.log(Level.FINE, "getListOfDomainObject object is an array! " + obj.toString(), this);
							for (Object obj1 : ((Object[]) obj)) {
								AtomTools.log(Level.FINE, "getListOfDomainObject array contained object: " + obj1.toString(), this);
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
				totalSize -= ServerTools.prepareDOListForClient(resultList, domainClass, em, session);

			AtomTools.log(Level.FINER, "ServerSingelton.getListOfDomainObject - returning list to client now", this);
			return new DomainObjectList(domainClass, resultList, fromRow, pageSize, totalSize == null ? 0 : totalSize.intValue(), filters, sorters,
					searchString, onlyRelated);

		} catch (NoResultException noResultException) {
			ServerTools.log(Level.WARNING, "ServerSingelton.getListOfDomainObject - javax.persistence.NoResultException", this, noResultException);
			return new DomainObjectList(domainClass, new ArrayList<DomainObject>(), fromRow, pageSize, 0, filters, sorters, searchString, onlyRelated);
		} catch (Throwable t) {
			String queryString = "n.a.";
			if(query != null && query instanceof org.hibernate.query.internal.QueryImpl)
				queryString = "\n" + ((org.hibernate.query.internal.QueryImpl<?>) query).getQueryString();
			ServerTools.log(Level.SEVERE, "getListOfDomainObject error for query: " + queryString, this, t);
			AtomEMFactory.closeDBConnection();
			throw new AtomException("ServerSingelton.getListOfDomainObject unexpected Throwable happened, see cause", t);
		} finally {
			if (em != null) {
				AtomTools.log(Level.FINER, "ServerSingelton.getListOfDomainObject - closing entitymanager in finally block", this);
				ServerTools.closeDBConnection(null, em);
			}	
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
		EntityTransaction tx = null;
		try {
			em = AtomEMFactory.getEntityManager();
			tx = em.getTransaction();
//			tx.begin();

			Query query = em.createQuery("SELECT obj from " + dc + " obj");

			query.setMaxResults(Integer.MAX_VALUE);
			query.setFirstResult(0);

			List<?> queryResult = query.getResultList();
//			tx.commit(); tx.isActive()
			
			for(Object obj : queryResult) {
				if(obj instanceof DomainObject) {
					DomainObject dobj = (DomainObject) obj;
					AtomTools.log(Level.INFO, "updated stringRepresentation: " + dobj.getStringRepresentation(), this);
					tx.begin();
					em.merge(dobj);
					try {
						tx.commit();
					}
					catch(RollbackException e) {
						AtomTools.log(Level.SEVERE, "could not commit StringRepresentation" , this);
						em.detach(dobj);
					}
				}
			}

			

		} catch (Throwable t) {
			if (t instanceof AtomException) {
				throw (AtomException) t;
			} else {
				ServerTools.log(Level.SEVERE, "ServerTools.updateStringRepresentationsOnce exception: " + t.getClass().getSimpleName() + " - " + t.getMessage(), this, t);
			}
		} finally {
			ServerTools.closeDBConnection(tx, em);
		}
	}

	public DomainObject saveDomainObject(HttpServletRequest request, DomainObject instance) {

		ClientSession session = auth.getSessionFromCookie(request, false);
		return saveDomainObject(session, instance);
	}

	public DomainObject saveDomainObject(ClientSession session, DomainObject domainObject) {

		DomainObject origBak = domainObject;
		EntityManager em = null;
		EntityTransaction tx = null;
		
		try {
			em = AtomEMFactory.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			domainObject = saveDomainObject(session, domainObject, em);
			tx.commit();
		}
		catch(Throwable t) {
			String innerMostMessage = AtomTools.getInnerMostCause(t).getMessage();
			ServerTools.log(Level.SEVERE, "ServerSingleton.saveDomainObject Exception: " + innerMostMessage, this, t);
			if(t instanceof org.hibernate.exception.LockAcquisitionException) {
				// Don't care, seems to happen frequently even on first queries...
			}
			else {
				AtomEMFactory.closeDBConnection();
			}
		}
		finally {
			ServerTools.closeDBConnection(tx, em);
		}

		if(domainObject != null && domainObject.getObjectID() != null)
			return getDomainObject(session, domainObject.getObjectID(), domainObject.getClass());
		else if(origBak.getObjectID() != null)
			return getDomainObject(session, origBak.getObjectID(), origBak.getClass());
		else
			return null;
	}

	public DomainObject saveDomainObject(ClientSession session, DomainObject domainObject, EntityManager em) {
	
		try {			
			DomainClass requestedClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());
			DomainObject dbVersion = null;
	
			if (domainObject.getObjectID() == null) {
				AtomTools.checkPermissionMatch(AtomConfig.accessCreateNew, requestedClass.getAccessHandler().getAccessTypes(session, domainObject));
			} else {
				dbVersion = (DomainObject) em.find(domainObject.getClass(), domainObject.getObjectID());
				if (dbVersion == null) {
					if (domainObject instanceof FrameVisit || domainObject instanceof ClipBoardEntry) {
						throw new AtomException(AtomTools.getMessages().save_deleted(String.valueOf(domainObject.getObjectID())));
					}
				} else {
					AtomTools.checkPermissionMatch(AtomConfig.accessReadWrite, requestedClass.getAccessHandler().getAccessTypes(session, dbVersion));
	
					if (dbVersion instanceof FeaturedObject) {
						FeaturedObject fromDB = (FeaturedObject) dbVersion;
						FeaturedObject toSave = (FeaturedObject) domainObject;
						if (fromDB.getLastModifiedDate() != null && fromDB.getLastModifiedDate().after(toSave.getLastModifiedDate())) {
							// a newer change has been saved already!
							if (fromDB instanceof FrameVisit)
								return fromDB; //simply don't save it but present the newer one as the result of the save operation
							else
								throw new AtomException(AtomTools.getMessages().outdated());
						}
						if (fromDB.getCreationUser() != null && toSave.getCreationUser() != null && !fromDB.getCreationUser().equals(toSave.getCreationUser())) {
							throw new AtomException(AtomTools.getMessages().illegalChange());
						}
					}
					if(domainObject instanceof FrameVisit) {
						FrameVisit frameVisit = (FrameVisit) domainObject;
						FrameVisit frameVisitDB = (FrameVisit) dbVersion;
						if(frameVisit.getRepresentedInstance() != null)
							// don't save an object with it's visit, only when user command's to save not on visit!
							frameVisit.getRepresentedInstance().setFrameVisits(frameVisitDB.getFrameVisits());
					}
					domainObject.setFrameVisits(dbVersion.getFrameVisits()); // FrameVisits are only saved on their own, not with their represented objects (which would only contain visits of the saving user)
				}
			}
		
			domainObject.prepareSave(session);
			ServerTools.validateDomainObject(domainObject);

			ServerTools.handleRelatedObjects(em, domainObject, requestedClass, false, session, dbVersion);
			handleFileAttributesForSaveAction(em, domainObject, requestedClass);
			domainObject = em.merge(domainObject);
			return domainObject;


		} catch (AtomException ae) {
			throw ae;
		} catch (Throwable t) {
			String innerMostMessage = AtomTools.getInnerMostCause(t).getMessage();
			ServerTools.log(Level.SEVERE, "ServerTools.saveDomainobject exception: " + innerMostMessage, this, t);
			AtomEMFactory.closeDBConnection();
			throw new AtomException(innerMostMessage, t);
		}
	}

	
	private void handleFileAttributesForSaveAction(EntityManager em, DomainObject domainObject, DomainClass requestedClass) {
		if(domainObject == null)
			return;
		Collection<DomainClassAttribute> fas = requestedClass.getAllFileAttributes();
		if(fas != null && fas.size() > 0) {
			Class<?> rc = domainObject.getClass();
			for(DomainClassAttribute a : fas) {
				try {
					Method getAttribute = rc.getMethod("get" + AtomTools.upperFirstChar(a.getName()), new Class[] {});
					String val = (String) getAttribute.invoke(domainObject, new Object[] {});
					
					if(val != null && val.length() > 0) {
						FileAttributeRepresentation far = new FileAttributeRepresentation(val);
						PersistedFileAttribute pfa = em.find(PersistedFileAttribute.class, far.getFileID());
						
						if(!pfa.getForClassName().equals(requestedClass.getName()) || 
								!a.getName().equals(pfa.getForAttributeName()) || 
								(pfa.getForInstance() != null && !domainObject.equals(pfa.getForInstance()))
						  )
							throw new ValidationError("user is trying to link a file from a different attribute, denying - potential permission problem");
						
						if(!pfa.isForInstanceSaved() || !domainObject.equals(pfa.getForInstance())) {
							pfa.setForInstanceSaved(true);
							pfa.setForInstance(domainObject);
							// 2018-08-28 will be merged automatically as part of the owning DomainObject instance, seperate merge makes hibernate throw an exception and save nothing.
//							em.merge(pfa);
						}
					}
					
				} catch(NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public PersistedFileAttribute getFileAttribute(HttpServletRequest request, Integer id) {
		ClientSession cs = auth.getSessionFromCookie(request, false);
		
		EntityManager em = null;
		try {
			em = AtomEMFactory.getEntityManager();
			PersistedFileAttribute pfa = em.find(PersistedFileAttribute.class, id);
			
			if(pfa == null)
				return null;
			
			DomainObject fi = pfa.getForInstance();
			if(fi == null) {
				if(pfa.getUploader() != null && pfa.getUploader().equals(cs.getUser())) {
					AtomTools.log(Level.INFO, "user downloading file that he has uploaded himself - allowing although it has not yet been saved into an object instance", this);
					return pfa;
				}
				else
					throw new AuthenticationException("PersistedFileAttribute does not have a instance that it's for yet. Can't determine permissions, assuming no permission.");
			}
				
			
			DomainClass dc = DomainAnalyzer.getDomainClass(fi.getConcreteClass());
			Set<String> accessTypes = dc.getAccessHandler().getAccessTypes(cs, fi);
			
			if(!(accessTypes.contains(AtomConfig.accessReadOnly) || accessTypes.contains(AtomConfig.accessReadWrite)))
				throw new AuthenticationException("User does not have full ReadOnly || ReadWrite access to the instance of this file. Attribute Permissions not implemented.");
			
			return pfa;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ServerTools.closeDBConnection(null, em);
		}
		return null;
	}
	
	public PersistedFileAttribute saveFileAttribute(ClientSession session, FileItem fileItem, String forClassName, String forAttributeName, Integer forInstanceID) {
		
		PersistedFileAttribute pfa = null;
		EntityManager em = null;
		EntityTransaction tx = null;
		
		try {
			em = AtomEMFactory.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			DomainObject forInstance = null;
			if(forInstanceID != null) {
				//forInstance = getDomainObject(session, forInstanceID, forClassName);
				forInstance = (DomainObject) em.find(ServerTools.getClassForName(forClassName), forInstanceID);
			}

			Session hibernateSession = em.unwrap(Session.class);
			LobCreator lobCreator = Hibernate.getLobCreator(hibernateSession);
			Blob blob = lobCreator.createBlob(fileItem.getInputStream(), fileItem.getSize());
			
			pfa = new PersistedFileAttribute(blob, fileItem.getName(), fileItem.getContentType(), forClassName, forAttributeName, forInstance, session.getUser());
			pfa = em.merge(pfa);

			tx.commit();

		} catch (Throwable t) {
			if(tx != null && tx.isActive())
				tx.rollback();
			if (t instanceof AtomException) {
				throw (AtomException) t;
			} else {
				ServerTools.log(Level.SEVERE, "ServerTools.saveFileAttribute exception: " + t.getClass().getSimpleName() + " - " + t.getMessage(), this, t);
			}
		} finally {
			ServerTools.closeDBConnection(tx, em);
		}
		
		return pfa;
	}

	public boolean deleteDomainObject(HttpServletRequest request, DomainObject domainObject) {

		if (domainObject == null)
			return false;

		ClientSession session = auth.getSessionFromCookie(request, false);
		return deleteDomainObject(session, domainObject);
	}

	protected boolean deleteDomainObject(ClientSession session, DomainObject domainObject) {
		EntityManager em = AtomEMFactory.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		try {
			em = AtomEMFactory.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			boolean val = deleteDomainObject(session, domainObject, em);
			tx.commit();
			return val;
		}
		catch(Throwable t) {
			ServerTools.log(Level.SEVERE, "Exception during delete!", this, t);
		}
		finally {
			ServerTools.closeDBConnection(tx, em);
		}
		return false;
	}

	protected boolean deleteDomainObject(ClientSession session, DomainObject domainObject, EntityManager em) {

		DomainClass requestedClass = DomainAnalyzer.getDomainClass(domainObject.getConcreteClass());

		if (domainObject.getObjectID() != null) {
			
			AtomTools.checkPermissionMatch(AtomConfig.accessDelete, requestedClass.getAccessHandler().getAccessTypes(session, domainObject));
							
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
			em.remove(dbVersion);
			return true;
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
		String hql;
		boolean useHql = attr.getListBoxUseHql();
		
		if (attr.getListBoxAnyExistingValue()) {
			hql = "SELECT DISTINCT " + attr.getName() + " FROM " + requestedClass.getTableName();
			useHql = true;
//			String where = attr.getWhere();
//			if(where != null)
//				sql += " WHERE " + where;
		}
		else {
			hql = attr.getListBoxSql();
		}
		
		if (hql == null || hql.length() < 1) {

			AtomTools.log(Level.WARNING,
					"you should not ask server for static ListBoxValues that the client can read out of the DomainTree metadata himself..", this);
			return attr.getListBoxMapped();
		} else {
			
			EntityManager em = null;
			EntityTransaction tx = null;
			final LinkedHashMap<String, String> returnValue = new LinkedHashMap<String, String>();

			try {
				em = AtomEMFactory.getEntityManager();
				tx = em.getTransaction();
				tx.begin();
				
				if(useHql) {
					Query query = em.createQuery(hql);
					for (Object o : query.getResultList()) {
						if(o instanceof String) {
							String s = (String)o;
							returnValue.put(s,s);
						}
						else if(o instanceof PersistentString) {
							PersistentString s = (PersistentString)o;
							returnValue.put(s.getValue(), s.getValue());
						}
						else if(o == null) {
							// don't care.
						}
						else {
							AtomTools.log(Level.SEVERE, "need to implement handling " + o.getClass().toString() + " -> " + o.toString(), this);
						}
					}
				}
				else {
					((Session) em.unwrap(Session.class)).doWork(new Work() {
						@Override
						public void execute(Connection connection) throws SQLException {
	
							java.sql.Statement stmt = connection.createStatement();
							stmt.execute(hql);
							java.sql.ResultSet rs = stmt.getResultSet();
	
							while (rs.next()) {
								if (rs.getMetaData().getColumnCount() == 1)
									returnValue.put(rs.getString(1), rs.getString(1));
								else if (rs.getMetaData().getColumnCount() == 2)
									returnValue.put(rs.getString(1), rs.getString(2));
								else
									throw new AtomException("ListBoxSql selects an invalid number of columns! --> " + hql);
							}
						}
					});
				}
				
				return returnValue;

			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				ServerTools.closeDBConnection(tx, em);
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
			try {
				DomainObjectList subResult = getListOfDomainObject(domainClass, fromRow, pageSize, null, null, result.getSearchTerm(), result.isOnlyScanStringRepresentation(), session, result.isOnlyRelated());
				if (subResult != null & subResult.getTotalSize() > 0) {
					result.addList(subResult);
				}
			}
			catch(AuthenticationException e) {
				ServerTools.log(Level.INFO, "User has no permissions to search Class: " + domainClass.getName(), this, e);
			}
		}
		for (DomainClass subClass : domainClass.getSubClasses()) {
			processClassSearch(result, subClass, fromRow, pageSize, session);
		}
	}
}
