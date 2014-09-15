/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;

public class ClientConfig {

    // StyleNames
    public static final String AtomGuiDecoration = "AtomGuiDecoration";
    public static final String CursorNS = "CursorNS";
    public static final String CursorWE = "CursorWE";
    
    public static final String FavoritesNormal = "FavoritesNormal";
    public static final String FavoritesDragOver = "FavoritesDragOver";
    public static final String AttributeViewDragOver = "AttributeViewDragOver";
    
    public static final String generalDnDGroup = "generalDnDGroup";
    
//    public static final String colorBackgroundSensitive = "#ffffff";
//    public static final String colorBackgroundInsensitive = "#f0ebe2";
//
//    public static final String colorTextSensitive = "#575A5F"; //"#000000";
//    public static final String colorTextInsensitive = "#848284";
//    
//    public static final String textShadowSensitive = "none";
//    public static final String textShadowInsensitive = "0.7px 1.4px 0px white";
    
    public static final String colorDragReady = "#dcffdc";
    
    public static final String colorDomainClassMatch = "#f9cbcb";
    public static final String colorDragOverMismatch = "#a9a9a9";
    public static final String colorDragOverMatch = "#f99999";

    public static final String colorDisabledField = "#ebebe4";
    

    // client UI configurations that don't need to be changeable at runtime
    public static final int applicationMargin = 5;
    public static final int borderThickness = 5;
    public static final int doubleClickTime = 200;

    private static ClientConfig instance;

    /**
     * 
     */
    private ClientConfig() {
    }

    private static ClientConfig getInstance() {
	if (instance == null)
	    instance = new ClientConfig();
	return instance;
    }

    /* ******************************************************************************************** */
    /*  browser type */
    /* ******************************************************************************************** */

    private static native String getBrowserTypeNative() /*-{
       	var ua = navigator.userAgent; //.toLowerCase()
       	return ua;
//       	if (ua.indexOf("opera") != -1) {
//       		return "opera";
//       	} else if (ua.indexOf("webkit") != -1) { 
//       		return "safari"; 
//       	} else if (ua.indexOf("msie 6.0") != -1) {
//       		return "ie6";
//       	} else if (ua.indexOf("msie 7.0") != -1) {
//       		return "ie7";
//       	} else if (ua.indexOf("gecko") != -1) { 
//       		var result = /rv:([0-9]+)\.([0-9]+)/.exec(ua);
//       		if (result && result.length == 3){
//       			var version = (parseInt(result[1]) * 10) + parseInt(result[2]);
//       			if (version >= 18) return "gecko1_8"; 
//       		} 
//       		return "gecko";
//       	}
//       	return "unknown=" + ua; 
       }-*/;

    enum BrowserType {
	opera, safari, ie6, ie7, gecko
    }

    private static String cachedBrowserType = null;

    public static String getBrowserType() {
	if (cachedBrowserType == null) {
		cachedBrowserType = getBrowserTypeNative();
		AtomTools.log(Log.LOG_LEVEL_INFO, "browser-type: '" + cachedBrowserType + "'", getInstance());
	}
	return cachedBrowserType;
    }

    /* ******************************************************************************************** */
    /*  general graphic properties */
    /* ******************************************************************************************** */

    public static int getScrollBarLengthMinimum() {
	// TODO: return browser dependent value
	// 30 for GWT hosted mode browser
	if(GWT.isScript() == false || getBrowserType() == "gecko1_8") {
	    // we are running in hosted mode
	    return 30;
	} else {
	    return 25;
	}
    }

    public static int getScrollBarThickness() {
	// TODO: return browser dependent value
	//15 for GWT hosted mode browser
	if("gecko".equals(getBrowserType())) { //z.B. Hosted Mode Browser
	    return 15;
	}
	else if("gecko1_8".equals(getBrowserType())) { //z.B.: Firefox 3.0
	    return 25;
	} else {
	    return 20;
	}
    }

    public static int getApplicationWidthMinimum() {
//	return getScrollBarLengthMinimum() * 2 + borderThickness * 3;
	return 300;
    }

    public static int getApplicationHeightMinimum() {
//	return getScrollBarLengthMinimum() * 3 + borderThickness * 4;
	return 200;
    }

    public static int getSplitterCenter() {
	return applicationMargin + ((int) Math.floor(borderThickness * 1.5));
    }

    /* ******************************************************************************************** */
    /*  south bar properties */
    /* ******************************************************************************************** */

    private int actualSouthHeight = 80;

    public static int getSouthHeightDislpay(int applicationHeight) {
	return AtomTools.getIntInRange(getSouthHeightMinimum(), getSouthHeight(), getSouthHeightMaximum(applicationHeight));
    }

    public static void setSouthHeight(int newSouthHeight, boolean storeOutOfRange, int applicationHeight) {
	if (storeOutOfRange)
	    getInstance().actualSouthHeight = newSouthHeight;
	else
	    getInstance().actualSouthHeight = AtomTools.getIntInRange(getSouthHeightMinimum(), newSouthHeight, getSouthHeightMaximum(applicationHeight));
    }

    private static int getSouthHeight() {
	return getInstance().actualSouthHeight;
    }

    private static int getSouthHeightMinimum() {
	return getScrollBarLengthMinimum();
    }

    private static int getSouthHeightMaximum(int availableHeight) {
	return Math.min(availableHeight / 2, availableHeight - getScrollBarLengthMinimum() * 2 - borderThickness * 3);
    }

    /* ******************************************************************************************** */
    /*  west bar properties */
    /* ******************************************************************************************** */

    private int actualWestWidth = 150;

    public static int getWestWidthDisplay(int applicationWidth) {
	return AtomTools.getIntInRange(getWestWidthMinimum(), getWestWidth(), getWestWidthMaximum(applicationWidth));
    }

    public static void setWestWidth(int newWestWidth, boolean storeOutOfRange, int applicationWidth) {
	if (storeOutOfRange)
	    getInstance().actualWestWidth = newWestWidth;
	else
	    getInstance().actualWestWidth = AtomTools.getIntInRange(getWestWidthMinimum(), newWestWidth, getWestWidthMaximum(applicationWidth));
    }

    private static int getWestWidth() {
	return getInstance().actualWestWidth;
    }

    private static int getWestWidthMinimum() {
	return getScrollBarLengthMinimum();
    }

    private static int getWestWidthMaximum(int applicationWidth) {
	return (applicationWidth - borderThickness * 3) / 2;
    }
    
//    private static int getWestHeightMinimum() {
//	return getScrollBarLengthMinimum()*2 + borderThickness;
//    }

    /* ******************************************************************************************** */
    /*  northwest bar properties */
    /* ******************************************************************************************** */

    private int actualNorthWestHeight = 200;

    public static int getNorthWestHeightDisplay(int applicationHeight) {
	return AtomTools.getIntInRange(getNorthWestHeightMinimum(), getNorthWestHeight(), getNorthWestHeightMaximum(applicationHeight));
    }

    public static void setNorthWestHeight(int newNorthWestHeight, boolean storeOutOfRange, int applicationHeight) {
	if (storeOutOfRange)
	    getInstance().actualNorthWestHeight = newNorthWestHeight;
	else
	    getInstance().actualNorthWestHeight = AtomTools.getIntInRange(getNorthWestHeightMinimum(), newNorthWestHeight,
		    getNorthWestHeightMaximum(applicationHeight));
    }

    public static int getNorthWestHeight() {
	return getInstance().actualNorthWestHeight;
    }

    private static int getNorthWestHeightMinimum() {
	return getScrollBarLengthMinimum();
    }

    private static int getNorthWestHeightMaximum(int northHeight) {
	return northHeight - borderThickness - getScrollBarLengthMinimum();
    }
}
