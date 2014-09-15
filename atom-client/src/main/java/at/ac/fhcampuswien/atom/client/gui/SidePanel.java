/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.ClientConfig;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SidePanel<E extends DomainObject> extends Composite {

	private static SidePanelUiBinder uiBinder = GWT.create(SidePanelUiBinder.class);

	interface SidePanelUiBinder extends UiBinder<Widget, SidePanel<?>> {
	}

	interface SidePanelStyle extends CssResource {
		String label();

		String space();

		String spaceMarked();

		String closeButton();
		
		String roundedLabel();
	}
	
	@UiField
	protected SidePanelStyle sidePanelStyle;

	protected FlexTable flexTable;
	
	@UiField
	protected ScrollPanel scrollPanel;

	private RootPanel[] markTargets;

	public SidePanel(RootPanel[] markTargets) {
		initWidget(uiBinder.createAndBindUi(this));

		this.markTargets = markTargets;
		//titleTextField.setHTML(this.title);
		
		flexTable = new FlexTable();
//		flexTable.setStylePrimaryName(sidePanelStyle.space());
		scrollPanel.add(flexTable);
//		scrollPanel.addScrollHandler(new ScrollHandler() {
//			
//			@Override
//			public void onScroll(ScrollEvent event) {
//				AtomTools.log(Log.LOG_LEVEL_TRACE, "AtomSidePanel.onScroll - " + event.toString(), this);
//			}
//		});
	}

	protected ArrayList<SidePanelLabel<E>> lablesList = new ArrayList<SidePanelLabel<E>>();
	protected HashMap<E, SidePanelLabel<E>> identfiedLables = new HashMap<E, SidePanelLabel<E>>();

	protected void rebuildElements() {
		flexTable.clear();
		for (int i = 0; i < lablesList.size(); i++) {
			flexTable.setWidget(i, 0, lablesList.get(i));
		}
		resize(null);
	}

	protected void moveLabelToTop(E identifier) {
		SidePanelLabel<E> labelToMove = identfiedLables.get(identifier);
		if (labelToMove != null && lablesList.indexOf(labelToMove) > 0) {
			/* liste nur aendern, falls das frame nicht schon das oberste ist */
			lablesList.remove(labelToMove);
			lablesList.add(0, labelToMove);
			rebuildElements();
		}
	}
	
	protected boolean representsInstanceOfClass(E o, DomainClass dc) {
		return dc.isClassOfInstance(o);
	}
	
	public void suggestObjectsForDND(DomainClass dc) {
		for (Entry<E, SidePanelLabel<E>> entry : identfiedLables.entrySet())
		{
			if(dc != null && representsInstanceOfClass(entry.getKey(), dc)) {
				entry.getValue().getElement().getStyle().setBackgroundColor(ClientConfig.colorDomainClassMatch);
			}
			else {
				entry.getValue().getElement().getStyle().clearBackgroundColor();
			}
		}
	}
	
	public void mark() {
		for(RootPanel rp : markTargets) {
			rp.removeStyleName(sidePanelStyle.space());
			rp.addStyleName(sidePanelStyle.spaceMarked());
		}
//		scrollPanel.setStylePrimaryName(sidePanelStyle.spaceMarked());
//		flexTable.setStylePrimaryName(sidePanelStyle.spaceMarked());
	}

	public void unMark() {
		for(RootPanel rp : markTargets) {
			rp.removeStyleName(sidePanelStyle.spaceMarked());
			rp.addStyleName(sidePanelStyle.space());
		}
//		scrollPanel.setStylePrimaryName(sidePanelStyle.space());
//		flexTable.setStylePrimaryName(sidePanelStyle.space());
	}

	protected void addElement(SidePanelLabel<E> closeableLabel) {
		lablesList.add(0, closeableLabel);
		App.registerDndWidget(closeableLabel.dndWidgetAsWidget);
	}

	protected void addElementAtTheEnd(SidePanelLabel<E> closeableLabel) {
		lablesList.add(closeableLabel);
		App.registerDndWidget(closeableLabel.dndWidgetAsWidget);
	}

	protected void removeElement(E identifier){
		SidePanelLabel<E> label = identfiedLables.get(identifier);
		if(label != null) {
			lablesList.remove(label);
			identfiedLables.remove(identifier);
			if(label.dndWidget != null) {
				App.unRegisterDndWidget(label.dndWidgetAsWidget);
			}
			rebuildElements();
		}
		else
			AtomTools.log(Log.LOG_LEVEL_WARN, "could not remove element, it wasn't present", this);
	}

	protected Object getIdentifierOfNumber(int i) {
		return lablesList.get(i).identifier;
	}

	protected Boolean loadingEntriesFromServerFinished = null;
	public void loadEntriesFromServer(final DomainClass classOfList) {
		if (loadingEntriesFromServerFinished != null)
			return;
		else
			loadingEntriesFromServerFinished = false;

		RPCCaller.getSinglton().loadListOfDomainObjects(classOfList, null, null, 0, 30, false, null, true, true, false,
				new AsyncCallback<DomainObjectList>() {

					@Override
					public void onSuccess(DomainObjectList result) {
						processLoadedEntries(result);
						rebuildElements();
						loadingEntriesFromServerFinished = true;
					}

					@Override
					public void onFailure(Throwable caught) {
						AtomTools.log(Log.LOG_LEVEL_ERROR, "Could not load SidePanel Entry-List - " + classOfList.getName(), this);
					}
				});
	}
	
	protected abstract void processLoadedEntries(DomainObjectList result);
	
	public abstract void removeFromUI(DomainObject representedObject);
	
	protected int getLabelSpace() {
//		int scrollBarWidth = scrollPanel.getElement().getOffsetWidth() - scrollPanel.getElement().getClientWidth();
//		int scrollBarHeight = scrollPanel.getElement().getOffsetHeight() - scrollPanel.getElement().getClientHeight();
		
//		AtomTools.log(Log.LOG_LEVEL_TRACE, 
//				"OffsetWidthE = " + scrollPanel.getElement().getOffsetWidth() + "; ClientWidthE = " + scrollPanel.getElement().getClientWidth() +
//				"; OffsetWidth = " + scrollPanel.getOffsetWidth()
//				+ "; MaximumVerticalScrollPosition = " + scrollPanel.getMaximumVerticalScrollPosition()
//				+ "; MinimumVerticalScrollPosition = " + scrollPanel.getMinimumVerticalScrollPosition()
//				+ "; MaximumHorizontalScrollPosition = " + scrollPanel.getMaximumHorizontalScrollPosition()
//				+ "; MinimumHorizontalScrollPosition = " + scrollPanel.getMinimumHorizontalScrollPosition()
//				+ "; currentLayoutScrolled = " + currentLayoutScrolled
//				, this);
		
		int totalWidth = scrollPanel.getOffsetWidth();
		int closeIconSpace = 44;
		int scrollBarSpace = (currentLayoutScrolled ? 0 : -15);
		int extra = Window.Navigator.getUserAgent().contains("Chrome") ? 0 : 2;
		
		return totalWidth - (closeIconSpace + scrollBarSpace + extra);
	}
	
	public void resize(ResizeEvent event) {
		resizeTimer.cancel();
		resizeTimer.schedule(10);
	}
	
	private boolean currentLayoutScrolled = false;
	private Timer resizeTimer = new Timer() {

		@Override
		public void run() {
			currentLayoutScrolled = (scrollPanel.getMaximumVerticalScrollPosition() > 0);
			int space = getLabelSpace();
			boolean changed = false;
			for(SidePanelLabel<E> spl : lablesList) {
				changed = changed || spl.update(space);
			}
			if(changed) {
				resizeTimer.schedule(10);
			}
		}
	};
}
