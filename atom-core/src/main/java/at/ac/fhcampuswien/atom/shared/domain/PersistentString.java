/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import javax.persistence.ManyToOne;

import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;

//@Entity
@ClassNamePlural("PersistentStrings")
@AnalyzerIgnore
public class PersistentString extends DomainObject {

	@AnalyzerIgnore
	private static final long serialVersionUID = 8875224958340977068L;

	private String value;

	@ManyToOne
	private DomainObject owner;

	private int type;

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

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

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
