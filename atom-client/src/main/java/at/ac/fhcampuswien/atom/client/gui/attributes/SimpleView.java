/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.shared.AtomConfig;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class SimpleView<F extends Object> extends AttributeView<F, TextBox, String> {

	private static SimpleViewUiBinder uiBinder = GWT
	.create(SimpleViewUiBinder.class);

	interface SimpleViewUiBinder extends
		UiBinder<Widget, SimpleView<?>> {
	}
	
	interface PanelStyle extends CssResource {
		String textBox();
	}

	@UiField
	protected TextBox textBox;
	
	@Override
	protected boolean createFieldWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		field = textBox;
		textBox.addKeyUpHandler(new MyKeyUpHandler());

		textBox.addValueChangeHandler(getVCHandler());
		
		return false;
	}
	
	/**
	 * @see at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView#showValue()
	 */
	@Override
	protected void showValue() {
		if(value != null) {
			textBox.setText(value.toString());
		}
		else if(AtomConfig.nullReasonNotRelationEssential.equals(this.getNullReason())) {
			textBox.setText("Permission denied!");
		}
		else if(AtomConfig.nullReasonLazyLoading.equals(this.getNullReason())) {
			textBox.setText("bitte warten.. noch nicht fertig geladen..");
		}
		else if(this.getNullReason() != null) {
			textBox.setText("Unbekannte Null-Reason - Bitte an Entwickler melden: " + this.getNullReason().toString());
		}
		else {
			textBox.setText("");
		}
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		field.setEnabled(!readOnly);
	}
}
