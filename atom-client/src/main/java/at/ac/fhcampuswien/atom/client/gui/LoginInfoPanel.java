/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class LoginInfoPanel extends Composite {

	private static LoginInfoPanelUiBinder uiBinder = GWT
			.create(LoginInfoPanelUiBinder.class);

	interface LoginInfoPanelUiBinder extends
			UiBinder<Widget, LoginInfoPanel> {
	}
	
	interface PanelStyle extends CssResource {
		String text();
	}
	
	protected @UiField PanelStyle panelStyle;
	protected @UiField DockLayoutPanel root;
	
	protected @UiField HTML nameLabel;
	protected @UiField HTML nameSpacer;

	protected @UiField HTML profileLabel;
	protected @UiField HTML profileSpacer;
	protected @UiField Image profileArrow;
	
	protected @UiField HTML logInOutLabel;
	protected @UiField HTML logInOutSpacer;
	protected @UiField Image logInOutArrow;

	@UiHandler({"logInOutLabel", "logInOutArrow"})
	void onClick_logInOut(ClickEvent event) {
		if("Anmelden".equalsIgnoreCase(logInOutLabel.getText())) {
			App.actionLogin();
		}
		else if("Abmelden".equalsIgnoreCase(logInOutLabel.getText())) {
			App.actionLogout();
		}
		else {
			AtomTools.log(Log.LOG_LEVEL_FATAL, "unknown login state: " + logInOutLabel.getText(), this);
		}
	}

	@UiHandler({"profileLabel", "profileArrow"})
	void onClick_profile(ClickEvent event) {
		App.actionOpenProfile();
	}

	public LoginInfoPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		processing();
	}
	
	public void loginState(Boolean state, String userString) {
		if(state == null) {
			processing();
		}
		else if(state == false) {
			loggedOut();
		}
		else {
			loggedInUser(userString);
		}
	}

	private void loggedInUser(String userName) {
		root.clear();
		
		logInOutLabel.setText(AtomTools.getMessages().logout());
		nameLabel.setText(AtomTools.getMessages().logged_in_as(userName));
		
		root.addEast(logInOutLabel, 70);
		root.addEast(logInOutSpacer, 5);
		root.addEast(logInOutArrow, 11);
		root.addEast(profileLabel, 90);
		root.addEast(profileSpacer, 5);
		root.addEast(profileArrow, 11);
		root.addEast(nameSpacer, 35);
		root.add(nameLabel);
	}
	
	private void loggedOut() {
		root.clear();
		
		logInOutLabel.setText(AtomTools.getMessages().login());
		root.addEast(logInOutLabel, 100);
		root.addEast(logInOutSpacer, 5);
		root.addEast(logInOutArrow, 11);
	}
	
	private void processing() {
		root.clear();
		nameLabel.setText(AtomTools.getMessages().login_pruefen());
		root.addEast(nameSpacer, 50);
		root.add(nameLabel);
	}
}
