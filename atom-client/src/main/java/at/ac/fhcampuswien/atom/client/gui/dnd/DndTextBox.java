/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import java.util.ArrayList;
import java.util.List;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DndTextBox extends TextBox implements AtomDNDWidget {
	// SourcesMouseEvents

	private ArrayList<DomainObject> representedObjects = new ArrayList<DomainObject>();

	// private MouseListenerCollection mouseListeners;

	public DndTextBox() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "ObservantTextBox created", this);
		this.sinkEvents(Event.ONDBLCLICK + Event.ONCLICK + Event.ONKEYPRESS);
		super.setEnabled(true);
		
		this.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "click!", this);
			}
		});
	}

	public void setValue(DomainObject newValue) {
		representedObjects.clear();
		representedObjects.add(newValue);
		// value = newValue;
		this.setText(newValue.getStringRepresentation());
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "doubleclick: '" + event + "'",
					this);
			if (representedObjects.size() > 0)
				App.openDetailView(representedObjects.get(0),
						null, false);
		} else
			super.onBrowserEvent(event);
	}

	@Override
	public Widget getDndProxy(int x, int y) {
		Label proxyLabel = new Label(
				AtomTools.domainObjectsString(representedObjects));
		// proxyLabel.setStylePrimaryName(this.getStylePrimaryName());
		return proxyLabel;
	}

	public DomainObject getRepresentedDomainObject() {
		return representedObjects.get(0);
	}

	@Override
	public List<DomainObject> getRepresentedDomainObjects() {
		return representedObjects;
	}

	@Override
	public void previewDragStart(int x, int y) throws VetoDragException {
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(true);
	}
}
