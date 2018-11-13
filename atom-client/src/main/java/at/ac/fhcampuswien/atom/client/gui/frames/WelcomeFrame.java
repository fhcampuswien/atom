/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;

import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WelcomeFrame extends Frame {

	private static WelcomeFrameUiBinder uiBinder = GWT.create(WelcomeFrameUiBinder.class);
	
	interface PanelStyle extends CssResource {
		String whiteSpace();

		String space();
		
		String block();
		
		String inBlockHeader();
		
		String link();
		
		String arrow();
		
		String padding();
		
		String objectImage();
	}

	interface WelcomeFrameUiBinder extends UiBinder<Widget, WelcomeFrame> {
	}

	@UiField
	protected PanelStyle panelStyle;
	@UiField
	protected ScrollPanel center;
	
	private FlowPanel flowPanel;

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public WelcomeFrame() {
		super(AtomTools.getMessages().willkommen_in_app(), AtomTools.getMessages().willkommen(), CenterHeader.State.EMPTY, me, null, null, AtomConfig.FrameType.WELCOME);
		initWidget(uiBinder.createAndBindUi(this));
		center.add(new Label(AtomTools.getMessages().willkommen_in_app()));
		
		RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
			
			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Level.SEVERE, "could not get domainTree! -> " + reason, this);
			}
			
			@Override
			public void recieve(DomainClass domainTree) {
				center.clear();
				flowPanel = new FlowPanel();
				flowPanel.setStyleName(panelStyle.space());
				center.add(flowPanel);
				ArrayList<DomainClass> sortingList = new ArrayList<DomainClass>();
				walkTree(domainTree, sortingList);
				
				Collections.sort(sortingList, DomainClass.nameComparator);
				for(DomainClass dc : sortingList) {
					createLinks(dc);
				}
			}
		});
	}
	
	private void walkTree(DomainClass domainClass, Collection<DomainClass> col) {
		col.add(domainClass);
		for(DomainClass subClass : domainClass.getSubClasses()) {
			walkTree(subClass, col);
		}
	}
	
	private void createLinks(DomainClass domainClass) {
		
		Set<String> at = domainClass.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession());
		
		if(domainClass.hideFromGui() || !AtomTools.isAccessAllowed(AtomConfig.accessMenue, at)) {
			return;
		}
		
		DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.PX);
		dockPanel.setStyleName(panelStyle.block());

		Image objectImage = new Image(domainClass.getObjectLogoImageData());
		objectImage.setStyleName(panelStyle.objectImage());
		dockPanel.addWest(objectImage, 50);	
		
		Label header = new Label(domainClass.getPluralName());
		header.setStyleName(panelStyle.inBlockHeader());
		dockPanel.addNorth(header, 24);
		
		VerticalPanel linkPanel = new VerticalPanel();
//		linkPanel.setStyleName(panelStyle.space());
		
//		image.
		
		Hyperlink hyperlink;
		
		boolean readAccess = AtomTools.isAccessAllowed(AtomConfig.accessReadOnly, at);
		boolean accessListAll = AtomTools.isAccessAllowed(AtomConfig.accessListAll, at);
		boolean readAccessRelated = domainClass.getAccessHandler().relationsExist();
		
		if(readAccess || accessListAll) {
			hyperlink = new Hyperlink(AtomTools.getMessages().Gesamtliste_anzeigen(), "LIST_ALL_" + domainClass.getName());
			hyperlink.ensureDebugId("LIST_ALL_" + domainClass.getName());
			linkPanel.add(prepareLink(hyperlink));
		}
		
		if(readAccessRelated) {
			hyperlink = new Hyperlink(AtomTools.getMessages().verknuepfte_anzeigen(domainClass.getPluralName()), "LIST_RELATED_" + domainClass.getName());
			hyperlink.ensureDebugId("LIST_RELATED_" + domainClass.getName());
			linkPanel.add(prepareLink(hyperlink));
		}
		
		if(readAccess || readAccessRelated) {
			hyperlink = new Hyperlink(domainClass.getPluralName() + " durchsuchen", "SEARCHCLASS_" + domainClass.getName());
			hyperlink.ensureDebugId("SEARCHCLASS_" + domainClass.getName());
			linkPanel.add(prepareLink(hyperlink));
		}
		
		if(AtomTools.isAccessAllowed(AtomConfig.accessCreateNew, at) && !domainClass.getIsAbstract()) {
			hyperlink = new Hyperlink(AtomTools.getMessages().neu_anlegen(domainClass.getSingularName()), "DETAIL_VIEW_new_" + domainClass.getName());
			hyperlink.ensureDebugId("DETAIL_VIEW_new_" + domainClass.getName());
			linkPanel.add(prepareLink(hyperlink));
		}
		
//		hyperlink.
		
//		Label testLabel = new Label("bla");
//		testLabel.set
				
		dockPanel.add(linkPanel);
		
		flowPanel.add(dockPanel);
	}
	
	private Widget prepareLink(Hyperlink link) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName(panelStyle.padding());
		horizontalPanel.add(new Image(AtomClientBundle.INSTANCE.linkArrow()));
		link.setStyleName(panelStyle.link());
		horizontalPanel.add(link);
		return horizontalPanel;
	}

//	public WelcomeFrame(String firstName) {
//		initWidget(uiBinder.createAndBindUi(this));
//
//		// Can access @UiField after calling createAndBindUi
//		button.setText(firstName);
//	}


}
