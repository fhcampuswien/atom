/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.ClientConfig;
import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.client.gui.attributes.CollectionView;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.ObservableListBoxProxy;
import at.ac.fhcampuswien.atom.client.gui.dnd.AtomDNDWidget;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectValueProvider.Special;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.PersistentString;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class GxtListBoxProxy extends ObservableListBoxProxy<Collection<DomainObject>, DomainObject> implements AtomDNDWidget, HasMouseDownHandlers,
		HasTouchStartHandlers {

	private ListView<DomainObject, String> myListBox;
	private ListStore<DomainObject> myListStore;
	
	private Set<ChangeHandler> changeHandlers = new HashSet<ChangeHandler>();

	public GxtListBoxProxy(CollectionView<Collection<DomainObject>, DomainObject> view) {
		super(view, false);
		myListStore = new ListStore<DomainObject>(new ModelKeyProvider<DomainObject>() {

			@Override
			public String getKey(DomainObject item) {
				if(item == null || item.getObjectID() == null) {
					AtomTools.log(Log.LOG_LEVEL_WARN, "damn, item is null or has no objectid?!", this);
					return "null";
				}
				return item.getObjectID().toString();
			}
		});
		myListBox = new ListView<DomainObject, String>(myListStore, new DomainObjectValueProvider<String>(Special.stringRepresentation));
		
		myListBox.setSize("100%", "100%");

		myListBox.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		
		myListBox.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<DomainObject>() {

			@Override
			public void onSelectionChanged(SelectionChangedEvent<DomainObject> event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "GxtListBox SelectionChanged: " + "CheckBox ListView (" + event.getSelection().size() + " items selected)",
						this);
				for (ChangeHandler changeHandler : changeHandlers) {
					changeHandler.onChange(null);
				}
			}
		});

		this.sinkEvents(Event.ONDBLCLICK);
		this.setSize("100%", "100%");
		this.add(myListBox);

		myListBox.addHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
		
//				Event event = be.getEvent();
				NativeEvent ne = event.getNativeEvent();
				int charCode = event.getCharCode();
				int keyCode = ne.getKeyCode();

				if ((charCode == 13 && keyCode == 13) || (charCode == 0 && keyCode == 13)) {
					openSelectedElement();
				}

				// AtomTools.log(Log.LOG_LEVEL_TRACE, "GxtListBox KeyPress BaseEvent fired: " + charCode + ";" + keyCode + ";" + be, this);
				AtomTools.log(Log.LOG_LEVEL_TRACE, "GxtListBox KeyPressed: Browser='" + ClientConfig.getBrowserType() + "', charCode=" + charCode
						+ ", keyCode=" + keyCode + " ; be='" + ne + "'", this);

				// chrome: charCode == 13 && keyCode == 13
				// firefox: charCode == 0 && keyCode == 13

				// NativeEvent nativeEvent = be.get
				//
				// int keyCode = nativeEvent.getKeyCode();
				// // AtomTools.log(Log.LOG_LEVEL_TRACE, "login_passwordbox_keyPress; UnicodeCharCode=\""+unicode+"\" ; keyCode=\""+ keyCode
				// +" ; CharCode=\""+String.valueOf(code)+"\"<endofline>", this);
				//
				// if(code == '\n' || code == '\r' || (unicode == 0 && keyCode == 13)) {
				// if (representedObjects.size() > 0 && DndListBox.this.getSelectedIndex() != -1)
				// AtomGUI.getSinglton().openDetailView(representedObjects.get(DndListBox.this.getSelectedIndex()), null, false);
				// }

		
			}
		}, KeyPressEvent.getType());
		

		// this.addKeyPressHandler(new KeyPressHandler() {
		//
		// @Override
		// public void onKeyPress(KeyPressEvent event) {
		// //GWT BUG: http://code.google.com/p/google-web-toolkit/issues/detail?id=5558
		//
		// }
		// });
	}

	private void openSelectedElement() {
		int selectedIndex = getSelectedIndex();
		if (selectedIndex >= 0)
			App.openDetailView((DomainObject) elements.get(selectedIndex), null, false);
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "doubleclick: '" + event + "'", this);
			openSelectedElement();
			// ListOfDomainObjectsView.this.editItemAtIndex(); elements.get(1)
		}
		super.onBrowserEvent(event);
	}

	@Override
	public Widget getListBoxWidget() {
		return myListBox;
	}

	@Override
	public boolean isItemSelected(int index) {
		List<DomainObject> selectedList = myListBox.getSelectionModel().getSelectedItems();
		for (DomainObject m : selectedList) {
			if(indexes.get(m).equals(index))
				return true;
//			if (((Integer) m.get("index")).intValue() == index)
//				return true;
		}
		// return elements.get(index).toString().equals(myListBox.getSelectedText());
		return false;
	}

	private Map<DomainObject, Integer> indexes = new HashMap<DomainObject, Integer>();
	
	@Override
	public void addItem(DomainObject item) {
		elements.put(itemCount, item);
		indexes.put(item, itemCount);
		
//		BaseModelData model = new BaseModelData();
//		model.set("name", item.toString());
//		model.set("index", Integer.valueOf(itemCount));

		myListStore.add(item);

		// myListBox.add(item.toString());
		// myListBox.a
		itemCount++;
	}

	@Override
	public void setTempLabel(String label) {
		
//		BaseModelData model = new BaseModelData();
//		model.set("name", label);
//		model.set("index", Integer.valueOf(-1));
//		myListStore.add(model);
		//FIXME: no easy fix available...
		// myListBox.
		PersistentString tmp = new PersistentString(label);
		elements.put(itemCount, tmp);
		indexes.put(tmp, itemCount);
		myListStore.add(tmp);
	}

	// public com.google.gwt.user.client.Element getElement() {
	// return myListBox.getElement();
	// }

	@Override
	public void clear() {
		itemCount = 0;
		myListStore.clear();
		// myListBox.clear();
	}

	@Override
	public int getSelectedIndex() {
		List<DomainObject> selectedList = myListBox.getSelectionModel().getSelectedItems();
		if (selectedList.size() > 1)
			AtomTools.log(Log.LOG_LEVEL_WARN, "this method asks for a single selected index, but the size of the selection is: " + selectedList.size(), this);

		if (selectedList.size() == 0)
			return -1;

		else
			return indexes.get(selectedList.get(0));
	}

	@Override
	public void addChangeHandler(ChangeHandler handler) {
		this.changeHandlers.add(handler);
//		myListBox.addHandler(handler, new GwtEvent.Type<ChangeHandler>());
		// myListBox.addHandler(handler, type)
		// myListBox.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
		//
		// @Override
		// public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
		// handler.onChange(null);
		// }
		// });
	}

	private Label proxyLabel;

	@Override
	public Widget getDndProxy(int x, int y) {
		String string = AtomTools.domainObjectsString(getRepresentedDomainObjects());
		if (proxyLabel == null)
			proxyLabel = new Label(string);
		else
			proxyLabel.setText(string);
		// proxyLabel.setStylePrimaryName(this.getStylePrimaryName());
		return proxyLabel;
	}

	@Override
	public void previewDragStart(int x, int y) throws VetoDragException {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "GxtListBoxProxy.previewDragStart() called with x=" + x + ", y=" + y + ", offsetWidth=" + this.getOffsetWidth(), this);
		if (myListBox == null || myListBox.getSelectionModel() == null || myListBox.getSelectionModel().getSelectedItems() == null
				|| myListBox.getSelectionModel().getSelectedItems().size() == 0 || x > this.getOffsetWidth() - 17) {
			throw new VetoDragException();
		}
	}

	@Override
	public List<DomainObject> getRepresentedDomainObjects() {
//		List<BaseModelData> selectedModels = myListBox.getSelectionModel().getSelectedItems();
		List<DomainObject> selectedObjects = myListBox.getSelectionModel().getSelectedItems();

//		for (BaseModelData model : selectedModels) {
//			selectedObjects.add(elements.get((Integer) model.get("index")));
//		}

		if (proxyLabel != null)
			proxyLabel.setText(AtomTools.domainObjectsString(selectedObjects));

		return selectedObjects;
	}
	
	public DomainObject getRepresentedDomainObject() {
		List<DomainObject> list = getRepresentedDomainObjects();
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		fixElementBorders();
//		if(readOnly)
//			myListBox.disable();
//		else
//			myListBox.enable();
//		myListBox.setEnabled(!readOnly);
	}
	
	public void fixElementBorders() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "GxtListBoxProxy.fixElementBorders() elements.size() = " + myListBox.getElements().size(), this);
		for(Element el : myListBox.getElements()) {
			el.getStyle().setBackgroundColor("");
			if (readOnly) {
				el.removeClassName(AtomClientBundle.INSTANCE.css().sensitiveBorder());
				el.addClassName(AtomClientBundle.INSTANCE.css().insensitiveBorder());
			} else {
				el.removeClassName(AtomClientBundle.INSTANCE.css().insensitiveBorder());
				el.addClassName(AtomClientBundle.INSTANCE.css().sensitiveBorder());
			}
//			el.getStyle().setBorderColor("green");
		}
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		fixElementBorders();
	}
	
	@Override
	public void setBackgroundColor(String color) {
		super.setBackgroundColor(color);
		for(Element el : myListBox.getElements()) {
			el.getStyle().setBorderColor(color);
		}
	}
}