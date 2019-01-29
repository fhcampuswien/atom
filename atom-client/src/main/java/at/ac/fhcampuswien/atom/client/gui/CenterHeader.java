/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import java.util.HashMap;
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

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.dnd.DndLabel;
import at.ac.fhcampuswien.atom.shared.AtomMessages;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

public class CenterHeader extends Composite {

	private static AtomMessages msg = AtomTools.getMessages();
	private static final String[] links = { msg.linkNew(), msg.linkFilter(), msg.linkEdit(), msg.linkDelete(),
			msg.linkSave(), msg.linkCancel(), msg.linkExport(), msg.linkImport(), msg.linkDuplicate() };

	private static void onClick(String link) {
		if(link.equals(msg.linkNew()))
			App.actionNew();
		else if(link.equals(msg.linkFilter()))
			App.actionFilter();
		else if(link.equals(msg.linkEdit()))
			App.actionEdit();
		else if(link.equals(msg.linkDelete()))
			App.actionDelete();
		else if(link.equals(msg.linkSave()))
			App.actionSave();
		else if(link.equals(msg.linkCancel()))
			App.actionCancel();
		else if(link.equals(msg.linkExport()))
			App.actionExport();
		else if(link.equals(msg.linkImport()))
			App.actionImport();
		else if(link.equals(msg.linkDuplicate()))
			App.actionDuplicate();
	}
	
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

	private HashMap<String, HTML> buttons = new HashMap<>();

	private int buttonSpace = 0;

	public CenterHeader() {
		initWidget(uiBinder.createAndBindUi(this));
		initButtons();

		App.registerDndWidget(centerHeaderLabel);

		setLoading(false);
	}

	private void initButtons() {
		
		for(String link : links) {
			final String finalLink = link;
			HTML button = new HTML(link);
			button.setStyleName(panelStyle.text());
			button.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					CenterHeader.onClick(finalLink);
				}
			});
			buttons.put(link, button);
		}
	}

	public void setHeader(String newHeader) {
		if (newHeader == null)
			AtomTools.log(Level.SEVERE,
					"there is something seriously wrong here, parameter newHeader must not be null!", this);

		String shortened = App.getShortenedString(newHeader, panelStyle.header(), getOffsetWidth() - buttonSpace - 10);
		centerHeaderLabel.setText(shortened);
		if (newHeader.equals(shortened)) {
			// clear tooltip if the whole name fits in the label
			centerHeaderLabel.setTitle(null);
		} else {
			// set tooltip if the label is smaller than the content we want to
			// write into it with the whole name
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


	public void setVisibleButtons(String[] links) {
		root.clear();
		root.addEast(new HTML(), 17);
		buttonSpace = 17;
		boolean first = true;
		if (buttons != null)
			for (String link : links) {
				if (first)
					first = false;
				else
					addSpace();

				addButton(buttons.get(link));
			}
		root.add(headerLoadingPanel);
	}
	
	public void setState(State newState) {
		setVisibleButtons(newState.links);
	}
	
	public enum State {
		OBJECT_LIST_NORMAL(new String[] {msg.linkImport(), msg.linkExport(), msg.linkNew()}),
		OBJECT_LIST_ONLYNEW(new String[] {msg.linkNew()}),
		OBJECT_LIST_READONLY(new String[] {msg.linkExport()}),
		OBJECT_DETAIL_VIEW_NORMAL(new String[] {msg.linkEdit(), msg.linkDelete(), msg.linkDuplicate()}),
		OBJECT_DETAIL_VIEW_ONLY_EDIT(new String[] {msg.linkEdit()}),
		OBJECT_DETAIL_VIEW_ONLY_DUPLICATE(new String[] {msg.linkDuplicate()}),
		OBJECT_EDIT_NORMAL(new String[] {msg.linkSave(), msg.linkCancel(), msg.linkDelete()}),
		OBJECT_EDIT_NEW(new String[] {msg.linkSave(), msg.linkCancel()}),
		OBJECT_EDIT_NO_DELETE(new String[] {msg.linkSave(), msg.linkCancel()}),
		OBJECT_SELECTOR_NO_NEW(new String[] {msg.linkSave(), msg.linkCancel()}),
		OBJECT_SELECTOR_WITH_NEW(new String[] {msg.linkNew(), msg.linkSave(), msg.linkCancel()}),
		EMPTY(new String[] {});
		
		private String[] links;
		private State(String[] links) {
			this.links = links;
		}
	}
}
