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

import java.util.logging.Level;
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
	private int multiSelectSize;
	private boolean hideNonSelectedInReadMode = false;
	
	public ListBoxView(LinkedHashMap<String, String> keyDisplayMap, String multiSelectSeperator, int multiSelectSize, boolean hideNonSelectedInReadMode) {
		super();
		this.keyDisplayMap = keyDisplayMap;
		this.multiSelectSeperator = multiSelectSeperator;
		this.multiSelectSize = multiSelectSize;
		this.hideNonSelectedInReadMode = hideNonSelectedInReadMode;
		
		buildListBox();
	}
	
	public ListBoxView(DomainClass representedClass, String attributeName, String multiSelectSeperator, int multiSelectSize, boolean hideNonSelectedInReadMode) {
		this.multiSelectSeperator = multiSelectSeperator;
		this.multiSelectSize = multiSelectSize;
		this.hideNonSelectedInReadMode = hideNonSelectedInReadMode;
		
		RPCCaller.getSinglton().loadListBoxChoices(representedClass, attributeName, new WaitingFor<LinkedHashMap<String,String>>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.SEVERE, "getListBoxChoices failed -> " + reason, this);
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
//			AtomTools.log(Level.SEVERE, "unknown ListBoxDisplayType: " + viewType, this);
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
//				AtomTools.log(Level.SEVERE, "keys and display arrays have different length! cannot build ListBox items!", this);
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
//			AtomTools.log(Level.SEVERE, "unkown ListBoxID:" + listBoxID, this);
//		}
		
		
		if(multiSelectSeperator != null && !"".equals(multiSelectSeperator)) {
			listBox.setMultipleSelect(true);
			listBox.getElement().getStyle().setHeight(multiSelectSize, Unit.PX);
		}
		
		
		if(hideNonSelectedInReadMode && multiSelectSeperator != null && !"".equals(multiSelectSeperator)) {
			setReadOnly(this.readOnly);			
		}
		else {
			listBox.clear();
			if(keyDisplayMap != null)
			for (Map.Entry<String, String> entry : keyDisplayMap.entrySet()) {
				listBox.addItem(entry.getValue(), entry.getKey());
			}
			showValue();
		}
		
		listBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				AtomTools.log(Level.FINER, "listBox Value changed", this);
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
		
		if(multiSelectSeperator != null && !"".equals(multiSelectSeperator)) {
			unselectAll();
			if(value != null) for(String v : value.split(multiSelectSeperator)) {
				showSingleValue(v, false);
			}
		}
		else {
			boolean found = showSingleValue(value, true);
			
			if(!found) {
				listBox.addItem(value, value);
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
		if(!hideNonSelectedInReadMode || !readOnly)
		for(int i=0 ; i < listBox.getItemCount() ; i++ ) {
			if(listBox.getValue(i).equals(value)) {
				if(deselectOthers)
					listBox.setSelectedIndex(i);
				else
					listBox.setItemSelected(i, true);
				
				found = true;
				break;
			}
		}
		if(!found) {
			listBox.addItem(value, value);
			if(!hideNonSelectedInReadMode && !readOnly)
				if(multiSelectSeperator != null && !"".equals(multiSelectSeperator))
					listBox.setSelectedIndex(listBox.getItemCount()-1);
				else
					listBox.setItemSelected(listBox.getItemCount()-1, true);
		}
		return found;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		this.readOnly = readOnly;
		listBox.setEnabled(!readOnly);
		
		if(hideNonSelectedInReadMode && multiSelectSeperator != null && !"".equals(multiSelectSeperator)) {
			if(readOnly) {
				listBox.clear();
			}
			else {
				listBox.clear();
				if(keyDisplayMap != null)
				for (Map.Entry<String, String> entry : keyDisplayMap.entrySet()) {
					listBox.addItem(entry.getValue(), entry.getKey());
				}
			}
			showValue();
		}
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
