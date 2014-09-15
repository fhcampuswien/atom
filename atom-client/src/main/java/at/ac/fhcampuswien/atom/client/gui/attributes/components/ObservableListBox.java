/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes.components;

import java.util.Collection;

import at.ac.fhcampuswien.atom.client.gui.attributes.CollectionView;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ListBox;

public class ObservableListBox<C extends Collection<T>, T> extends ListBox {

	/**
	 * 
	 */
	private final CollectionView<C, T> collectionView;

	public ObservableListBox(CollectionView<C, T> collectionView) {
		super(true);
		this.collectionView = collectionView;
		this.setVisibleItemCount(5);
		this.sinkEvents(Event.ONDBLCLICK);
		// this.setEnabled(true);
		// this.setFocus(true);
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "doubleclick: '" + event + "'", this);
			this.collectionView.editItemAtIndex(this.getSelectedIndex());
		}
		super.onBrowserEvent(event);
	}
}