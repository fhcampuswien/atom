/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget.ActionMode;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class DomainObjectListFrame extends ExternalLoaderFrame {

	private static DomainObjectListFrameUiBinder uiBinder = GWT.create(DomainObjectListFrameUiBinder.class);

	interface DomainObjectListFrameUiBinder extends UiBinder<Widget, DomainObjectListFrame> {
	}

	interface PanelStyle extends CssResource {
		String space();
	}

	@UiField
	protected PanelStyle panelStyle;
	@UiField
	protected SimplePanel root;

	private DomainObjectListWidget domainObjectListWidget;
	
	private boolean relatedOnly, onlyWriteables, quickSearch;

	public DomainObjectListFrame(DomainClass domainClass, String searchTerm, boolean quickSearch, DataFilter[] filters, boolean relatedOnly, boolean onlyWriteables) {
		super();
		
		this.isLoading = true;
		
		this.representedClass = domainClass;
		this.representedSearchTerm = searchTerm;
		this.quickSearch = quickSearch;
		this.relatedOnly = relatedOnly;
		this.onlyWriteables = onlyWriteables;
		this.dataFilters = filters;
		
		updateTitles();
		
		
		init(longTitle, shortTitle, getCenterHeaderState(domainClass), me, null, domainClass, frameType);
		
		initWidget(uiBinder.createAndBindUi(this));

		makeRPCCallerCacheListBoxValues();
		
		AtomTools.log(Level.FINER, "DomainObjectListFrame Constructor finished (" + domainClass.getName() + " - " + relatedOnly + ")", this);

		// RPCCaller.getSinglton().loadListOfDomainObjects(representedClass,
		// filters, sorters, fromRow, pageSize, false, listNotifier);
	}
	
	@Override
	protected void updateTitles() {
		String pName = representedClass.getPluralName();
		String pseudoSearchTerm = representedSearchTerm;
		
		if(dataFilters != null && dataFilters.length == 1 && (pseudoSearchTerm == null || pseudoSearchTerm.length() < 1)) {
			pseudoSearchTerm = dataFilters[0].getValue();
			frameType = FrameType.FILTERCLASS;
		}
		
		if(pseudoSearchTerm != null && pseudoSearchTerm.length() > 0) {
			longTitle = "Durchsuche " + pName + " nach \"" + pseudoSearchTerm + "\"";
			shortTitle = pName + " (" + pseudoSearchTerm + ")";
			if(frameType == null) {
				if(quickSearch) {
					frameType = FrameType.SEARCHCLASS_SIMPLE;
				}
				else {
					frameType = FrameType.SEARCHCLASS;
				}
			}
		}
		else {
			if(relatedOnly) {
				longTitle = AtomTools.getMessages().liste_von_related(pName);
				shortTitle = pName + " (" + AtomTools.getMessages().related() + ")";
				frameType = FrameType.LIST_RELATED;
			}
			else {
				longTitle = AtomTools.getMessages().liste_von(pName);
				shortTitle = pName;
				frameType = FrameType.LIST_ALL;
			}
			if(dataFilters != null && dataFilters.length > 0) {
				frameType = FrameType.FILTERCLASS;
				String filterString = " [Filter:";
				for(DataFilter f : dataFilters) {
					filterString += " " + f.getColumn() + AtomTools.getOneCharFilterType(f.getFilterType()) + f.getValue() + ";";
				}
				filterString = filterString.substring(0, filterString.length()-1) + "]";
				longTitle += filterString;
				shortTitle += filterString;
			}
		}
	}
	
	private int listBoxValuesCachingOpenCalls = 0;
	private boolean listBoxCachingFinishedLoop = false;
	
	private void makeRPCCallerCacheListBoxValues() {
		
		listBoxCachingFinishedLoop = false;
		
		for(DomainClassAttribute attribute : representedClass.getSortedAttributesListView()) {
			String listBoxSql = attribute.getListBoxSql();
			if(listBoxSql != null && listBoxSql.length() > 0) {
				listBoxValuesCachingOpenCalls++;
				
				RPCCaller.getSinglton().loadListBoxChoices(representedClass, attribute.getName(), new WaitingFor<LinkedHashMap<String, String>>() {

					@Override
					public void requestFailed(String reason) {
						AtomTools.log(Level.SEVERE, "getListBoxChoices failed -> " + reason, this);
						listBoxValuesCachingOpenCalls--;
					}

					@Override
					public void recieve(LinkedHashMap<String, String> result) {
						listBoxValuesCachingOpenCalls--;
						if(listBoxValuesCachingOpenCalls == 0 && listBoxCachingFinishedLoop == true) {
							createWidget();
						}
					}
				});
				
			}
		}
		listBoxCachingFinishedLoop = true;
		
		if(listBoxValuesCachingOpenCalls == 0) {
			createWidget();
		}
	}

	private void createWidget() {
		domainObjectListWidget = new DomainObjectListWidget(representedClass, null, representedSearchTerm, quickSearch, dataFilters, relatedOnly, onlyWriteables, false, ActionMode.DEFAULT_OPEN, DomainObjectListFrame.this);
		root.add(domainObjectListWidget);
	}

	static private CenterHeader.State getCenterHeaderState(DomainClass domainClass) {
		Set<String> at = domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession());
		if (!domainClass.getIsAbstract()
				&& AtomTools.isAccessAllowed(AtomConfig.accessCreateNew, at)) {
			// if(!domainClass.getIsAbstract() && domainClass.isAccessAllowed(RPCCaller.getSinglton().getClientSession(), AtomConfig.accessReadWrite)) {
			if(domainClass.isExportable())
				return CenterHeader.State.OBJECT_LIST_NORMAL;
			else
				return CenterHeader.State.OBJECT_LIST_ONLYNEW;
		} else if (AtomTools.isAccessAllowed(AtomConfig.accessSomeReadOnly, at)) {
			// else if(domainClass.isAccessAllowed(RPCCaller.getSinglton().getClientSession(), AtomConfig.accessLinkage)) {
			if(domainClass.isExportable())
				return CenterHeader.State.OBJECT_LIST_READONLY;
			else
				return CenterHeader.State.EMPTY;
		} else {
			throw new AtomException("User has no Permissions on the DomainClass '" + domainClass.getName() + "'");
		}
	}

	// private class ListNotifier implements AsyncCallback<List<DomainObject>>,
	// Notifiable {
	//
	// @Override
	// public void doNotify(String notifyReason) {
	// // i don't have the logic for filtering and ordering on the client!
	// // displayData(RPCCaller.getSinglton().getLoadedList(representedClass));
	// // therefore, reload from server:
	// RPCCaller.getSinglton().loadListOfDomainObjects(representedClass,
	// filters, sorters, fromRow, pageSize, true, ListNotifier.this);
	// }
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// domainObjectListWidget.showError("Beim laden der Daten vom Server ist ein Fehler aufgetreten.");
	// AtomTools.log(Level.SEVERE, "fehler beim laden der liste: " +
	// caught, this);
	// }
	//
	// @Override
	// public void onSuccess(List<DomainObject> result) {
	// // domainObjectListWidget.displayNewList(result);
	// }

	// }

	@Override
	public void resize(ResizeEvent event) {
		if(domainObjectListWidget != null)
			domainObjectListWidget.resize(event);
	}

	@Override
	public void goingVisible() {
		if(domainObjectListWidget != null)
			domainObjectListWidget.goingVisible();
	}

	@Override
	public boolean goingInvisible() {
		if(domainObjectListWidget != null)
			domainObjectListWidget.goingInvisible();
		return true;
	}

	public boolean isRelatedOnly() {
		return relatedOnly;
	}
}
