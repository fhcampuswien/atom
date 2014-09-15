/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PopupObjectButton extends Composite implements HasClickHandlers {

	private static PopupObjectButtonUiBinder uiBinder = GWT.create(PopupObjectButtonUiBinder.class);

	interface PopupObjectButtonUiBinder extends UiBinder<Widget, PopupObjectButton> {
	}

	interface PopupObjectButtonStyle extends CssResource {
		String over();

		String normal();
		
		String activeSpace();
		
		String inActiveSpace();
	}

	@UiField
	protected PopupObjectButtonStyle popupObjectButtonStyle;
	@UiField
	protected HTML contentField;
	@UiField
	protected FocusPanel popupObjectButton;
	@UiField
	protected DockLayoutPanel panel;
	@UiField
	protected Image borderImage;

	public @UiConstructor
	PopupObjectButton(String content, boolean active) {
		initWidget(uiBinder.createAndBindUi(this));
		contentField.setHTML(content);
		this.active = active;
		// popupObjectButton.setStyleName(popupObjectButtonStyle.over());
	}

	public void prepareViewing() {
		borderImage.setResource(AtomClientBundle.INSTANCE.menuPopupNormal());
		popupObjectButton.setStyleName(popupObjectButtonStyle.normal());
		panel.setStyleName(popupObjectButtonStyle.inActiveSpace());
	}

	@UiHandler("popupObjectButton")
	void doMouseOver(MouseOverEvent event) {
		if(active) {
			borderImage.setResource(AtomClientBundle.INSTANCE.menuPopupOver());
			popupObjectButton.setStyleName(popupObjectButtonStyle.over());
			panel.setStyleName(popupObjectButtonStyle.activeSpace());
		}
	}

	@UiHandler("popupObjectButton")
	void doMouseOut(MouseOutEvent event) {
		borderImage.setResource(AtomClientBundle.INSTANCE.menuPopupNormal());
		popupObjectButton.setStyleName(popupObjectButtonStyle.normal());
		panel.setStyleName(popupObjectButtonStyle.inActiveSpace());
	}
	
	private boolean active = true;
	public void setActive(boolean active) {
		this.active = active;
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
