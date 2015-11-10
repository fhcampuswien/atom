/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import at.ac.fhcampuswien.atom.shared.Access;
import at.ac.fhcampuswien.atom.shared.AccessForOes;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeDisplayName;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeGroup;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeLoadingPolicy;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromListGui;
import at.ac.fhcampuswien.atom.shared.annotations.ObjectImage;
import at.ac.fhcampuswien.atom.shared.annotations.RelationEssential;

/**
 * 
 * @author kaefert
 *
 *

	INSERT INTO Atom.dbo.StoreableUser (objectID)
	SELECT p.Per_Atom_ID
	FROM [Campus_Daten].[dbo].[tbl_Personen] p
	WHERE NOT EXISTS(
		SELECT objectID FROM Atom.dbo.StoreableUser
		WHERE p.Per_Atom_ID = objectID
	);
	
	SELECT * FROM Atom.dbo.StoreableUser
	
 */

@Entity
@ObjectImage("data:image/png;base64,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")
public class StoreableUser extends DomainObject {

	@AnalyzerIgnore
	private static final long serialVersionUID = 2422853819641167767L;

	private Integer Per_ID;

	public Integer getPer_ID() {
		return Per_ID;
	}

	public void setPer_ID(Integer per_ID) {
		Per_ID = per_ID;
	}

	@RelationEssential //to not fail on clearing it because no setter is available
	public Integer getHaupt_OrE_ID() {
		return null;
	}
	
	@RelationEssential //to not fail on clearing it because no setter is available
	public CharSequence getOrE_ID_List() {
		return "(-1)";
	}
	
	@HideFromListGui
	@AttributeDisplayName("Erstellte Objektinstanzen")
	@AttributeGroup("System")
	@OneToMany(mappedBy = "creationUser", fetch = FetchType.LAZY)
	@AttributeLoadingPolicy(requiredForStringRepresentation = false, whenNotPrimary = false, withLists = false)
	private Set<FeaturedObject> createdInstances;
	
	@HideFromListGui
	@AttributeDisplayName("Zuletzt bearbeitete Instanzen")
	@AttributeGroup("System")
	@OneToMany(mappedBy = "updateUser", fetch = FetchType.LAZY)
	@AttributeLoadingPolicy(requiredForStringRepresentation = false, whenNotPrimary = false, withLists = false)
	private Set<FeaturedObject> updatedInstances;
	
	public HashSet<Access> accessForMatchingOes(HashSet<AccessForOes> oeas) {
		return null;
	}
	
	public Set<FeaturedObject> getCreatedInstances() {
		return createdInstances;
	}

	public void setCreatedInstances(Set<FeaturedObject> createdInstances) {
		this.createdInstances = createdInstances;
	}

	public Set<FeaturedObject> getUpdatedInstances() {
		return updatedInstances;
	}

	public void setUpdatedInstances(Set<FeaturedObject> updatedInstances) {
		this.updatedInstances = updatedInstances;
	}
	
	public void loadRelevantSeverSessionInfos() {
	}
}
