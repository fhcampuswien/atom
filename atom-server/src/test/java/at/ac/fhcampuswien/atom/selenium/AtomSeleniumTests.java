/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.lightoze.gwt.i18n.server.LocaleProxy;
import com.thoughtworks.selenium.Wait;

public class AtomSeleniumTests extends TestBase {

    	private static int defaultTimeout = 30000;
    
	LoginPage loginPage;
	
//	@Parameters({ "path" })
	@BeforeClass
	public void testInit() {

    	//enable AtomTools.getMessages() to work without GWT.
    	LocaleProxy.initialize();
    	
		// Load the page in the browser
		webDriver.get(websiteUrl);
		loginPage = PageFactory.initElements(webDriver, LoginPage.class);
	}

	@Test
	public void loginMaxMustermann() throws InterruptedException {
		saveScreenshot("beforeLogin");
		String loginLabel = loginPage.loginUser("mamus", "diatest123", "Max Mustermann");
		Assert.assertTrue(loginLabel != null && loginLabel.contains("Max Mustermann"));
	}

	@DataProvider(name = "DomainClasses")
	public Object[][] getDomainClasses() {
	 return new Object[][] {
	   { "at.ac.fhcampuswien.atom.shared.domain.Publikation" }
	   //at.ac.fhcampuswien.atom.shared.domain.Veranstaltung
	 };
	}
	
	private int countAfterListOpened;
	
	public void openWelcomeScreen() {
		WebElement logoButton = waitForElement("home-logo", defaultTimeout); //"gwt-debug-logoButton"
		logoButton.click();
	}
	
	@Test(groups = { "wanted" }, dataProvider="DomainClasses") //, dependsOnMethods = "loginMaxMustermann")
	public void displayListOf(String domainClassName) {
		
		openWelcomeScreen();
		WebElement link = waitForElement("gwt-debug-LIST_ALL_" + domainClassName, defaultTimeout);
		link.click();
		
		//Problem: get will refresh the page if the url is already the same, therefore replaced with welcomescreen-clicking
//		webDriver.get(websiteUrl + "/#LIST_ALL_" + domainClassName);
		
//		WebElement listRootPanel = WaitForElement("gwt-debug-listRootPanel", 4000);
		WebElement listRootPanel = waitTillElementContains("gwt-debug-listRootPanel", defaultTimeout, "Anzeige Eintrag");
		
//		Label that contains the count of elements in the list on the client and on the server
//		listRootPanel.findElement(By.xpath("./*/*[position()=2]/*[position()=2]/*/*[position()=13]"))
		
//		refreshButton that will make the list refresh
//		listRootPanel.findElement(By.xpath("./*/*[position()=2]/*[position()=2]/*/*[position()=11]"))
		
		String countLabel = listRootPanel.findElement(By.xpath("./*/*[position()=2]/*[position()=2]/*/*[position()=13]")).getText();
		String count = countLabel.substring(countLabel.lastIndexOf(" ")+1);
		countAfterListOpened = Integer.valueOf(count);
		System.out.println("count = " + count);
		
//		driver.findElement(by.) element.getText()
//		System.out.println("listRootPanel.text=" + listRootPanel.getText());
//		printChildText(listRootPanel, " ");
	}

	@Test() //dependsOnMethods = "displayListOf")
	public void openFirstListElement() {
		WebElement listRootPanel = waitTillElementContains("gwt-debug-listRootPanel", defaultTimeout, "Anzeige Eintrag");
		if(countAfterListOpened > 0) {
			//                                             RootPanel/div tabindex=0 /div style pixel/div class XMOC /div class XG0B /div class XB-B /table cl. XNOC /tbody nr. 2   /tr/td/div
			WebElement div = listRootPanel.findElement(By.xpath("./*[position()=1]/*[position()=2]/*[position()=1]/*[position()=2]/*[position()=1]/*[position()=1]/*[position()=2]/*/*"));
//			WebElement tr = listRootPanel.findElement(By.xpath("./*[position()=1]/*[position()=2]/*[position()=1]/*[position()=2]/*[position()=1]/*[position()=1]/*[position()=2]/*"));
//			WebElement td = listRootPanel.findElement(By.xpath("./*[position()=1]/*[position()=2]/*[position()=1]/*[position()=2]/*[position()=1]/*[position()=1]/*[position()=2]/*/*"));
			
			Actions actions = new Actions(webDriver);
			
			actions.doubleClick(div);
			Action dblClick = actions.build();
			dblClick.perform();
			
//			Action dblClick = actions.doubleClick(div);
//			dblClick.build().perform();
//			
//			actions.click(div);
//			actions.click(div);
			
//			actions.doubleClick(div);
//			actions.doubleClick(td);
//			actions.doubleClick(tr);
		}
		else
			System.out.println("could not open element, since the list was empty!");
	}
	
	//@Test(groups = { "wanted" }) //, dependsOnMethods = "displayListOf")
	public void refreshList() {
		WebElement listRootPanel = waitTillElementContains("gwt-debug-listRootPanel", defaultTimeout, "Anzeige Eintrag");
		WebElement button = listRootPanel.findElement(By.xpath("./*/*[position()=2]/*[position()=2]/*/*[position()=11]"));
		button.click();
	}
	
	@Test(dataProvider="DomainClasses") //, dependsOnMethods = "loginMaxMustermann")
	public void openAndRefreshList(String domainClassName) {
		displayListOf(domainClassName);
		refreshList();
	}

	@Test(dataProvider="DomainClasses") //, dependsOnMethods = "loginMaxMustermann")
	public void exportDomainObjectList(String domainClassName) {
		displayListOf(domainClassName);
		WebElement exportButton = waitForElement("gwt-debug-CenterHeader-exportButton", defaultTimeout);
		exportButton.click();
	}
	
	@Test()
	public void logoutCurrentUser() {
		if(loginPage.logout())
			System.out.println("Logged out current user");
		else
			System.out.println("nobody was logged in while running test logoutCurrentUser()");
	}
	
	@SuppressWarnings("unused")
	private void printChildText(WebElement element, String prefix) {
		List<WebElement> childs = element.findElements(By.xpath("./*"));
		for(WebElement el : childs) {
			String text = el.getText();
			System.out.println(prefix + "child.text=" + text.substring(0, Math.min(50, text.length())));
			printChildText(el, " " + prefix);
		}
	}
	
	private WebElement waitForElement(final String id, final int timeout) {
		new Wait() {
			@Override
			public boolean until() {
				try {
					List<WebElement> elements = webDriver.findElements(By.id(id));
					return (elements.size() > 0);
				}
				catch(NoSuchElementException e) {
					return false;
				}
			}
		}.wait("Element was not findable within " + timeout + " ms!", timeout);
		return webDriver.findElement(By.id(id));
	}
	
	private WebElement waitTillElementContains(final String id, final int timeout, final String content) {
		new Wait() {
			@Override
			public boolean until() {
				try {
					WebElement element = webDriver.findElement(By.id(id));
					return (element.getText().contains(content));
				}
				catch(NoSuchElementException e) {
					return false;
				}
			}
		}.wait("Element was not findable within " + timeout + " ms!", timeout);
		return webDriver.findElement(By.id(id));
	}
}
