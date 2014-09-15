/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import at.ac.fhcampuswien.atom.client.gui.SpriteImagesAttributeGroupTabHeader.SpriteImagesAttributeGroupTabHeaderStyle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AttributeGroupTabHeader extends Composite {

	private static AttributeGroupTabHeaderUiBinder uiBinder = GWT.create(AttributeGroupTabHeaderUiBinder.class);

	interface AttributeGroupTabHeaderUiBinder extends UiBinder<Widget, AttributeGroupTabHeader> {
	}

	interface AttributeGroupTabHeaderStyle extends CssResource {
		String label();

		String label_inactive();
	}

	@UiField
	protected SpriteImagesAttributeGroupTabHeaderStyle style;

	@UiField
	protected AttributeGroupTabHeaderStyle attributeGroupTabHeaderStyle;
	
	// protected @UiField
	// FocusPanel focusPanel;
	// protected @UiField
	// DockLayoutPanel dockLayoutPanel;

	@UiField
	protected Label label;
	@UiField
	protected HTML pic;

	private String content;

	@UiConstructor
	public AttributeGroupTabHeader(String content) {
		this.content = content;

		initWidget(uiBinder.createAndBindUi(this));
		label.setWordWrap(false);
		label.setText(this.content);
		// label.setStylePrimaryName(attributeGroupTabHeaderStyle.inactive());

		// dockLayoutPanel.setStyleName(attributeGroupTabHeaderStyle.normal());
	}

	public void setActive() {
		pic.setStyleName(style.tabViewCornerActive());
		label.setStylePrimaryName(attributeGroupTabHeaderStyle.label());
	}

	public void setInactive() {
		pic.setStyleName(style.tabViewCornerInactive());
		label.setStylePrimaryName(attributeGroupTabHeaderStyle.label_inactive());
	}

	public void changeLabel(String newLabel) {
		label.setText(newLabel);
	}

	// @UiHandler("campusPanelButton")
	// void doMouseOver(MouseOverEvent event) {
	// //TODO: implement
	// }
	//
	// @UiHandler("campusPanelButton")
	// void doMouseOut(MouseOutEvent event) {
	// //TODO: implement
	// }
}
