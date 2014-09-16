/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition.ViewType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class RadioButtonsView extends AttributeView<String, RadioButtonsView, Boolean> {

	private static RadioButtonsViewUiBinder uiBinder = GWT
	.create(RadioButtonsViewUiBinder.class);

	interface RadioButtonsViewUiBinder extends
		UiBinder<Widget, RadioButtonsView> {
	}
	
	interface PanelStyle extends CssResource {
		String listBox();

		String button();
		String buttonTable();
		String labelTable();
	}

	@UiField
	protected PanelStyle panelStyle;
	
	@UiField
	protected FlowPanel panel;

	private List<RadioButton> radioButtons;
	private String radioButtonGroup;
	private ListBoxDefinition.ViewType viewType = null;
	
	private LinkedHashMap<String, String> keyDisplayMap = null;
	
	public RadioButtonsView(String radioButtonGroup, LinkedHashMap<String, String> keyDisplayMap, ListBoxDefinition.ViewType viewType) {
		super();
		this.radioButtonGroup = radioButtonGroup;
		this.keyDisplayMap = keyDisplayMap;
		this.viewType = viewType;

		buildDisplay();
	}
	
	public RadioButtonsView(DomainClass representedClass, String attributeName, ListBoxDefinition.ViewType viewType) {
		
		this.viewType = viewType;
		radioButtonGroup = representedClass.getName() + "." + attributeName;
		
		RPCCaller.getSinglton().loadListBoxChoices(representedClass, attributeName, new WaitingFor<LinkedHashMap<String,String>>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "getListBoxChoices failed -> " + reason, this);
			}
			
			@Override
			public void recieve(LinkedHashMap<String,String> result) {
				RadioButtonsView.this.keyDisplayMap = result;
				buildDisplay();
			}
		});
		
		
		if(keyDisplayMap == null) {
			keyDisplayMap = new LinkedHashMap<String, String>(1);
			keyDisplayMap.put(null, "loading, please wait..");
			buildDisplay();
		}
	}
		
	private void buildDisplay() {

		if (radioButtons == null) {
			radioButtons = new ArrayList<RadioButton>();
		} else {
			radioButtons.clear();
		}
		
		for (Entry<String, String> entry : keyDisplayMap.entrySet()) {
			RadioButton r = new RadioButton(radioButtonGroup, entry.getValue());
			r.addValueChangeHandler(getVCHandler());
		    radioButtons.add(r);
		}
		
		if(viewType == ViewType.RadioTable) {
			FlexTable table = new FlexTable();
			
			for (int i = 0; i < radioButtons.size(); i++) {
				RadioButton b = radioButtons.get(i);
				Label l = new Label(b.getText());
				l.setStyleName(panelStyle.labelTable());
				table.setWidget(0, i, l);
				
				b.setText("");
				b.setStyleName(panelStyle.buttonTable());

				table.setWidget(1, i, b);
			}
			panel.add(table);
		}
		else if(viewType == ViewType.RadioButtons) {
			for (RadioButton b : radioButtons) {
				b.setStyleName(panelStyle.button());
				b.setWordWrap(false);
				panel.add(b);
				panel.add(new HTMLPanel("span", " &nbsp;"));
			}
		}
		else {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "unknown RadioButtons ViewType = " + viewType.toString(), this);
		}
		
		showValue();
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
		Iterator<String> iterator = keyDisplayMap.keySet().iterator();
		for (int i = 0; i < radioButtons.size(); i++) {
			String key = iterator.next();
			if (key.equals(value)) {
				radioButtons.get(i).setValue(true);
			} else
				radioButtons.get(i).setValue(false);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		this.readOnly = readOnly;
		for (RadioButton b : radioButtons) {
			b.setEnabled(!readOnly);
		}
	}

	@Override
	protected void readValue() {
		Iterator<String> iterator = keyDisplayMap.keySet().iterator();
		for (int i = 0; i < radioButtons.size(); i++) {
			String key = iterator.next();
			if (radioButtons.get(i).getValue() == true) {
				value = key;
				break;
			}
		}
	}

	@Override
	public void setValue(Object value) {
		if(value != null) {
			this.value = value.toString();
		}
		else {
			value = null;
		}
		showValue();
	}

}