/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import java.util.List;

import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;

public class DomainObjectDropController extends AbstractDropController {

	public interface HandlesDrops {
		public boolean interestedIn(DomainObject domainObject);
		public void dropperLeft();
		public boolean drop(DomainObject domainObject);
	}
	
	Widget target;
	HandlesDrops dropHandler;

	public DomainObjectDropController(Widget target, HandlesDrops theDropHandler) {
		super(target);
		dropHandler = theDropHandler;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		AtomTools.log(Log.LOG_LEVEL_ERROR, "onDrop called (should have been canceled to preserve orginal widget!)", this);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		AtomTools.log(Log.LOG_LEVEL_TRACE, "onEnter called", this);
		for (Widget widget : context.selectedWidgets) {
		    List<DomainObject> objects = ((AtomDNDWidget) widget).getRepresentedDomainObjects();
		    for(DomainObject object : objects) {
			dropHandler.interestedIn(object);
		    }
		}
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		AtomTools.log(Log.LOG_LEVEL_TRACE, "onLeave called", this);
		dropHandler.dropperLeft();
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		super.onPreviewDrop(context);
		AtomTools.log(Log.LOG_LEVEL_TRACE, "onPreviewDrop called", this);
		for (Widget widget : context.selectedWidgets) {
		    List<DomainObject> objects = ((AtomDNDWidget) widget).getRepresentedDomainObjects();
		    for(DomainObject object : objects) {
			dropHandler.drop(object);
		    }
		}
		throw new VetoDragException();
	}

}
