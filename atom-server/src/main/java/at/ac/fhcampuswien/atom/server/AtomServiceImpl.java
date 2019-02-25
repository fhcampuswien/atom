/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.DomainObjectSearchResult;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;
import at.ac.fhcampuswien.atom.shared.rpc.AtomService;

/**
 * The server side implementation of the RPC service.
 * The instance of this Servlet is shared between multiple Requests, also simultaneous Requests.
 * Therefore class variables are quite problematic.
 *  
 */
public class AtomServiceImpl extends RemoteServiceServlet implements AtomService {

	private static final long serialVersionUID = -51531178490542502L;

	private ServerSingleton server;

	public AtomServiceImpl() {
		server = ServerSingleton.getInstance();
	}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		ServerTools.log(Level.SEVERE, "unexpectedFailure happened in doPost method: " + e.getMessage(), this, e);
		e.printStackTrace();
		throw new AtomException("unexpectedFailure happened in doPost method", e);
	}

	@Override
	public Boolean logoutActiveUser(String cookieValue) {
		return server.getAuth().logout(getThreadLocalRequest(), getThreadLocalResponse());
	}

	@Override
	public DomainObject getDomainObject(String clientSession, Integer id, String nameOfClass) throws AtomException {
		return server.getDomainObject(getThreadLocalRequest(), id, nameOfClass);
	}

	@Override
	public DomainObjectSearchResult searchDomainObjects(String clientSession, String searchString, int pageSize, boolean onlyRelated, boolean onlyWriteables, boolean onlyScanStringRepresentation, String onlyScanClassWithName) throws AtomException {
		return server.searchDomainObjects(getThreadLocalRequest(), searchString, pageSize, onlyRelated, onlyWriteables, onlyScanStringRepresentation, onlyScanClassWithName);
	}

	@Override
	public DomainObjectList getListOfDomainObject(String clientSession, String nameOfClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, boolean onlyRelated, boolean onlyWriteables) throws AtomException {
		return server.getListOfDomainObjects(getThreadLocalRequest(), nameOfClass, fromRow, pageSize, filters, sorters, searchString, onlyScanStringRepresentation, onlyRelated, onlyWriteables);
	}

	@Override
	public DomainObject saveDomainObject(String clientSession, DomainObject domainObject) throws ValidationError {
		return server.saveDomainObject(getThreadLocalRequest(), domainObject);
	}

	@Override
	public boolean deleteDomainObject(String clientSession, DomainObject domainObject) {
		return server.deleteDomainObject(getThreadLocalRequest(), domainObject);
	}

	@Override
	public DomainClass getDomainTree(String clientSession) {
		return server.getDomainTreeForUser(getThreadLocalRequest());
	}

	@Override
	public ClientSession getNewSession(String userName, String password) throws AtomException {
		return server.getAuth().getSessionForCredentials(userName, password, getThreadLocalRequest(), getThreadLocalResponse());
	}

	@Override
	public ClientSession getSessionForCookie(String cookieValue) throws AtomException {
		return server.getAuth().getSessionFromCookie(getThreadLocalRequest());
	}

	@Override
	public LinkedHashMap<String, String> getAttributeChoiceList(String clientSession, String nameOfClass, String nameOfAttribute) throws AtomException {
		return server.getAttributeChoiceList(getThreadLocalRequest(), nameOfClass, nameOfAttribute);
	}
	
	@Override
	public String getServerInfo() {
		return server.getDatabaseInfo(getThreadLocalRequest());
	}
}
