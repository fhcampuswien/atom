/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.dnd.AtomDNDWidget;
import at.ac.fhcampuswien.atom.client.gui.dnd.DndLabel;
import at.ac.fhcampuswien.atom.client.gui.dnd.DndLink;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class SidePanelLabel<E extends DomainObject> extends HorizontalPanel {

	public interface NameSource {
		String getName();
	}

	private final SidePanel<E> sidePanel;
	E identifier;
	AtomDNDWidget dndWidget;
	Widget dndWidgetAsWidget;
	HasText dndWidgetHasText;

	private NameSource source;

	public SidePanelLabel(SidePanel<E> sidePanel, DndLabel dndLabel, E identifier) {
		
		this.sidePanel = sidePanel;
		this.identifier = identifier;
		this.dndWidget = dndLabel;
		this.dndWidgetAsWidget = dndLabel;
		this.dndWidgetHasText = dndLabel;
		// this.fullString = fullString;

		setup();
	}

	public SidePanelLabel(SidePanel<E> sidePanel, E identifier, NameSource source, String link, DomainObject target) {
		this.sidePanel = sidePanel;
		this.identifier = identifier;
		this.source = source;
		
		DndLink dndLink = new DndLink(App.getShortenedString(source.getName(), null, sidePanel.getLabelSpace()), link, target, this);
		dndLink.setStyleName(this.sidePanel.sidePanelStyle.label());
		this.dndWidget = dndLink;
		this.dndWidgetAsWidget = dndLink;
		this.dndWidgetHasText = dndLink.getLink();
		
		setup();
	}
	
	public SidePanelLabel(SidePanel<E> sidePanel, E identifier, NameSource source, ClickHandler action, DomainObject target) {
		
		this.sidePanel = sidePanel;
		this.identifier = identifier;
		this.source = source;

		final DndLabel dndLabel = new DndLabel(App.getShortenedString(source.getName(), null, sidePanel.getLabelSpace()),
				target);
		dndLabel.setStyleName(this.sidePanel.sidePanelStyle.label());
		dndLabel.addClickHandler(action);
		
		this.dndWidget = dndLabel;
		this.dndWidgetAsWidget = dndLabel;
		this.dndWidgetHasText = dndLabel;
		
		setup();
	}
	
	private void setup() {
		this.sidePanel.identfiedLables.put(identifier, this);
		Image upImage = new Image(AtomClientBundle.INSTANCE.closeFrameButton());
		Image downImage = new Image(AtomClientBundle.INSTANCE.closeFrameButtonInvertiert());
//		Image tonne = new Image(AtomClientBundle.INSTANCE.muelltonne());

		PushButton pushButton = new PushButton(upImage, downImage, new ClickHandler() {
			public void onClick(ClickEvent event) {
				SidePanelLabel.this.sidePanel.removeElement(SidePanelLabel.this.identifier);
			}
		});
		pushButton.setStylePrimaryName(this.sidePanel.sidePanelStyle.closeButton());
		this.setStylePrimaryName(this.sidePanel.sidePanelStyle.roundedLabel());
		this.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(this.dndWidgetAsWidget);
		this.add(pushButton);
		
		this.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				App.suggestDND(null);
			}
		}, MouseOutEvent.getType());
		
		this.addDomHandler(new MouseOverHandler() {
			
			private String scl = null;
			private DomainClass dcl = null; 
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				DomainClass select = null;
				DomainObject ro = dndWidget.getRepresentedDomainObject();
				if(ro != null && ro.getObjectID() != null) {
					String scn = ro.getConcreteClass();
					if(scn.equals(scl)) {
						select = dcl;
					}
					else {
						DomainClass tree = RPCCaller.getSinglton().getLoadedDomainTree();
						if(tree != null) {
							dcl = select = tree.getDomainClassNamed(scn);
							scl = scn;
						}
						else {
							scl = null;
							dcl = null;
						}
					}
				}
				App.suggestDND(select);
			}
		}, MouseOverEvent.getType());
	}
	
	public void update(NameSource source) {
		if(source != null) {
			this.source = source;
		}
		update(sidePanel.getLabelSpace());		
	}

	private int lastResizeWidth = 0;
	/**
	 * @param space
	 * @return true if label has been changed, false otherwise
	 */
	public boolean update(int space) {
		
		boolean sizeChange = false;
		if(lastResizeWidth != space && space > 20) {
			lastResizeWidth = space;
			sizeChange = true;
		}
		
		//always perform update, since it might be a name change!
		
		String name = this.source.getName();
		
		if(name == null || name.length() <= 0)
			return false;
		
		String shortended = App.getShortenedString(name, this.sidePanel.sidePanelStyle.label(), space);
		this.dndWidgetHasText.setText(shortended);
		
		//if the full text is too long, set it as tooltip
		if(!shortended.equals(name))
			this.dndWidgetAsWidget.setTitle(name);
		else
			// if not, clear previous tooltip
			this.dndWidgetAsWidget.setTitle(null);
		
		return !sizeChange;
		
//		}
//		return false;
	}

	// public String getFullString() {
	// return fullString;
	// }
	//
	// public DndLabel getDndLabel() {
	// return dndLabel;
	// }
}