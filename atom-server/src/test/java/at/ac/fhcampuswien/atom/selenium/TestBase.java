/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.selenium;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.ScreenshotException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import at.ac.fhcampuswien.atom.selenium.util.Browser;
import at.ac.fhcampuswien.atom.selenium.util.PropertyLoader;
import at.ac.fhcampuswien.atom.selenium.webdriver.WebDriverFactory;

/*
 * Base class for all the test classes
 * 
 * @author Sebastiano Armeli-Battana
 */

public class TestBase {
    private static final String SCREENSHOT_FOLDER = "test-output/screenshots/";
    private static final String SCREENSHOT_FORMAT = ".png";

    protected WebDriver webDriver;

    protected String gridHubUrl;

    protected String websiteUrl;

    protected Browser browser;

    @Parameters({ "url", "browserName" })
    @BeforeClass
    public void init(String url, String browserName) {
	websiteUrl = url;
	// websiteUrl = PropertyLoader.loadProperty("site.url");
	gridHubUrl = PropertyLoader.loadProperty("grid2.hub");

	browser = new Browser();
	browser.setName(browserName);
	// browser.setName(PropertyLoader.loadProperty("browser.name"));
	browser.setVersion(PropertyLoader.loadProperty("browser.version"));
	browser.setPlatform(PropertyLoader.loadProperty("browser.platform"));

	String username = PropertyLoader.loadProperty("user.username");
	String password = PropertyLoader.loadProperty("user.password");

	webDriver = WebDriverFactory.getInstance(gridHubUrl, browser, username, password);

	// webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	webDriver.manage().window().setSize(new Dimension(1024, 768));
	// webDriver.
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
	if (webDriver != null) {
	    // webDriver.quit();
	}
    }

    @AfterMethod
    public void setScreenshot(ITestResult result) {
	if (true) { // (!result.isSuccess()) {
	    saveScreenshot(result.getName());
	}
    }

    private int screenshotCounter = 0;

    protected void saveScreenshot(String name) {
	try {
	    // WebDriver returned = new Augmenter().augment(webDriver);
	    // if (returned != null) {

	    File f = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
	    screenshotCounter++;
	    try {
		FileUtils.copyFile(f, new File(SCREENSHOT_FOLDER + screenshotCounter + "#" + name + "_" + browser.getName() + SCREENSHOT_FORMAT));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    // }
	} catch (ScreenshotException se) {
	    se.printStackTrace();
	}
    }
}
