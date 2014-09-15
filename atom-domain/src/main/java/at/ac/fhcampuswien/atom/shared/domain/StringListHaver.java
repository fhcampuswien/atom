/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.util.Set;

import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeGroup;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;

//@Entity
@AnalyzerIgnore
@ClassNamePlural("StringListHavers")
public class StringListHaver extends FeaturedObject {

    @AnalyzerIgnore
    private static final long serialVersionUID = 5445394343671831681L;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @Where(clause = "type=1")
    @AttributeGroup("RechteStrings")
    private Set<PersistentString> accessReadWriteRolesStrings;
    
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @Where(clause = "type=2")
    @AttributeGroup("RechteStrings")
    private Set<PersistentString> accessReadOnlyRolesStrings;
    
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    @Where(clause = "type=3")
    @AttributeGroup("RechteStrings")
    private Set<PersistentString> accessDeniedRolesStrings;
    
    public Set<PersistentString> getAccessReadWriteRolesStrings() {
	return accessReadWriteRolesStrings;
    }

    public Set<PersistentString> getAccessReadOnlyRolesStrings() {
	return accessReadOnlyRolesStrings;
    }

    public Set<PersistentString> getAccessDeniedRolesStrings() {
	return accessDeniedRolesStrings;
    }

    public void setAccessReadWriteRolesStrings(Set<PersistentString> newRoles) {
	accessReadWriteRolesStrings = newRoles;
    }

    public void setAccessReadOnlyRolesStrings(Set<PersistentString> newRoles) {
	accessReadOnlyRolesStrings = newRoles;
    }

    public void setAccessDeniedRolesStrings(Set<PersistentString> newRoles) {
	accessDeniedRolesStrings = newRoles;
    }
    
    
    @Override
    public void prepareSave(ClientSession session) {
        super.prepareSave(session);
        
        if (getAccessReadWriteRolesStrings() != null)
	    for (PersistentString string : getAccessReadWriteRolesStrings()) {
		string.setOwner(this);
		string.setType(1);
	    }

	if (getAccessReadOnlyRolesStrings() != null)
	    for (PersistentString string : getAccessReadOnlyRolesStrings()) {
		string.setOwner(this);
		string.setType(2);
	    }

	if (getAccessDeniedRolesStrings() != null)
	    for (PersistentString string : getAccessDeniedRolesStrings()) {
		string.setOwner(this);
		string.setType(3);
	    }
    }
}
