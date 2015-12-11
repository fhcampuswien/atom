/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromGui;

@Entity
@ClassNamePlural("PersistentStrings")
// @AnalyzerIgnore
@HideFromGui
@AccessListRoles(accessTypes = { AtomConfig.accessLinkage, AtomConfig.accessReadOnly }, value = "*")
public class PersistentString extends DomainObject {

	@AnalyzerIgnore
	private static final long serialVersionUID = 8875224958340977068L;

	private String value;

	@ManyToOne
	private DomainObject owner;

	private String ownersAttribute;

//	private int type;

	public PersistentString() {
	}

	public PersistentString(String initialValue) {
		this.value = initialValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DomainObject getOwner() {
		return owner;
	}

	public void setOwner(DomainObject owner) {
		this.owner = owner;
	}

	public String getOwnersAttribute() {
		return ownersAttribute;
	}

	public void setOwnersAttribute(String ownersAttribute) {
		this.ownersAttribute = ownersAttribute;
	}

//	public void setType(int type) {
//		this.type = type;
//	}
//
//	public int getType() {
//		return type;
//	}

	@Override
	public String getStringRepresentation() {
		stringRepresentation = value;
		return stringRepresentation;
	}

	@Override
	public String toString() {
		return value;
	}
}

/*

##### delete PersistentStrings with no owner
DELETE
FROM [I@H].[dbo].[PersistentString]
WHERE owner_objectID IS NULL

SELECT *
FROM [I@H].[dbo].[PersistentString] p
WHERE NOT EXISTS (SELECT d.objectID FROM [I@H].[dbo].[DomainObject] d where d.objectID = p.owner_objectID)
--> gives empty result set

SELECT *
FROM [I@H].[dbo].[PersistentString] p
WHERE NOT EXISTS (SELECT k.objectID FROM [I@H].[dbo].[Kontakt] k where k.objectID = p.owner_objectID)
  AND NOT EXISTS (SELECT a.objectID FROM [I@H].[dbo].[AuslaendischeInstitution] a where a.objectID = p.owner_objectID)
--> gives empty result set

UPDATE p
  SET p.[ownersAttribute] = 'arbeitsschwerpunkteMulti'
  FROM [I@H].[dbo].[PersistentString] AS p
  INNER JOIN [I@H].[dbo].[Kontakt] AS k
  ON k.objectID = p.owner_objectID

UPDATE p
  SET p.[ownersAttribute] = 'departmentsFachbereiche'
  FROM [I@H].[dbo].[PersistentString] AS p
  INNER JOIN [I@H].[dbo].[AuslaendischeInstitution] AS k
  ON k.objectID = p.owner_objectID
  WHERE p.type = 1
  
UPDATE p
  SET p.[ownersAttribute] = 'forschungsfelder'
  FROM [I@H].[dbo].[PersistentString] AS p
  INNER JOIN [I@H].[dbo].[AuslaendischeInstitution] AS k
  ON k.objectID = p.owner_objectID
  WHERE p.type = 2

 */