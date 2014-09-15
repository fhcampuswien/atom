/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.thoughtworks.selenium.Wait;

/*
 * Sample page
 * 
 * @author Sebastiano Armeli-Battana
 */
public class LoginPage extends Page {

	@FindBy(how = How.ID, using = "gwt-debug-loginNameLabel")
	private WebElement loginLabel;

	@FindBy(id = "gwt-debug-userNameBox")
	@CacheLookup
	private WebElement userBox;

	@FindBy(id = "gwt-debug-passwordBox")
	@CacheLookup
	private WebElement passwordBox;

	@FindBy(id = "gwt-debug-loginButton")
	@CacheLookup
	private WebElement loginButton;

	@FindBy(id = "gwt-debug-logInOutButtonGlobal")
	//@CacheLookup
	private WebElement logInOutButtonGlobal;

	public LoginPage(WebDriver webDriver) {
		super(webDriver);
	}

	public String loginUser(String username, String password, String fullName) {
		String loggedin = getLoginLabel();

		if (loggedin != null) {
			if (fullName != null && loggedin.contains(fullName))
				return loggedin;
			else {
				logInOutButtonGlobal.click();
			}
		}

		userBox.sendKeys(username);
		passwordBox.sendKeys(password);
		loginButton.click();
		return getLoginLabel();
	}

	public boolean logout() {
		String loggedin = getLoginLabel();
		if (loggedin != null) {
			logInOutButtonGlobal.click();
			return true;
		}
		return false;
	}

	public String getLoginLabel() {
		try {

			new Wait() {
				
				private int counter = 0;
				
				@Override
				public boolean until() {
					try {
						System.out.println("starting call of logInOutButtonGlobal.getText() #" + counter);
//						AtomTools.log(Log.LOG_LEVEL_INFO, "starting call of logInOutButtonGlobal.getText() #" + counter, this);
						String text = logInOutButtonGlobal.getText();
						if (text == null)
							text = "NULL";
						System.out.println("#" + counter + " logInOutButtonGlobal.getText() = " + text);
//						AtomTools.log(Log.LOG_LEVEL_INFO, "#" + counter + " logInOutButtonGlobal.getText() = " + text, this);
						counter++;
						return AtomTools.getMessages().login().equals(text) || AtomTools.getMessages().logout().equals(text);
					}
					catch (NoSuchElementException e) {
						return false;
					}
					catch (StaleElementReferenceException e) {
						logInOutButtonGlobal = null;
						return false;
					}
					catch (Throwable t) {
						return false;
					}
				}
			}.wait("LoginInfoPanel did not settle to a point where it allows login or logout action within 30 seconds.", 30000);

			if (AtomTools.getMessages().login().equals(logInOutButtonGlobal.getText()))
				return null;

			if (loginLabel != null) {
				new Wait() {
					@Override
					public boolean until() {
						String text = loginLabel.getText();
						return !(text.contains("warten") || text.contains("wait"));
					}
				}.wait("Login did not finish within 10 seconds!!! ", 10000);
				return loginLabel.getText();
			} else
				return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
