/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader.State;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget;
import at.ac.fhcampuswien.atom.client.gui.gxt.DomainObjectListWidget.ActionMode;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DomainObjectSelectorFrame extends Frame {

	private static DomainObjectSelectorFrameUiBinder uiBinder = GWT.create(DomainObjectSelectorFrameUiBinder.class);
	
	interface PanelStyle extends CssResource {
//		String whiteSpace();

		String space();
	}

	interface DomainObjectSelectorFrameUiBinder extends UiBinder<Widget, DomainObjectSelectorFrame> {
	}
	
	@UiField
	protected SimplePanel root;

	@UiField
	protected PanelStyle panelStyle;
	
//	private DockLayoutPanel panel;
	private DomainObjectListWidget domainObjectListWidget;
//	private Popup popup;
	
	private DomainObjectSelectionHandler resultHandler;

	public interface DomainObjectSelectionHandler {
		public void cancelSelection();
		public void handleDomainObjectList(List<DomainObject> list);
	}
	
	/**
	 * 
	 */
	public DomainObjectSelectorFrame(final String representedClassName, final boolean multiSelect, final String attributeName,
			final DomainObjectList preloadedResult, final String searchString, final boolean simpleSearch, DomainObjectSelectionHandler resultHandler) {
		super("Bitte wählen Sie " + representedClassName.substring(representedClassName.lastIndexOf(".")) + " für Attribut " + attributeName, "wähle "
				+ attributeName, State.OBJECT_EDIT_NO_DELETE, me, null, null, AtomConfig.FrameType.OBJECT_SELECTOR);
		// super(false, true);
		// super(new Label("x"));

		AtomTools.log(Level.FINER, "creating DomainObjectSelectorFrame instance", this);

		this.resultHandler = resultHandler;
		
		initWidget(uiBinder.createAndBindUi(this));
//		panel = new DockLayoutPanel(Unit.PX);
//		panel.setStyleName(panelStyle.space());
		// panel.addNorth(new Label(representedClass), 15);

//		Button cancelButton = new Button("cancel");
//		cancelButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				// DomainObjectSelectPopup.this.hide();
//				popup.hide();
//			}
//		});

//		panel.addSouth(cancelButton, 20);
//		// this.set
//
//		Button testButton = new Button("Übernehmen");
//		testButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				AtomTools.log(Level.FINE, "testButton clicked", this);
//				resultHandler.handleDomainObjectList(domainObjectListWidget.getRepresentedDomainObjects());
//				// DomainObjectSelectPopup.this.hide();
//				popup.hide();
//			}
//		});
//		panel.addSouth(testButton, 20);

		// panel.add(new DomainObjectListFrame(representedClass, false));

		
		RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.SEVERE, "could not get domainTree! -> " + reason, this);
			}
			
			@Override
			public void recieve(DomainClass domainTree) {
				DomainClass representedClass = domainTree.getDomainClassNamed(representedClassName);
				DomainObjectSelectorFrame.this.representedClass = representedClass;
				
				if (multiSelect) {
					 DomainObjectSelectorFrame.this.setTitles("Bitte wählen Sie " + representedClass.getPluralName() + " für Attribut " + attributeName, "wähle " + attributeName);
				 } else {
					 DomainObjectSelectorFrame.this.setTitles("Bitte wählen Sie " + representedClass.getSimpleName() + " für Attribut " + attributeName, "wähle " + attributeName);
				 }
				
				DomainObjectSelectorFrame.this.changeCenterHeaderButtonPanelState(getCenterHeaderState(representedClass));

				DomainObjectListWidget.ActionMode mode;
				if (multiSelect) {
					mode = ActionMode.MULTI_SELECT;
				} else {
					mode = ActionMode.SINGLE_SELECT;
				}
				
				domainObjectListWidget = new DomainObjectListWidget(representedClass, preloadedResult, searchString, simpleSearch, null, false, false, mode, DomainObjectSelectorFrame.this);
				root.add(domainObjectListWidget);
				domainObjectListWidget.resize(lastEvent);
			}
		});
		
		// Uncomment if you want drag and drop inside the popup
		// listWidget.goingVisible();

		// this.addCloseHandler(new CloseHandler<PopupPanel>() {
		//
		// @Override
		// public void onClose(CloseEvent<PopupPanel> event) {
		// AtomTools.log(Level.INFO, "closed by onClose from CloseHandler", this);
		// }
		// });
	}
	
	ResizeEvent lastEvent;
	
	@Override
	public void resize(ResizeEvent event) {
		lastEvent = event;
		if(domainObjectListWidget != null)
			domainObjectListWidget.resize(event);
	}

	@Override
	public void goingVisible() {
		if(domainObjectListWidget != null)
			domainObjectListWidget.goingVisible();
	}

	private boolean closing = false;
	
	@Override
	public boolean goingInvisible() {
		if(closing) {
			if(domainObjectListWidget != null)
				domainObjectListWidget.goingInvisible();
			return true;
		}
		else if(Window.confirm("Wollen sie das Bearbeiten der Verknüpfung abbrechen?")) {
			closing = true;
			App.closeFrame(this);
			return true;
		}
		else {
			return false;
		}
			
//		
//		if(false || !) {
//			if(domainObjectListWidget != null)
//				domainObjectListWidget.goingInvisible();
//			return true;
//		} else {
//			AtomGUI.getSinglton().closeFrame(this);
//			
//			if(domainObjectListWidget != null)
//				domainObjectListWidget.goingInvisible();
//			return true;
//		}
//		
//		if(Window.confirm("Wollen sie das Bearbeiten der Verknüpfung abbrechen?")) {
//			closing = true;
//			if(domainObjectListWidget != null)
//				domainObjectListWidget.goingInvisible();
//			AtomGUI.getSinglton().closeFrame(this);
//			return true;
//		}
//		else {
//			return false;
//		}
	}
	
	static private CenterHeader.State getCenterHeaderState(DomainClass domainClass) {
		Set<String> at = domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession());
		if (!domainClass.getIsAbstract()
				&& AtomTools.isAccessAllowed(AtomConfig.accessCreateNew, at)) {
			// if(!domainClass.getIsAbstract() && domainClass.isAccessAllowed(RPCCaller.getSinglton().getClientSession(), AtomConfig.accessReadWrite)) {
			return CenterHeader.State.OBJECT_SELECTOR_WITH_NEW;
		} else if (AtomTools.isAccessAllowed(AtomConfig.accessLinkage, at)) {
			// else if(domainClass.isAccessAllowed(RPCCaller.getSinglton().getClientSession(), AtomConfig.accessLinkage)) {
			return CenterHeader.State.OBJECT_SELECTOR_NO_NEW;
		} else {
			throw new AtomException("User has no Permissions on the DomainClass '" + domainClass.getName() + "'");
		}
	}
	
	@Override
	public void actionCancel() {
		AtomTools.log(Level.FINE, "DomainObjectSelectorFrame canceled!", this);
		closing = true;
		App.closeFrame(this);
		resultHandler.cancelSelection();
	}
	
	@Override
	public void actionSave() {
		AtomTools.log(Level.FINE, "DomainObjectSelectorFrame save action", this);
		closing = true;
		App.closeFrame(this);
		resultHandler.handleDomainObjectList(domainObjectListWidget.getRepresentedDomainObjects());
	}
	
	@Override
	public void actionNewInfoOnly() {
		closing = true;
	}
	
	@Override
	public void actionObjectDelivered(DomainObject instance) {
		AtomTools.log(Level.FINE, "DomainObjectSelectorFrame save action", this);
		closing = true;
		App.closeFrame(this);
		List<DomainObject> list = new ArrayList<DomainObject>(1);
		list.add(instance);
		resultHandler.handleDomainObjectList(list);
	}
	
}
