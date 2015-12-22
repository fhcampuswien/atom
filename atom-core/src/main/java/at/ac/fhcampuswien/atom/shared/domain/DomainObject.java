/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.google.gwt.user.client.rpc.GwtTransient;
import com.gwtent.reflection.client.Reflectable;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DataSorter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeDisplayName;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeGroup;
import at.ac.fhcampuswien.atom.shared.annotations.AttributePlacement;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.DefaultAttributeGroupName;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromDetailGui;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromListGui;
import at.ac.fhcampuswien.atom.shared.annotations.ObjectImage;
import at.ac.fhcampuswien.atom.shared.annotations.OrderedAttributeGroups;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.RelationEssential;

/**
 * 
 * @author thomas.kaefer
 * 
 *         Fließkomma: Double / Float (Float noch kein AttributeView!)
 *         Ganzzahlig: Integer / Long String: String, Langtext mit Annotation
 *         "@Lob", Formatierter Langtext: zusätzlich Annotation
 *         "@StringFormattedLob"
 * 
 *         Verknüpfung: Instanz von DomainObject bzw. Subklasse --> Achtung!
 *         Muss fetch = FetchType.EAGER verwenden! Liste von Verknüpfungen:
 *         Set<DomainObject bzw. Subklasse> Liste von Verknüpfungen mit
 *         Reihenfolge: Nicht vollständig implementiert! (List<..>)
 * 
 * 
 *         Wenn ein Attribut nicht bearbeitbar sein soll, einfach die Methode
 *         setAttributeName() weglassen. ACHTUNG: Alle Attribute die keine set
 *         Methode haben müssen als @RelationEssential definiert werden, sonst
 *         können keine Instanzen an User übermittelt werden, die blos
 *         LinkageRight zugewiesen haben,
 * 
 *         @com.google.gwt.user.client.rpc.GwtTransient kann verwendet werden um
 *         Felder nicht an den GWT Client zu übertragen
 *         @javax.persistence.Transient kann verwendet werden, um ein Attribut
 *         nicht mittels Hibernate in der Datenbank zu speichern das transient
 *         keyword (private transient AttributeType attributenName) bewirkt
 *         beide der obenstehenden Effekte
 *         
 *         Mit der Annotation @AnnalyzerIgnore kann der {@link DomainAnalyzer}
 *         angewiesen werden, Felder und Methoden zu ignorieren.
 *         
 *         ACHTUNG: @AnnalyzerIgnore kann auf Collections nur angewendet werden,
 *         wenn diese zumindest eine der Annotations @javax.persistence.Transient
 *         und @com.google.gwt.user.client.rpc.GwtTransient zugewiesen bekommen
 *         hat.
 *         Erklärung: Hibernate Collections müssen entweder transkribiert werden,
 *         oder dürfen nicht an den Client gesendet werden. 
 */

//gwt-ent reflection, currently concidering to replace it with https://github.com/WeTheInternet/xapi/tree/master/gwt/gwt-reflect 
@Reflectable(superClasses = false, assignableClasses = true, relationTypes = false, fields = false, methods = true, constructors = true, classAnnotations = false, fieldAnnotations = false)

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
// @ObjectImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAJCAYAAADkZNYtAAAAwklEQVR42lVQWw7CIBDkoFJLLwL1FNrSejAfP21ijJ7A2I47CzSRBJbdmR2YNdfpiX07oAo9mnCE9b3mdY42jHBth8v8gmHRhohKG6IQosa0eyUSr32EKSCbCFZelCT/AnD+pMqKiZhhoSi6rEalVcg8SHT5ZbNrR7mckdD/tWhtUbKTJlPMWVVPxrixfrTf+g7VIb1s+IUmdNtEGAmQScyFQc0RM1TU8Wwmk2Gb84Kp8n166OfViIxpG1kmF4Hb/MYPmyYNcDZbmSIAAAAASUVORK5CYII=")
@ObjectImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC0AAAAtCAYAAAA6GuKaAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9sMFQ4hIKt1FMEAAAwISURBVFjD7Zh5kB3Ffcc/3XO8t2933+5Ku9rVHrpvQGYldCEZS4AIGIzkIBRxJwQfONgVSBFwIaXKlQgTlylcsY2IhUwCAsc2RiBsjI0FIeCgg+gKCCQhwbLSWtpD+3bfNTM9050/5q1uBWSrEpHKr+pX3W9ez6+//bt74P/p/zJNeQSmrCjNV3xCQE8tAW3+9tBPBF4x/R/jyfAHx4xc9JM9jH2o5vCzs52u+Ktf/ftd399kOO971wLI2Ss/9rvyfwNwxaxH5s297cKpQaih3P0HAP3bL5ydoJ1LVsH0R7juxilfecgvd1O2MLhug3XRyrtPR87/GGhn/g9R6/4cXHFe16imRb0IlJQC10Yn3Ludz6yoO+tAq5duBeCCGcNX7xzViK/BExIcG+Mm63QitcS5dBXOZ//p7HKPwfNXfn7CdRdOfqcfA1AUEhwLXBuTcP4aG0e98KdnB+jEgn+GqvsSc6+f8dV/iVIQaUFUAu3a4FrosmSzsO27ABJXP35mQd93muuTi57Af+4Wxt44/fwdtUPmhV4EQQh+SAERa9q2wHWJUqlvll/x8CB/7c2/H+ilp3i+/CP+P0HLqUQ8NlT/oKMyDSoEFYEKKSLAcWK2bROlKkRQOejej5JpHw80ISXLtKYHkg82N08AbqsZNuzidFPTyHxXV8ehvXtf08Y8tr+9fRNQGND+8lNs0Pf4YhJXPnqdnDphcp8XGTACIUCIGLRrgRQAAjCa1A3JP1n9Le/HN3Z/JOilwKiWFm5tb2cZXPfpO++8u2XmzNZhc+aQamwcWDbK7+kZte+NN27p2LRpd/13v/vwPb2931l+CuDlNz9F/sf/VnbOZ6cs2yqSEIYiruUxaG/APWLQMXAn1Sh08YvA/ambnqLwxPUntgLHP1gGq65fvfrWlrlzwRgAc9w6M/Bu765dPLlw4Sv3ZrMXA/CFVli55Rh5Y+998equ1nOfzbhlIIRAlKQJwTkdB3h7R+cx0oUUVIVFsnt310Q/vyNzSk0vBf4u5uVXP/zwraMXLDBBX58oHjyIkFKYUxy0fMgQs+TJJ+clr7n26ZTyl3yxWBjl3jZttoW8RkVhe6hzT2eqrPtyboXAaNDRYcAg8BHg2KXfJY0YY0RNjRg8ovmhTvizkzZdA2ZdCtPHT5y4YcELL2CXlSFdl1xbG15n59HmO6xngSApbTwdmr997O/1mnS2rXrQkBqDrsl7RXJenqxXCHDTjq79lGD4YqidAkaBiUAIWrp7aW87dAzo0h5mQhW9bTt2z7TL3N3ZRxYfq+kBPyyHNYOamwnzeRN5nkg1DiXM9uP3dCOlAAESQRhpulSBjijPmkM7+I/wgGiZP9KaLq1RmUK/6fezKB8cz+D42vXDg+C/CO88D85wqP9jqLsI3DoCYYPjwvG2BNGlxaCWEQ237PrGpUvTd/yU/rgZPGLqr8MfNcOLk2+/3bRcfrmoOeccDrz6ClGxiJRxsBPC2ux77KryqWqqxy+TJJMuoQ7IeFn6/Cz9QY7+IB77gizZIEcuyB+JAkouYirAHo1bPJfAnwepsWAKx4SOYwkmJYpse21LHc98ubvqL39G33euOeLTFfA5W0pULifCXI4Pn11DlM8iBgJbwMzaH3HehPE0JofwYbATLwoICgGB9gkihU9AYCsCofAdnyCpUCZARArD8ZrMAlsJ5FakfAy9bw7wbRD+kV7FGCMH1YqRk1oeff8ZFg4ABrDngnBhpJASlc/TvXkzRhWxLIElS6AlTL1yPMMb63l9/0Z6+jOoUFMM+2J/lBZY4iSxKo4MxgARCJuGVB0mqfHSOYpDigS/6IYeB0R0tGOLLYeUWdI67sr225+cEq64YfNh0BPj7RJIaYqdnWL/i79g2PxLSFansUoVVkhDjT+Yuyq/xu1ji6zPv8mvMy8TKU1X2M1Wf2vJBU5WYGOwKZnmwrIZhFKxTf0nGZHBoKE/AmXHPYiwTnj7tX5pL5w17stPh098qaa2wvR+8/NYNpgJsDhhzLioWETnshT3fYDtOgg0BB4EBVadf4DJgyeQFmkarHrmVMyiSqSpMOV8umwOVU6aysoyZCVkk11Y5Q6T0uMY64xmmj2DprCJ1zPr2Z2LXQsNRCI+VHsLJJaACEFKsAbYEgUjGFlptR7K5Vceun9hdvCytdjrwXwO9hitKXZ2YVmGKFAc2raZsnSKVN1gEpXlnO/M5Zc9L3Hj4CX4OkBpRZPTSENlPb0qw1DRyDu9u/DxuGjkDHryh3h+z2/Yk2tjQ7SdyFJgC7Bc0AYsA7aJrWMk1FWCklAM4oaq5FYRmGxFjWypq1zVDVcY22ABXAAqDTdLMEIgZMmXTaQI+/vwu7vZuqiVm4Yv4qnf/ZQxydH4YYAfBXhhgI4M0kiGuvWkRRUbP9jCr/a+xpaureRVEWMMaAGmxFoc9Rs42ASV18eaTjiQdGKNCwFSiraiMV9rHTJ2XfmU3xQfuLZdAnwL1vXBHieZFMZgtAatIYog1DHvz/8OPwoY447m/VwbReVRVB6e8vCUjxf4ZP08OtS4JoGrXVAWKAGBKI2Ab+IxEOCXOCqBNyK2AiIGXp6AlAuuLZ465HDPFZ+6l1nfsA9HjgdX5ZSi+pxzBZZLGMWgoxDCENZ3vcHr3RtpSTSxuWc7KlR4gU8h8A4foKg8CkGRMIzQKoKQEgtQA2Nprjgy1yKOYUlsYlnKOFKALSFhY2yL1uF1l025+aIRVqm4sBy6Zxmj7Xx+XsOFs41bVU2YLwqV9YkieO6qKtbnNzAlPZm0Vcm2Q28zJFGHF3r4KsAPA4IwIIgUvX6GA14X3cWeY1usw+NRmhUGepuh9oY45UkxUBzM4JQtLqoWXJ7I+lPzba88+OuND7yz+c03jk+u5X8B909y3a+OvOQShlxwAaovQ75jH5deth6qfZoTTdwx9jZ2ZfZSLstIO2lCHaJ0SKAVoQ7pyB/g3b7d7Orbc2KrZWmwAGniZOtG0DYdzvsl6EJ8NCG4rk4I0/GB2rRh/Q9yL/1k9cG3t7TT19kFBNbR8hyw1sN2O4raee+9Udnt26vdyrSsGDGSH9buMDgh/X6/QAsmVY9nV/8e0olKfOPHFVH7BCagT/bRrXroDXpL4EoszYkFSBjIthjZcpOokgHjRSG4uPDBzpefW7Niwx1Xf6n31bVr8+179uPnM3F1OrGfFnFVZ1AtNLfC+FEwrQYmP7Bi6IVU+yUTGxYPX4glbIJIUZsYhB/5qIQirPDplAd532vjg6590GnHvixO1mMCMjJOx0wxatyjKnx3/RP6zZefe//ZH71FMdMJ+KWoMP/tJeCI0imLmz9cHGHx/fr3qPGONDTS4eYRS9ic2cbEQeOIKhShrVBG0RP28KG/j3bVHkvLWNBrlYrJcWQZxFv17eZvOudDJgPk47xA+LHuiEeFS1DiLCCptS0sA44+HEjK8nm5+AqzmqaxO3yXoWYooR8SEhKEAaGK4rQGkNJQpsGT4ImYjwJt7CALmZ2l/KFP62J7EtKA5uv1EU4AbklewoCreV/vpq44iKRVRr/XjxQSZUJ8FRCpENRxNzVLQ6pkQ1/EFdDSYBMdtd8Z+lizN4xNm4ygPDJY2hAKiCw2ZjailKK9sA9PeRTDIkEYEIX6qDx9FEclW7raUBHGmURZ4el8S7E+1qqtBUm6dh39FeNJR8OoCARGGAQCIejT/QxN1NMRdOBIB88UKeg8nvE5cpE9zAZhBJYR7KnSvFb9EG97d7Iz2/exP8yf5gElk8fMZlFwD62ZaVSGg0lEoLUZ6jaSkAmh0UQmoj/Kko2yh+98CAQGKDiaA4l2Xq35GY/2fw8692OJCFtG+NEZBw0VLuQCC3CZOWwys9VljNdfYWJfAxWKtKk2EikcaZOPChR0AYQxOEawvxzeqvhXdoSr+LncQPeBDwFF0tZ4p+Udpwn6RM0ngHKuHHMtC3L30trTgB05VXYazwT4oTL0Oj7rhvyWx8Pl7Nu3HSiWUpr+fTf+Q0APJCgZHyBZzcKGy5mXW+iOEVfpvDkYbree4enk87zb9ibgYUtFqDV/IAnOHAnAxbaqGJ5sIBeFHPQ6S7k+OL6qnV0kxUBFdc6wUj7Z9F9B6JURgaQPpAAAAABJRU5ErkJggg==")
@ClassNamePlural("DomainObjects")
@DefaultAttributeGroupName("Basisdaten")
@OrderedAttributeGroups({ "Basisdaten", "System" })
@RelationDefinition(where = "1 = 0")

//@AccessLists(rolesLists = { // @AccessListRoles(accessType =
// AtomConfig.accessLinkage, value = { "*" }),
//@AccessListRoles(accessTypes = AtomConfig.accessReadWrite, value = { "Administrator" }) })
// orgEinheitenLists = { @AccessListOrgEinheiten(accessType = "accessReadWrite",
// value = { "Online-Services" }, onlyLeiter = false, onlyHauptrolle = false) }
public class DomainObject implements Serializable, com.google.gwt.user.client.rpc.IsSerializable {

	@AnalyzerIgnore
	private static final long serialVersionUID = 8875224958340977068L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer objectID;

	@AttributeDisplayName("Object ID")
	@AttributeGroup("System")
	@AttributePlacement(value = 9.9, type = AttributePlacement.defaultType)
	@RelationEssential
	public Integer getObjectID() {
		return objectID;
	}

	/**
	 * This field is a defined to be a computed field. There must not be
	 * a setter allowing to set it's value. It is not defined as transient
	 * merly as a mean to allow it's usage by the quicksearch feature directly
	 * in the database.
	 */
	@Column(length = 300)
	@RelationEssential
	@AttributeDisplayName("Schnellsuche")
	protected String stringRepresentation;

	@HideFromListGui
	@AttributeGroup("System")
	@AttributePlacement(value = 30.0, type = AttributePlacement.inGroupType)
	@javax.persistence.Transient
	@RelationEssential
	private Boolean completelyLoaded;

	@AnalyzerIgnore
	public void setCompletelyLoaded(Boolean completelyLoaded) {
		this.completelyLoaded = completelyLoaded;
	}

	public Boolean getCompletelyLoaded() {
		return completelyLoaded;
	}

	// @SuppressWarnings("unused")
	// required for hibernate
	// private void setObjectID(Long objectID) {
	// this.objectID = objectID;
	// }

	@HideFromDetailGui
	@HideFromListGui
	@AttributePlacement(10020)
	@AttributeGroup("System")
	@RelationEssential
	public String getConcreteClass() {
		// AtomTools.log(logLevel, message, caller)
		// String className = this.toString();
		// int atIndex = className.indexOf("@");
		// if(atIndex != -1)
		// return className.substring(0, className.indexOf("@"));
		// else
		// return className;
		return this.getClass().getName();
	}

	@AttributeGroup("System")
	@AttributePlacement(value = 50.0, type = AttributePlacement.inGroupType)
	@RelationEssential
	public String getConcreteClassShort() {
		String concreteClass = getConcreteClass();
		return concreteClass.substring(concreteClass.lastIndexOf('.') + 1);
	}

	@AttributePlacement(10010)
	@AttributeGroup("System")
	@RelationEssential
	public String getStringRepresentation() {
		stringRepresentation = getConcreteClassShort() + " " + getObjectID();
		return stringRepresentation;
	}
	
	@AnalyzerIgnore
	public static String getListQuery(DomainClass domainClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString) {
		return null;
	}

	@AnalyzerIgnore
	public static String getListCountQuery(DomainClass domainClass, int fromRow, int pageSize, ArrayList<DataFilter> filters,
			ArrayList<DataSorter> sorters, String searchString) {
		return null;
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public boolean isUserRelated(ClientSession session) {
		return false;
	}

	public boolean isUserRelated(ClientSession session, String relationType) {
		
		// backwards compatibility for DomainObjects that only know 
		// the defaultRelation and override the method without type
		if(RelationDefinition.defaultRelation.equals(relationType))
			return isUserRelated(session);
		else if(RelationDefinition.noRelationRequired.equals(relationType))
			return true;
		else
			return false;
	}

	/*
	 * return "SELECT * FROM " + getQueryCommonPart(domainClass, fromRow,
	 * pageSize, filters, sorters, searchString); return "SELECT count(*) FROM "
	 * + getQueryCommonPart(domainClass, fromRow, pageSize, filters, sorters,
	 * searchString);
	 * 
	 * protected static String getQueryCommonPart(DomainClass domainClass, int
	 * fromRow, int pageSize, ArrayList<DataFilter> filters,
	 * ArrayList<DataSorter> sorters, String searchString) { return
	 * domainClass.getTableName() + getJoins(domainClass) + ""; }
	 * 
	 * protected static String getJoins(DomainClass domainClass) { DomainClass
	 * superClass = domainClass.getSuperClass(); if (superClass == null) return
	 * ""; else { return " INNER JOIN " + superClass.getTableName() + " ON " +
	 * domainClass.getTableName() + ".objectID = " + superClass.getTableName() +
	 * ".objectID " + getJoins(superClass); } }
	 */

	@javax.persistence.Transient
	@AnalyzerIgnore
	private HashMap<String, Integer> nullReasons;

	@AnalyzerIgnore
	public HashMap<String, Integer> getNullReasons() {
		if (nullReasons == null)
			nullReasons = new HashMap<String, Integer>();
		return nullReasons;
	}

	@AnalyzerIgnore
	public void addNullReasons(String attribute, Integer reason) {
		if (nullReasons == null)
			nullReasons = new HashMap<String, Integer>();
		nullReasons.put(attribute, reason);
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return this.getObjectID() == null;
		
		if(!(obj instanceof DomainObject))
			return false;
		
		DomainObject other = (DomainObject) obj;
		
		if(this.getObjectID() == null && other.getObjectID() == null && this.getConcreteClass().equals(other.getConcreteClass()))
			return true;
		
		return this.getObjectID() != null && this.getObjectID().equals(other.getObjectID());
	}

	@Override
	public int hashCode() {
		if (this.getObjectID() != null)
			return this.getObjectID();

		else
			return super.hashCode();
	}
	
	@AnalyzerIgnore
	@OneToMany(mappedBy = "representedInstance", fetch = FetchType.LAZY) //, orphanRemoval = true)  CascadeType.ALL 
//	@OnDelete(action = OnDeleteAction.CASCADE)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@GwtTransient
	private Set<FrameVisit> frameVisits;

	@AnalyzerIgnore
	public Set<FrameVisit> getFrameVisits() {
		return frameVisits;
	}
	
	@AnalyzerIgnore
	@OneToMany(mappedBy = "instance", fetch = FetchType.LAZY) //, orphanRemoval = true)  CascadeType.ALL
//	@OnDelete(action = OnDeleteAction.CASCADE)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@GwtTransient
	private Set<ClipBoardEntry> clipBoardEntries;

	@AnalyzerIgnore
	public Set<ClipBoardEntry> getClipBoardEntries() {
		return clipBoardEntries;
	}

	@AnalyzerIgnore
	@javax.persistence.Transient
	private Set<String> userPermissions;

	@AnalyzerIgnore
	public Set<String> getUserPermissions() {
		return userPermissions;
	}

	@AnalyzerIgnore
	public void setUserPermissions(Set<String> userPermissions) {
//		if(userPermissions != null && userPermissions.size() == 1 && AtomConfig.accessCreateNew.equals(userPermissions.iterator().next()))
//			AtomTools.log(Level.INFO, "conditional breakpoint workaround", this);
//		if(userPermissions != null && userPermissions.size() == 0)
//			AtomTools.log(Level.INFO, "conditional breakpoint workaround2", this);
		this.userPermissions = userPermissions;
	}

	public void prepareSave(ClientSession session) {
	}

	public void prepareForClient() {
	}
	
	public void onDetailViewOpened() {
	}
	
	@com.google.gwt.user.client.rpc.GwtTransient
	@javax.persistence.Transient
	@AnalyzerIgnore
	protected transient HashMap<String,LinkedHashSet<String>> shownFields = null;

	@AnalyzerIgnore
	public static String showAllButListed = "all but those listed here!";
	@AnalyzerIgnore
	public static String autoDetectAttributeGroup = "autoDetectAttributeGroup";

	@AnalyzerIgnore
	private void initShownFields(String group, boolean hide, int initialLength) {
		if(shownFields == null)
			shownFields = new HashMap<String, LinkedHashSet<String>>(1);
		
		LinkedHashSet<String> groupFields = new LinkedHashSet<String>(initialLength + (hide ? 1 : 0));
		if(hide)
			groupFields.add(showAllButListed);
		
		shownFields.put(group, groupFields);
	}
	
	@AnalyzerIgnore
	public void modifyVisibleFields(String group, boolean hide, boolean onlyThose, String... fields) {
		
		if(group == null || group.length() == 0)
			group = autoDetectAttributeGroup;
		
		if(onlyThose || shownFields == null || 
				shownFields.get(group) == null || 
				shownFields.get(group).size() == 0 || 
				(shownFields.get(group).size() == 1 && showAllButListed.equals(shownFields.get(group).iterator().next())) ) {
			if(!hide && !onlyThose)
				return;
			
			initShownFields(group, hide, fields.length);
		}

		if(fields == null || fields.length <= 0) {
			return;
		}
		
		if(hide == (shownFields.size() > 0 && 
				shownFields.get(group) != null &&
				shownFields.get(group).size() > 0 && 
				showAllButListed.equals(shownFields.get(group).iterator().next())
				)
		  ) {
			Collections.addAll(shownFields.get(group), fields);
		}
		else if(shownFields.size() > 0) {
			for(String field : fields) {
				shownFields.get(group).remove(field);
			}
		}
//		notifyVisibleFieldsWatchers();
	}

	@AnalyzerIgnore
	public void modifyVisibleFields(boolean hide, boolean onlyThose, String... fields) {
		modifyVisibleFields(autoDetectAttributeGroup, hide, onlyThose, fields);
	}
	
	@AnalyzerIgnore
	public void hideFields(String group, boolean onlyThose, String... fields) {
		modifyVisibleFields(group, true, onlyThose, fields);
	}

	@AnalyzerIgnore
	public void showFields(String group, boolean onlyThose, String... fields) {
		modifyVisibleFields(group, false, onlyThose, fields);
	}
	
	@AnalyzerIgnore
	public void hideFields(boolean onlyThose, String... fields) {
		modifyVisibleFields(true, onlyThose, fields);
	}

	@AnalyzerIgnore
	public void showFields(boolean onlyThose, String... fields) {
		modifyVisibleFields(false, onlyThose, fields);
	}

	@AnalyzerIgnore
	public void showAllFields() {
		shownFields = null;
		notifyVisibleFieldsWatchers();
	}

	@AnalyzerIgnore
	public HashMap<String, LinkedHashSet<String>> getShownFields() {
		return shownFields;
	}

	@AnalyzerIgnore
	public void setShownFields(HashMap<String, LinkedHashSet<String>> shownFields) {
		this.shownFields = shownFields;
	}
	
	@AnalyzerIgnore
	public String[] getShownFields(String group) {
		if(shownFields == null)
			return new String[] {};
		else
			return shownFields.get(group).toArray(new String[] {});
	}
	
	@com.google.gwt.user.client.rpc.GwtTransient
	@javax.persistence.Transient
	@AnalyzerIgnore
	private HashSet<Notifiable<HashMap<String,LinkedHashSet<String>>>> watchers = null;
	
	@AnalyzerIgnore
	public boolean registerWatcher(Notifiable<HashMap<String,LinkedHashSet<String>>> watcher) {
		if(watchers == null) {
			watchers = new HashSet<Notifiable<HashMap<String,LinkedHashSet<String>>>>();
		}
		return watchers.add(watcher);
	}

	@AnalyzerIgnore
	public boolean unregisterWatcher(Notifiable<HashMap<String, LinkedHashSet<String>>> shownFieldsNotifiable) {
		if(watchers != null) {
			return watchers.remove(shownFieldsNotifiable);
		}
		return false;
	}

	@AnalyzerIgnore
	protected void notifyVisibleFieldsWatchers() {
		if(watchers != null)
		for(Notifiable<HashMap<String,LinkedHashSet<String>>> watcher : watchers) {
			watcher.doNotify(shownFields);
		}
	}
}
