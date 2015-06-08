/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.DomainObjectSearchResult;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;
import at.ac.fhcampuswien.atom.shared.rpc.AtomService;
import at.ac.fhcampuswien.atom.shared.rpc.AtomServiceAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RPCCaller {

	private static RPCCaller singlton = null;

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final AtomServiceAsync atomRPCAsync = GWT.create(AtomService.class);

	private ClientSession clientSession = null;
	private DomainClass domainTree = null;

	private Map<DomainClass, Set<DomainObjectList>> loadedLists = null;
	private Map<Integer, DomainObject> loadedObjects = null;
	private Map<String, LinkedHashMap<String, String>> loadedListBoxChoices = null;

	private Map<String, Date> classChanges = null;


	public static RPCCaller getSinglton() {
		if (singlton == null)
			singlton = new RPCCaller();
		return singlton;
	}

	private RPCCaller() {
	}
	
	private void clearEverything() {
		getLoadedLists().clear();
		getLoadedListBoxChoices().clear();
		getLoadedObjects().clear();
		// domainTree = null;
	}

	private Map<String, Date> getClassChanges() {
		if (classChanges == null) {
			classChanges = new HashMap<String, Date>();
		}
		return classChanges;
	}
	
	public void getServerInfo(AsyncCallback<String> callback) {
		atomRPCAsync.getServerInfo(callback);
	}

	public Date getClassChangeDate(String className) {
		return getClassChanges().get(className);
	}

	private Map<DomainClass, Set<DomainObjectList>> getLoadedLists() {
		if (loadedLists == null) {
			loadedLists = new HashMap<DomainClass, Set<DomainObjectList>>();
		}
		return loadedLists;
	}

	private Set<DomainObjectList> getLoadedLists(DomainClass forClass) {
		if (getLoadedLists().get(forClass) == null)
			getLoadedLists().put(forClass, new HashSet<DomainObjectList>());
		return getLoadedLists().get(forClass);
	}

	private Map<String, LinkedHashMap<String, String>> getLoadedListBoxChoices() {
		if (loadedListBoxChoices == null)
			loadedListBoxChoices = new HashMap<String, LinkedHashMap<String, String>>();
		return loadedListBoxChoices;
	}

	public DomainObject getLoadedObject(Integer id) {
		return getLoadedObjects().get(id);
	}

	private Map<Integer, DomainObject> getLoadedObjects() {
		if (loadedObjects == null)
			loadedObjects = new HashMap<Integer, DomainObject>();
		return loadedObjects;
	}

	public void checkExistingSession(String campusAuthCookie, final Notifiable<String> objectToNotify) {
		atomRPCAsync.getSessionForCookie(campusAuthCookie, new AsyncCallback<ClientSession>() {
			public void onFailure(Throwable caught) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "session invalid: " + caught.getMessage(), this);
				objectToNotify.doNotify("session invalid: " + caught.getMessage());
			}

			public void onSuccess(ClientSession result) {
				clientSession = result;
				AtomTools.log(Log.LOG_LEVEL_INFO, "session valid;" + result.getUserFirstName() + " " + result.getUserLastName(), this);
				objectToNotify.doNotify("session valid;" + result.getUserFirstName() + " " + result.getUserLastName());
			}
		});
	}

	public void loginUser(String username, String password, final Notifiable<String> objectToNotify) {
		atomRPCAsync.getNewSession(username, password, new AsyncCallback<ClientSession>() {
			public void onFailure(Throwable caught) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "could not login user", this, caught);
				String failed = "Vom Server gemeldeter Fehler: ";
				if (caught.getMessage() != null)
					failed += caught.getMessage();
				else
					failed += caught.toString();
				caught.printStackTrace();
				objectToNotify.doNotify(failed);
			}

			public void onSuccess(ClientSession result) {
				clientSession = result;
				objectToNotify.doNotify("login successful;" + result.getUserFirstName() + " " + result.getUserLastName());
			}
		});
	}

	public void logout() {
		atomRPCAsync.logoutActiveUser(clientSession.getCookieValue(), new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "logout successful; result: " + result != null ? result.toString() : "", this);
				RPCCaller.this.clearEverything();
				Window.Location.reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "logout failure; caught: " + caught.getMessage(), this);
			}
		});
	}

	public ClientSession getClientSession() {
		return clientSession;
	}

	public boolean isUserAdmin() {
		if (clientSession != null) {
			for (String role : clientSession.getRoles().values()) {
				if ("Administrator".equals(role) || "AtomAdmin".equals(role))
					return true;
			}
		}
		return false;
	}

	private boolean domainTreeCallInProgress;
	private HashSet<WaitingFor<DomainClass>> waitingForDomainTree = new HashSet<WaitingFor<DomainClass>>();

	public void getDomainTree(WaitingFor<DomainClass> waiting) {
		if (domainTree == null) {
			// if the domainTree is not on the client, we have to call the
			// notifiable later
			if (waiting != null) {
				waitingForDomainTree.add(waiting);
			}
			// if and only if there is no domainTreeCallInProgress we initiate
			// one
			if (!domainTreeCallInProgress) {
				domainTreeCallInProgress = true;
				try {
					atomRPCAsync.getDomainTree(clientSession.getCookieValue(), new AsyncCallback<DomainClass>() {
						public void onFailure(Throwable caught) {
							if (caught instanceof AuthenticationException)
								App.actionLogout();
							else {
								AtomTools.log(Log.LOG_LEVEL_FATAL, "getDomainTree failed @Server. " + caught, this);
							}
							for (WaitingFor<DomainClass> waiting : waitingForDomainTree) {
								waiting.requestFailed(caught.getMessage());
							}
							domainTreeCallInProgress = false;
						}

						public void onSuccess(DomainClass result) {
							domainTree = result;
							for (WaitingFor<DomainClass> waiting : waitingForDomainTree) {
								waiting.recieve(result);
							}
							domainTreeCallInProgress = false;
						}
					});
				} catch (Throwable t) {
					AtomTools.log(Log.LOG_LEVEL_FATAL, "getDomainTree failed @Client. " + t, waiting);
				}
			}
		} else {
			if (waiting != null)
				waiting.recieve(domainTree);
		}
	}

	public DomainClass getLoadedDomainTree() {
		getDomainTree(null);
		return domainTree;
	}

	public void loadListBoxChoices(final DomainClass domainClass, final String attributeName,
			final WaitingFor<LinkedHashMap<String, String>> resultHandler) {
		LinkedHashMap<String, String> cached = getLoadedListBoxChoices().get(domainClass.getName() + "." + attributeName);
		if (cached != null) {
			resultHandler.recieve(cached);
			return;
		}
		atomRPCAsync.getAttributeChoiceList(getClientSession().getCookieValue(), domainClass.getName(), attributeName,
				new AsyncCallback<LinkedHashMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {

						if (caught instanceof AuthenticationException)
							App.actionLogout();
						else
							AtomTools.log(Log.LOG_LEVEL_ERROR, "getAttributeChoiceList failed on server (" + domainClass.getName()
									+ "." + attributeName + "); exception: " + caught.getMessage(), this);
					}

					@Override
					public void onSuccess(LinkedHashMap<String, String> result) {
						AtomTools.log(Log.LOG_LEVEL_TRACE, "getAttributeChoiceList server returned result (" + domainClass.getName()
								+ "." + attributeName + ")", this);
						getLoadedListBoxChoices().put(domainClass.getName() + "." + attributeName, result);
						resultHandler.recieve(result);
					}
				});
		AtomTools.log(Log.LOG_LEVEL_TRACE, "getAttributeChoiceList server-call started (" + domainClass.getName() + "."
				+ attributeName + ")", this);
	}

	public void searchDomainObjects(String searchString, boolean onlyScanStringRepresentation, int pageSize, boolean onlyRelated, String onlyScanClassWithName,
			final AsyncCallback<DomainObjectSearchResult> outerCallback) {
		atomRPCAsync.searchDomainObjects(clientSession.getCookieValue(), searchString, pageSize, onlyRelated, onlyScanStringRepresentation, onlyScanClassWithName, 
				new AsyncCallback<DomainObjectSearchResult>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof AuthenticationException)
							App.actionLogout();
						else
							AtomTools.log(Log.LOG_LEVEL_FATAL, "searching DomainObjects failed: " + caught, this);

						outerCallback.onFailure(caught);
					}

					public void onSuccess(DomainObjectSearchResult result) {

						outerCallback.onSuccess(result);

						for (DomainObjectList subResult : result.getResult()) {
							List<DomainObject> objects = subResult.getDomainObjects();
							if (objects != null && objects.size() > 0) {
								for (DomainObject obj : objects) {
									getLoadedObjects().put(obj.getObjectID(), obj);
								}
								// DomainClass classOfList =
								// subResult.getDomainClass();
								// processLoadedList(objects, classOfList,
								// changedObjects);
							}
						}
						// RPCCaller.this.secondaryUpdate = secondaryUpdate;
						// RPCCaller.this.causeOfUpdate = outerCallback;
						// distributeChangedObjects(changedObjects, null);
						// RPCCaller.this.causeOfUpdate = null;
					}
				});
	}

	public void loadDomainObject(Integer objectID, String nameOfClass, final WaitingFor<DomainObject> waiting) {
		atomRPCAsync.getDomainObject(clientSession.getCookieValue(), objectID, nameOfClass, new AsyncCallback<DomainObject>() {

			public void onFailure(Throwable caught) {

				if (caught instanceof AuthenticationException)
					App.actionLogout();
				else
					AtomTools.log(Log.LOG_LEVEL_FATAL, "fetching DomainObject failed: " + caught, this);

				if (waiting != null)
					waiting.requestFailed(caught.getMessage());
			}

			public void onSuccess(DomainObject result) {
				// HashSet<DomainObject> set = new HashSet<DomainObject>();
				// set.add(result);
				// processLoadedDomainObject(null, result, new
				// HashSet<DomainObject>(), new HashSet<DomainObject>());
				// getLoadedObjects().put(result.getObjectID(), result);
				// distributeChangedObjects(set, null);

				if (result != null) {
					getLoadedObjects().put(result.getObjectID(), result);
				}
				if (waiting != null) {
					if (result != null)
						waiting.recieve(result);
					else
						waiting.requestFailed(AtomTools.getMessages().instance_doesnt_exist());
				}
			}
		});
	}

	public void loadListOfDomainObjects(final DomainClass classOfList, Collection<DataFilter> filters, ArrayList<DataSorter> sorters,
			int fromRow, int pageSize, final boolean secondaryUpdate, final String searchString, boolean onlyScanStringRepresentation, boolean onlyRelated,
			boolean forceRefresh, final AsyncCallback<DomainObjectList> callback) {

		AtomTools.log(Log.LOG_LEVEL_TRACE, "RPCCaller.loadListOfDomainObjects - method start", this);

		if (!forceRefresh)
			for (DomainObjectList list : getLoadedLists(classOfList)) {
				if (list.useable(classOfList.getName(), classOfList, fromRow, pageSize, filters, sorters, searchString, onlyRelated)) {
					AtomTools.log(Log.LOG_LEVEL_TRACE, "RPCCaller.loadListOfDomainObjects - using old list for new request (=end)",
							this);
					callback.onSuccess(list);
					return;
				}
			}

		ArrayList<DataFilter> newFilters = filters == null ? null : new ArrayList<DataFilter>(filters);
		ArrayList<DataSorter> newSorters = sorters == null ? null : new ArrayList<DataSorter>(sorters);

		atomRPCAsync.getListOfDomainObject(clientSession.getCookieValue(), classOfList.getName(), fromRow, pageSize, newFilters,
				newSorters, searchString, onlyScanStringRepresentation, onlyRelated, new AsyncCallback<DomainObjectList>() {
					public void onFailure(Throwable caught) {

						if (caught instanceof AuthenticationException)
							App.actionLogout();
						else
							AtomTools.log(Log.LOG_LEVEL_ERROR,
									"RPCCaller.loadListOfDomainObjects - fetching DomainObjectList failed: " + caught, this);

						callback.onFailure(caught);
					}

					public void onSuccess(DomainObjectList result) {
						AtomTools.log(Log.LOG_LEVEL_TRACE, "RPCCaller.loadListOfDomainObjects - server returned data", this);
						// HashSet<DomainObject> changedObjects = new
						// HashSet<DomainObject>();
						// processLoadedList(result.getDomainObjects(),
						// classOfList, changedObjects);
						getLoadedLists(classOfList).add(result);

						if (result != null && result.getDomainObjects() != null)
							for (DomainObject obj : result.getDomainObjects()) {
								getLoadedObjects().put(obj.getObjectID(), obj);
							}
						// RPCCaller.this.secondaryUpdate = secondaryUpdate;
						// RPCCaller.this.causeOfUpdate = callback;
						// if (searchString == null || "".equals(searchString))
						// distributeChangedObjects(changedObjects,
						// classOfList);
						// else
						// distributeChangedObjects(changedObjects, null);

						callback.onSuccess(result);
						// RPCCaller.this.causeOfUpdate = null;
						AtomTools.log(Log.LOG_LEVEL_TRACE, "RPCCaller.loadListOfDomainObjects - finished distributing data", this);
					}
				});

		AtomTools.log(Log.LOG_LEVEL_TRACE, "RPCCaller.loadListOfDomainObjects - method end", this);
	}

	public void deleteDomainObject(final DomainObject domainObject, final Notifiable<String> notifiable) {
		if (domainObject == null) {
			if(notifiable != null) {
				notifiable.doNotify("'null' can't be deleted, it's a constant ;)");
			}
		} else {
			atomRPCAsync.deleteDomainObject(clientSession.getCookieValue(), domainObject, new AsyncCallback<Boolean>() {
				public void onFailure(Throwable caught) {

					if (caught instanceof AuthenticationException)
						App.actionLogout();

					failed(caught);
				}

				public void onSuccess(Boolean result) {
					if (result) {

						getClassChanges().put(domainObject.getConcreteClass(), new Date());

						// HashSet<DomainObject> changedObjects = new
						// HashSet<DomainObject>();
						// processDomainObjectDeletion(domainObject,
						// changedObjects);
						// RPCCaller.this.secondaryUpdate = false;
						// distributeChangedObjects(changedObjects, null);
						getLoadedObjects().remove(domainObject.getObjectID());
						if (notifiable != null)
							notifiable.doNotify("deletion successful");
					} else {
						failed(null);
					}
				}

				private void failed(Throwable caught) {
					AtomTools.log(Log.LOG_LEVEL_ERROR, "DomainObject instance could not be deleted: '" + domainObject + " ("
							+ domainObject + ") error: '" + caught + "'", this);
					if (notifiable != null)
						notifiable.doNotify("deletion failed");
				}
			});
		}
	}

	public void saveDomainObject(final DomainObject domainObject, final WaitingFor<DomainObject> reciever) {
		if(domainObject.getObjectID() == null) {
			AtomTools.log(Log.LOG_LEVEL_DEBUG, "sending DomainObject without ID to the server --> creating new instance=row", this);
		}
		atomRPCAsync.saveDomainObject(clientSession.getCookieValue(), domainObject, new AsyncCallback<DomainObject>() {
			public void onFailure(Throwable caught) {

				// if (caught instanceof AuthenticationException)
				// AtomGUI.getSinglton().logout();
				//
				// else

				if (caught instanceof ValidationError) {
					// Produziertes GUI-Bild sieht unschön aus (save ist
					// quasi abgeschlossen, scheitern bisher nicht gut
					// verarbeitet), ist aber nicht ganz so schlimm,
					// da "brave" clients keine Instanzen die nicht korrekt
					// validieren an den Server zum speichern schicken, und den
					// Fehler schon vorher zeigen

					//TODO: implement a presentation for validation errors
					//AtomGUI.getSinglton().deliverError((ValidationError) caught);
				} else {
					AtomTools.logStackTrace(Log.LOG_LEVEL_ERROR, caught, this);
					AtomTools.log(Log.LOG_LEVEL_ERROR, "saveDomainObject failed on Server; " + caught.getMessage(), this);
				}
			}

			public void onSuccess(DomainObject result) {
				getClassChanges().put(domainObject.getConcreteClass(), new Date());
				
				if(result != null) {
					getLoadedObjects().put(result.getObjectID(), result);
					if (reciever != null)
						reciever.recieve(result);
				}	

				// HashSet<DomainObject> changedObjects = new
				// HashSet<DomainObject>();
				// changedObjects.add(result);
				// processLoadedDomainObject(null, result, new
				// HashSet<DomainObject>(), changedObjects);
				// RPCCaller.this.secondaryUpdate = false;
				// distributeChangedObjects(changedObjects, null);
			}
		});
	}

	// public void getDomainClassNamed(Notifiable notifiable) {
	// DomainClass localDomainTree = getDomainTree(new Notifiable() {
	//
	// @Override
	// public void doNotify(String notifyReason) {
	// // TO DO Auto-generated method stub
	//
	// }
	// });
	//
	// if(localDomainTree != null) {
	// notifiable.notify("DomainTree is available");
	// }
	// }

	// private HashMap<Long, HashSet<Notifiable>> domainObjectChangeSubscribers
	// = null;
	// private HashMap<DomainClass, HashSet<Notifiable>>
	// domainClassChangeSubscribers = null;

	// public void registerChangeListener(Long objectID, Notifiable notifiable)
	// {
	// if (domainObjectChangeSubscribers == null)
	// domainObjectChangeSubscribers = new HashMap<Long, HashSet<Notifiable>>();
	// if (domainObjectChangeSubscribers.get(objectID) == null)
	// domainObjectChangeSubscribers.put(objectID, new HashSet<Notifiable>());
	// domainObjectChangeSubscribers.get(objectID).add(notifiable);
	// }
	//
	// public boolean unregisterChangeListener(Long objectID, Notifiable
	// notifiable) {
	// if (domainObjectChangeSubscribers != null &&
	// domainObjectChangeSubscribers.get(objectID) != null)
	// return domainObjectChangeSubscribers.get(objectID).remove(notifiable);
	// else
	// return false;
	// }
	//
	// public void registerChangeListener(DomainClass domainClass, Notifiable
	// notifiable) {
	// if (domainClassChangeSubscribers == null)
	// domainClassChangeSubscribers = new HashMap<DomainClass,
	// HashSet<Notifiable>>();
	// if (domainClassChangeSubscribers.get(domainClass) == null)
	// domainClassChangeSubscribers.put(domainClass, new HashSet<Notifiable>());
	// domainClassChangeSubscribers.get(domainClass).add(notifiable);
	// }
	//
	// public boolean unregisterChangeListener(DomainClass domainClass,
	// Notifiable notifiable) {
	// if (domainClassChangeSubscribers != null &&
	// domainClassChangeSubscribers.get(domainClass) != null)
	// return domainClassChangeSubscribers.get(domainClass).remove(notifiable);
	// else
	// return false;
	// }

	// public Set<DomainObject> getLoadedList(DomainClass classOfList) {
	// Set<DomainObject> returnSet = new HashSet<DomainObject>();
	// for (DomainObject domainObject : getLoadedObjects().values()) {
	// if (classOfList.isClassOfInstance(domainObject))
	// returnSet.add(domainObject);
	// }
	// return returnSet;
	// }

	// private HashMap<DomainClass, Date> listsLoaded = null;
	// private HashMap<Long, DomainObject> loadedObjects = null;

	// /**
	// * For protection against circle-updates
	// */
	// private boolean secondaryUpdate = false;
	//
	// /**
	// * For protection against circle-updates
	// */
	// private Object causeOfUpdate = null;

	// private void processLoadedList(List<DomainObject> newList, DomainClass
	// classOfList, HashSet<DomainObject> changedObjects) {
	// if (listsLoaded == null)
	// listsLoaded = new HashMap<DomainClass, Date>();
	// listsLoaded.put(classOfList, new Date());
	//
	// HashSet<DomainObject> processedObjects = new HashSet<DomainObject>();
	// if (newList != null)
	// for (DomainObject domainObject : newList) {
	// processLoadedDomainObject(classOfList.getDomainClassNamed(domainObject.getConcreteClass()),
	// domainObject, processedObjects, changedObjects);
	// }
	// }

	// private void processLoadedDomainObject(DomainClass domainClass,
	// DomainObject domainObject, HashSet<DomainObject> processedObjects,
	// HashSet<DomainObject> changedObjects) {
	// if (domainObject != null) {
	// if (domainClass == null)
	// domainClass =
	// domainTree.getDomainClassNamed(domainObject.getConcreteClass());
	// processedObjects.add(domainObject);
	// HashSet<DomainObject> distributedToObjects = new HashSet<DomainObject>();
	//
	// for (DomainObject domainObjectToCheck : getLoadedObjects().values()) {
	// if
	// (!domainObjectToCheck.getObjectID().equals(domainObject.getObjectID()))
	// updateRelations(domainObjectToCheck, domainObject, distributedToObjects,
	// changedObjects);
	// }
	// getLoadedObjects().put(domainObject.getObjectID(), domainObject);
	//
	// changedObjects.add(domainObject);
	//
	// HashSet<DomainObject> relatedObjects =
	// ClientTools.getDirectlyRelatedObjects(domainClass, domainObject);
	// for (DomainObject relatedObject : relatedObjects)
	// if (!processedObjects.contains(relatedObject))
	// processLoadedDomainObject(domainTree.getDomainClassNamed(relatedObject.getConcreteClass()),
	// relatedObject, processedObjects, changedObjects);
	// }
	// }

	// private void processDomainObjectDeletion(DomainObject domainObject,
	// HashSet<DomainObject> changedObjects) {
	// getLoadedObjects().remove(domainObject.getObjectID());
	// // loadedObjects.put(domainObject.getObjectID(), null);
	// changedObjects.add(domainObject);
	// // TODO: if deletion of referenced objects is possible: handle
	// // references
	// }

	// private void distributeChangedObjects(HashSet<DomainObject>
	// changedObjects, DomainClass loadedClass) {
	// if (domainClassChangeSubscribers != null || domainObjectChangeSubscribers
	// != null) {
	// HashSet<DomainClass> changedClasses = new HashSet<DomainClass>();
	//
	// // THIS IS ONLY USEFUL IF A CLASS IS LOADED AS A WHOLE. WHICH DOESN'T
	// HAPPEN WITH PAGING!
	// // if (loadedClass != null) {
	// // changedClasses.add(loadedClass);
	// // Set<DomainObject> knownObjectsOfClass = getLoadedList(loadedClass);
	// // for (DomainObject knownObject : knownObjectsOfClass) {
	// // if (!changedObjects.contains(knownObject))
	// // processDomainObjectDeletion(knownObject, changedObjects);
	// // }
	// // }
	//
	// // DomainClass domainClass =
	// // domainTree.getDomainClassNamed(domainObject.getConcreteClass());
	// for (DomainObject domainObject : changedObjects) {
	// if (domainObjectChangeSubscribers != null) {
	// HashSet<Notifiable> subscribers =
	// domainObjectChangeSubscribers.get(domainObject.getObjectID());
	// if (subscribers != null)
	// for (Notifiable notifiable : subscribers)
	// notifiable.doNotify("Object changed");
	// }
	// if (domainClassChangeSubscribers != null) {
	// DomainClass changedClass =
	// domainTree.getDomainClassNamed(domainObject.getConcreteClass());
	// if (!changedClasses.contains(changedClass))
	// changedClasses.add(changedClass);
	// }
	// }
	// if (domainClassChangeSubscribers != null) {
	// for (DomainClass domainClass : changedClasses) {
	// addSuperClassesToSet(domainClass, changedClasses);
	// }
	// for (DomainClass domainClass : domainClassChangeSubscribers.keySet()) {
	// if (changedClasses.contains(domainClass))
	// for (Notifiable notifiable :
	// domainClassChangeSubscribers.get(domainClass))
	// if (!secondaryUpdate && RPCCaller.this.causeOfUpdate != notifiable)
	// notifiable.doNotify("Class changed");
	// }
	// }
	// }
	// }

	// private void addSuperClassesToSet(DomainClass domainClass,
	// HashSet<DomainClass> set) {
	// DomainClass superClass = domainClass.getSuperClass();
	// if (superClass != null) {
	// set.add(superClass);
	// addSuperClassesToSet(superClass, set);
	// }
	// }

	// @SuppressWarnings({ "unchecked" })
	// private void updateRelations(DomainObject domainObjectToCheck,
	// DomainObject domainObject, HashSet<DomainObject> distributedToObjects,
	// HashSet<DomainObject> changedObjects) {
	// distributedToObjects.add(domainObjectToCheck);
	//
	// DomainClass classOfObject = getDomainTree(null,
	// false).getDomainClassNamed(domainObjectToCheck.getConcreteClass());
	// for (DomainClassAttribute domainClassAttribute :
	// classOfObject.getAttributesOfTypeDomainObject()) {
	// DomainObject relatedObject = (DomainObject)
	// ClientTools.getAttributeValue(classOfObject, domainClassAttribute,
	// domainObjectToCheck);
	// if (relatedObject != null && relatedObject != domainObject) {
	// if (relatedObject.getObjectID().equals(domainObject.getObjectID())) {
	// ClientTools.setAttributeValue(classOfObject, domainClassAttribute,
	// domainObjectToCheck, domainObject);
	// if (changedObjects.contains(domainObjectToCheck))
	// changedObjects.add(domainObjectToCheck);
	// } else if (!distributedToObjects.contains(relatedObject)) {
	// updateRelations(relatedObject, domainObject, distributedToObjects,
	// changedObjects);
	// }
	// }
	// }
	// for (DomainClassAttribute domainClassAttribute :
	// classOfObject.getCollectionAttributes()) {
	// Collection<DomainObject> set = (Collection<DomainObject>)
	// ClientTools.getAttributeValue(classOfObject, domainClassAttribute,
	// domainObjectToCheck);
	// if (set != null) {
	// Collection<DomainObject> newCollection = null;
	// if (domainClassAttribute.getType().startsWith("java.util.List"))
	// newCollection = new ArrayList<DomainObject>();
	// else
	// newCollection = new HashSet<DomainObject>();
	// boolean differ = false;
	// for (DomainObject relatedObject : set) {
	// if (relatedObject.getObjectID().equals(domainObject.getObjectID())) {
	// newCollection.add(domainObject);
	// if (relatedObject != domainObject)
	// differ = true;
	// } else {
	// if (!distributedToObjects.contains(relatedObject)) {
	// updateRelations(relatedObject, domainObject, distributedToObjects,
	// changedObjects);
	// }
	// newCollection.add(relatedObject);
	// }
	// }
	// if (differ) {
	// ClientTools.setAttributeValue(classOfObject, domainClassAttribute,
	// domainObjectToCheck, newCollection);
	// if (changedObjects.contains(domainObjectToCheck))
	// changedObjects.add(domainObjectToCheck);
	// }
	// }
	// }
	// }

	// public boolean isUpToDateListAvailable(DomainClass classOfList) {
	// Date lastLoaded = getDateListLastLoaded(classOfList);
	// return (lastLoaded.after(new Date(new Date().getTime() - 1000)));
	// }

	// private Date getDateListLastLoaded(DomainClass classOfList) {
	// Date lastLoaded = new Date(Long.MIN_VALUE);
	// if (listsLoaded == null)
	// return lastLoaded;
	//
	// if (listsLoaded.containsKey(classOfList))
	// lastLoaded = listsLoaded.get(classOfList);
	// if (classOfList.getSuperClass() != null) {
	// Date lastLoadedSuper =
	// getDateListLastLoaded(classOfList.getSuperClass());
	// if (lastLoaded.before(lastLoadedSuper))
	// lastLoaded = lastLoadedSuper;
	// }
	//
	// return lastLoaded;
	// }
}
