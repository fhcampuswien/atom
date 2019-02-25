/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.rpc;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.DomainObjectSearchResult;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("rpc")
public interface AtomService extends RemoteService {

	/**
     * If the given ClientSession is valid, and there exists an instance of DomainObject which id is equal to the given
     * one, that instance is returned.
     * 
     * @param clientSession
     * @param id
     * @return
     */
    public DomainObject getDomainObject(String clientSession, Integer id, String nameOfClass) throws AtomException;

    public DomainObjectSearchResult searchDomainObjects(String clientSession, String searchString, int pageSize, boolean onlyRelated, boolean onlyWriteables, boolean onlyScanStringRepresentation, String onlyScanClassWithName) throws AtomException;

    /**
     * If the given ClientSession is valid, and the parameter nameOfClass contains a valid name of a class derived from
     * DomainObject, then this method will return a list of instances of that class that are stored in the database.
     * 
     * @param clientSession
     * @param nameOfClass
     * @return
     * @throws Exception
     *             is thrown if no class with that name is known.
     */
    public DomainObjectList getListOfDomainObject(String clientSession, String nameOfClass, int fromRow, int pageSize, ArrayList<DataFilter> filters, ArrayList<DataSorter> sorters, String searchString, boolean onlyScanStringRepresentation, boolean onlyRelated, boolean onlyWriteables) throws AtomException;

    /**
     * If the given ClientSession is valid, the given DomainObject instance will be persisted into the database and
     * returned again. This is done, since the save action can change the DomainObject instance, e.g. an instance that
     * is persisted for the first time will have an id afterwards.
     * 
     * @param clientSession
     * @param domainObject
     * @return
     */
    public DomainObject saveDomainObject(String clientSession, DomainObject domainObject) throws AtomException;

    /**
     * If the given ClientSession is valid, the given DomainObject instance will be deleted. If this action is
     * successful, the boolean value true will be returned.
     * 
     * @param clientSession
     * @param domainObject
     * @return
     */
    public boolean deleteDomainObject(String clientSession, DomainObject domainObject) throws AtomException;

    /**
     * If the given ClientSession is valid, this method will return a representation of the derivation hierarchy below
     * the class DomainObject. This representation is build with the help of the Java reflection API. Every class within
     * the hierarchy is represented by an instance of DomainClass. The analysis itself is done by the class
     * DomainAnalyzer.java.
     * 
     * @param clientSession
     * @return
     */
    public DomainClass getDomainTree(String clientSession) throws AtomException;

//    public List<DomainClass> getAccessibleDomainClasses(String clientSession);
    
    /**
     * This method checks the validity of an username password pair against the CampusAuth webservice. If the user
     * password pair can be successfully verified, a ClientSession instance, which represents a valid session, is
     * returned.
     * 
     * @param userName
     * @param password
     * @return
     * @throws Exception
     *             is thrown if authentication webservice tells the credentials to be invalid, or if it can't be
     *             reached.
     */
    public ClientSession getNewSession(String userName, String password) throws AtomException;

    public Boolean logoutActiveUser(String cookieValue) throws AtomException;
    
    /**
     * This method allows clients to verify the validity of an existing session which can be stored in a cookie inside
     * the users browser. This is done by a set of application which all use the same user authentification service.
     * 
     * @param cookieValue
     * @return
     * @throws Exception
     *             is thrown if authentication webservice tells the credentials to be invalid, or if it can't be
     *             reached.
     */
    public ClientSession getSessionForCookie(String cookieValue) throws AtomException;
    
    public LinkedHashMap<String, String> getAttributeChoiceList(String clientSession, String nameOfClass, String nameOfAttribute) throws AtomException;
    
    public String getServerInfo();
}
