/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import java.util.HashSet;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ObjectSelectorMenue extends Composite {

	private static ObjectSelectorMenueUiBinder uiBinder = GWT.create(ObjectSelectorMenueUiBinder.class);

	interface ObjectSelectorMenueUiBinder extends UiBinder<Widget, ObjectSelectorMenue> {
	}

	interface PanelStyle extends CssResource {
		String popup();
	}

	private static ObjectSelectorMenue singlton = null;

	public static ObjectSelectorMenue getSinglton() {
		if (singlton == null) {
			AtomTools.log(Log.LOG_LEVEL_TRACE, "constructing DomainObjectMenue now!", null);
			singlton = new ObjectSelectorMenue();
		}
		AtomTools.log(Log.LOG_LEVEL_TRACE, "returning DomainObjectMenue", null);
		return singlton;
	}

	public static void prepareViewing() {
		for (PopupObjectButton popupObjectButton : getSinglton().buttons) {
			popupObjectButton.prepareViewing();
		}
	}

	public @UiField
	PanelStyle panelStyle;
	protected @UiField
	DockLayoutPanel root;
	private DomainClass domainTree = null;
	private HashSet<PopupObjectButton> buttons = new HashSet<PopupObjectButton>();
	private PopupObjectButton pleaseWaitButton = new PopupObjectButton("bitte warten, lade Liste", false);

	private ObjectSelectorMenue() {
		initWidget(uiBinder.createAndBindUi(this));
		loadDomainTree();
	}

	private void loadDomainTree() {
		RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {

			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
			}

			@Override
			public void recieve(DomainClass domainTree) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "got domainTree", this);
				ObjectSelectorMenue.this.domainTree = domainTree;
				buildMenue();
			}
		});

		if (domainTree == null) {
			root.addNorth(pleaseWaitButton, 23);
			AtomTools.log(Log.LOG_LEVEL_TRACE, "I have unfinished business here because the domainTree was not ready!", this);
		}
	}

	private boolean built = false;

	private void buildMenue() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "building DomainObjectMenue now!", this);
		if (built == false) {
			built = true;
			root.clear();
			addClassToMenue(domainTree, "");
			addSubclassesToMenue(domainTree, "&#160;");
		}
	}

	private void addSubclassesToMenue(DomainClass superClass, String prefix) {
		for (final DomainClass subClass : superClass.getSubClasses()) {
			addClassToMenue(subClass, prefix);
			addSubclassesToMenue(subClass, prefix + "&#160;");
		}
	}

	private void addClassToMenue(final DomainClass domainClass, String prefix) {

		boolean access = !domainClass.hideFromGui() && 
				AtomTools.isAccessAllowed(AtomConfig.accessMenue, domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession()));

		PopupObjectButton popupObjectButton = new PopupObjectButton(prefix + domainClass.getSimpleName(), access);

		if (access) {
			popupObjectButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					App.openList(domainClass, false);
//					AtomGUI.getSinglton().closeOpenMenue();
//					AtomGUI.getSinglton().menueItemClicked(domainClass);
				}
			});
		}

		if (!access && domainClass.getSubClasses() != null && domainClass.getSubClasses().size() > 0) {
			access = accessToSubclass(domainClass);
		}

		if (access) {
			buttons.add(popupObjectButton);
			root.addNorth(popupObjectButton, 23);
		}
	}
	
	private boolean accessToSubclass(DomainClass domainClass) {
		boolean access = false;
		
		if(domainClass.getSubClasses() != null) {
			for (DomainClass subClass : domainClass.getSubClasses()) {
				access = accessToSubclass(subClass);
				if(access) return true;
			}
		}	
		
		return access || AtomTools.isAccessAllowed(AtomConfig.accessMenue, domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession()));
	}
}
