/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.dnd.DndLabel;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CenterHeader extends Composite {

	private static CenterHeaderUiBinder uiBinder = GWT.create(CenterHeaderUiBinder.class);

	interface CenterHeaderUiBinder extends UiBinder<Widget, CenterHeader> {
	}

	interface PanelStyle extends CssResource {
		String text();

		String header();
	}

	@UiField
	protected PanelStyle panelStyle;
	@UiField
	protected DockLayoutPanel root;
	@UiField
	protected Image loadingGif;
	@UiField
	protected DndLabel centerHeaderLabel;
	@UiField
	protected FlowPanel headerLoadingPanel;

	private HTML neuButton = new HTML(">NEU");
	private HTML filterButton = new HTML(">FILTERN");
	private HTML editButton = new HTML(">BEARBEITEN");
	private HTML deleteButton = new HTML(">L&Ouml;SCHEN");
	private HTML saveButton = new HTML(">SPEICHERN");
	private HTML cancelButton = new HTML(">ABBRECHEN");
	private HTML exportButton = new HTML(">EXPORT");
	private HTML importButton = new HTML(">IMPORT");
	private HTML duplicateButton = new HTML(">DUPLIZIEREN");

	private int buttonSpace = 0;

	public CenterHeader() {
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();

		App.registerDndWidget(centerHeaderLabel);

		neuButton.ensureDebugId("CenterHeader-neuButton");
		filterButton.ensureDebugId("CenterHeader-filterButton");
		editButton.ensureDebugId("CenterHeader-editButton");
		deleteButton.ensureDebugId("CenterHeader-deleteButton");
		cancelButton.ensureDebugId("CenterHeader-cancelButton");
		exportButton.ensureDebugId("CenterHeader-exportButton");
		importButton.ensureDebugId("CenterHeader-importButton");
		importButton.ensureDebugId("CenterHeader-duplicateButton");

		setLoading(false);
	}

	private void initButtons() {
		neuButton.setStyleName(panelStyle.text());
		filterButton.setStyleName(panelStyle.text());
		editButton.setStyleName(panelStyle.text());
		deleteButton.setStyleName(panelStyle.text());
		saveButton.setStyleName(panelStyle.text());
		cancelButton.setStyleName(panelStyle.text());
		exportButton.setStyleName(panelStyle.text());
		importButton.setStyleName(panelStyle.text());
		duplicateButton.setStyleName(panelStyle.text());

		neuButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionNew();
			}
		});

		filterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionFilter();
			}
		});

		editButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionEdit();
			}
		});

		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionDelete();
			}
		});

		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionSave();
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionCancel();
			}
		});

		exportButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionExport();
			}
		});

		importButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionImport();
			}
		});
		
		duplicateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				App.actionDuplicate();
			}
		});
	}

	public void setHeader(String newHeader) {
		if(newHeader == null)
			AtomTools.log(Level.SEVERE, "there is something seriously wrong here, parameter newHeader must not be null!", this);
		
		String shortened = App.getShortenedString(newHeader, panelStyle.header(), getOffsetWidth() - buttonSpace - 10);
		centerHeaderLabel.setText(shortened);
		if(newHeader.equals(shortened)) {
			//clear tooltip if the whole name fits in the label
			centerHeaderLabel.setTitle(null);
		}
		else {
			//set tooltip if the label is smaller than the content we want to write into it with the whole name
			centerHeaderLabel.setTitle(newHeader);
		}
	}

	public void setRepresentedDomainObject(DomainObject representedObject) {
		centerHeaderLabel.setRepresentedDomainObject(representedObject);
	}

	public void setLoading(boolean currentlyLoading) {
		loadingGif.setVisible(currentlyLoading);
	}

	protected void addButton(HTML button) {
		int width = App.getTextWidth(button.getText(), panelStyle.text());
		buttonSpace += width + 3;
		root.addEast(button, width + 3);
	}

	protected void addSpace() {
		root.addEast(new HTML(), 10);
		buttonSpace += 10;
	}

	public void setState(State newState) {
		// root.remove(neuButton);
		// root.remove(filterButton);
		// root.remove(editButton);
		// root.remove(deleteButton);
		// root.remove(saveButton);
		// root.remove(cancelButton);

		root.clear();
		root.addEast(new HTML(), 17);
		buttonSpace = 17;
		
		newState.buildState(this);
		root.add(headerLoadingPanel);
	}

	public enum State {
		OBJECT_LIST_NORMAL {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.importButton);
				panel.addSpace();
				panel.addButton(panel.exportButton);
				panel.addSpace();
				panel.addButton(panel.neuButton);
			}
		},
		OBJECT_LIST_READONLY {
			protected void buildState(CenterHeader panel) {
				// panel.addButton(panel.filterButton);
				panel.addButton(panel.importButton);
				panel.addSpace();
				panel.addButton(panel.exportButton);
			}
		},
		OBJECT_DETAIL_VIEW_NORMAL {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.editButton);
				panel.addSpace();
				panel.addButton(panel.deleteButton);
				panel.addSpace();
				panel.addButton(panel.duplicateButton);
			}
		},
		OBJECT_DETAIL_VIEW_NO_DELETE {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.editButton);
				panel.addSpace();
				panel.addButton(panel.duplicateButton);
			}
		},
		OBJECT_DETAIL_VIEW_ONLY_DUPLICATE {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.duplicateButton);
			}
		},
		OBJECT_EDIT_NORMAL {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.saveButton);
				panel.addSpace();
				panel.addButton(panel.cancelButton);
				panel.addSpace();
				panel.addButton(panel.deleteButton);
			}
		},
		OBJECT_EDIT_NEW {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.saveButton);
				panel.addSpace();
				panel.addButton(panel.cancelButton);
			}
		},
		OBJECT_EDIT_NO_DELETE {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.saveButton);
				panel.addSpace();
				panel.addButton(panel.cancelButton);
			}
		},
		OBJECT_SELECTOR_NO_NEW {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.saveButton);
				panel.addSpace();
				panel.addButton(panel.cancelButton);
			}
		},
		OBJECT_SELECTOR_WITH_NEW {
			protected void buildState(CenterHeader panel) {
				panel.addButton(panel.neuButton);
				panel.addSpace();
				panel.addButton(panel.saveButton);
				panel.addSpace();
				panel.addButton(panel.cancelButton);
			}
		},
		EMPTY {
			protected void buildState(CenterHeader panel) {
			}
		};

		protected abstract void buildState(CenterHeader panel);
	}
}
