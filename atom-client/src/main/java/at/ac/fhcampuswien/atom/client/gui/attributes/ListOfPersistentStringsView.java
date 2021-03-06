/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition.ViewType;
import at.ac.fhcampuswien.atom.shared.domain.PersistentString;

public class ListOfPersistentStringsView extends CollectionView<Collection<PersistentString>, PersistentString> {

	public enum Suggestions {
		NONE, SERVER, LOCAL
	}
	
	private String collectionType = null;
	@SuppressWarnings("unused")
	private ListOfPersistentStringsView() {
		//prevent creation without specifying type!
	}
	
	public ListOfPersistentStringsView(String collectionType) {
		this.collectionType = collectionType;		
	}
	
	public ListOfPersistentStringsView(String collectionType, DomainClass domainClass, String attributeName, boolean allowOtherValues, ListBoxDefinition.ViewType viewType) {
		this.collectionType = collectionType;
		this.allowOtherValues = allowOtherValues;
		RPCCaller.getSinglton().loadListBoxChoices(domainClass, attributeName, new WaitingFor<LinkedHashMap<String,String>>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.SEVERE, "failed to load ListBoxChoices from Server: " + reason, ListOfPersistentStringsView.this);
			}
			
			@Override
			public void recieve(LinkedHashMap<String, String> result) {
				dropDownValues = result.values().toArray(new String[] {});
				useSuggestBox = viewType == ViewType.FilterAbleDropDown;
			}
		});
		
	}
	
	public ListOfPersistentStringsView(String collectionType, LinkedHashMap<String, String> choiceMap, boolean allowOtherValues, ListBoxDefinition.ViewType viewType) {
		this.collectionType = collectionType;
		this.allowOtherValues = allowOtherValues;
		dropDownValues = choiceMap.values().toArray(new String[] {});
		useSuggestBox = viewType == ViewType.FilterAbleDropDown;
	}
	
	protected void addNewItem(Object newItem) {
		if (value == null) {
			value = ClientTools.getPersistentStringsCollection(collectionType);
		}
		super.addNewItem(newItem);
	}

	@Override
	protected Object getObjectForString(String string) {
		return new PersistentString(string);
	}

	@Override
	public void editItem(Object item, String newStringValue) {
		if(item != null)
			if(item instanceof PersistentString)
				((PersistentString) item).setValue(newStringValue);
			else
				AtomTools.log(Level.SEVERE, "the item should be a PersistentString instance! expect GUI-Errors", this);
		else {
			addNewItem(new PersistentString(newStringValue));
		}
		
		showValue();
			
			
		// AtomTools.log(Level.SEVERE,
		// "If the Edit String Mechanism of the CollectionView is used, the editItem method must be overwritten",
		// this);
	}
}
