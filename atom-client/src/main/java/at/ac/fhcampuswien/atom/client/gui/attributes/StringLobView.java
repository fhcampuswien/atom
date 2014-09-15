/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TextArea;

public class StringLobView extends AttributeView<String, TextArea, String> {

	/**
	 * needs to be overridden for non-string values that might be represented by
	 * StringView because there is no special for its type.
	 */
	@Override
	public void setValue(Object value) {
		if (value != null)
			this.value = value.toString();
		else
			this.value = "";
		showValue();
	}

	@Override
	protected boolean createFieldWidget() {
		field = new TextArea();
		field.addKeyUpHandler(new MyKeyUpHandler());
		field.addValueChangeHandler(getVCHandler());
		
		Style fieldStyle = field.getElement().getStyle();
		fieldStyle.setWidth(100, Unit.PCT);
		fieldStyle.setHeight(150, Unit.PX);
		fieldStyle.setProperty("border", "2px inset");
		fieldStyle.setProperty("boxSizing", "border-box");
		
		fieldStyle.setPaddingLeft(3, Unit.PX);
		fieldStyle.setColor("#575A5F !important");
		fieldStyle.setFontSize(11, Unit.PX);
		fieldStyle.setProperty("fontFamily", "Verdana, Arial, Helvetica, sans-serif");
		
		fieldStyle.setProperty("borderTop", "1px solid #CCC");
		fieldStyle.setProperty("borderRight", "1px solid #999");
		fieldStyle.setProperty("borderBottom", "1px solid #999");
		fieldStyle.setProperty("borderLeft", "1px solid #999");
		
		//fieldStyle.setProperty("border", "2px #C9D5E0 inset");
		fieldStyle.setProperty("boxSizing", "border-box");
		fieldStyle.setProperty("WebkitBoxSizing", "border-box");
		fieldStyle.setProperty("MozBoxSizing", "border-box");
		
//		fieldStyle.setProperty("borderCollapse", "collapse");
		return true;
//		field.setStylePrimaryName("StringViewTextBox");
	}

	@Override
	protected void showValue() {
		((TextArea) this.field).setText(value);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		TextArea textArea = ((TextArea) this.field);
		textArea.setReadOnly(readOnly);
	}

	@Override
	protected void readValue() {
		this.value = ((TextArea) this.field).getText();
	}
}
