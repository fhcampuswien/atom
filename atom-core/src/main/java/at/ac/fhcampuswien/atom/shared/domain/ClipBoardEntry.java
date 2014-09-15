/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AccessLists;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeLoadingPolicy;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromGui;
import at.ac.fhcampuswien.atom.shared.annotations.Searchable;
import at.ac.fhcampuswien.atom.shared.annotations.SortColumn;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

@Entity
@ClassNamePlural("FrameVisits")
@HideFromGui
@AccessLists(rolesLists= {
		@AccessListRoles(value = { "*" }, accessTypes = { AtomConfig.accessCreateNew }),
		@AccessListRoles(value = { "*" }, accessTypes = { AtomConfig.accessReadWrite }, requiredRelations = RelationDefinition.defaultRelation)
})
@SortColumn("added ASC")
@Searchable(false)
@RelationDefinition(where = "p.objectID = {$objectID} OR obj.creationUser.objectID = {$objectID} OR obj.updateUser.objectID = {$objectID}", distinct = true, joins = "LEFT OUTER JOIN obj.owner p")
public class ClipBoardEntry extends FeaturedObject implements Serializable {

	@AnalyzerIgnore
	private static final long serialVersionUID = -11009381736405542L;

	//cascade = { CascadeType.ALL }, 
	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@Nonnull @JoinColumn(nullable = false)
	@AttributeLoadingPolicy(requiredForStringRepresentation=true, whenNotPrimary=true, withLists=true)
	private StoreableUser owner;

	@ManyToOne(fetch = FetchType.EAGER)
//	@OnDelete(action = OnDeleteAction.CASCADE)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@Nonnull @JoinColumn(nullable = false)
	private DomainObject instance;

	private Date added;

	// @AnalyzerIgnore @Transient @GwtTransient
	// private transient boolean createdLabel = false;

	@SuppressWarnings("unused")
	// for GWT serialization
	private ClipBoardEntry() {
	}

	public ClipBoardEntry(StoreableUser owner, DomainObject instance, Date added) {
		this.owner = owner;
		this.instance = instance;
		this.added = added;
	}

	@Override
	public void prepareSave(ClientSession session) {
		if (!isUserRelated(session)) {
			throw new ValidationError("You can only save your own ClipBoardEntry instances! (owner==you)");
		}
		super.prepareSave(session);
	}

	@Override
	public boolean isUserRelated(ClientSession session) {
		// if (super.isUserRelated(session))
		// return true;
		// else
		return (owner != null && owner.equals(session.getUser())) || (session.getUser() == null && owner == null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ClipBoardEntry))
			return false;
		else {
			ClipBoardEntry other = (ClipBoardEntry) obj;
			return owner != null && owner.equals(other.owner) && instance != null && instance.equals(other.instance);
		}
	}

	@Override
	public int hashCode() {
		return 3 * (owner == null ? 0 : owner.hashCode()) + 7 * (instance == null ? 0 : instance.hashCode());
	}

	@Override
	public String toString() {
		return "ClipBoardEntry " + (instance == null ? "" : instance.toString()) + "(" + String.valueOf(getObjectID()) + ")";
	}

	public StoreableUser getOwner() {
		return owner;
	}

	public void setOwner(StoreableUser owner) {
		this.owner = owner;
	}

	public DomainObject getInstance() {
		return instance;
	}

	public void setInstance(DomainObject instance) {
		this.instance = instance;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}
}
