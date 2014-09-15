/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import java.util.ArrayList;
import java.util.List;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DndListBox extends ListBox implements AtomDNDWidget {
	// SourcesMouseEvents

	private ArrayList<DomainObject> representedObjects = new ArrayList<DomainObject>();

	// private MouseListenerCollection mouseListeners;

	public DndListBox() {
		super(true);
		this.setVisibleItemCount(5);
		AtomTools.log(Log.LOG_LEVEL_TRACE, "DndListBox created", this);
		this.sinkEvents(Event.ONDBLCLICK);
//		this.getElement().getStyle().setProperty("border", "2px inset");
		
		this.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				//GWT BUG: http://code.google.com/p/google-web-toolkit/issues/detail?id=5558
				int unicode = event.getUnicodeCharCode();
				char code = event.getCharCode(); 
				NativeEvent nativeEvent = event.getNativeEvent();
				int keyCode = nativeEvent.getKeyCode();
//				AtomTools.log(Log.LOG_LEVEL_TRACE, "login_passwordbox_keyPress; UnicodeCharCode=\""+unicode+"\" ; keyCode=\""+ keyCode +" ; CharCode=\""+String.valueOf(code)+"\"<endofline>", this);
				
				if(code == '\n' || code == '\r' || (unicode == 0 && keyCode == 13)) { 
					if (representedObjects.size() > 0 && DndListBox.this.getSelectedIndex() != -1)
						App.openDetailView(representedObjects.get(DndListBox.this.getSelectedIndex()), null, false);
				}
			}
		});
		
//		this.addMouseDownHandler(new MouseDownHandler() {
//			
//			@Override
//			public void onMouseDown(MouseDownEvent event) {
//				AtomTools.log(Log.LOG_LEVEL_TRACE, "MouseDownEvent: '" + event + "'", this);
//			}
//		});
	}

	public void addItem(DomainObject item) {
		// representedObjects.clear();
		representedObjects.add(item);
		// value = newValue;
		// this.setText(newValue.getStringRepresentation());
		this.addItem(item.toString());
	}

	public void clear() {
		super.clear();
		representedObjects.clear();
	}

	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "doubleclick: '" + event + "'", this);
			if (representedObjects.size() > 0 && this.getSelectedIndex() != -1)
				App.openDetailView(representedObjects.get(this.getSelectedIndex()), null, false);
		}
		if (DOM.eventGetType(event) == Event.ONCLICK) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "ONCLICK: '" + event + "'", this);
		}
		super.onBrowserEvent(event);
	}

	Label proxyLabel = null;

	public Widget getDndProxy(int x, int y) {
		String string = AtomTools.domainObjectsString(getRepresentedDomainObjects());
		if (proxyLabel == null)
			proxyLabel = new Label(string);
		else
			proxyLabel.setText(string);
		// proxyLabel.setStylePrimaryName(this.getStylePrimaryName());
		return proxyLabel;
	}

	public DomainObject getRepresentedDomainObject() {
		return representedObjects.get(0);
	}

	public List<DomainObject> getRepresentedDomainObjects() {
		List<DomainObject> selectedObjects = new ArrayList<DomainObject>();
		for (int i = 0; i < representedObjects.size(); i++) {
			if (this.isItemSelected(i)) {
				selectedObjects.add(representedObjects.get(i));
			}
		}
		if (proxyLabel != null)
			proxyLabel.setText(AtomTools.domainObjectsString(selectedObjects));
		return selectedObjects;
	}

	public void previewDragStart(int x, int y) throws VetoDragException {
	}
}
