/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.Collection;

import java.util.logging.Level;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.PersistentString;

public class ListOfPersistentStringsView extends CollectionView<Collection<PersistentString>, PersistentString> {

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
