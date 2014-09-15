/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.InputDialogBox;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.InputDialogBox.Callback;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.ObservableListBoxProxy;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CollectionView<C extends Collection<T>, T extends Object> extends AttributeView<C, Widget, C> implements ChangeHandler {

	private static CollectionViewUiBinder uiBinder = GWT.create(CollectionViewUiBinder.class);

	interface CollectionViewUiBinder extends UiBinder<Widget, CollectionView<?, ?>> {
	}

	interface PanelStyle extends CssResource {
		String listBox();
	}
	
	@UiField
	protected PanelStyle panelStyle;

	@UiField
	protected SimplePanel listBoxPanel;
	

	
	protected ObservableListBoxProxy<C, T> listBox;

	@UiField
	protected PushButton searchButton;
	@UiField
	protected PushButton addButton;
	@UiField
	protected PushButton removeButton;

	private boolean empty;

	private HashMap<String, T> elementsStrings;
	

	@UiHandler("searchButton")
	protected void onClick_searchButton(ClickEvent event) {
		findOrCreateItemAndAddToCollection(true);
	}
	
	@UiHandler("addButton")
	protected void onClick_addButton(ClickEvent event) {
		findOrCreateItemAndAddToCollection(false);
	}

	@UiHandler("removeButton")
	protected void onClick_removeButton(ClickEvent event) {
		AtomTools.log(Log.LOG_LEVEL_INFO, "remove selected", this);
		for (int i = listBox.getItemCount() - 1; i >= 0; i--) {
			if (listBox.isItemSelected(i)) {
				value.remove(elementsStrings.get(listBox.getItemText(i)));
			}
		}
		showValue();
	}

	@Override
	protected boolean createFieldWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		listBox = createListBox();
		listBoxPanel.add(listBox);
		
		field = listBox.getListBoxWidget();
		field.addStyleName(panelStyle.listBox());
		field.addStyleName(AtomClientBundle.INSTANCE.css().attributeViewGeneral());
		
		listBox.addChangeHandler(this);
		removeButton.setEnabled(false);
		return false;
	}

	protected ObservableListBoxProxy<C, T> createListBox() {
		return new ObservableListBoxProxy<C, T>(this, true);
	}

	@Override
	protected void showValue() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "CollectionView.showValue() called", this);
		
		listBox.clear();

		if (elementsStrings == null) {
			elementsStrings = new HashMap<String, T>();
		} else {
			elementsStrings.clear();
		}

		if (value != null && value.size() != 0) {
			
			List<T> displayList = null;
			if(value instanceof List<?>) {
				displayList = (List<T>) value;
			}
			else {
//			if(java.util.HashSet.class.equals(value.getClass())) {
				displayList = new ArrayList<T>(value);
				
				Collections.sort(displayList,
		                 new Comparator<T>()
		                 {
		                     public int compare(T f1, T f2)
		                     {
		                         return f1.toString().compareTo(f2.toString());
		                     }        
		                 });
			}
				
				
			AtomTools.log(Log.LOG_LEVEL_DEBUG, "CollectionView.value.type = " + value.getClass().toString(), this);
			for (T item : displayList) {
				String string = item.toString();
				listBox.addItem(item);
				elementsStrings.put(string, item);
			}

			empty = false;
		} else {
			empty = true;
			
			if(AtomConfig.nullReasonNotRelationEssential.equals(this.getNullReason())) {
				listBox.setTempLabel("Keine Berechtigung");
			}
			else if(AtomConfig.nullReasonLazyLoading.equals(this.getNullReason())) {
				listBox.setTempLabel("bitte warten.. noch nicht fertig geladen..");
			}
			else {
//				listBox.setTempLabel("leere Liste");
			}
		}
		setReadOnly(readOnly);
		
//		if(listBox instanceof GxtListBoxProxy) {
//			((GxtListBoxProxy) listBox).fixElementBorders();
//		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		listBox.setReadOnly(readOnly);
		searchButton.setEnabled(!readOnly);
		addButton.setEnabled(!readOnly);
		AtomTools.log(Log.LOG_LEVEL_TRACE, "CollectionView setReadOnly removeButtonLogic = " + (!readOnly && listBox.getSelectedIndex() != -1 && !empty), this);
		removeButton.setEnabled(!readOnly && listBox.getSelectedIndex() != -1 && !empty);
	}

	@Override
	protected void readValue() {

	}

	@SuppressWarnings("unchecked")
	protected void addNewItem(Object newItem) {
		if (newItem != null) {
			if (value == null) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "creating arraylist for generic collection, this could be wrong!", this);
				value = (C) new ArrayList<Object>();
			}
			value.add((T) newItem);
		}
	}

	protected void findOrCreateItemAndAddToCollection(boolean search) {
		AtomTools.log(Log.LOG_LEVEL_INFO, "show popup to enter a new entry", this);
		
		new InputDialogBox(null, null, new InputDialogBox.Callback() {
			
			@Override
			public void processInput(Object oldValue, String newValue, Boolean checked) {
				CollectionView.this.addNewItem(getObjectForString(newValue));
				CollectionView.this.showValue();
			}
			
			@Override
			public void inputCanceled() {
				// don't care;
				
			}
		});
		
		AtomTools.log(Log.LOG_LEVEL_INFO, "popup showed", this);
	}

	/**
	 * should be overridden by collectionViews which can get/generate 
	 * their wanted objects from a string that identifies it.
	 * 
	 * @param string that represents/identifies the object
	 * @return the object that is represented by the given string
	 */
	protected Object getObjectForString(String string) {
		return string;
	}

	public void onChange(ChangeEvent event) {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "ListBox onChange called: " + listBox.getSelectedIndex(), this);
		setReadOnly(readOnly);
		// decideDragable();
	}

	public void editItemAtIndex(int index) {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "edit index " + index, this);
		if (!this.readOnly) {
			new InputDialogBox(index == -1 ? null : listBox.getItem(index), null, new Callback() {
				
				@Override
				public void processInput(Object oldValue, String newValue, Boolean checked) {
					CollectionView.this.editItem(oldValue, newValue);
				}
				
				@Override
				public void inputCanceled() {
					// don't care;
				}
			});
		}
	}

	public void editItem(Object item, String newStrinValue) {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "If the Edit String Mechanism of the CollectionView is used, the editItem method must be overwritten", this);
	}

	// @Override
	// public void onClick(ClickEvent event) {
	// AtomTools.log(Log.LOG_LEVEL_TRACE, "ListBox onClick called; "
	// + event.getSource().hashCode() + ";"
	// + event.getNativeEvent().hashCode() + ";"
	// + event.getRelativeElement().hashCode() + ";"
	// + event.getAssociatedType().getName() + ";"
	// + event.getAssociatedType().hashCode() + ";"
	// + event.getAssociatedType().toString() + ";"
	// + event.hashCode() + ";"
	// + event.toDebugString() + ";"
	// , this);
	// }

}
