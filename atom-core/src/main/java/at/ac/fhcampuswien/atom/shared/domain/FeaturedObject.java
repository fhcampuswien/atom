/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeGroup;
import at.ac.fhcampuswien.atom.shared.annotations.AttributePlacement;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.ObjectImage;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;

@Entity
// @SecondaryTables( {
// @SecondaryTable(name = "tbl_Personen", catalog = "Campus_Daten", schema =
// "dbo",
// })
@ClassNamePlural("FeaturedObjects")
@AccessListRoles(accessTypes = AtomConfig.accessReadWrite, value = { "Administrator", "CIS-Administrator" })
@RelationDefinition(where = "obj.creationUser.objectID = {$objectID} OR obj.updateUser.objectID = {$objectID}", distinct = true)
@ObjectImage("data:image/png;base64,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")
public class FeaturedObject extends DomainObject { // extends
	// LazyPojo

	@AnalyzerIgnore
	private static final long serialVersionUID = 8875224958340977068L;

	@AttributePlacement(10)
	public Integer getObjectID() {
		return super.getObjectID();
	}

	@AttributePlacement(value = 10100, type = AttributePlacement.defaultType)
	@AttributeGroup("System")
	private Date creationDate;

	@AttributePlacement(10200)
	@AttributeGroup("System")
	private Date lastModifiedDate;

	@AttributePlacement(10300)
	@AttributeGroup("System")
	@ManyToOne(fetch = FetchType.EAGER)
	//cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.ALL }, 
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private StoreableUser creationUser;

	// @Column(insertable=false, updatable=false, columnDefinition="")
	@AttributePlacement(10400)
	@AttributeGroup("System")
	@ManyToOne(fetch = FetchType.EAGER)
	//cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.ALL }, 
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	private StoreableUser updateUser;

	// @Column(columnDefinition="cp.Per_Vorname + \" \" + cp.Per_Nachname",
	// insertable=false, updatable=false)
	// private String creationUserName;

	public Date getCreationDate() {
		return creationDate;
	}

	@AnalyzerIgnore
	// only for the server, so it can clear the property for linkageRight users
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@AnalyzerIgnore
	// only for the server, so it can clear the property for linkageRight users
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public StoreableUser getCreationUser() {
		return creationUser;
	}

	@AnalyzerIgnore
	// only for the server
	public void setCreationUser(StoreableUser creationUser) {
		this.creationUser = creationUser;
	}

	public StoreableUser getUpdateUser() {
		return updateUser;
	}

	@AnalyzerIgnore
	// only for the server
	public void setUpdateUser(StoreableUser updateUser) {
		this.updateUser = updateUser;
	}

	public void prepareSave(ClientSession session) {
		Date now = new Date();
		if (creationDate == null && getObjectID() == null) {
			creationDate = now;
			creationUser = session.getUser();
		}
		lastModifiedDate = now;
		updateUser = session.getUser();
		
		super.prepareSave(session);
	}

	@Override
	public boolean isUserRelated(ClientSession session) {
		return (getCreationUser()    != null && getCreationUser().equals(session.getUser()) || 
				getUpdateUser() != null && getUpdateUser().equals(session.getUser())); 
	}
	// public String getCreationUserName() {
	// return creationUserName;
	// }

	// @SuppressWarnings("unused")
	// // required for hibernate
	// private void setCreationDate(Date creationDate) {
	// this.creationDate = creationDate;
	// }

	// @SuppressWarnings("unused")
	// // required for hibernate
	// private void setLastModifiedDate(Date lastModifiedDate) {
	// this.lastModifiedDate = lastModifiedDate;
	// }

	// public Boolean canRead(ClientSession clientSession) {
	// return checkAccess(false, clientSession);
	// }
	//
	// public Boolean canWrite(ClientSession clientSession) {
	// return checkAccess(true, clientSession);
	// }
	//
	// private Boolean checkAccess(boolean writeAccess, ClientSession
	// clientSession) {
	// Map<Integer, String> roles = clientSession.getRoles();
	// boolean deny = false, read = false, write = false;
	//
	// for (PortalUserRole role : accessDeniedRoles) {
	// if (roles.containsKey(role.getRole_ID())) {
	// deny = true;
	// }
	// }
	//
	// for (PortalUserRole role : accessReadOnlyRoles) {
	//
	// if (roles.containsKey(role.getRole_ID())) {
	// read = true;
	// }
	// }
	//
	// for (PortalUserRole role : accessReadWriteRoles) {
	// if (roles.containsKey(role.getRole_ID())) {
	// write = true;
	// }
	// }
	//
	// if (deny && (write || read) || write && read) {
	// AtomTools.log(Level.SEVERE, "inconsistent access rules!", this);
	// }
	// if (write || (!writeAccess && read)) {
	// return true;
	// } else if (deny || (writeAccess && read)) {
	// return false;
	// } else {
	// return null;
	// }
	// }
}
