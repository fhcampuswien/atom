/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import java.util.ArrayList;

import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget.ActionMode;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.DomainObjectSearchResult;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DomainObjectSearchFrame extends Frame {

	private static DomainObjectSearchViewUiBinder uiBinder = GWT.create(DomainObjectSearchViewUiBinder.class);

	interface DomainObjectSearchViewUiBinder extends UiBinder<Widget, DomainObjectSearchFrame> {
	}

	interface PanelStyle extends CssResource {
		String whiteSpace();

		String space();
		
		String loadingLabel();
	}

	@UiField
	protected PanelStyle panelStyle;
	@UiField
	protected SimplePanel center;
	@UiField
	protected SimplePanel northPanel;

	private AtomTabPanel tabPanel;
	private Label loadingLabel;
	private ArrayList<DomainObjectListWidget> lists;
	private final boolean onlyScanStringRepresentation, onlyRelated;

	private int pageSize = 5;

	public DomainObjectSearchFrame(String searchTerm, boolean onlyScanStringRepresentation, boolean onlyRelated) {
		super("Suche nach \"" + searchTerm + "\"", "Suche nach \"" + searchTerm + "\"", CenterHeader.State.EMPTY, me, null, null, onlyScanStringRepresentation ? AtomConfig.FrameType.SEARCH_SIMPLE : AtomConfig.FrameType.SEARCH);

		initWidget(uiBinder.createAndBindUi(this));

		this.representedSearchTerm = searchTerm;
		this.onlyScanStringRepresentation = onlyScanStringRepresentation;
		this.onlyRelated = onlyRelated;

		tabPanel = new AtomTabPanel();
		tabPanel.setStyleName(panelStyle.space());

		center.add(tabPanel.getDeckPanel());
		northPanel.add(tabPanel.getTabBar());

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			private int previousSelected = -1;

			public void onSelection(SelectionEvent<Integer> event) {
				int selected = event.getSelectedItem().intValue();
				if (lists != null) {
					if (previousSelected != -1) {
						lists.get(previousSelected).goingInvisible();
					}
					AtomTools.log(Level.FINER, "search: switch tab, resizeSingle, lastResizeEvent=" + lastResizeEvent, this);
					lists.get(selected).goingVisible();
					lists.get(selected).resizeSingle(lastResizeEvent);
				}
				previousSelected = selected;
			}
		});

		final AttributeGroupTabHeader attributeGroupTabHeader = new AttributeGroupTabHeader("bitte warten...");
		attributeGroupTabHeader.setStyleName(panelStyle.space());
		loadingLabel = new Label("Bitte warten, Durchsuche Datenbank und lade Ergebnis ...");
		loadingLabel.setStyleName(panelStyle.loadingLabel());
		tabPanel.add(loadingLabel, attributeGroupTabHeader);

		pageSize = DomainObjectListWidget.getRowsFitting(Window.getClientHeight(), true);

		//FIXME: acquire value for onlyScanClassWithName from GUI
		RPCCaller.getSinglton().searchDomainObjects(searchTerm, onlyScanStringRepresentation, pageSize, onlyRelated, null, new AsyncCallback<DomainObjectSearchResult>() {

			public void onFailure(Throwable caught) {
				loadingLabel.setText("Fehler beim laden des Suchergebnisses vom Server: " + caught);
				loadingLabel.setStyleName(panelStyle.loadingLabel());
				AtomTools.log(Level.SEVERE, "Fehler beim laden des Suchergebnisses vom Server: " + caught, this);
			}

			public void onSuccess(DomainObjectSearchResult resultHolder) {
				if (resultHolder.getResult().size() > 0) {
					tabPanel.clear();
					lists = new ArrayList<DomainObjectListWidget>(resultHolder.getResult().size());
					for (DomainObjectList subResult : resultHolder.getResult()) {
						if (subResult.getDomainObjects().size() > 0) {
							DomainClass classOfList = subResult.getDomainClass();
							AttributeGroupTabHeader attributeGroupTabHeader = new AttributeGroupTabHeader(classOfList.getPluralName() + " ("
									+ subResult.getTotalSize() + ")");
							attributeGroupTabHeader.setStyleName(panelStyle.space());
							DomainObjectListWidget domainObjectListWidget = new DomainObjectListWidget(classOfList, subResult, resultHolder.getSearchTerm(), DomainObjectSearchFrame.this.onlyScanStringRepresentation, null, DomainObjectSearchFrame.this.onlyRelated, true, ActionMode.DEFAULT_OPEN, DomainObjectSearchFrame.this);
							lists.add(domainObjectListWidget);
							DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
							dockLayoutPanel.setStyleName(panelStyle.whiteSpace());
							dockLayoutPanel.addNorth(new Label(""), 3);
							dockLayoutPanel.addWest(new Label(""), 10);
							dockLayoutPanel.addSouth(new Label(""), 2);
							dockLayoutPanel.add(domainObjectListWidget);
							tabPanel.add(dockLayoutPanel, attributeGroupTabHeader);
						}
					}
				} else {
					loadingLabel.setText("Fuer diesen Suchbegriff wurden keine Ergebnisse gefunden.");
					attributeGroupTabHeader.changeLabel("nichts gefunden");
				}
			}
		});
	}

	ResizeEvent lastResizeEvent;

	@Override
	public void resize(ResizeEvent event) {
		lastResizeEvent = event;
		if (lists != null && lists.size() > 0 && tabPanel.getSelectedIndex() != -1)
			lists.get(tabPanel.getSelectedIndex()).resize(event);
	}

	@Override
	public void goingVisible() {
		if (lists != null && lists.size() > 0 && tabPanel.getSelectedIndex() != -1)
			lists.get(tabPanel.getSelectedIndex()).goingVisible();
	}

	@Override
	public boolean goingInvisible() {
		if (lists != null && lists.size() > 0 && tabPanel.getSelectedIndex() != -1)
			lists.get(tabPanel.getSelectedIndex()).goingInvisible();
		return true;
	}
	
	@Override
	public Boolean isSimpleSearch() {
		return onlyScanStringRepresentation;
	}
}
