/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.Date;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateView extends AttributeView<Date, DateBox, String> {

	private String displayName;
	
	public DateView(String displayName) {
		super();
		this.displayName = displayName;
	}

	public DateView(Date value) {
		super(value);
	}

	@Override
	protected boolean createFieldWidget() {
		field = new DateBox(new DatePicker(), null, new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
		field.getDatePicker().getElement().getStyle().setZIndex(40);
		// new DatePicker(), null, GWT.create(DefaultFormat.class)

//		field.addValueChangeHandler(getVCHandler());
		field.getTextBox().addValueChangeHandler(getVCHandler());

		Style fieldStyle = field.getElement().getStyle();
		fieldStyle.setWidth(100, Unit.PCT);
		fieldStyle.setHeight(26, Unit.PX);

		fieldStyle.setPaddingLeft(3, Unit.PX);
		fieldStyle.setColor("#575A5F !important");
		fieldStyle.setFontSize(11, Unit.PX);
		fieldStyle.setProperty("fontFamily", "Verdana, Arial, Helvetica, sans-serif");

		fieldStyle.setProperty("borderTop", "1px solid #CCC");
		fieldStyle.setProperty("borderRight", "1px solid #999");
		fieldStyle.setProperty("borderBottom", "1px solid #999");
		fieldStyle.setProperty("borderLeft", "1px solid #999");

		// fieldStyle.setProperty("border", "2px #C9D5E0 inset");
		fieldStyle.setProperty("boxSizing", "border-box");
		fieldStyle.setProperty("WebkitBoxSizing", "border-box");
		fieldStyle.setProperty("MozBoxSizing", "border-box");

		return true;
		// field.getFormat()

		// field.setStylePrimaryName("StringViewTextBox");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void showValue() {

		if (this.value == null || (this.value.getHours() == 0 && this.value.getMinutes() == 0 && this.value.getSeconds() == 0))
			field.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
		else
			field.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy HH:mm"))); // :ss:SSS

		if (value != null) {
			field.setValue(value);
		} else {
			TextBox textBox = field.getTextBox();

			if (AtomConfig.nullReasonNotRelationEssential.equals(this.getNullReason())) {
				textBox.setText("Permission denied!");
			} else if (AtomConfig.nullReasonLazyLoading.equals(this.getNullReason())) {
				textBox.setText("bitte warten.. noch nicht fertig geladen..");
			} else {
				field.setValue(value);
			}
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		DateBox dateBox = ((DateBox) this.field);
		dateBox.setEnabled(!readOnly);
	}

	@Override
	protected void readValue() {
		/**
		 * we do the parsing ourself, because we want strict parsing and the default is lenient.
		 */
		String textValue = field.getTextBox().getValue();
		
		field.removeStyleName(AtomClientBundle.INSTANCE.css().attributeViewValidationError());
		try {
			if(textValue == null || textValue.length() <= 0)
				value = null;
			else if (textValue.length() == "dd.MM.yyyy HH:mm".length())
				value = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm").parseStrict(textValue);
			else if (textValue.length() == "dd.MM.yyyy".length())
				value = DateTimeFormat.getFormat("dd.MM.yyyy").parseStrict(textValue);
			else {
				field.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy HH:mm"))); // :ss:SSS
				field.getTextBox().setValue(textValue);
				value = field.getValue();
			}	
		}
		catch(IllegalArgumentException e) {
			field.addStyleName(AtomClientBundle.INSTANCE.css().attributeViewValidationError());
			throw new ValidationError(AtomTools.getMessages().invalid_date(displayName));
		}
	}
}
