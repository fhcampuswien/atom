/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.ClientConfig;
import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.ObservableListBoxProxy;
import at.ac.fhcampuswien.atom.client.gui.dnd.DomainObjectDropController;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectDetailFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectSelectorFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectSelectorFrame.DomainObjectSelectionHandler;
import at.ac.fhcampuswien.atom.client.gui.gxt.GxtListBoxProxy;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import java.util.logging.Level;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ListOfDomainObjectsView extends CollectionView<Collection<DomainObject>, DomainObject> {

	private DomainObjectDropController domainObjectDropController;
	private String representedType;
	private String attributeName;
	private boolean onlyWriteables;
	private boolean madeDraggable = false;
	private DomainObjectDetailFrame owningFrame;
	private boolean theMouseIsOverMe = false;

	private void initValueCollectionIfNull() {
		if (value == null) {
			if (representedType.contains("Set"))
				value = new HashSet<DomainObject>();
			else if (representedType.contains("List"))
				value = new ArrayList<DomainObject>();
			else {
				AtomTools.log(Level.WARNING, "using ArrayList for unknown Collection type: " + representedType, this);
				value = new ArrayList<DomainObject>();
			}
		}
	}
	
	@Override
	protected ObservableListBoxProxy<Collection<DomainObject>, DomainObject> createListBox() {
		// return new SimpleComboBox<String>();
		return new GxtListBoxProxy(this);
	}

	private class MyDropHandler implements DomainObjectDropController.HandlesDrops {
		private String previousColor = ClientConfig.colorDragReady;

		public boolean interestedIn(DomainObject domainObject) {
			AtomTools.log(Level.INFO, "ListOfDomainObjectsView.MyDropHandler.interestedIn", this);
			previousColor = listBox.getListBoxWidget().getElement().getStyle().getBackgroundColor();
			if (!readOnly
					&& domainObject != null
					&& (value == null || !value.contains(domainObject))
					&& RPCCaller.getSinglton().getLoadedDomainTree() != null
					&& RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType))
							.isClassOfInstance(domainObject)) {
				boolean have = false;

				if (value != null)
					for (DomainObject obj : value) {
						if (obj.equals(domainObject))
							have = true;
					}

				if (!have) {
					listBox.setBackgroundColor(ClientConfig.colorDragOverMatch);
					return true;
				}
			}

			if (!readOnly)
				listBox.setBackgroundColor(ClientConfig.colorDragOverMismatch);

			return false;
		}

		public void dropperLeft() {
			if (!readOnly) {
				listBox.setBackgroundColor(previousColor);
			}
		}

		public boolean drop(DomainObject domainObject) {
			if (!readOnly)
				listBox.setBackgroundColor(ClientConfig.colorDragReady);
			if (!readOnly
					&& domainObject != null
					&& (value == null || !value.contains(domainObject))
					&& RPCCaller.getSinglton().getLoadedDomainTree() != null
					&& RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType))
							.isClassOfInstance(domainObject)) {

				boolean have = false;

				if (value != null)
					for (DomainObject obj : value) {
						if (obj.equals(domainObject))
							have = true;
					}

				if (!have) {
					initValueCollectionIfNull();
					value.add(domainObject);
					App.refreshActiveFrame();
					return true;
				}
			}
			return false;
		}
	}

	public ListOfDomainObjectsView(String type, String attributeName, boolean onlyWriteables, DomainObjectDetailFrame forFrame) {
		representedType = type;
		this.attributeName = attributeName;
		this.onlyWriteables = onlyWriteables;
		this.owningFrame = forFrame;
		domainObjectDropController = new DomainObjectDropController(this, new MyDropHandler());
		searchButton.setVisible(true);

		this.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				App.suggestDND(null);
				theMouseIsOverMe = false;
			}
		}, MouseOutEvent.getType());

		this.addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {

				theMouseIsOverMe = true;
				DomainClass tree = RPCCaller.getSinglton().getLoadedDomainTree();
				if (tree != null) {
					App.suggestDND(tree.getDomainClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType)));
				} else {
					App.suggestDND(null);
				}
			}
		}, MouseOverEvent.getType());
	}

	@Override
	protected void findOrCreateItemAndAddToCollection(boolean search) {

		// TODO best would be if the gxt table understands the filters and shows it in the GUI accordingly..

		if(search) {
			AtomTools.log(Level.INFO, "show popup to enter a new entry", this);
			
			ClientTools.getFilterConfigFromUser(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType), true, new WaitingFor<Collection<DataFilter>>() {
				
				@Override
				public void recieve(final Collection<DataFilter> filters) {
					listBox.setTempLabel("... suche, bitte warten ...");
					
//					DataFilter quickSearch = null;
//					DataFilter deepSearch = null;
//					for(DataFilter f : result) {
//						if(AtomConfig.specialFilterDeepSearch.equals(f.getColumn()))
//							deepSearch = f;
//						else if(AtomConfig.specialFilterQuickSearch.equals(f.getColumn()))
//							quickSearch = f;
//					}
					
					DomainClass representedClass = RPCCaller.getSinglton().getLoadedDomainTree().getDomainClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType));
//					final String searchString;
//					final boolean onlyScanStringRepresentation;
//					if(deepSearch != null) {
//						searchString = deepSearch.getValue();
//						onlyScanStringRepresentation = true;
//					}
//					else {
//						onlyScanStringRepresentation = false;
//						if(quickSearch != null)
//							searchString = quickSearch.getValue();
//						else
//							searchString = null;
//					}
					
					final boolean showResultAsList = filters.contains(AtomConfig.showResultAsListBooleanTransport);
					if(showResultAsList)
						filters.remove(AtomConfig.showResultAsListBooleanTransport);
					
					RPCCaller.getSinglton().loadListOfDomainObjects(representedClass, filters, null, 0, 25, false, null, false, false, onlyWriteables, true, new AsyncCallback<DomainObjectList>() {
						
						@Override
						public void onSuccess(DomainObjectList result) {
							if(result == null || result.getDomainObjects() == null || result.getDomainObjects().size() <= 0) {
								AtomTools.log(Level.INFO, "nichts gefunden", this);
								ListOfDomainObjectsView.this.showValue();
							}
							else if(result.getDomainObjects().size() < result.getTotalSize() || (showResultAsList && result.getTotalSize() > 1)) {
								getObjectsFromSelectorFrame(result, null, false);
							}
							else {
								initValueCollectionIfNull();
								value.addAll(result.getDomainObjects());
								ListOfDomainObjectsView.this.showValue();
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AtomTools.log(Level.SEVERE, "Fehler beim laden der Liste vom Server: " + caught, this);
						}
					});
				}

				@Override
				public void requestFailed(String reason) {
					// don't care;
				}
			});
			AtomTools.log(Level.INFO, "popup showed", this);
		}
		else {
			getObjectsFromSelectorFrame(null, null, true);
		}

		// popup.center();
		// AtomGUI.getSinglton().openList(
		// RPCCaller.getSinglton().getDomainTree(null,
		// false).getDomainClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType)));
	}
	
	private void getObjectsFromSelectorFrame(DomainObjectList preloadedResult, String searchString, boolean simpleSearch) {
		DomainObjectSelectorFrame frame = new DomainObjectSelectorFrame(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType), true,
				attributeName, preloadedResult, searchString, simpleSearch, onlyWriteables, new DomainObjectSelectionHandler() {

					@Override
					public void handleDomainObjectList(List<DomainObject> list) {
						initValueCollectionIfNull();
						value.addAll(list);
						App.addFrame(owningFrame);
						showValue();
					}

					@Override
					public void cancelSelection() {
						App.addFrame(owningFrame);
					}
				});
		App.addFrame(frame);
	}

	@Override
	public void goingInvisible() {
		if (madeDraggable) {
			App.unregisterDndDropController(domainObjectDropController);
			// if
			// (!ClientConfig.getBrowserType().toLowerCase().contains("chrom"))
			App.unRegisterDndWidget(listBox);
			AtomTools.log(Level.FINER, "ListOfDomainObjects - goingInvisible - madeNotDraggable", this);
			madeDraggable = false;
		}
	}

	@Override
	public void goingVisible() {
		if (!madeDraggable) {
			App.registerDndDropController(domainObjectDropController);
			// if
			// (!ClientConfig.getBrowserType().toLowerCase().contains("chrom"))
			App.registerDndWidget(listBox);
			AtomTools.log(Level.FINER, "ListOfDomainObjects - goingVisible - madeDraggable", this);
			madeDraggable = true;
		}
		showValue();
	}

	@Override
	protected void showValue() {
		super.showValue();
		((GxtListBoxProxy) this.listBox).fixElementBorders();
	}

	// @Override
	// public void resize(ResizeEvent event) {
	// ((GxtListBoxProxy) listBox).fixElementBorders();
	// }

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);

		if (!readOnly)
			listBox.setBackgroundColor(ClientConfig.colorDragReady);
	}

	@Override
	public void suggestDND(DomainClass dc) {
		if (dc == null || theMouseIsOverMe) {
			listBox.setBackgroundColor(ClientConfig.colorDragReady);
		} else if (dc.classOrSuperClassNamed(AtomTools.getListedType(ListOfDomainObjectsView.this.representedType))) {
			this.listBox.setBackgroundColor(ClientConfig.colorDomainClassMatch);
		} else {
			this.listBox.setBackgroundColor(ClientConfig.colorDragReady); // colorDragOverMismatch);
		}
	}
}
