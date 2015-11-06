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
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DndLink extends FocusPanel implements AtomDNDWidget {

	private List<DomainObject> representedObjects;
	//private Widget parent;
	
	private Hyperlink myLink = null;
	
	public DndLink(String text, String link, DomainObject representedDomainObject, Widget parent) {
		
		myLink = new Hyperlink(text, link);
		this.setWidget(myLink);
		
		//workaround default blue link text styling
		myLink.getElement().getFirstChildElement().getStyle().setColor("#475765");

		representedObjects = new ArrayList<DomainObject>(1);
		representedObjects.add(representedDomainObject);
//		this.parent = parent;
		// this.representedDomainObject = representedDomainObject;
		
		if(!draggable()) {
			this.getElement().getStyle().setCursor(Cursor.POINTER);
		}
	}
	
	public Hyperlink getLink() {
		return myLink;
	}

	private boolean draggable() {
		return !(representedObjects.size() == 0 || representedObjects.get(0) == null || representedObjects.get(0).getObjectID() == null);
	}

	@Override
	public Widget getDndProxy(int x, int y) {
		
		String labelText = myLink.getText();
		String objectsString = AtomTools
				.domainObjectsString(representedObjects);
		if ("keine DomainObjekte".equals(objectsString)) {
			return new Label(labelText);
		} else {
			return new Label(objectsString);
		}
		// if(getRepresentedDomainObject() != null)
		// labelText = getRepresentedDomainObject().getStringRepresentation();

		// proxyLabel.setHeight(this.getOffsetHeight() + "px");
		// proxyLabel.setWidth(this.getOffsetWidth() + "px");
		// proxyLabel.setPixelSize(this.getOffsetWidth(),
		// this.getOffsetHeight());
		// proxyLabel.setStylePrimaryName("dndProxy");
		// proxyLabel.setWidth("100px");

		// proxyLabel.setStylePrimaryName(this.getStylePrimaryName());
		// return proxyLabel;
	}

	public void setRepresentedDomainObject(DomainObject domainObject) {
		representedObjects.clear();
		representedObjects.add(domainObject);
		// this.representedDomainObject = domainObject;
		
		if(!draggable()) {
			this.getElement().getStyle().setCursor(Cursor.POINTER);
		}
	}

	//
	public DomainObject getRepresentedDomainObject() {
		if (representedObjects.size() > 0)
			return representedObjects.get(0);
		else
			return null;
	}

	@Override
	public List<DomainObject> getRepresentedDomainObjects() {
		return representedObjects;
	}

	@Override
	public void previewDragStart(int x, int y) throws VetoDragException {
		AtomTools.log(Level.FINER, "DndLabel.previewDragStart(x="+x+",y="+y+")", this);
		if(!draggable())
			throw new VetoDragException();
	}

//	, HasDragHandle
//	@Override
//	public Widget getDragHandle() {
//		return parent;
//	}
}
