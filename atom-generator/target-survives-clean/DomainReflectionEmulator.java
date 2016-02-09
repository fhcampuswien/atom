package at.ac.fhcampuswien.atom.shared;

import at.ac.fhcampuswien.atom.shared.domain.*;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class DomainReflectionEmulator {
	
	private DomainReflectionEmulator() {
	}

	public static String getAttributeValueAsString(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject) {
		Object value = getAttributeValue(domainClass, domainClassAttribute, domainObject);
		String stringValue = "empty";
		if (value != null) {
			stringValue = value.toString();
		}
		return stringValue;
	}

	public static Object getAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject) {
		String className = domainClass.getName();
		String attributeName = domainClassAttribute.getName();
		if ("at.ac.fhcampuswien.atom.shared.domain.DomainObject"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.DomainObject) domainObject)
						.getCompletelyLoaded();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.DomainObject) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.DomainObject) domainObject)
						.getConcreteClassShort();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.DomainObject) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.DomainObject) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PersistentString"
				.equals(className)) {
			if ("owner".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getOwner();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getCompletelyLoaded();
			if ("ownersAttribute".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getOwnersAttribute();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getConcreteClassShort();
			if ("value".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getValue();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.FeaturedObject"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getCompletelyLoaded();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getCreationUser();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getLastModifiedDate();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getUpdateUser();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getCreationDate();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FeaturedObject) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry"
				.equals(className)) {
			if ("owner".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getOwner();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getCompletelyLoaded();
			if ("instance".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getInstance();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getCreationUser();
			if ("added".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getAdded();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getLastModifiedDate();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getUpdateUser();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getCreationDate();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getConcreteClass();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getStringRepresentation();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.FrameVisit"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getCompletelyLoaded();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getCreationUser();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getLastModifiedDate();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getConcreteClassShort();
			if ("lastVisit".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getLastVisit();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getUpdateUser();
			if ("filters".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getFilters();
			if ("FrameHashCode".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getFrameHashCode();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getCreationDate();
			if ("representedClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getRepresentedClass();
			if ("representedInstance".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getRepresentedInstance();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getConcreteClass();
			if ("nameOfRepresentedClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getNameOfRepresentedClass();
			if ("frameShortTitle".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getFrameShortTitle();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getStringRepresentation();
			if ("frameType".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getFrameType();
			if ("simpleSearch".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getSimpleSearch();
			if ("representedSearchString".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getRepresentedSearchString();
			if ("visitor".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getVisitor();
			if ("HistoryString".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getHistoryString();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getCompletelyLoaded();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getCreationUser();
			if ("veranstaltungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getVeranstaltungen();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getLastModifiedDate();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getUpdateUser();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getTyp();
			if ("generierungs_id".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getGenerierungs_id();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getCreationDate();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getConcreteClass();
			if ("ort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getOrt();
			if ("abteilung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getAbteilung();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getNode();
			if ("kooperationsvereinbarungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getKooperationsvereinbarungen();
			if ("name_de".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getName_de();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getStringRepresentation();
			if ("staat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getStaat();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getObjectID();
			if ("name_en".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.getName_en();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Veranstaltung"
				.equals(className)) {
			if ("organisierende_oe".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getOrganisierende_oe();
			if ("vortraege".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getVortraege();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getCreationUser();
			if ("ende".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getEnde();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getConcreteClassShort();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getTyp();
			if ("beschreibung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getBeschreibung();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getConcreteClass();
			if ("titel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getTitel();
			if ("land".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getLand();
			if ("beteiligteOEs".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getBeteiligteOEs();
			if ("beginn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getBeginn();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getCompletelyLoaded();
			if ("art".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getArt();
			if ("teilnehmerInnenKreis".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getTeilnehmerInnenKreis();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getLastModifiedDate();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getUpdateUser();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getCreationDate();
			if ("url".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getUrl();
			if ("ort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getOrt();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getNode();
			if ("typ_beschreibung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getTyp_beschreibung();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getStringRepresentation();
			if ("organisierendeEEs".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getOrganisierendeEEs();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Publikation"
				.equals(className)) {
			if ("titelBuch".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTitelBuch();
			if ("oestat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getOestat();
			if ("typAkadTitel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTypAkadTitel();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getConcreteClassShort();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTyp();
			if ("konferenzDatum".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getKonferenzDatum();
			if ("pubMedID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPubMedID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getConcreteClass();
			if ("institution".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getInstitution();
			if ("titel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTitel();
			if ("linkVolltext".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getLinkVolltext();
			if ("veroeffentlichung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getVeroeffentlichung();
			if ("seiten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getSeiten();
			if ("orgeinheiten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getOrgeinheiten();
			if ("band".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getBand();
			if ("personenTyp".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPersonenTyp();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getCompletelyLoaded();
			if ("linkSuche".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getLinkSuche();
			if ("anmerkungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getAnmerkungen();
			if ("erfasstAm".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getErfasstAm();
			if ("sprache".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getSprache();
			if ("erfasstRefWorks".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getErfasstRefWorks();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getCreationDate();
			if ("verlag".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getVerlag();
			if ("linkAbstract".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getLinkAbstract();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getNode();
			if ("hauptforschungsgebiet".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getHauptforschungsgebiet();
			if ("verknuepfungString".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getVerknuepfungString();
			if ("issn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getIssn();
			if ("heft".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getHeft();
			if ("doi".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getDoi();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getCreationUser();
			if ("isbn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getIsbn();
			if ("peerKommentar".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPeerKommentar();
			if ("link".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getLink();
			if ("konferenzName".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getKonferenzName();
			if ("personen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPersonen();
			if ("peerReviewed".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPeerReviewed();
			if ("openAccess".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getOpenAccess();
			if ("konferenzDatumEnde".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getKonferenzDatumEnde();
			if ("correspondingAuthor".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getCorrespondingAuthor();
			if ("herausgeber".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getHerausgeber();
			if ("personenExtern".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getPersonenExtern();
			if ("titelZeitschrift".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTitelZeitschrift();
			if ("studienjahr".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getStudienjahr();
			if ("erscheinungsort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getErscheinungsort();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getLastModifiedDate();
			if ("titelAnmerkungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getTitelAnmerkungen();
			if ("zeitpunkt".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getZeitpunkt();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getUpdateUser();
			if ("konferenzOrt".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getKonferenzOrt();
			if ("studiengaenge".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getStudiengaenge();
			if ("auflage".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getAuflage();
			if ("quelle".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getQuelle();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getStringRepresentation();
			if ("erscheinungsjahr".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getErscheinungsjahr();
			if ("kurzfassung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getKurzfassung();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy"
				.equals(className)) {
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getCreationUser();
			if ("positiv".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getPositiv();
			if ("verbessert".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getVerbessert();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getConcreteClassShort();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getConcreteClass();
			if ("organisation3".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getOrganisation3();
			if ("organisation2".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getOrganisation2();
			if ("inhalte1".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getInhalte1();
			if ("inhalte2".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getInhalte2();
			if ("organisation1".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getOrganisation1();
			if ("gesamt".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getGesamt();
			if ("leiter".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getLeiter();
			if ("inhalte3".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getInhalte3();
			if ("referenz".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getReferenz();
			if ("sonstiges".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getSonstiges();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getCompletelyLoaded();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getLastModifiedDate();
			if ("vorlesung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getVorlesung();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getUpdateUser();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getCreationDate();
			if ("unterricht2".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getUnterricht2();
			if ("unterricht3".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getUnterricht3();
			if ("unterricht1".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getUnterricht1();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getStringRepresentation();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Message".equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getCompletelyLoaded();
			if ("senderAddress".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getSenderAddress();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getCreationUser();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getLastModifiedDate();
			if ("testDoubleField".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getTestDoubleField();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getUpdateUser();
			if ("nextMessage".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getNextMessage();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getTyp();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getCreationDate();
			if ("binaryContent".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getBinaryContent();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getConcreteClass();
			if ("persistentStrings".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getPersistentStrings();
			if ("longText".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getLongText();
			if ("previousMessages".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getPreviousMessages();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getStringRepresentation();
			if ("text".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getText();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getObjectID();
			if ("multiSelect".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.getMultiSelect();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung"
				.equals(className)) {
			if ("foerderbetrag".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getFoerderbetrag();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getCreationUser();
			if ("endDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getEndDate();
			if ("foerderung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getFoerderung();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getConcreteClassShort();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getTyp();
			if ("dauerInLE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getDauerInLE();
			if ("kurskosten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getKurskosten();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getConcreteClass();
			if ("kostenstelle".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getKostenstelle();
			if ("zeitaufwand".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getZeitaufwand();
			if ("dauerInTagen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getDauerInTagen();
			if ("sysmodified".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getSysmodified();
			if ("reisekosten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getReisekosten();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getCompletelyLoaded();
			if ("art".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getArt();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getLastModifiedDate();
			if ("typBeschreibung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getTypBeschreibung();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getUpdateUser();
			if ("abschluss".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getAbschluss();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getCreationDate();
			if ("veranstalter".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getVeranstalter();
			if ("syscreated".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getSyscreated();
			if ("kostentraeger".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getKostentraeger();
			if ("person".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getPerson();
			if ("name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getName();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getStringRepresentation();
			if ("durchfuehrendeOE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getDurchfuehrendeOE();
			if ("gesamtkosten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getGesamtkosten();
			if ("veranstalterTyp".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getVeranstalterTyp();
			if ("startDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getStartDate();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getCompletelyLoaded();
			if ("typ_erklaerung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getTyp_erklaerung();
			if ("art".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getArt();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getCreationUser();
			if ("ende".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getEnde();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getLastModifiedDate();
			if ("vertragsAbschluss".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getVertragsAbschluss();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getUpdateUser();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getTyp();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getCreationDate();
			if ("beschreibung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getBeschreibung();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getConcreteClass();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getNode();
			if ("titel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getTitel();
			if ("art_erklaerung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getArt_erklaerung();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getStringRepresentation();
			if ("orgeinheiten".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getOrgeinheiten();
			if ("vereinbarungsPartner".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getVereinbarungsPartner();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getObjectID();
			if ("beginn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.getBeginn();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Vortrag".equals(className)) {
			if ("beteiligte_Personen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getBeteiligte_Personen();
			if ("datum".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getDatum();
			if ("veranstaltung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getVeranstaltung();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getCompletelyLoaded();
			if ("art".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getArt();
			if ("entsendende_OE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getEntsendende_OE();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getCreationUser();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getLastModifiedDate();
			if ("qualitaets_typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getQualitaets_typ();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getUpdateUser();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getTyp();
			if ("art_sonstige_erklaerung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getArt_sonstige_erklaerung();
			if ("externe_veranstaltung_veranstalter".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getExterne_veranstaltung_veranstalter();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getCreationDate();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getConcreteClass();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getNode();
			if ("titel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getTitel();
			if ("vortragende_personen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getVortragende_personen();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getStringRepresentation();
			if ("externe_veranstaltung_name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getExterne_veranstaltung_name();
			if ("qualitaets_typ_erklaerung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getQualitaets_typ_erklaerung();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getCompletelyLoaded();
			if ("vergeben_durch".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getVergeben_durch();
			if ("typ_erklaerung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getTyp_erklaerung();
			if ("creationUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getCreationUser();
			if ("lastModifiedDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getLastModifiedDate();
			if ("preistraeger".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getPreistraeger();
			if ("verleihungsdatum".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getVerleihungsdatum();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getConcreteClassShort();
			if ("updateUser".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getUpdateUser();
			if ("typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getTyp();
			if ("creationDate".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getCreationDate();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getConcreteClass();
			if ("studiengang".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getStudiengang();
			if ("student_name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getStudent_name();
			if ("node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getNode();
			if ("name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getName();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getStringRepresentation();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.StoreableUser"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getCompletelyLoaded();
			if ("Per_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getPer_ID();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getConcreteClassShort();
			if ("Haupt_OrE_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getHaupt_OrE_ID();
			if ("updatedInstances".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getUpdatedInstances();
			if ("createdInstances".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getCreatedInstances();
			if ("OrE_ID_List".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getOrE_ID_List();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalPerson"
				.equals(className)) {
			if ("Mtb_Bank".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Bank();
			if ("vortraege".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getVortraege();
			if ("Email".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getEmail();
			if ("Mtb_Fachbereiche".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Fachbereiche();
			if ("Mtb_Funktion_en".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Funktion_en();
			if ("Wohnsitz_Strasse".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getWohnsitz_Strasse();
			if ("Mtb_Anforderung_Standard_PC".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Standard_PC();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getConcreteClassShort();
			if ("Per_Nachname".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Nachname();
			if ("Per_Titel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Titel();
			if ("tel_privat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getTel_privat();
			if ("updatedInstances".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getUpdatedInstances();
			if ("Mtb_Raumnummer".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Raumnummer();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getConcreteClass();
			if ("Mtb_Konto".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Konto();
			if ("Mtb_Anford_Vorg_Telefonklappe".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Vorg_Telefonklappe();
			if ("Per_SVNr".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_SVNr();
			if ("Per_Titel_En".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Titel_En();
			if ("Firmenadresse_PLZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getFirmenadresse_PLZ();
			if ("Mtb_Anforderung_Email".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Email();
			if ("Mtb_Anforderung_AnzeigeAufHomepage".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_AnzeigeAufHomepage();
			if ("Per_GeburtsDat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_GeburtsDat();
			if ("OrE_ID_List".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getOrE_ID_List();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getCompletelyLoaded();
			if ("Mtb_Anforderung_Notebook".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Notebook();
			if ("Firmenadresse_Ort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getFirmenadresse_Ort();
			if ("Mtb_Anford_Vorg_Standard_PC".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Vorg_Standard_PC();
			if ("Mtb_Anford_Vorg_Telefonapparat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Vorg_Telefonapparat();
			if ("publikationen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPublikationen();
			if ("createdInstances".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getCreatedInstances();
			if ("Mtb_Habilitation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Habilitation();
			if ("Mtb_VordienstzeitenInJahren".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_VordienstzeitenInJahren();
			if ("tel_fax".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getTel_fax();
			if ("Per_Vorname".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Vorname();
			if ("Mtb_Steuernummer".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Steuernummer();
			if ("tel_mobil".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getTel_mobil();
			if ("Per_Titel_Nachgestellt_En".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Titel_Nachgestellt_En();
			if ("Wohnsitz_Anmerkung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getWohnsitz_Anmerkung();
			if ("Haupt_OrE_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getHaupt_OrE_ID();
			if ("Mtb_Anforderung_Tuerschild".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Tuerschild();
			if ("Mtb_Anford_Kom_EDV_Sonder".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Kom_EDV_Sonder();
			if ("Wohnsitz_PLZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getWohnsitz_PLZ();
			if ("Mtb_Anford_Kom_Moebel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Kom_Moebel();
			if ("Mtb_Anforderung_Telefonklappe".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Telefonklappe();
			if ("Per_Status".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Status();
			if ("Mtb_BehinderungLiegtVor".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_BehinderungLiegtVor();
			if ("Mtb_BLZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_BLZ();
			if ("Vtr_HauptberufBIS".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getVtr_HauptberufBIS();
			if ("Mtb_Anforderung_EDV_Sonder".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_EDV_Sonder();
			if ("Per_Berufsbezeichnung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Berufsbezeichnung();
			if ("Per_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_ID();
			if ("preiseAuszeichnungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPreiseAuszeichnungen();
			if ("Email2".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getEmail2();
			if ("tel_firma".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getTel_firma();
			if ("Per_Geschlecht".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Geschlecht();
			if ("Wohnsitz_Staat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getWohnsitz_Staat();
			if ("Wohnsitz_Ort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getWohnsitz_Ort();
			if ("Firmenadresse_Anmerkung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getFirmenadresse_Anmerkung();
			if ("Personen_Typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPersonen_Typ();
			if ("Mtb_Anforderung_Moebel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Moebel();
			if ("CIS_Node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getCIS_Node();
			if ("Per_Nationalitaet".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Nationalitaet();
			if ("tel_firma_mobil".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getTel_firma_mobil();
			if ("Mtb_Funktion".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Funktion();
			if ("Per_Titel_Nachgestellt".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getPer_Titel_Nachgestellt();
			if ("stellen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getStellen();
			if ("Mtb_HoechsteAbgeschlosseneAusbildung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_HoechsteAbgeschlosseneAusbildung();
			if ("Mtb_Sozialversicherung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Sozialversicherung();
			if ("dienstvertraege".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getDienstvertraege();
			if ("studiengaenge".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getStudiengaenge();
			if ("Firmenadresse_Strasse".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getFirmenadresse_Strasse();
			if ("Mtb_Anforderung_Telefonapparat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anforderung_Telefonapparat();
			if ("Firmenadresse_Staat".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getFirmenadresse_Staat();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getStringRepresentation();
			if ("Mtb_Visitenkarte".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Visitenkarte();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getObjectID();
			if ("Mtb_Anford_Vorg_Notebook".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.getMtb_Anford_Vorg_Notebook();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung"
				.equals(className)) {
			if ("Set_Name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getSet_Name();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getCompletelyLoaded();
			if ("Set_Krzz".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getSet_Krzz();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getConcreteClassShort();
			if ("Set_Beschreibung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getSet_Beschreibung();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getObjectID();
			if ("Set_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getSet_ID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang"
				.equals(className)) {
			if ("stG_EvaluierungVerpflichtend".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_EvaluierungVerpflichtend();
			if ("stG_BakkArbeiten_Anzahl_Pruefungsrelevant"
					.equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeiten_Anzahl_Pruefungsrelevant();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getConcreteClassShort();
			if ("stG_Bank".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Bank();
			if ("stG_BakkArbeit3_pruefungsrelevant".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit3_pruefungsrelevant();
			if ("stG_BakkArbeit2_SichtbarStn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit2_SichtbarStn();
			if ("stG_BakkArbeit1_SichtbarStn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit1_SichtbarStn();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getConcreteClass();
			if ("stG_Art_Code".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Art_Code();
			if ("stG_BakkArbeit3_SichtbarStn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit3_SichtbarStn();
			if ("stG_Aktiv".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Aktiv();
			if ("stG_Krzz".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Krzz();
			if ("stG_CreditsGesamt".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_CreditsGesamt();
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getCompletelyLoaded();
			if ("stG_AkadGradDatum".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_AkadGradDatum();
			if ("stG_Leiter_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Leiter_ID();
			if ("stG_OBV_id".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_OBV_id();
			if ("stG_Tel".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Tel();
			if ("stG_Standortinfo".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Standortinfo();
			if ("stG_Fax".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Fax();
			if ("stG_BISForm".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BISForm();
			if ("stG_Email".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Email();
			if ("stG_Foerderungsvertrag_GZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Foerderungsvertrag_GZ();
			if ("stG_Kennzahl".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Kennzahl();
			if ("stG_Name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Name();
			if ("stG_Strasse".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Strasse();
			if ("stG_Regelstudiendauer".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Regelstudiendauer();
			if ("stG_Kontonummer".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Kontonummer();
			if ("vortragende".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getVortragende();
			if ("stG_Start_Sem".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Start_Sem();
			if ("stG_Annerkennungsbescheid_GZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Annerkennungsbescheid_GZ();
			if ("stG_BakkArbeit1_pruefungsrelevant".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit1_pruefungsrelevant();
			if ("stG_DeI_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_DeI_ID();
			if ("stG_PLZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_PLZ();
			if ("stG_BakkArbeiten_Anzahl".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeiten_Anzahl();
			if ("stG_AntragDatum".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_AntragDatum();
			if ("stG_BLZ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BLZ();
			if ("stG_Hausordnung".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Hausordnung();
			if ("stG_Ort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Ort();
			if ("stG_Color".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Color();
			if ("stG_OffiziellerName_Englisch".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_OffiziellerName_Englisch();
			if ("stG_OffiziellerName".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_OffiziellerName();
			if ("stG_Dep_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Dep_ID();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStringRepresentation();
			if ("stG_Sto_Krzz_Show".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Sto_Krzz_Show();
			if ("stG_Fernlehre".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_Fernlehre();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getObjectID();
			if ("stG_BakkArbeit2_pruefungsrelevant".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.getStG_BakkArbeit2_pruefungsrelevant();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getCompletelyLoaded();
			if ("orgeinheit".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getOrgeinheit();
			if ("OrS_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getOrS_ID();
			if ("istLeiter".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getIstLeiter();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getConcreteClassShort();
			if ("stellen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getStellen();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getObjectID();
			if ("OrS_Name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getOrS_Name();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getCompletelyLoaded();
			if ("OrP_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getOrP_ID();
			if ("person".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getPerson();
			if ("OrP_Beginn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getOrP_Beginn();
			if ("OrP_Hauptrolle".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getOrP_Hauptrolle();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getConcreteClassShort();
			if ("OrP_Ende".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getOrP_Ende();
			if ("stelle".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getStelle();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalUserRole"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getCompletelyLoaded();
			if ("Role_NameW".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getRole_NameW();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getConcreteClassShort();
			if ("Role_Name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getRole_Name();
			if ("Role_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getRole_ID();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getCompletelyLoaded();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getConcreteClassShort();
			if ("MVe_LetzterTag".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_LetzterTag();
			if ("Mve_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMve_ID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getConcreteClass();
			if ("MVe_HauptberuflichLehrend".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_HauptberuflichLehrend();
			if ("mitarbeiter".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMitarbeiter();
			if ("MVe_BeschaeftigungsAusmass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_BeschaeftigungsAusmass();
			if ("MVe_VertragsBeginn".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_VertragsBeginn();
			if ("MVe_VertragsEnde".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_VertragsEnde();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getStringRepresentation();
			if ("MVe_BeschaeftigungsArt2".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_BeschaeftigungsArt2();
			if ("MVe_BeschaeftigungsArt1".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_BeschaeftigungsArt1();
			if ("MVe_Wochenstunden".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_Wochenstunden();
			if ("MVe_Verwendungscode".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getMVe_Verwendungscode();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.getObjectID();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalFolder"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getCompletelyLoaded();
			if ("studiengaenge".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getStudiengaenge();
			if ("Fol_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getFol_ID();
			if ("Fol_Name".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getFol_Name();
			if ("childFolders".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getChildFolders();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getStringRepresentation();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getConcreteClassShort();
			if ("Fol_Parent".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getFol_Parent();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getObjectID();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.getConcreteClass();
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit"
				.equals(className)) {
			if ("completelyLoaded".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getCompletelyLoaded();
			if ("OrE_Typ".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_Typ();
			if ("OrE_CIS_Node".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_CIS_Node();
			if ("vortraege".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getVortraege();
			if ("OrE_Name_EN".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_Name_EN();
			if ("veranstaltungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getVeranstaltungen();
			if ("OrE_Name_Public_EN".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_Name_Public_EN();
			if ("publikationen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getPublikationen();
			if ("ConcreteClassShort".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getConcreteClassShort();
			if ("uebergeordneteOE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getUebergeordneteOE();
			if ("stellen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getStellen();
			if ("OrE_ID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_ID();
			if ("OrE_Name_Public_DE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_Name_Public_DE();
			if ("ConcreteClass".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getConcreteClass();
			if ("untergeordneteOEs".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getUntergeordneteOEs();
			if ("kooperationsvereinbarungen".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getKooperationsvereinbarungen();
			if ("stringRepresentation".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getStringRepresentation();
			if ("OrE_Name_DE".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getOrE_Name_DE();
			if ("objectID".equals(attributeName))
				return ((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.getObjectID();
		}
		throw new AtomException("Attribute " + className + "." + attributeName
				+ " not found. Could not get value!");

		//throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not get value!");
	}
	
	public static void setAttributeValue(DomainClass domainClass, DomainClassAttribute domainClassAttribute, DomainObject domainObject, Object value) {
		String className = domainClass.getName();
		String attributeName = domainClassAttribute.getName();
		if ("at.ac.fhcampuswien.atom.shared.domain.PersistentString"
				.equals(className)) {
			if ("owner".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.setOwner((at.ac.fhcampuswien.atom.shared.domain.DomainObject) value);
			if ("ownersAttribute".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.setOwnersAttribute((java.lang.String) value);
			if ("value".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PersistentString) domainObject)
						.setValue((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry"
				.equals(className)) {
			if ("owner".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.setOwner((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) value);
			if ("instance".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.setInstance((at.ac.fhcampuswien.atom.shared.domain.DomainObject) value);
			if ("added".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry) domainObject)
						.setAdded((java.util.Date) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.FrameVisit"
				.equals(className)) {
			if ("lastVisit".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.setLastVisit((java.util.Date) value);
			if ("representedInstance".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.setRepresentedInstance((at.ac.fhcampuswien.atom.shared.domain.DomainObject) value);
			if ("frameShortTitle".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.setFrameShortTitle((java.lang.String) value);
			if ("visitor".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.FrameVisit) domainObject)
						.setVisitor((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit"
				.equals(className)) {
			if ("veranstaltungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setVeranstaltungen((java.util.Set) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setTyp((java.lang.String) value);
			if ("generierungs_id".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setGenerierungs_id((java.lang.String) value);
			if ("ort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setOrt((java.lang.String) value);
			if ("abteilung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setAbteilung((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setNode((java.lang.Long) value);
			if ("kooperationsvereinbarungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setKooperationsvereinbarungen((java.util.Set) value);
			if ("name_de".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setName_de((java.lang.String) value);
			if ("staat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setStaat((java.lang.String) value);
			if ("name_en".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) domainObject)
						.setName_en((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Veranstaltung"
				.equals(className)) {
			if ("organisierende_oe".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setOrganisierende_oe((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("vortraege".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setVortraege((java.util.Set) value);
			if ("ende".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setEnde((java.util.Date) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setTyp((java.lang.String) value);
			if ("beschreibung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setBeschreibung((java.lang.String) value);
			if ("titel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setTitel((java.lang.String) value);
			if ("land".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setLand((java.lang.String) value);
			if ("beteiligteOEs".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setBeteiligteOEs((java.util.Set) value);
			if ("beginn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setBeginn((java.util.Date) value);
			if ("art".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setArt((java.lang.String) value);
			if ("teilnehmerInnenKreis".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setTeilnehmerInnenKreis((java.lang.String) value);
			if ("url".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setUrl((java.lang.String) value);
			if ("ort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setOrt((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setNode((java.lang.Long) value);
			if ("typ_beschreibung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setTyp_beschreibung((java.lang.String) value);
			if ("organisierendeEEs".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) domainObject)
						.setOrganisierendeEEs((java.util.Set) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Publikation"
				.equals(className)) {
			if ("titelBuch".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTitelBuch((java.lang.String) value);
			if ("oestat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setOestat((java.lang.String) value);
			if ("typAkadTitel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTypAkadTitel((java.lang.String) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTyp((java.lang.String) value);
			if ("konferenzDatum".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setKonferenzDatum((java.util.Date) value);
			if ("pubMedID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setPubMedID((java.lang.String) value);
			if ("institution".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setInstitution((java.lang.String) value);
			if ("titel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTitel((java.lang.String) value);
			if ("linkVolltext".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setLinkVolltext((java.lang.String) value);
			if ("veroeffentlichung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setVeroeffentlichung((java.util.Date) value);
			if ("seiten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setSeiten((java.lang.String) value);
			if ("orgeinheiten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setOrgeinheiten((java.util.Set) value);
			if ("band".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setBand((java.lang.String) value);
			if ("linkSuche".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setLinkSuche((java.lang.String) value);
			if ("anmerkungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setAnmerkungen((java.lang.String) value);
			if ("erfasstAm".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setErfasstAm((java.util.Date) value);
			if ("sprache".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setSprache((java.lang.String) value);
			if ("erfasstRefWorks".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setErfasstRefWorks((java.lang.Boolean) value);
			if ("verlag".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setVerlag((java.lang.String) value);
			if ("linkAbstract".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setLinkAbstract((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setNode((java.lang.Long) value);
			if ("hauptforschungsgebiet".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setHauptforschungsgebiet((java.lang.String) value);
			if ("verknuepfungString".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setVerknuepfungString((java.lang.String) value);
			if ("issn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setIssn((java.lang.String) value);
			if ("heft".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setHeft((java.lang.String) value);
			if ("doi".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setDoi((java.lang.String) value);
			if ("isbn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setIsbn((java.lang.String) value);
			if ("peerKommentar".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setPeerKommentar((java.lang.String) value);
			if ("link".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setLink((java.lang.String) value);
			if ("konferenzName".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setKonferenzName((java.lang.String) value);
			if ("personen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setPersonen((java.util.Set) value);
			if ("peerReviewed".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setPeerReviewed((java.lang.Boolean) value);
			if ("openAccess".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setOpenAccess((java.lang.Boolean) value);
			if ("konferenzDatumEnde".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setKonferenzDatumEnde((java.util.Date) value);
			if ("correspondingAuthor".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setCorrespondingAuthor((java.lang.String) value);
			if ("herausgeber".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setHerausgeber((java.lang.String) value);
			if ("personenExtern".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setPersonenExtern((java.lang.String) value);
			if ("titelZeitschrift".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTitelZeitschrift((java.lang.String) value);
			if ("studienjahr".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setStudienjahr((java.lang.String) value);
			if ("erscheinungsort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setErscheinungsort((java.lang.String) value);
			if ("titelAnmerkungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setTitelAnmerkungen((java.lang.String) value);
			if ("zeitpunkt".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setZeitpunkt((java.lang.String) value);
			if ("konferenzOrt".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setKonferenzOrt((java.lang.String) value);
			if ("studiengaenge".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setStudiengaenge((java.util.Set) value);
			if ("auflage".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setAuflage((java.lang.String) value);
			if ("quelle".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setQuelle((java.lang.String) value);
			if ("erscheinungsjahr".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setErscheinungsjahr((java.lang.Integer) value);
			if ("kurzfassung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Publikation) domainObject)
						.setKurzfassung((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy"
				.equals(className)) {
			if ("positiv".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setPositiv((java.lang.String) value);
			if ("verbessert".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setVerbessert((java.lang.String) value);
			if ("organisation3".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setOrganisation3((java.lang.Integer) value);
			if ("organisation2".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setOrganisation2((java.lang.Integer) value);
			if ("inhalte1".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setInhalte1((java.lang.Integer) value);
			if ("inhalte2".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setInhalte2((java.lang.Integer) value);
			if ("organisation1".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setOrganisation1((java.lang.Integer) value);
			if ("gesamt".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setGesamt((java.lang.Integer) value);
			if ("leiter".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setLeiter((java.lang.Integer) value);
			if ("inhalte3".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setInhalte3((java.lang.Integer) value);
			if ("referenz".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setReferenz((java.lang.String) value);
			if ("sonstiges".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setSonstiges((java.lang.String) value);
			if ("vorlesung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setVorlesung((java.lang.String) value);
			if ("unterricht2".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setUnterricht2((java.lang.Integer) value);
			if ("unterricht3".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setUnterricht3((java.lang.Integer) value);
			if ("unterricht1".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy) domainObject)
						.setUnterricht1((java.lang.Integer) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Message".equals(className)) {
			if ("senderAddress".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setSenderAddress((java.lang.String) value);
			if ("testDoubleField".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setTestDoubleField((java.lang.Double) value);
			if ("nextMessage".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setNextMessage((at.ac.fhcampuswien.atom.shared.domain.Message) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setTyp((java.lang.String) value);
			if ("binaryContent".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setBinaryContent((java.lang.String) value);
			if ("persistentStrings".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setPersistentStrings((java.util.Set) value);
			if ("longText".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setLongText((java.lang.String) value);
			if ("previousMessages".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setPreviousMessages((java.util.Set) value);
			if ("text".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setText((java.lang.String) value);
			if ("multiSelect".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Message) domainObject)
						.setMultiSelect((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung"
				.equals(className)) {
			if ("foerderbetrag".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setFoerderbetrag((java.lang.Float) value);
			if ("endDate".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setEndDate((java.util.Date) value);
			if ("foerderung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setFoerderung((java.lang.Boolean) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setTyp((java.lang.String) value);
			if ("dauerInLE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setDauerInLE((java.lang.Float) value);
			if ("kurskosten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setKurskosten((java.lang.Float) value);
			if ("kostenstelle".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setKostenstelle((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("zeitaufwand".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setZeitaufwand((java.lang.String) value);
			if ("dauerInTagen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setDauerInTagen((java.lang.Float) value);
			if ("sysmodified".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setSysmodified((java.util.Date) value);
			if ("reisekosten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setReisekosten((java.lang.Float) value);
			if ("art".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setArt((java.lang.String) value);
			if ("typBeschreibung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setTypBeschreibung((java.lang.String) value);
			if ("abschluss".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setAbschluss((java.lang.String) value);
			if ("veranstalter".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setVeranstalter((java.lang.String) value);
			if ("syscreated".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setSyscreated((java.util.Date) value);
			if ("kostentraeger".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setKostentraeger((java.lang.String) value);
			if ("person".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setPerson((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) value);
			if ("name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setName((java.lang.String) value);
			if ("durchfuehrendeOE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setDurchfuehrendeOE((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("gesamtkosten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setGesamtkosten((java.lang.Float) value);
			if ("veranstalterTyp".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setVeranstalterTyp((java.lang.String) value);
			if ("startDate".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung) domainObject)
						.setStartDate((java.util.Date) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung"
				.equals(className)) {
			if ("typ_erklaerung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setTyp_erklaerung((java.lang.String) value);
			if ("art".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setArt((java.lang.String) value);
			if ("ende".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setEnde((java.util.Date) value);
			if ("vertragsAbschluss".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setVertragsAbschluss((java.util.Date) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setTyp((java.lang.String) value);
			if ("beschreibung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setBeschreibung((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setNode((java.lang.Long) value);
			if ("titel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setTitel((java.lang.String) value);
			if ("art_erklaerung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setArt_erklaerung((java.lang.String) value);
			if ("orgeinheiten".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setOrgeinheiten((java.util.Set) value);
			if ("vereinbarungsPartner".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setVereinbarungsPartner((at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit) value);
			if ("beginn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung) domainObject)
						.setBeginn((java.util.Date) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Vortrag".equals(className)) {
			if ("beteiligte_Personen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setBeteiligte_Personen((java.lang.String) value);
			if ("datum".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setDatum((java.util.Date) value);
			if ("veranstaltung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setVeranstaltung((at.ac.fhcampuswien.atom.shared.domain.Veranstaltung) value);
			if ("art".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setArt((java.lang.String) value);
			if ("entsendende_OE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setEntsendende_OE((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("qualitaets_typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setQualitaets_typ((java.lang.String) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setTyp((java.lang.String) value);
			if ("art_sonstige_erklaerung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setArt_sonstige_erklaerung((java.lang.String) value);
			if ("externe_veranstaltung_veranstalter".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setExterne_veranstaltung_veranstalter((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setNode((java.lang.Long) value);
			if ("titel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setTitel((java.lang.String) value);
			if ("vortragende_personen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setVortragende_personen((java.util.Set) value);
			if ("externe_veranstaltung_name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setExterne_veranstaltung_name((java.lang.String) value);
			if ("qualitaets_typ_erklaerung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Vortrag) domainObject)
						.setQualitaets_typ_erklaerung((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung"
				.equals(className)) {
			if ("vergeben_durch".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setVergeben_durch((java.lang.String) value);
			if ("typ_erklaerung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setTyp_erklaerung((java.lang.String) value);
			if ("preistraeger".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setPreistraeger((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) value);
			if ("verleihungsdatum".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setVerleihungsdatum((java.util.Date) value);
			if ("typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setTyp((java.lang.String) value);
			if ("studiengang".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setStudiengang((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) value);
			if ("student_name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setStudent_name((java.lang.String) value);
			if ("node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setNode((java.lang.Long) value);
			if ("name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung) domainObject)
						.setName((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.StoreableUser"
				.equals(className)) {
			if ("Per_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.setPer_ID((java.lang.Integer) value);
			if ("updatedInstances".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.setUpdatedInstances((java.util.Set) value);
			if ("createdInstances".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.StoreableUser) domainObject)
						.setCreatedInstances((java.util.Set) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalPerson"
				.equals(className)) {
			if ("Mtb_Bank".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Bank((java.lang.String) value);
			if ("vortraege".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setVortraege((java.util.Set) value);
			if ("Email".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setEmail((java.lang.String) value);
			if ("Mtb_Fachbereiche".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Fachbereiche((java.lang.String) value);
			if ("Mtb_Funktion_en".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Funktion_en((java.lang.String) value);
			if ("Wohnsitz_Strasse".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setWohnsitz_Strasse((java.lang.String) value);
			if ("Mtb_Anforderung_Standard_PC".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Standard_PC((java.lang.Integer) value);
			if ("Per_Nachname".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Nachname((java.lang.String) value);
			if ("Per_Titel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Titel((java.lang.String) value);
			if ("tel_privat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setTel_privat((java.lang.String) value);
			if ("updatedInstances".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setUpdatedInstances((java.util.Set) value);
			if ("Mtb_Raumnummer".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Raumnummer((java.lang.Integer) value);
			if ("Mtb_Konto".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Konto((java.lang.String) value);
			if ("Mtb_Anford_Vorg_Telefonklappe".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Vorg_Telefonklappe((java.lang.String) value);
			if ("Per_SVNr".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_SVNr((java.lang.String) value);
			if ("Per_Titel_En".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Titel_En((java.lang.String) value);
			if ("Firmenadresse_PLZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setFirmenadresse_PLZ((java.lang.String) value);
			if ("Mtb_Anforderung_Email".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Email((java.lang.Boolean) value);
			if ("Mtb_Anforderung_AnzeigeAufHomepage".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_AnzeigeAufHomepage((java.lang.Boolean) value);
			if ("Per_GeburtsDat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_GeburtsDat((java.util.Date) value);
			if ("Mtb_Anforderung_Notebook".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Notebook((java.lang.Integer) value);
			if ("Firmenadresse_Ort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setFirmenadresse_Ort((java.lang.String) value);
			if ("Mtb_Anford_Vorg_Standard_PC".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Vorg_Standard_PC((java.lang.String) value);
			if ("Mtb_Anford_Vorg_Telefonapparat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Vorg_Telefonapparat((java.lang.String) value);
			if ("publikationen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPublikationen((java.util.Set) value);
			if ("createdInstances".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setCreatedInstances((java.util.Set) value);
			if ("Mtb_Habilitation".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Habilitation((java.lang.Boolean) value);
			if ("Mtb_VordienstzeitenInJahren".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_VordienstzeitenInJahren((java.lang.Integer) value);
			if ("tel_fax".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setTel_fax((java.lang.String) value);
			if ("Per_Vorname".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Vorname((java.lang.String) value);
			if ("Mtb_Steuernummer".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Steuernummer((java.lang.String) value);
			if ("tel_mobil".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setTel_mobil((java.lang.String) value);
			if ("Per_Titel_Nachgestellt_En".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Titel_Nachgestellt_En((java.lang.String) value);
			if ("Wohnsitz_Anmerkung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setWohnsitz_Anmerkung((java.lang.String) value);
			if ("Mtb_Anforderung_Tuerschild".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Tuerschild((java.lang.Boolean) value);
			if ("Mtb_Anford_Kom_EDV_Sonder".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Kom_EDV_Sonder((java.lang.String) value);
			if ("Wohnsitz_PLZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setWohnsitz_PLZ((java.lang.String) value);
			if ("Mtb_Anford_Kom_Moebel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Kom_Moebel((java.lang.String) value);
			if ("Mtb_Anforderung_Telefonklappe".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Telefonklappe((java.lang.Integer) value);
			if ("Per_Status".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Status((java.lang.Integer) value);
			if ("Mtb_BehinderungLiegtVor".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_BehinderungLiegtVor((java.lang.Boolean) value);
			if ("Mtb_BLZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_BLZ((java.lang.String) value);
			if ("Vtr_HauptberufBIS".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setVtr_HauptberufBIS((java.lang.Integer) value);
			if ("Mtb_Anforderung_EDV_Sonder".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_EDV_Sonder((java.lang.Boolean) value);
			if ("Per_Berufsbezeichnung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Berufsbezeichnung((java.lang.String) value);
			if ("Per_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_ID((java.lang.Integer) value);
			if ("preiseAuszeichnungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPreiseAuszeichnungen((java.util.Set) value);
			if ("Email2".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setEmail2((java.lang.String) value);
			if ("tel_firma".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setTel_firma((java.lang.String) value);
			if ("Per_Geschlecht".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Geschlecht((java.lang.Character) value);
			if ("Wohnsitz_Staat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setWohnsitz_Staat((java.lang.String) value);
			if ("Wohnsitz_Ort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setWohnsitz_Ort((java.lang.String) value);
			if ("Firmenadresse_Anmerkung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setFirmenadresse_Anmerkung((java.lang.String) value);
			if ("Personen_Typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPersonen_Typ((java.lang.String) value);
			if ("Mtb_Anforderung_Moebel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Moebel((java.lang.Boolean) value);
			if ("CIS_Node".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setCIS_Node((java.lang.Integer) value);
			if ("Per_Nationalitaet".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Nationalitaet((java.lang.String) value);
			if ("tel_firma_mobil".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setTel_firma_mobil((java.lang.String) value);
			if ("Mtb_Funktion".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Funktion((java.lang.String) value);
			if ("Per_Titel_Nachgestellt".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setPer_Titel_Nachgestellt((java.lang.String) value);
			if ("stellen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setStellen((java.util.Set) value);
			if ("Mtb_HoechsteAbgeschlosseneAusbildung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_HoechsteAbgeschlosseneAusbildung((java.lang.Integer) value);
			if ("Mtb_Sozialversicherung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Sozialversicherung((java.lang.String) value);
			if ("dienstvertraege".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setDienstvertraege((java.util.Set) value);
			if ("studiengaenge".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setStudiengaenge((java.util.Set) value);
			if ("Firmenadresse_Strasse".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setFirmenadresse_Strasse((java.lang.String) value);
			if ("Mtb_Anforderung_Telefonapparat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anforderung_Telefonapparat((java.lang.Integer) value);
			if ("Firmenadresse_Staat".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setFirmenadresse_Staat((java.lang.String) value);
			if ("Mtb_Visitenkarte".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Visitenkarte((java.lang.Integer) value);
			if ("Mtb_Anford_Vorg_Notebook".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) domainObject)
						.setMtb_Anford_Vorg_Notebook((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung"
				.equals(className)) {
			if ("Set_Name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.setSet_Name((java.lang.String) value);
			if ("Set_Krzz".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.setSet_Krzz((java.lang.String) value);
			if ("Set_Beschreibung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.setSet_Beschreibung((java.lang.String) value);
			if ("Set_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung) domainObject)
						.setSet_ID((java.lang.Integer) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang"
				.equals(className)) {
			if ("stG_EvaluierungVerpflichtend".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_EvaluierungVerpflichtend((java.lang.Boolean) value);
			if ("stG_BakkArbeiten_Anzahl_Pruefungsrelevant"
					.equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeiten_Anzahl_Pruefungsrelevant((java.lang.Integer) value);
			if ("stG_Bank".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Bank((java.lang.String) value);
			if ("stG_BakkArbeit3_pruefungsrelevant".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit3_pruefungsrelevant((java.lang.Boolean) value);
			if ("stG_BakkArbeit2_SichtbarStn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit2_SichtbarStn((java.lang.Boolean) value);
			if ("stG_BakkArbeit1_SichtbarStn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit1_SichtbarStn((java.lang.Boolean) value);
			if ("stG_Art_Code".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Art_Code((java.lang.Integer) value);
			if ("stG_BakkArbeit3_SichtbarStn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit3_SichtbarStn((java.lang.Boolean) value);
			if ("stG_Aktiv".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Aktiv((java.lang.Boolean) value);
			if ("stG_Krzz".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Krzz((java.lang.String) value);
			if ("stG_CreditsGesamt".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_CreditsGesamt((java.lang.Integer) value);
			if ("stG_AkadGradDatum".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_AkadGradDatum((java.util.Date) value);
			if ("stG_Leiter_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Leiter_ID((java.lang.Integer) value);
			if ("stG_OBV_id".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_OBV_id((java.lang.String) value);
			if ("stG_Tel".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Tel((java.lang.String) value);
			if ("stG_Standortinfo".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Standortinfo((java.lang.String) value);
			if ("stG_Fax".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Fax((java.lang.String) value);
			if ("stG_BISForm".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BISForm((java.lang.Integer) value);
			if ("stG_Email".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Email((java.lang.String) value);
			if ("stG_Foerderungsvertrag_GZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Foerderungsvertrag_GZ((java.lang.String) value);
			if ("stG_Kennzahl".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Kennzahl((java.lang.Integer) value);
			if ("stG_Name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Name((java.lang.String) value);
			if ("stG_Strasse".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Strasse((java.lang.String) value);
			if ("stG_Regelstudiendauer".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Regelstudiendauer((java.lang.Integer) value);
			if ("stG_Kontonummer".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Kontonummer((java.lang.String) value);
			if ("vortragende".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setVortragende((java.util.Set) value);
			if ("stG_Start_Sem".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Start_Sem((java.lang.Integer) value);
			if ("stG_Annerkennungsbescheid_GZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Annerkennungsbescheid_GZ((java.lang.String) value);
			if ("stG_BakkArbeit1_pruefungsrelevant".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit1_pruefungsrelevant((java.lang.Boolean) value);
			if ("stG_DeI_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_DeI_ID((java.lang.Integer) value);
			if ("stG_PLZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_PLZ((java.lang.String) value);
			if ("stG_BakkArbeiten_Anzahl".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeiten_Anzahl((java.lang.Integer) value);
			if ("stG_AntragDatum".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_AntragDatum((java.util.Date) value);
			if ("stG_BLZ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BLZ((java.lang.String) value);
			if ("stG_Hausordnung".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Hausordnung((java.lang.String) value);
			if ("stG_Ort".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Ort((java.lang.String) value);
			if ("stG_Color".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Color((java.lang.String) value);
			if ("stG_OffiziellerName_Englisch".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_OffiziellerName_Englisch((java.lang.String) value);
			if ("stG_OffiziellerName".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_OffiziellerName((java.lang.String) value);
			if ("stG_Dep_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Dep_ID((java.lang.Integer) value);
			if ("stG_Sto_Krzz_Show".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Sto_Krzz_Show((java.lang.Boolean) value);
			if ("stG_Fernlehre".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_Fernlehre((java.lang.Boolean) value);
			if ("stG_BakkArbeit2_pruefungsrelevant".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang) domainObject)
						.setStG_BakkArbeit2_pruefungsrelevant((java.lang.Boolean) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle"
				.equals(className)) {
			if ("orgeinheit".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.setOrgeinheit((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("OrS_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.setOrS_ID((java.lang.Integer) value);
			if ("stellen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.setStellen((java.util.Set) value);
			if ("OrS_Name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) domainObject)
						.setOrS_Name((java.lang.String) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson"
				.equals(className)) {
			if ("OrP_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setOrP_ID((java.lang.Integer) value);
			if ("person".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setPerson((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) value);
			if ("OrP_Beginn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setOrP_Beginn((java.util.Date) value);
			if ("OrP_Hauptrolle".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setOrP_Hauptrolle((java.lang.Boolean) value);
			if ("OrP_Ende".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setOrP_Ende((java.util.Date) value);
			if ("stelle".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson) domainObject)
						.setStelle((at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalUserRole"
				.equals(className)) {
			if ("Role_NameW".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.setRole_NameW((java.lang.String) value);
			if ("Role_Name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.setRole_Name((java.lang.String) value);
			if ("Role_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalUserRole) domainObject)
						.setRole_ID((java.lang.Integer) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag"
				.equals(className)) {
			if ("MVe_LetzterTag".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_LetzterTag((java.util.Date) value);
			if ("MVe_HauptberuflichLehrend".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_HauptberuflichLehrend((java.lang.Boolean) value);
			if ("mitarbeiter".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMitarbeiter((at.ac.fhcampuswien.atom.shared.domain.PortalPerson) value);
			if ("MVe_BeschaeftigungsAusmass".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_BeschaeftigungsAusmass((java.lang.Integer) value);
			if ("MVe_VertragsBeginn".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_VertragsBeginn((java.util.Date) value);
			if ("MVe_VertragsEnde".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_VertragsEnde((java.util.Date) value);
			if ("MVe_BeschaeftigungsArt2".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_BeschaeftigungsArt2((java.lang.Integer) value);
			if ("MVe_BeschaeftigungsArt1".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_BeschaeftigungsArt1((java.lang.Integer) value);
			if ("MVe_Wochenstunden".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_Wochenstunden((java.lang.Double) value);
			if ("MVe_Verwendungscode".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag) domainObject)
						.setMVe_Verwendungscode((java.lang.Integer) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalFolder"
				.equals(className)) {
			if ("studiengaenge".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.setStudiengaenge((java.util.Set) value);
			if ("Fol_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.setFol_ID((java.lang.Integer) value);
			if ("Fol_Name".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.setFol_Name((java.lang.String) value);
			if ("childFolders".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.setChildFolders((java.util.Set) value);
			if ("Fol_Parent".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) domainObject)
						.setFol_Parent((at.ac.fhcampuswien.atom.shared.domain.PortalFolder) value);
		}
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit"
				.equals(className)) {
			if ("OrE_Typ".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_Typ((java.lang.String) value);
			if ("vortraege".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setVortraege((java.util.Set) value);
			if ("OrE_Name_EN".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_Name_EN((java.lang.String) value);
			if ("veranstaltungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setVeranstaltungen((java.util.Set) value);
			if ("OrE_Name_Public_EN".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_Name_Public_EN((java.lang.String) value);
			if ("publikationen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setPublikationen((java.util.Set) value);
			if ("uebergeordneteOE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setUebergeordneteOE((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) value);
			if ("stellen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setStellen((java.util.Set) value);
			if ("OrE_ID".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_ID((java.lang.Integer) value);
			if ("OrE_Name_Public_DE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_Name_Public_DE((java.lang.String) value);
			if ("untergeordneteOEs".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setUntergeordneteOEs((java.util.Set) value);
			if ("kooperationsvereinbarungen".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setKooperationsvereinbarungen((java.util.Set) value);
			if ("OrE_Name_DE".equals(attributeName))
				((at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit) domainObject)
						.setOrE_Name_DE((java.lang.String) value);
		}
		throw new AtomException("Attribute " + className + "." + attributeName
				+ " not found. Could not set value!");
		
		//throw new AtomException("Attribute " + className + "." + attributeName " not found. Could not set value!");
	}
	
	public static DomainObject makeInstance(DomainClass domainClass) {
		String className = domainClass.getName();
		if ("at.ac.fhcampuswien.atom.shared.domain.PersistentString"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PersistentString();
		if ("at.ac.fhcampuswien.atom.shared.domain.FeaturedObject"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.FeaturedObject();
		if ("at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.ExterneEinheit();
		if ("at.ac.fhcampuswien.atom.shared.domain.Veranstaltung"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Veranstaltung();
		if ("at.ac.fhcampuswien.atom.shared.domain.Publikation"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Publikation();
		if ("at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.EvaluierungCampusAcademy();
		if ("at.ac.fhcampuswien.atom.shared.domain.Message".equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Message();
		if ("at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.SynergyAusWeiterbildung();
		if ("at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Kooperationsvereinbarung();
		if ("at.ac.fhcampuswien.atom.shared.domain.Vortrag".equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Vortrag();
		if ("at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PreisAuszeichnung();
		if ("at.ac.fhcampuswien.atom.shared.domain.StoreableUser"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.StoreableUser();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalPerson"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalPerson();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalEinstellung();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalStudiengang();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalOrgStelle();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalOrgStellePerson();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalUserRole"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalUserRole();
		if ("at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.Dienstvertrag();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalFolder"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalFolder();
		if ("at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit"
				.equals(className))
			return new at.ac.fhcampuswien.atom.shared.domain.PortalOrgeinheit();
		throw new AtomException("DomainClass " + className
				+ " not found. Could not create instance!");

		//throw new AtomException("DomainClass " + domainClass.getName() + " not found. Could not create instance!");
	}
}
