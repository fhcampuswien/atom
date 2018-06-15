/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.GwtTransient;

public class AccessHandler implements java.io.Serializable {

	private static final long serialVersionUID = -666503038590515178L;
	
	private transient HashSet<AccessForOes> accessOes = new HashSet<AccessForOes>();
	private transient HashSet<AccessForRoles> accessRoles = new HashSet<AccessForRoles>();
	private transient AccessHandler parent;

	/**
	 * Maps from String username to Set<String> accessTypes
	 */
	@GwtTransient
	@Transient
	private transient Map<String, Set<Access>> userAccessCache = new HashMap<String, Set<Access>>();

	private Set<Access> usersAccess = null;

	private boolean isForAttribute = false;
	
	/**
	 * use this for AttributeAccessHandlers
	 * they never have parents, and must not store found access in instances,
	 * since its only access to one of the instances attributes.
	 */
	public AccessHandler() {
		isForAttribute = true;
	}
	
	public AccessHandler(AccessHandler parent) {
		this.parent = parent;
	}
	

	/**
	 * @param roles if a user has one of these roles
	 * @param requiredRelations and one of those relations (default is {@link at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition#noRelationRequired noRelationRequired})
	 * @param accessTypes he gets all of those accessTypes to this class
	 */
	public void addAccessRoles(String[] accessTypes, String[] requiredRelations, String[] roles) {
		accessRoles.add(new AccessForRoles(accessTypes, requiredRelations, roles));
	}

	public void addAccessOE(String[] accessTypes, String[] requiredRelations, boolean onlyHauptrolle, boolean onlyLeiter, String[] oes) {
		accessOes.add(new AccessForOes(accessTypes, requiredRelations, onlyHauptrolle, onlyLeiter, oes));
	}
	
	public void fillUsersAccess(ClientSession session) {
		usersAccess = getAccess(session);
	}

	public Set<Access> getUsersAccess() {
		if (!GWT.isClient())
			throw new AtomException("This method is only allowed for client access. On the Server it's use would lead to unforseeable results due to the multithreaded multiuser nature of the server.");
		return usersAccess;
	}

	public Set<Access> getAccess(ClientSession session) {
		
		if (GWT.isClient())
			return getUsersAccess();
		
		if (session == null)
			return null;

		// FIXME: with cache I got wrong permissions for user thkaf & object ClipBoardEntry (could not add stuff to the clipboard permanently)
		if (userAccessCache.get(session.getUsername()) != null)
			return userAccessCache.get(session.getUsername());

		//Set<String> foundTypes = new HashSet<String>();

		Map<Integer, String> roles = session.getRoles();
		Set<Access> foundAccess = new HashSet<Access>();

		for(AccessForRoles ar : accessRoles) {
			for(String role : ar.roles) {
				if (role.equals("*") || (roles != null && (roles.containsValue(role) || roles.containsKey(AtomTools.parseStringAsIntNullForErrors(role))))) {
					foundAccess.add(ar.access);
					break;
				}
			}
		}
		
		if(session.getUser() != null)
			foundAccess.addAll(session.getUser().accessForMatchingOes(accessOes));
		
		if (foundAccess.size() > 0) {
			if (parent != null) {
				Set<Access> parentAccess = parent.getAccess(session);
				if (parentAccess != null)
					foundAccess.addAll(parentAccess);
			}
			userAccessCache.put(session.getUsername(), foundAccess);
			return foundAccess;
		}
		else if (parent != null) {
			userAccessCache.put(session.getUsername(), parent.getAccess(session));
			return userAccessCache.get(session.getUsername());
		}
		else {
			return null;
		}
	}
	
	public Set<String> getNoRelationRequiredAccess(ClientSession session) {
		Set<String> noRelationRequired = new HashSet<String>();
		Set<Access> a = getAccess(session);
		if(a != null) for(Access access : a) {
			for(String relation : access.getRequiredRelations()) {
				if(relation.equals(RelationDefinition.noRelationRequired)) {
					Collections.addAll(noRelationRequired, access.getAccessTypes());
					break;
				}
				else for(String permission : access.getAccessTypes()) {
					if(AtomConfig.accessLinkage.equals(permission))
						noRelationRequired.add(AtomConfig.accessSomeLinkage);
					if(AtomConfig.accessReadOnly.equals(permission))
						noRelationRequired.add(AtomConfig.accessSomeReadOnly);
					if(AtomConfig.accessReadWrite.equals(permission))
						noRelationRequired.add(AtomConfig.accessSomeReadWrite);
				}
			}
		}
		return noRelationRequired;
	}
	
	public boolean relationsExist() {
		
		for(Access a : getUsersAccess()) {
			if(AtomTools.isAccessAllowed(AtomConfig.accessLinkage, a.getAccessTypes())) {
				String[] rels = a.getRequiredRelations();
				for(String r : rels) {
					if(r != null && !RelationDefinition.noRelationRequired.equals(r))
						return true;
				}
			}
		}
		return false;
	}
	
	public Set<String> getAccessTypes(ClientSession session, DomainObject instance) {
		Set<Access> classAccess = getAccess(session);
		Set<String> ati = new HashSet<String>();
		if (classAccess != null) {
			for(Access access : classAccess) {
				for(String relationType : access.getRequiredRelations()) {
					if(instance.isUserRelated(session, relationType)) {
						Collections.addAll(ati, access.getAccessTypes());
						break;
					}
				}
			}
		}
		if(!isForAttribute)
			instance.setUserPermissions(ati);
		return ati;
	}
	
	public static Set<String> getAllAccessTypes(Set<Access> access) {
		Set<String> at = new HashSet<String>();
		for(Access a : access) {
			Collections.addAll(at, a.getAccessTypes());
		}
		return at;
	}
	
	public Set<String> getAllAccessTypes(ClientSession session) {
		return AccessHandler.getAllAccessTypes(getAccess(session));
	}
}
