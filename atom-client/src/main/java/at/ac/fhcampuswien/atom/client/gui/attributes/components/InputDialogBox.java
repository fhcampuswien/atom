/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes.components;

import java.util.Arrays;
import java.util.logging.Level;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.ClientTools;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class InputDialogBox extends DialogBox {
	
	//alt ist besser
	public interface Callback {
		public void processInput(Object oldValue, String newValue, Boolean checked);
		public void inputCanceled();
	}
	
	public InputDialogBox(final Object oldValue, final String[] dropDownValues, boolean useSuggestBox, boolean allowOtherValues, final Callback callback) {
		this(oldValue, dropDownValues, "Bitte geben Sie das neue Element ein.", null, null, useSuggestBox, allowOtherValues, callback);
	}
	
	private Widget inputBoxOrContainer;
	private FocusWidget inputBox;
	private CheckBox checkBox;
	private Callback callback;
	private Object oldValue;
	private boolean allowOtherValues;
	private String[] dropDownValues;
	
	public InputDialogBox(Object oldValue, String[] dropDownValues, String title, Boolean checkBoxDefault, String checkBoxLabel, boolean useSuggestBox, boolean allowOtherValues, Callback callback) {
		super(false, true);
		this.callback = callback;
		this.oldValue = oldValue;
		this.allowOtherValues = allowOtherValues;
		this.dropDownValues = dropDownValues;

		final String oldValueString = (oldValue != null) ? oldValue.toString() : null;

		AtomTools.log(Level.INFO, "show popup to enter a new entry", this);
		
		if(dropDownValues == null || dropDownValues.length <= 0) {
			inputBox = new TextBox();
			inputBox.addKeyPressHandler(new KeyPressHandler() {
				
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if(ClientTools.hasEnterKeyBeenPressed(event)) {
						commit(false);
					}
				}
			});
		}
		else {
			if(useSuggestBox) {
				MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
				for(String s : dropDownValues) {
					if(s != null && s.length()>0)
						oracle.add(s);
				}
				SuggestBox suggestBox = new SuggestBox(oracle);
				inputBox = suggestBox.getValueBox();
				inputBoxOrContainer = suggestBox;
			}
			else {
				ListBox listBox = new ListBox();
				listBox.setMultipleSelect(false);
				for(String s : dropDownValues) {
					listBox.addItem(s);
				}
				inputBox = listBox;
			}
		}

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		
		if(checkBoxDefault != null) {
//			HorizontalPanel checkPanel = new HorizontalPanel();
			checkBox = new CheckBox(checkBoxLabel);
			checkBox.setValue(checkBoxDefault);
		}
		else {
			checkBox = null;
		}
		
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button cancelButton = new Button("Abbrechen", new ClickHandler() {
			public void onClick(ClickEvent event) {
				commit(true);
			}
		});
		Button commitButton = new Button("Bestätigen", new ClickHandler() {
			public void onClick(ClickEvent event) {
				commit(false);
			}
		});

		if (oldValueString != null && oldValueString.length() > 0)
			setValueOfInputBox(inputBox, oldValueString);

		inputBox.setSize("100%", "20px");
		commitButton.setSize("100%", "26px");
		cancelButton.setSize("100%", "26px");
		buttonPanel.setSize("100%", "100%");

		buttonPanel.add(cancelButton);
		buttonPanel.add(commitButton);
		dockLayoutPanel.addSouth(buttonPanel, 26);
		if(checkBoxDefault != null)
			dockLayoutPanel.addSouth(checkBox, 26);
		
		if(inputBoxOrContainer != null)
			dockLayoutPanel.add(inputBoxOrContainer);
		else
			dockLayoutPanel.add(inputBox);
		
		dockLayoutPanel.setSize("100%", ((checkBoxDefault != null ? 26 : 0) + 65) + "px");
		dockLayoutPanel.getElement().getStyle().setProperty("minWidth", "200px");

		this.setText(title);
		this.setWidget(dockLayoutPanel);
		this.getElement().getStyle().setZIndex(40);
		// dialogBox.setSize("100%", "100%");
		this.center();
		inputBox.setFocus(true);
		// dialogBox.setVisible(true);
		AtomTools.log(Level.INFO, "popup showed", this);

	}
	
	private void commit(boolean cancelled) {
		if(cancelled){
			callback.inputCanceled();
		}
		else {
			String text = getValueOfInputBox(inputBox);
			if(!(allowOtherValues || Arrays.stream(dropDownValues).anyMatch(text::equals))) {
				if(AtomTools.getMessages().illegalValue().equals(getText()))
					setText(AtomTools.getMessages().illegalValue() + " (1)");
				else
					setText(AtomTools.getMessages().illegalValue());
				return;
			}
			callback.processInput(oldValue, text, checkBox == null ? null : checkBox.getValue());
		}
		InputDialogBox.this.hide();
	}
	
	private String getValueOfInputBox(FocusWidget inputBox) {
		if(inputBox instanceof TextBox) {
			return ((TextBox) inputBox).getText();	
		}
		else if(inputBox instanceof ListBox) {
			ListBox listBox = ((ListBox) inputBox);
			return listBox.getItemText(listBox.getSelectedIndex());
		}
		throw new AtomException("inputBox must be either TextBox or ListBox, but was " + inputBox.toString());
	}
	
	private void setValueOfInputBox(FocusWidget inputBox, String value) {
		if(inputBox instanceof TextBox) {
			((TextBox) inputBox).setText(value);
		}
		else if(inputBox instanceof ListBox) {
			ListBox listBox = ((ListBox) inputBox);
			boolean done = false;
			for(int i=0 ; i < listBox.getItemCount() || done ; i++) {
				if(listBox.getValue(i).equals(value)) {
					listBox.setSelectedIndex(i);
					done = true;
				}
			}
			if(!done) {
				listBox.addItem(value);
				listBox.setSelectedIndex(listBox.getItemCount()-1);
			}
		}
	}
}
