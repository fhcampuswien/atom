/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.SidePanelLabel;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.FrameVisit;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Frame extends Composite {
	protected static Widget me = new Label("this");	
	
	private SidePanelLabel<FrameVisit> frameLabel;
	protected AtomConfig.FrameType frameType;

	protected String longTitle, shortTitle;
	protected Widget content;
	protected CenterHeader.State centerHeaderButtonPanelState;
	protected DomainObject representedObject;
	protected DomainClass representedClass;
	protected String representedSearchTerm = null;
	protected DataFilter[] dataFilters = null;
	
	protected void setTitles(String longTitle, String shortTitle) {
		this.longTitle = longTitle;
		this.shortTitle = shortTitle;
		
		if(frameLabel != null)
			frameLabel.update(new SidePanelLabel.NameSource() {

				@Override
				public String getName() {
					return Frame.this.shortTitle;
				}
			}); 
			//setText(AtomGUI.getSinglton().getShortenedString(shortTitle, null, 165));
		
		App.frameChanged(this);
	}
	
	protected void changeCenterHeaderButtonPanelState(CenterHeader.State centerHeaderButtonPanelState) {
		this.centerHeaderButtonPanelState = centerHeaderButtonPanelState;
		App.frameChanged(this);
	}

	public Frame(String longTitle, String shortTitle,
			CenterHeader.State centerHeaderButtonPanelState,
			Widget content, DomainObject representedObject,
			DomainClass representedClass, AtomConfig.FrameType frameType) {
		init(longTitle, shortTitle, centerHeaderButtonPanelState, content, representedObject, representedClass, frameType);
	}
	
	protected Frame() {
	}
	
	protected void init(String longTitle, String shortTitle,
			CenterHeader.State centerHeaderButtonPanelState,
			Widget content, DomainObject representedObject,
			DomainClass representedClass, AtomConfig.FrameType frameType) {
		this.longTitle = longTitle;
		this.shortTitle = shortTitle;
		this.centerHeaderButtonPanelState = centerHeaderButtonPanelState;
		if(content == me)
			this.content = this;
		else
			this.content = content;
			
		this.representedObject = representedObject;
		this.representedClass = representedClass;
		this.frameType = frameType;
	}

	public String getLongTitle() {
		return longTitle;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public CenterHeader.State getCenterHeaderButtonPanelState() {
		return centerHeaderButtonPanelState;
	}

	public Widget getContent() {
		return content;
	}

	public DomainObject getRepresentedObject() {
		return representedObject;
	}

	public DomainClass getRepresentedClass() {
		return representedClass;
	}
	
	public String getRepresentedSearchTerm() {
	    return representedSearchTerm;
	}
	
	public DataFilter[] getDataFilters() {
		return dataFilters;
	}
	
	protected void updateTitles() {
	}
	
	public void setDataFilters(DataFilter[] filters) {
		if(!AtomTools.arraysContainEqualElements(dataFilters, filters)) {
			this.dataFilters = filters;
			if(filters != null && filters.length > 0 && (frameType.equals(FrameType.LIST_ALL) || frameType.equals(FrameType.LIST_RELATED))) {
				frameType = FrameType.FILTERCLASS;
			}
			updateTitles();
			App.frameChanged(this);
		}
	}
	
	public FrameType getFrameType() {
		if(frameType == null)
			AtomTools.log(Log.LOG_LEVEL_ERROR, "FrameType must never be null!", this);
		return frameType;
	}
	
	public void setFrameLabel(SidePanelLabel<FrameVisit> sidePanelLabel) {
//		if(this.frameLabel != null)
//			AtomTools.log(Log.LOG_LEVEL_ERROR, "Someone tried to call setFrameLabel at a point where a FrameLabel was already present!", this);
//		else
			this.frameLabel = sidePanelLabel;
	}

	public void actionSave() {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "A generic Frame cannot save!", this);
	}
	
	public void actionEdit() {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "A generic Frame cannot edit!", this);
	}
	
	public void actionDelete() {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "A generic Frame cannot delete!", this);
	}
	
	public void actionCancel() {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "A generic Frame cannot cancel!", this);
	}
	
	public void actionFilter() {
		AtomTools.log(Log.LOG_LEVEL_ERROR, "A generic Frame cannot filter!", this);
	}

	public void actionObjectDelivered(DomainObject instance) {
		AtomTools.log(Log.LOG_LEVEL_WARN, "A generic Frame does not process deliveries of instances of its type", this);
	}

	public void actionNewInfoOnly() {
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "this frame does not process actionNewInfoOnly events", this);
	}

	public boolean goingInvisible() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "Frame '" + longTitle + "' going invisible", this);
		return true;
	}

	public void goingVisible() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "Frame '" + longTitle + "' going visible", this);
	}

	public void resize(ResizeEvent event) {
	    AtomTools.log(Log.LOG_LEVEL_DEBUG, "this frame does not process resize events.", this);
	}

	public void deliverError(ValidationError ve) {
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "this frame does not process delivered Errors.", this);
		
		final PopupPanel popupPanel = new PopupPanel(false);
		popupPanel.getElement().getStyle().setZIndex(1000);
		VerticalPanel popUpPanelContents = new VerticalPanel();
		popupPanel.setTitle("Error");
		HTML message = new HTML(ve.getMessage());
	    Button button = new Button("Close", new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				popupPanel.hide();
			}
		});
	    SimplePanel holder = new SimplePanel();
	    holder.add(button);
	    popUpPanelContents.add(message);
	    popUpPanelContents.add(holder);
	    popupPanel.setWidget(popUpPanelContents);
	    popupPanel.center();
	}

	public void suggestDND(DomainClass dc) {
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Frame) {
			Frame other = (Frame) obj;
			
			return (representedClass == null ? other.getRepresentedClass() == null : representedClass.equals(other.getRepresentedClass())) &&
					(representedObject == null ? other.getRepresentedObject() == null : representedObject.equals(other.getRepresentedObject())) &&
					(representedSearchTerm == null ? other.getRepresentedSearchTerm() == null : representedSearchTerm.equals(other.getRepresentedSearchTerm())) &&
					(frameType == null ? other.getFrameType() == null : frameType.equals(other.getFrameType()));

//			boolean classMatch, instanceMatch, searchMatch, typeMatch;
//			
//			if(this.getRepresentedClass() == null && other.getRepresentedClass() == null)
//				classMatch = true;
//			else if(this.getRepresentedClass() == null || other.getRepresentedClass() == null)
//				classMatch = false;
//			else
//				classMatch = this.getRepresentedClass().equals(other.getRepresentedClass());
//			
//			if(!classMatch)
//				return false;
//			
//			if(this.getRepresentedObject() == null && other.getRepresentedObject() == null)
//				instanceMatch = true;
//			else if(this.getRepresentedObject() == null || other.getRepresentedObject() == null)
//				instanceMatch = false;
//			else
//				instanceMatch = this.getRepresentedObject().equals(other.getRepresentedObject());
//			
//			if(!instanceMatch)
//				return false;
//			
//			if(this.getRepresentedSearchTerm() == null && other.getRepresentedSearchTerm() == null)
//				searchMatch = true;
//			else if(this.getRepresentedSearchTerm() == null || other.getRepresentedSearchTerm() == null)
//				searchMatch = false;
//			else
//				searchMatch = this.getRepresentedSearchTerm().equals(other.getRepresentedSearchTerm());
//			
//			if(!searchMatch)
//				return false;
//			
//			if(this.getFrameType() == null && other.getFrameType() == null)
//				typeMatch = true;
//			else if(this.getFrameType() == null || other.getFrameType() == null)
//				typeMatch = false;
//			else
//				typeMatch = this.getFrameType().equals(other.getFrameType());
//			
//			return classMatch && instanceMatch && searchMatch && typeMatch;
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return AtomTools.getFrameHashCode(representedClass, representedObject, representedSearchTerm, frameType, dataFilters);
	}
	
	@Override
	public String toString() {
		return "Frame " + (shortTitle != null && shortTitle.length() > 0 ? shortTitle : longTitle);
	}

	public Boolean isSimpleSearch() {
		return null;
	}
}