/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.LinkedHashMap;
import java.util.Map;

import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.Notifiable;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ListBoxView extends AttributeView<String, ListBoxView, String> {

	private static ListBoxViewUiBinder uiBinder = GWT
	.create(ListBoxViewUiBinder.class);

	interface ListBoxViewUiBinder extends
		UiBinder<Widget, ListBoxView> {
	}
	
	interface PanelStyle extends CssResource {
		String listBox();
	}

	@UiField
	protected PanelStyle panelStyle;
	
//	protected SimplePanel simplePanel;

	@UiField
	protected ListBox listBox;
	
	private LinkedHashMap<String, String> keyDisplayMap = null;
	private String multiSelectSeperator = null;
	
	public ListBoxView(LinkedHashMap<String, String> keyDisplayMap, String multiSelectSeperator) {
		super();
		this.keyDisplayMap = keyDisplayMap;
		this.multiSelectSeperator = multiSelectSeperator;
		
		buildListBox();
	}
	
	public ListBoxView(DomainClass representedClass, String attributeName, String multiSelectSeperator) {
		this.multiSelectSeperator = multiSelectSeperator;
		
		RPCCaller.getSinglton().loadListBoxChoices(representedClass, attributeName, new WaitingFor<LinkedHashMap<String,String>>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Log.LOG_LEVEL_ERROR, "getListBoxChoices failed -> " + reason, this);
			}
			
			@Override
			public void recieve(LinkedHashMap<String,String> result) {
				ListBoxView.this.keyDisplayMap = result;
				buildListBox();
			}
		});
		
		
		if(keyDisplayMap == null) {
			keyDisplayMap = new LinkedHashMap<String, String>(1);
			keyDisplayMap.put(null, "loading, please wait..");
			buildListBox();
		}
	}
	
//	private void buildDisplay() {
//		if(viewType == AtomConfig.listBoxViewTypeDropDown) {
//			buildListBox();
//		}	
//		else if(viewType == AtomConfig.listBoxViewTypeRadioButtons) {
//			buildRadioButtons();
//		}
//		else {
//			AtomTools.log(Log.LOG_LEVEL_ERROR, "unknown ListBoxDisplayType: " + viewType, this);
//		}
//	}
//	
//	private void buildRadioButtons() {
//		if(radioButtons == null) {
//			radioButtons = new ArrayList<RadioButton>();
//		}
//		else {
//			radioButtons.clear();
//		}
//		
//	}
	
//	private void createListBox() {
//		listBox = new ListBox();
//		listBox.setStyleName(panelStyle.listBox());
//		simplePanel.add(listBox);
////		<g:ListBox ui:field="listBox" styleName='{panelStyle.listBox}' />
//	}
	
	private void buildListBox() {
		
//		if(listBox == null) 
//			createListBox();
		
//		if(listBoxID.equals(AtomConfig.listBoxKeysOnly)) {
//			listBox.clear();
//			for(String s : keys) {
//				listBox.addItem(s, s);
//			}
//		}
//		else if(listBoxID.equals(AtomConfig.listBoxKeysNValues)) {
//			listBox.clear();
//			if(keys.length != display.length) {
//				AtomTools.log(Log.LOG_LEVEL_ERROR, "keys and display arrays have different length! cannot build ListBox items!", this);
//			}
//			else for(int i = 0 ; i < keys.length ; i++) {
//				listBox.addItem(display[i], keys[i]);
//			}
//		}
//		else if(listBoxID.equals(AtomConfig.listBoxGeschlecht)) {
//			listBox.clear();
//			listBox.addItem("Männlich", "M");
//			listBox.addItem("Weiblich", "W");
//		}
//		else {
//			AtomTools.log(Log.LOG_LEVEL_ERROR, "unkown ListBoxID:" + listBoxID, this);
//		}
		
		listBox.clear();
		
		if(multiSelectSeperator != null && !"".equals(multiSelectSeperator)) {
			listBox.setMultipleSelect(true);
			listBox.getElement().getStyle().setHeight(60, Unit.PX);
		}
		
		for (Map.Entry<String, String> entry : keyDisplayMap.entrySet()) {
			listBox.addItem(entry.getValue(), entry.getKey());
		}
		
		showValue();
		
		listBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "listBox Value changed", this);
				validateAndMark("irrelevant");
				if(changeHandlers != null) {
					readValue();
					for(Notifiable<Object> ch : changeHandlers) {
						ch.doNotify(value);
					}
				}
			}
		});
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
		
		if(multiSelectSeperator != null && multiSelectSeperator != "") {
			unselectAll();
			for(String v : value.split(multiSelectSeperator)) {
				showSingleValue(v, false);
			}
		}
		else {
			boolean found = showSingleValue(value, true);
			
			if(!found) {
				if(!"".equals(listBox.getValue(listBox.getItemCount()-1))) {
					listBox.addItem("", "");
				}
				listBox.setSelectedIndex(listBox.getItemCount()-1);
			}
		}
	}
	
	private void unselectAll() {
		for(int i=0 ; i < listBox.getItemCount() ; i++ ) {
			listBox.setItemSelected(i, false);
		}
	}
	
	private boolean showSingleValue(String value, boolean deselectOthers) {

		if(value == null || "".equals(value)) {
			return false; //nothing to show
		}

		boolean found = false;
		for(int i=0 ; i < listBox.getItemCount() ; i++ ) {
			if(listBox.getValue(i).equals(value.toString())) {
				if(deselectOthers)
					listBox.setSelectedIndex(i);
				else
					listBox.setItemSelected(i, true);
				
				found = true;
				break;
			}
		}
		return found;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		this.readOnly = readOnly;
		listBox.setEnabled(!readOnly);
	}

	@Override
	protected void readValue() {
		if(multiSelectSeperator != null && multiSelectSeperator != "") {
			value = "";
			for(int i=0 ; i < listBox.getItemCount() ; i++ ) {
				if(listBox.isItemSelected(i)) {
					if("".equals(value))
						value = listBox.getValue(i);
					else
						value = value + multiSelectSeperator + listBox.getValue(i);
				}
			}
		}
		else {
			int index = listBox.getSelectedIndex();
			value = listBox.getValue(index);
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
		buildListBox();
	}

}
