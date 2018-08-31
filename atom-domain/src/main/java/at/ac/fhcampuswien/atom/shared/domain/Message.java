/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared.domain;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeDisplayName;
import at.ac.fhcampuswien.atom.shared.annotations.AttributePlacement;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeValidators;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.FileAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromListGui;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition.ViewType;
import at.ac.fhcampuswien.atom.shared.annotations.ObjectImage;
import at.ac.fhcampuswien.atom.shared.annotations.RelationEssential;
import at.ac.fhcampuswien.atom.shared.annotations.SliderAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.StringFormattedLob;

@Entity
@ClassNamePlural("Messages")
@ObjectImage("data:image/gif;base64,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")
@AccessListRoles(accessTypes=AtomConfig.accessReadWrite, value= { "Administrator", "DIA-Selenium-Testing" })
public class Message extends FeaturedObject {

	@AnalyzerIgnore
	private static final long serialVersionUID = 8367723419937102182L;

	// @Column(name = "MESSAGE_TEXT")
	private String text;

	@Lob
	@StringFormattedLob
	private String longText;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH )
	// cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.ALL },
	// @JoinColumn(name = "NEXT_MESSAGE_ID")
	private Message nextMessage;

	@OneToMany(mappedBy = "nextMessage", fetch = FetchType.LAZY, cascade = CascadeType.DETACH )
	// , cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.ALL }
	private Set<Message> previousMessages;

	@SliderAttribute(defaultValue = 0, maxValue = 100, minValue = -100, stepSize = 0.1, roundTo = 5)
	private Double testDoubleField;

	@AttributeValidators({AttributeValidators.email})
	private String senderAddress;

	private String typ;

	@FileAttribute
	private String binaryContent;

	@ListBoxDefinition(viewType = ViewType.RadioTable, multiSelectSeperator=",", keys = { "1", "2", "3", "4" }, display = { "ms1", "ms2", "ms3", "ms4" })
	private String multiSelect;
	
	@OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "ownersAttribute='persistentStrings'")
	@AttributeDisplayName("persistent Strings")
	@AttributeValidators(AttributeValidators.notEmpty)
	@ListBoxDefinition(anyExistingValue=true,allowOtherValues=true)
	private Set<PersistentString> persistentStrings;

	@ListBoxDefinition(viewType=ViewType.FilterAbleDropDown, anyExistingValue=true, allowOtherValues=true)
	private String suggestString;
	
	public Message() {
	}

	public Message(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@AttributePlacement(20)
	@AttributeDisplayName("Typ")
	@RelationEssential
	@ListBoxDefinition(viewType = ViewType.RadioButtons, keys = { "MessageType1", "MessageType2", "MessageType3", "MessageType4", "MessageType5", "MessageType6", "MessageType7", "MessageType8", "MessageType9", "MessageType10", "MessageType11" })
	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public Message getNextMessage() {
		return nextMessage;
	}

	public void setNextMessage(Message nextMessage) {
		if (this.nextMessage != null && !this.nextMessage.equals(nextMessage)) {
			if (this.nextMessage.getPreviousMessages() != null) {
				try {
					this.nextMessage.getPreviousMessages().remove(this);
				} catch (Throwable t) {
					AtomTools.log(Level.FINE, "failed to remove myself from nextMessage.getPreviousMessages after different message got set. probably a lazy loading collection without session on server side", this);
				}
			}
		}
		this.nextMessage = nextMessage;

		if (nextMessage != null && nextMessage.getPreviousMessages() != null) {
			nextMessage.getPreviousMessages().add(this);
		}
	}

	public void setPreviousMessages(Set<Message> previousMessages) {

		// clearing the property - we might not have a session to access
		// lazyLoading attribute previousMessages
		try {
			Set<Message> oldPreviousMessages = this.previousMessages;
			if (oldPreviousMessages != null) {
				for (Message message : oldPreviousMessages) {
					if (this.equals(message.getNextMessage()))
						message.setNextMessage(null);
				}
			}
		} catch (Throwable t) {
			AtomTools.log(Level.FINE, "failed to go through oldPreviousMessages - probably not loaded (lazy loading) and session closed.", this);
		}
		this.previousMessages = previousMessages;
		
		if (previousMessages != null) {
			for (Message message : previousMessages) {
				message.setNextMessage(this);
			}
		}
	}

	public Set<Message> getPreviousMessages() {
		return previousMessages;
	}

	public void setTestDoubleField(Double testLongField) {
		this.testDoubleField = testLongField;
	}

	public Double getTestDoubleField() {
		return testDoubleField;
	}

	public void setSenderAddress(String senderAdress) {
		this.senderAddress = senderAdress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setLongText(String longText) {
		this.longText = longText;
	}

	public String getLongText() {
		return longText;
	}

	public String getBinaryContent() {
		return binaryContent;
	}

	public void setBinaryContent(String binaryContent) {
		this.binaryContent = binaryContent;
	}
	
	public String getMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}

	public Set<PersistentString> getPersistentStrings() {
		return persistentStrings;
	}

	public void setPersistentStrings(Set<PersistentString> persistentStrings) {
		this.persistentStrings = persistentStrings;
	}
	
	public String getSuggestString() {
		return suggestString;
	}

	public void setSuggestString(String suggestString) {
		this.suggestString = suggestString;
	}


	@Override
	@HideFromListGui
	public String getConcreteClass() {
		return super.getConcreteClass();
	}

	@Override
	@HideFromListGui
	public String getStringRepresentation() {
		stringRepresentation = "Message: " + getText();
		return stringRepresentation;
	}

	@Override
	@HideFromListGui
	public Date getCreationDate() {
		return super.getCreationDate();
	}

	@Override
	@HideFromListGui
	public Date getLastModifiedDate() {
		return super.getCreationDate();
	}
}
