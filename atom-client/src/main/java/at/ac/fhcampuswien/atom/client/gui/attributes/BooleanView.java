/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.HashMap;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class BooleanView extends AttributeView<Boolean, BooleanView, Boolean> {

	private static SimpleViewUiBinder uiBinder = GWT
	.create(SimpleViewUiBinder.class);

	@UiField
	protected RadioButton trueButton;
	@UiField
	protected RadioButton falseButton;
	@UiField
	protected RadioButton nullButton;
	
	
	private String attributeName = "";

	interface SimpleViewUiBinder extends
		UiBinder<Widget, BooleanView> {
	}
	
	interface PanelStyle extends CssResource {
		String textBox();
	}
	
//	private String attributeName, trueLabel, falseLabel, nullLabel;
	
	public BooleanView(String attributeName, HashMap<Boolean, String> booleanMeanings) {
		this(attributeName, booleanMeanings.get(true), booleanMeanings.get(false), booleanMeanings.get(null));
	}
	
	public BooleanView(String attributeName, String trueLabel, String falseLabel, String nullLabel) {
//		this.attributeName = attributeName;
//		this.trueLabel = trueLabel;
//		this.falseLabel = falseLabel;
//		this.nullLabel = nullLabel;
		
		this.attributeName = attributeName;
		
		trueButton.setName(attributeName);
		falseButton.setName(attributeName);
		nullButton.setName(attributeName);
		
		if(trueLabel != null)
			trueButton.setText(trueLabel);
		if(falseLabel != null)
			falseButton.setText(falseLabel);
		if(nullLabel != null)
			nullButton.setText(nullLabel);
		
		trueButton.addValueChangeHandler(getVCHandler());
		falseButton.addValueChangeHandler(getVCHandler());
		nullButton.addValueChangeHandler(getVCHandler());
	}
	
	@Override
	protected boolean createFieldWidget() {
		field = this;
		initWidget(uiBinder.createAndBindUi(this));

		return false;
	}
	
	/**
	 * @see at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView#showValue()
	 */
	@Override
	protected void showValue() {
		trueButton.setValue(value == null ? false : value);
		falseButton.setValue(value == null ? false : !value);
		nullButton.setValue(value == null ? true : false);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		trueButton.setEnabled(!readOnly);
		falseButton.setEnabled(!readOnly);
		nullButton.setEnabled(!readOnly);
	}

	@Override
	protected void readValue() {
		if(trueButton.getValue())
			value = true;
		else if (falseButton.getValue())
			value = false;
		else if (nullButton.getValue())
			value = null;
		else
			AtomTools.log(Level.WARNING, "none of the 3 booleanView options (true, false, null) is selected! attribute:" + attributeName, this);
	}

}
