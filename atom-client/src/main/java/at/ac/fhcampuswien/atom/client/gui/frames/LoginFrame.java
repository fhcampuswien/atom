/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import at.ac.fhcampuswien.atom.client.App;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginFrame extends Composite {

	private static LoginFrameUiBinder uiBinder = GWT
			.create(LoginFrameUiBinder.class);

	interface LoginFrameUiBinder extends UiBinder<Widget, LoginFrame> {
	}

	public LoginFrame() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	protected @UiField Button loginButton;
	protected @UiField TextBox userNameBox;
	protected @UiField PasswordTextBox passwordBox;
	protected @UiField HTML errorLabel;

	@UiHandler("loginButton")
	void onClick_loginButton(ClickEvent event) {
		App.actionLogin();
	}
	
	@UiHandler("passwordBox")
	void onKeyPress_passwordBox(KeyPressEvent event) {
		//GWT BUG: http://code.google.com/p/google-web-toolkit/issues/detail?id=5558
		int unicode = event.getUnicodeCharCode();
		char code = event.getCharCode(); 
		NativeEvent nativeEvent = event.getNativeEvent();
		int keyCode = nativeEvent.getKeyCode();
//		AtomTools.log(Log.LOG_LEVEL_TRACE, "login_passwordbox_keyPress; UnicodeCharCode=\""+unicode+"\" ; keyCode=\""+ keyCode +" ; CharCode=\""+String.valueOf(code)+"\"<endofline>", this);
		
		if(code == '\n' || code == '\r' || (unicode == 0 && keyCode == 13)) {
			App.actionLogin();
		}
	}

	public String getEnteredUserName() {
		return userNameBox.getText();
	}

	public String getEnteredPassword() {
		return passwordBox.getText();
	}

	public void setError(String userString) {
		errorLabel.setHTML(userString);
	}
}
