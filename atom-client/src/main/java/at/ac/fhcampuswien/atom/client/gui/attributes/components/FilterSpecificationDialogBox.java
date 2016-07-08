/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;

public class FilterSpecificationDialogBox extends DialogBox {
	
	private class FilterSpecLine extends HorizontalPanel {
		
		DomainClassAttribute attribute;
		ListBox fieldSelection, filterTypeBox;
		TextBox textBox = null;
		DateBox dateBox = null;
		Button removeLineButton;
		
		private TextBox getTextBox() {
			if(textBox == null)
				textBox = new TextBox();
			return textBox;
		}
		
		private DateBox getDateBox() {
			if(dateBox == null)
				dateBox = new DateBox(new DatePicker(), null, new DefaultFormat(DateTimeFormat.getFormat("dd.MM.yyyy")));
			return dateBox;
		}
		
		public FilterSpecLine() {
			super();
			
			fieldSelection = new ListBox();
			fieldSelection.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					attribute = attributesMap.get(fieldSelection.getValue(fieldSelection.getSelectedIndex()));
					updateFilterSpecLineForSelectedAttribute();
					hideSelectedAttributesFromOtherFilterSelections();
				}
			});
			
			HashSet<String> selectedAttributes = getSelectedAttributes();
			for(DomainClassAttribute d : attributes) {
				if(!selectedAttributes.contains(d.getName()))
					fieldSelection.addItem(d.getDisplayName(), d.getName());
			}
			
			fieldSelection.setSelectedIndex(0);
			Style fsStyle = fieldSelection.getElement().getStyle();
			fsStyle.setHeight(27, Unit.PX);
			fsStyle.setMargin(2, Unit.PX);
			this.add(fieldSelection);

			attribute = attributesMap.get(fieldSelection.getValue(0));
			filterTypeBox = new ListBox();
			Style ftStyle = filterTypeBox.getElement().getStyle();
			ftStyle.setHeight(27, Unit.PX);
			ftStyle.setWidth(36, Unit.PX);
			ftStyle.setMargin(2, Unit.PX);

			this.add(filterTypeBox);			
			
			removeLineButton = new Button("-", new ClickHandler() {
				public void onClick(ClickEvent event) {
					filterSpecificationPanel.remove(FilterSpecLine.this);
					lines.remove(FilterSpecLine.this);
					updateRemoveabilityOfLines();
					hideSelectedAttributesFromOtherFilterSelections();
				}
			});
			Style rlStyle = removeLineButton.getElement().getStyle();
			rlStyle.setMargin(2, Unit.PX);

			updateFilterSpecLineForSelectedAttribute();
			
			lines.add(FilterSpecLine.this);
			updateRemoveabilityOfLines();
			hideSelectedAttributesFromOtherFilterSelections();
		}
		
		String[] lastAttributeTypes;
		private void updateFilterSpecLineForSelectedAttribute() {
			String[] types = AtomTools.getComperator4Type(attribute.getType());
			if(DomainClassAttribute.dateType.equals(attribute.getType())) {
				if(textBox != null)
					this.remove(textBox);
				this.remove(removeLineButton);
				this.add(getDateBox());
				this.add(removeLineButton);
				dateBox.setFocus(true);
			} else {
				if(dateBox != null)
					this.remove(dateBox);
				this.remove(removeLineButton);
				this.add(getTextBox());
				this.add(removeLineButton);
				textBox.setFocus(true);
			}
			if(!Arrays.equals(types, lastAttributeTypes)) {
				filterTypeBox.clear();
				for(String s : types) {
					filterTypeBox.addItem(s);
				}
				lastAttributeTypes = types;
			}
		}
	}
	
	private WaitingFor<Collection<DataFilter>> callback;
	private ArrayList<DomainClassAttribute> attributes = null;
	private HashMap<String, DomainClassAttribute> attributesMap = null;
	private boolean showShowListButton = false;
	private VerticalPanel filterSpecificationPanel = null;
	private HorizontalPanel buttonPanel = null;
	private ArrayList<FilterSpecLine> lines = new ArrayList<FilterSpecificationDialogBox.FilterSpecLine>();
	

	public FilterSpecificationDialogBox(String title, ArrayList<DomainClassAttribute> attributes, boolean showShowListButton, WaitingFor<Collection<DataFilter>> callback) {
		super(false, true);
		this.callback = callback;
		this.attributes = attributes;
		this.showShowListButton = showShowListButton;
		
		attributesMap = new HashMap<String, DomainClassAttribute>(attributes.size());
		for(DomainClassAttribute a : attributes) {
			attributesMap.put(a.getName(), a);
		}
		
		setupFilterSpecificationGUI();
		this.setText(title);
	}
	
	
	private void updateRemoveabilityOfLines() {
		int size = lines.size();
		for(FilterSpecLine l : lines) {
			l.removeLineButton.setEnabled(size > 1);
		}
	}
	
	private void setupFilterSpecificationGUI() {
		
		filterSpecificationPanel = new VerticalPanel();
		filterSpecificationPanel.add(new FilterSpecLine());
		
		buttonPanel = new HorizontalPanel();
		Button cancelButton = new Button("Abbrechen", new ClickHandler() {
			public void onClick(ClickEvent event) {
				commit(null);
			}
		});
		Button commitButton = new Button(showShowListButton ? "Hinzufügen" : "Suchen", new ClickHandler() {
			public void onClick(ClickEvent event) {
				commit(false);
			}
		});
		Button addLineButton = new Button("+", new ClickHandler() {
			public void onClick(ClickEvent event) {
				addFilterSpecificationLine();
			}
		});
		buttonPanel.add(cancelButton);
		buttonPanel.add(commitButton);
		
		if(showShowListButton) {
			Button listButton = new Button("Auswahlliste anzeigen", new ClickHandler() {
				public void onClick(ClickEvent event) {
					commit(true);
				}
			});
			buttonPanel.add(listButton);
		}
		
		buttonPanel.add(addLineButton);
		
		filterSpecificationPanel.add(buttonPanel);
		this.setWidget(filterSpecificationPanel);
		this.getElement().getStyle().setZIndex(40);
		// dialogBox.setSize("100%", "100%");
		this.center();
		// dialogBox.setVisible(true);
		AtomTools.log(Level.INFO, "popup showed", this);
	}
	
	private void addFilterSpecificationLine() {		
		filterSpecificationPanel.remove(buttonPanel);
		filterSpecificationPanel.add(new FilterSpecLine());
		filterSpecificationPanel.add(buttonPanel);
	}
	
	private HashSet<String> getSelectedAttributes() {
		HashSet<String> selectedAttributes = new HashSet<String>(lines.size());
		for(FilterSpecLine l : lines) {
			selectedAttributes.add(l.fieldSelection.getValue(l.fieldSelection.getSelectedIndex()));
		}
		return selectedAttributes;
	}
	
	private void hideSelectedAttributesFromOtherFilterSelections() {
		HashSet<String> selectedAttributes = getSelectedAttributes();
		
		for(FilterSpecLine line : lines) {
			ListBox l = line.fieldSelection;
			String val = l.getValue(l.getSelectedIndex());

			l.clear();
			int i = 0;
			int iv = -1;
			
			for(DomainClassAttribute d : attributes) {
				
				if(val.equals(d.getName()) || !selectedAttributes.contains(d.getName())) {
					l.addItem(d.getDisplayName(), d.getName());
					
					if(val.equals(d.getName())) {
						iv = i;
					}
					i++;
				}
				l.setSelectedIndex(iv);
			}
		}
	}

	private void commit(Boolean showSelection) {
		if(showSelection == null){
			callback.requestFailed("user pressed cancel button");
		}
		else {
			HashSet<DataFilter> p = new HashSet<DataFilter>(lines.size());
			//HashMap<String,Object> p = new HashMap<String,Object>();
			
			for(FilterSpecLine line : lines) {
				ListBox l = line.fieldSelection;
				TextBox valueBox = (DomainClassAttribute.dateType.equals(line.attribute.getType())) ? line.dateBox.getTextBox() : line.textBox;
				String filterType = line.filterTypeBox.getValue(line.filterTypeBox.getSelectedIndex());
				String attrName = l.getValue(l.getSelectedIndex());
				String filterValue = valueBox.getText();
				String attrType = attributesMap.get(attrName).getType();
				p.add(new DataFilter(attrName, filterValue, filterType, attrType));
			}
			
			if(showSelection)
				p.add(AtomConfig.showResultAsListBooleanTransport);
			callback.recieve(p);
		}
		FilterSpecificationDialogBox.this.hide();
	}
}
