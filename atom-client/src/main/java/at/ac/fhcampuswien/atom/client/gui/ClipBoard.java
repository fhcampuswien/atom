/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import java.util.Date;
import java.util.HashSet;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.dnd.DomainObjectDropController;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.google.gwt.user.client.ui.RootPanel;

public class ClipBoard extends SidePanel<ClipBoardEntry> {

	private HashSet<Integer> idsOfStoredObjects = new HashSet<Integer>();

	public ClipBoard(RootPanel[] markTargets) {
		super(markTargets);
		App.registerDndDropController(new DomainObjectDropController(this, dropHandler));
	}

	private DomainObjectDropController.HandlesDrops dropHandler = new DomainObjectDropController.HandlesDrops() {

		public boolean interestedIn(DomainObject domainObject) {
			if (domainObject == null || ClipBoard.this.containsDomainObject(domainObject)) {
				return false;
			} else {
				ClipBoard.this.mark();
				return true;
			}
		}

		public void dropperLeft() {
			ClipBoard.this.unMark();
		}

		public boolean drop(DomainObject domainObject) {
			ClipBoard.this.unMark();
			if (domainObject == null || ClipBoard.this.containsDomainObject(domainObject)) {
				return false;
			} else {
				ClipBoard.this.addDomainObject(domainObject, null);
				return true;
			}
		}
	};

	public boolean containsDomainObject(DomainObject domainObject) {
		if(idsOfStoredObjects == null || domainObject == null)
			return false;
		return idsOfStoredObjects.contains(domainObject.getObjectID());
	}
	
	@Override
	protected boolean representsInstanceOfClass(ClipBoardEntry o, DomainClass dc) {
		return dc.isClassOfInstance(o.getInstance());
	}

	private void addDomainObject(final DomainObject domainObject, ClipBoardEntry cbe) {
		
		if(domainObject == null)
			return;
				
//		final DndLabel newLabel = new DndLabel(AtomGUI.getSinglton().getShortenedString(domainObject.getStringRepresentation(), null,
//				getLabelSpace()), domainObject);
//		newLabel.setStyleName(sidePanelStyle.frameHeader());
//		newLabel.addClickHandler(new ClickHandler() {
//
//			public void onClick(ClickEvent event) {
//				AtomGUI.getSinglton().openDetailView(newLabel.getRepresentedDomainObject(), null, false);
//			}
//		});
		
		if(cbe == null) {
			cbe = new ClipBoardEntry(RPCCaller.getSinglton().getClientSession().getUser(), domainObject, new Date());
			sendEntryToServer(cbe);
		}	
		
		SidePanelLabel.NameSource ns = new SidePanelLabel.NameSource() {

			@Override
			public String getName() {
				return domainObject.getStringRepresentation();
			}
		};
		
		SidePanelLabel<ClipBoardEntry> cl = new SidePanelLabel<ClipBoardEntry>(this, cbe, ns, "DETAIL_VIEW_" + cbe.getInstance().getObjectID() , cbe.getInstance());
		
		addElement(cl);
		// storedLabels.add(0, newLabel);
		idsOfStoredObjects.add(domainObject.getObjectID());
		rebuildElements();
	}
	
	private void sendEntryToServer(final ClipBoardEntry cbe) {
		RPCCaller.getSinglton().saveDomainObject(cbe, new WaitingFor<DomainObject>() {

			@Override
			public void requestFailed(String reason) {
			}

			@Override
			public void recieve(DomainObject instance) {
				if (instance != null && instance instanceof ClipBoardEntry) {
					removeElementLocal(cbe);
					ClipBoardEntry cbeS = (ClipBoardEntry) instance;
					addDomainObject(cbeS.getInstance(), cbeS);
				}
			}
		});
	}
	
	@Override
	protected void removeElement(ClipBoardEntry identifier) {
		RPCCaller.getSinglton().deleteDomainObject(identifier, null);
		removeElementLocal(identifier);
	}
	
	private void removeElementLocal(ClipBoardEntry identifier) {
		super.removeElement(identifier);
		idsOfStoredObjects.remove(identifier.getInstance().getObjectID());
	}

	@Override
	protected void processLoadedEntries(DomainObjectList result) {
		for(DomainObject dO : result.getDomainObjects()) {
			if(dO instanceof ClipBoardEntry) {
				ClipBoardEntry cbo = (ClipBoardEntry) dO;
				if(!containsDomainObject(cbo.getInstance())) {
					addDomainObject(cbo.getInstance(), cbo);
				}
			}
		}
	}

	@Override
	public void removeFromUI(DomainObject representedObject) {
		if(representedObject == null || representedObject.getObjectID() == null)
			return;
		
		for(ClipBoardEntry cbe : identfiedLables.keySet()) {
			if(cbe.getInstance() != null && representedObject.getObjectID().equals(cbe.getInstance().getObjectID()))
				removeElement(cbe);
		}
	}
}
