/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import java.util.ArrayList;
import java.util.List;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import java.util.logging.Level;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DndPanel extends SimplePanel implements AtomDNDWidget {
	// SourcesMouseEvents

	private ArrayList<DomainObject> representedObjects = new ArrayList<DomainObject>();

	// private MouseListenerCollection mouseListeners;

	public DndPanel() {
		AtomTools.log(Level.FINER, "DndPanel created", this);
		this.sinkEvents(Event.ONDBLCLICK);
//		this.getElement().getStyle().setProperty("border", "2px inset");
		
	}

	public void addItem(DomainObject item) {
		representedObjects.add(item);
	}
	
	public void removeItem(DomainObject item) {
		representedObjects.remove(item);
	}

	public void removeAllItems() {
		representedObjects.clear();
	}

	Label proxyLabel = null;

	public Widget getDndProxy(int x, int y) {
		String string = AtomTools.domainObjectsString(getRepresentedDomainObjects());
		if (proxyLabel == null)
			proxyLabel = new Label(string);
		else
			proxyLabel.setText(string);
		// proxyLabel.setStylePrimaryName(this.getStylePrimaryName());
		return proxyLabel;
	}

	public DomainObject getRepresentedDomainObject() {
		return representedObjects.get(0);
	}

	public List<DomainObject> getRepresentedDomainObjects() {
		return representedObjects;
	}

	public void previewDragStart(int x, int y) throws VetoDragException {
		AtomTools.log(Level.FINER, "DndPanel.previewDragStart called, x=" + x + ", y=" + y, this);
	}
}
