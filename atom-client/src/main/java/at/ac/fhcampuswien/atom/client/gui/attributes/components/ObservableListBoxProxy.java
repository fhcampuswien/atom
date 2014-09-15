/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import at.ac.fhcampuswien.atom.client.gui.attributes.CollectionView;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ObservableListBoxProxy<C extends Collection<T>, T> extends SimplePanel {
		
		/**
		 * 
		 */
		protected final CollectionView<C, T> collectionView;
		protected int itemCount = 0;
		protected Map<Integer,T> elements;
		
		private ListBox myListBox;
		
		public ObservableListBoxProxy( CollectionView<C, T> collectionView, boolean doStuff ) {
			this.collectionView = collectionView;
			elements = new HashMap<Integer, T>();
			if(doStuff) {
				myListBox = new ObservableListBox<C, T>(this.collectionView);
				this.add(myListBox);
			}
		}
		
		public Widget getListBoxWidget() {
			return myListBox;
		}
		
		public int getItemCount() {
			return itemCount;
		}
		
		public boolean isItemSelected(int index) {
			return myListBox.isItemSelected(index);
		}
		
		public Object getItem(int index) {
			return elements.get(index);
		}
		
		public String getItemText(int index) {
			return elements.get(index).toString();
		}
		
		public void addItem(T item) {
			elements.put(itemCount, item);
			itemCount++;
			myListBox.addItem(item.toString());
		}
		
		public void setTempLabel(String label) {
			myListBox.addItem(label);
		}
		
//		public com.google.gwt.user.client.Element getElement() {
//			return myListBox.getElement();
//		}
		
		public void clear() {
			itemCount = 0;
			myListBox.clear();
		}
		
		public int getSelectedIndex() {
			return myListBox.getSelectedIndex();
		}

		public void addChangeHandler(ChangeHandler handler) {
			myListBox.addChangeHandler(handler);
		}
		
		protected boolean readOnly;
		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}
		
		public void setBackgroundColor(String color) {
			if(getListBoxWidget() != null && getListBoxWidget().getElement() != null && getListBoxWidget().getElement().getStyle() != null)
				getListBoxWidget().getElement().getStyle().setBackgroundColor(color);

			else if(getListBoxWidget() == null)
				AtomTools.log(Log.LOG_LEVEL_ERROR, "getListBoxWidget() = null!", this);
			
			else if(getListBoxWidget().getElement() == null)
				AtomTools.log(Log.LOG_LEVEL_ERROR, "getListBoxWidget().getElement() = null!", this);
			
			else if(getListBoxWidget().getElement().getStyle() == null)
				AtomTools.log(Log.LOG_LEVEL_ERROR, "getListBoxWidget().getElement().getStyle() = null!", this);
		}
	}