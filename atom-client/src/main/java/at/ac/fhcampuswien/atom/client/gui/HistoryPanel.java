/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.frames.Frame;
import at.ac.fhcampuswien.atom.client.gui.frames.WelcomeFrame;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.FrameVisit;

public class HistoryPanel extends SidePanel<FrameVisit> {

	public HistoryPanel(RootPanel[] markTargets) {
		super(markTargets);
	}

	private BiMap<Frame, FrameVisit> frameVisits = HashBiMap.create(8);
	private HashMap<Integer, FrameVisit> framelessVisits = new HashMap<Integer, FrameVisit>();
	private HashMap<FrameVisit, SidePanelLabel<FrameVisit>> frameLabels = new HashMap<FrameVisit, SidePanelLabel<FrameVisit>>();

	private Frame welcomeFrame = null;

	@Override
	protected boolean representsInstanceOfClass(FrameVisit o, DomainClass dc) {
		return dc.isClassOfInstance(o.getRepresentedInstance());
	}

	public void addFrame(final Frame frame) {
		addFrame(frame, true);
	}

	public void addFrame(final Frame frame, boolean sendToServer) {
		FrameVisit visit = frameVisits.get(frame);

		if (visit != null) {
			this.showFrame(frame, sendToServer);
		} else {

			visit = showFrame(frame);

			if (visit == null)
				return; // currently active frame didn't want to leave, visit
						// won't happen.

			if (frame.getFrameType().equals(AtomConfig.FrameType.WELCOME))
				welcomeFrame = frame;

			if (frameLabels.get(visit) == null) {
				createAndShowLabel(visit, true);
				rebuildElements();
			} else {
				moveLabelToTop(visit);
			}
			frame.setFrameLabel(frameLabels.get(visit));

			// frame.getFrameLabel().setStyleName(atomSidePanelStyle.frameHeader());
			// frame.getFrameLabel().addClickHandler(new ClickHandler() {
			// public void onClick(ClickEvent event) {
			// HistoryPanel.this.showFrame(frame);
			// AtomTools.log(Level.FINER, "frameLabel onClick", this);
			// }
			// });

			// addElement(new CloseableLabel(frame.getFrameLabel(), visit));
		}
	}

	public void addFrame(final FrameVisit fv) {
		createAndShowLabel(fv, false);
		framelessVisits.put(fv.hashCode(), fv);
	}

	private void createAndShowLabel(final FrameVisit fv, boolean top) {
		// String shortenedString = AtomGUI.getSinglton().getShortenedString(fv.getFrameShortTitle(), null, getLabelSpace());
		// DndLabel dl = new DndLabel(shortenedString, fv.getRepresentedInstance());
		// dl.setStyleName(sidePanelStyle.frameHeader());
		// dl.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// HistoryPanel.this.showFrame(fv);
		// AtomTools.log(Level.FINER, "frameLabel onClick", this);
		// }
		// });
		// SidePanelLabel<FrameVisit> cl = new SidePanelLabel<FrameVisit>(this, dl, fv);

		SidePanelLabel.NameSource nameSource = new SidePanelLabel.NameSource() {
			@Override
			public String getName() {
				return fv.getFrameShortTitle();
			}
		};

		SidePanelLabel<FrameVisit> cl = new SidePanelLabel<FrameVisit>(this, fv, nameSource, fv.getHistoryString(), fv.getRepresentedInstance());

		if (top)
			addElement(cl);
		else
			addElementAtTheEnd(cl);

		frameLabels.put(fv, cl);
	}

	public boolean showFrame(FrameVisit fv) {
		Frame frame = frameVisits.inverse().get(fv);
		if (frame != null)
			return showFrame(frame) != null;
		else {
			String historyString = fv.getHistoryString();
			return App.processCommand(historyString);
		}
	}

	public FrameVisit showFrame(final Frame frame) {
		return showFrame(frame, true);
	}
	
	public FrameVisit showFrame(final Frame frame, boolean sendToServer) {
		
		// currently active frame doesn't want to leave, no visit happens, return null.
		if (!App.showFrame(frame))
			return null;

		FrameVisit visit = frameVisits.get(frame);
		
		if (visit == null) {
			visit = new FrameVisit(RPCCaller.getSinglton().getClientSession().getUser(), frame.getFrameType(), frame.getRepresentedObject(),
					frame.getRepresentedClass(), frame.getRepresentedSearchTerm(), frame.isSimpleSearch(), frame.getDataFilters(), frame.getShortTitle());
			FrameVisit frameless = framelessVisits.get(visit.hashCode());
			if (frameless != null) {
				//framelessVisits.remove(visit.hashCode());
				visit = frameless;
			}
			frameVisits.forcePut(frame, visit);
			framelessVisits.put(visit.hashCode(), visit);
		}
		visit.setLastVisit(new Date());

		if (sendToServer && loadingEntriesFromServerFinished != null && loadingEntriesFromServerFinished == true
				 && visit != null && visit.getFrameType() != FrameType.OBJECT_SELECTOR) {
			sendVisitToServer(frame, visit);
		}

		moveLabelToTop(visit);

		App.startCreatingNewState();
		History.newItem(visit.getHistoryString());
		App.finishedCreatingNewState();

		Window.setTitle("DIA - " + frame.getShortTitle());

		return visit;
	}

	private void sendVisitToServer(final Frame frame, FrameVisit visit) {
		// frameVisits.remove(frame);
		RPCCaller.getSinglton().saveDomainObject(visit, new WaitingFor<DomainObject>() {

			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.WARNING, "FrameVisit for Frame [" + frame.getTitle() + "] could not be saved. reason = " + reason, this);
			}

			@Override
			public void recieve(DomainObject instance) {
				if (instance != null && instance instanceof FrameVisit) {
					FrameVisit v = (FrameVisit) instance;
					frameVisits.remove(frame);
					frameVisits.forcePut(frame, v);
					framelessVisits.put(v.hashCode(), v);
				}
			}
		});
	}

	public Frame findFrame(DomainObject representedObject, DomainClass representedClass, String representedSearchTerm, Collection<DataFilter> filters, AtomConfig.FrameType frameType) {
		for (Frame anFrame : frameVisits.keySet()) {

			boolean objMatch = (representedObject != null && representedObject.equals(anFrame.getRepresentedObject()))
					|| (representedObject == null && (anFrame.getRepresentedObject() == null || anFrame.getRepresentedObject().getObjectID() == null));
			boolean typeMatch = frameType != null && frameType.equals(anFrame.getFrameType());
			boolean searchMatch = (representedSearchTerm != null && representedSearchTerm.equals(anFrame.getRepresentedSearchTerm()))
					|| (representedSearchTerm == null && anFrame.getRepresentedSearchTerm() == null);
			boolean classMatch = (representedClass != null && representedClass.equals(anFrame.getRepresentedClass()))
					|| (representedClass == null && anFrame.getRepresentedClass() == null);
			
			boolean filterMatch = false;
			if((anFrame.getDataFilters() == null || anFrame.getDataFilters().length <= 0) && (filters == null  || filters.size() <= 0)) {
				//both are null or empty and therefore equal
				filterMatch = true;
			}
			else if(anFrame.getDataFilters() == null || filters == null) {
				//one of the filter collections is null, and the other isn't (otherwise the first if would have matched)
				filterMatch = false;
			}
			else
				filterMatch = AtomTools.arraysContainEqualElements(anFrame.getDataFilters(), filters.toArray(new DataFilter[filters.size()]));
			
			if (objMatch && typeMatch && searchMatch && classMatch && filterMatch)
				return anFrame;
		}
		return null;
	}

	public boolean activateFrameIfExists(DomainObject representedObject, DomainClass representedClass, String representedSearchTerm, Collection<DataFilter> filters,
			AtomConfig.FrameType frameType) {
		Frame synonymFrame = findFrame(representedObject, representedClass, representedSearchTerm, filters, frameType);
		if (synonymFrame != null) {
			this.showFrame(synonymFrame);
			return true;
		}
		return false;
	}

	@Override
	public void removeElement(FrameVisit identifier) {
		removeElement(identifier, false, null);
	}
	
	public void removeElement(FrameVisit identifier, boolean keepVisible) {
		removeElement(identifier, false, null);
	}

	public void removeElement(FrameVisit identifier, final boolean keepVisible, final ArrayList<SidePanelLabel<FrameVisit>> needToDelete) {
		super.removeElement(identifier);
		RPCCaller.getSinglton().deleteDomainObject(identifier, new Notifiable<String>() {
			
			@Override
			public void doNotify(String reason) {
				AtomTools.log(Level.INFO, "HistoryPanel.removeElement - deleteDomainObject -> got response from server, notifying clearTimer to continue", this);
				continueClearing(needToDelete, !keepVisible);
			}
		});

		frameLabels.remove(identifier);
		Frame frame = frameVisits.inverse().get(identifier);
		if (frame != null && !keepVisible) {
			removeElement(frame);
		}
	}

	public void closeFrame(Frame frame) {
		removeElement(frameVisits.get(frame));
		if (App.isActiveFrame(frame))
			showTopFrame();
	}

	private void showTopFrame() {
		if (lablesList.size() > 0)
			showFrame((FrameVisit) getIdentifierOfNumber(0));
		else {
			if (welcomeFrame != null)
				addFrame(welcomeFrame);
			else
				addFrame(new WelcomeFrame());
		}
	}

	private void removeElement(Frame frame) {
		boolean activeFrame = App.isActiveFrame(frame);

		// cancel closing if the frame doesn't want to be closed
		if (activeFrame && !frame.goingInvisible()) {
			return;
		}

		if (activeFrame) {
			showTopFrame();
		}

		frameVisits.remove(frame);
	}

	@Override
	protected void processLoadedEntries(DomainObjectList result) {

		/**
		 * nasty bug-fix applied! although hashCode and equals of the Class FrameVisit always deliver the expected behavior, the BiMap (reverse part) will
		 * NOT(!) return a Frame that is associated with a FrameVisit of equal HashCode and which equal method will return true for the given FrameVisit.
		 * Somehow the hashcode stored by our Maps is different then the one that is returned by our .hashCode() method. Maybe GWT code will ignore the hashCode
		 * method on some cases?!
		 */
		Set<FrameVisit> priors = frameVisits.values();

		for (DomainObject d : result.getDomainObjects()) {
			if (d instanceof FrameVisit) {
				FrameVisit fv = (FrameVisit) d;
				Frame f = frameVisits.inverse().get(fv);

				// nasty bug-fix! begin
				if (f == null) {
					FrameVisit matched = null;
					for (FrameVisit one : priors) {
						if (one.equals(fv)) {
							matched = one;
							f = frameVisits.inverse().get(one);
							frameVisits.forcePut(f, fv);
							fv.setLastVisit(matched.getLastVisit());
							SidePanelLabel<FrameVisit> lbl = identfiedLables.get(matched);
							identfiedLables.remove(matched);
							identfiedLables.put(fv, lbl);
						}
					}
					if (matched != null) {
						priors.remove(matched);
					}
				}
				// nasty bug-fix! end

				if (f != null) {
					FrameVisit fvO = frameVisits.get(f);
					if (fvO != null)
						fv.setLastVisit(fvO.getLastVisit());
					frameVisits.put(f, fv);
					sendVisitToServer(f, fv);
				} else {
					addFrame(fv);
				}
				// RPCCaller.getSinglton().deleteDomainObject(d,
				// null);
			}
		}
	}

	/**
	 * @param pdo A DomainObject that has been deleted and should not continue to exist
	 */
	@Override
	public void removeFromUI(final DomainObject pdo) {
		if (pdo == null)
			return;
		
		HashSet<Frame> matchedFrames = new HashSet<Frame>();

		if (pdo.getObjectID() != null) {
			FrameVisit visit = framelessVisits.get(pdo.getObjectID());
			if (visit != null) {
				this.removeElement(visit, true);
				framelessVisits.remove(pdo.getObjectID());
			}
			
			for (Frame f : frameVisits.keySet()) {
				if (FrameType.DETAIL_VIEW.equals(f.getFrameType()) && f.getRepresentedObject() != null && f.getRepresentedObject().getObjectID() != null
						&& f.getRepresentedObject().getObjectID().equals(pdo.getObjectID())) {
					matchedFrames.add(f);
			    }
			}
			
		} else {
			// if I haven't got an ID, I have to look for the class. (DETAIL_VIEW_new views)
			DomainClass dt = RPCCaller.getSinglton().getLoadedDomainTree();
			DomainClass dc = dt.getDomainClassNamed(pdo.getConcreteClass());
			for (Frame f : frameVisits.keySet()) {
				if (FrameType.DETAIL_VIEW.equals(f.getFrameType()) && dc.equals(f.getRepresentedClass()) && (f.getRepresentedObject() == null || f.getRepresentedObject().getObjectID() == null)) {
					matchedFrames.add(f);
			    }
			}
		}
		for(Frame f : matchedFrames) {
			FrameVisit v = frameVisits.get(f);
			this.removeElement(v, true);
		}
	}

	public void clear() {
		// looping through all labels starting at the last ending at the second (leaving the first untouched)
		ArrayList<SidePanelLabel<FrameVisit>> needToDelete = new ArrayList<SidePanelLabel<FrameVisit>>();
		needToDelete.addAll(lablesList);
		needToDelete.remove(lablesList.get(0)); //don't delete the first one (=currently viewed)
//		App.processCommand("WELCOME");
		continueClearing(needToDelete, false);
	}
	
	private void continueClearing(ArrayList<SidePanelLabel<FrameVisit>> needToDelete, boolean showTopAfter) {
		if(needToDelete != null && needToDelete.size() > 0) {
			SidePanelLabel<FrameVisit> lbl = needToDelete.get(0);
			if(lbl.identifier.getFrameType() != FrameType.WELCOME)
				HistoryPanel.this.removeElement(lbl.identifier);
			needToDelete.remove(lbl);
		}
		else if(showTopAfter) {
			showTopFrame();
		}
	}
}
