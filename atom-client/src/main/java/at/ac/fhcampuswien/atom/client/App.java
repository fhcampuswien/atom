/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import at.ac.fhcampuswien.atom.client.gui.AtomClientBundle;
import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.client.gui.ClipBoard;
import at.ac.fhcampuswien.atom.client.gui.HistoryPanel;
import at.ac.fhcampuswien.atom.client.gui.LoginInfoPanel;
import at.ac.fhcampuswien.atom.client.gui.ObjectSelectorMenue;
import at.ac.fhcampuswien.atom.client.gui.dnd.AtomDNDWidget;
import at.ac.fhcampuswien.atom.client.gui.dnd.AtomDragController;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectDetailFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectListFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.DomainObjectSearchFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.Frame;
import at.ac.fhcampuswien.atom.client.gui.frames.ImportFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.LoginFrame;
import at.ac.fhcampuswien.atom.client.gui.frames.WelcomeFrame;
import at.ac.fhcampuswien.atom.client.rpc.RPCCaller;
import at.ac.fhcampuswien.atom.client.rpc.WaitingFor;
import at.ac.fhcampuswien.atom.shared.AtomConfig;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.domain.ClipBoardEntry;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.domain.FrameVisit;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

	private RootPanel topRoot, frameArea, sidePanelRestore, sidePanel, frameTitle, frameSpace, historyLabel, historyClear, historyCollapse, historyRoot, historyPanel, clipboardLabel, clipboardCollapse, clipboardRoot, clipboardPanel, searchBox, searchArrow, searchSimpleLabel, searchSimpleCheck, loginPan, homeLogo, appLogo, helpLogo, testarea, navPan2, sliderNS, sliderEW;

	private double sidePanelPct = 0.5, sidePanelPctDD = 0.5;
	
	private boolean loggedIn = false;
	private LoginInfoPanel loginInfoPanel;
	private LoginFrame loginFrame;

	private CenterHeader centerHeader;
	private HistoryPanel history;
	private ClipBoard clipboard;
	private BrowserHistoryHandler browserHistoryHandler;
	private static App singleton;

	private Frame lastShownFrame = null;
	private AtomDragController dragController = new AtomDragController(RootPanel.get());

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		AtomTools.log(Log.LOG_LEVEL_INFO, "started ATOM onModuleLoad", this);
		
		// xapi GWT compiler enhances the class
		// GwtReflect.magicClass(DomainObject.class);
		
		Logger logger = Logger.getLogger("test");
		logger.log(Level.WARNING, "test");
		
		// Inject the contents of the CSS file
		AtomClientBundle.INSTANCE.css().ensureInjected();

		Window.setTitle("DIA - loading..");

		singleton = this;
		topRoot = RootPanel.get();
		frameTitle = RootPanel.get("frame-title");
		frameSpace = RootPanel.get("frame");
		sidePanelRestore = RootPanel.get("side-panel-restore");
		historyClear = RootPanel.get("history-clear");
		historyCollapse = RootPanel.get("history-collapse");
		historyLabel = RootPanel.get("history-label");
		historyRoot = RootPanel.get("history");
		historyPanel = RootPanel.get("history-pan");
		clipboardCollapse = RootPanel.get("clipboard-collapse");
		clipboardLabel = RootPanel.get("clipboard-label");
		clipboardRoot = RootPanel.get("clipboard");
		clipboardPanel = RootPanel.get("clipboard-pan");
		searchBox = RootPanel.get("search-box");
		searchArrow = RootPanel.get("search-arrow");
		searchSimpleLabel = RootPanel.get("search-simple-label");
		searchSimpleCheck = RootPanel.get("search-simple-check");
		loginPan = RootPanel.get("login-pan");
		homeLogo = RootPanel.get("home-logo");
		appLogo = RootPanel.get("app-logo");
		helpLogo = RootPanel.get("help-logo");
		testarea = RootPanel.get("testarea");
		sliderNS = RootPanel.get("sliderNS");
		sliderEW = RootPanel.get("sliderEW");
		frameArea = RootPanel.get("frame-area");
		navPan2 = RootPanel.get("nav-pan2"); 
		sidePanel = RootPanel.get("side-panel-container");

		searchVisible(false);
		
		centerHeader = new CenterHeader();
		frameTitle.getElement().setInnerHTML("");
		frameTitle.getElement().getStyle().setPaddingTop(0, Unit.PX);
		frameTitle.getElement().getStyle().setHeight(38, Unit.PX);
		frameTitle.add(centerHeader);
		centerHeader.setHeader("Seite lädt, bitte warten..");
		centerHeader.setLoading(true);
		
		loginInfoPanel = new LoginInfoPanel();
		loginInfoPanel.loginState(false, null);
		loginPan.getElement().setInnerHTML("");
		loginPan.add(loginInfoPanel);

		browserHistoryHandler = new BrowserHistoryHandler();
		History.addValueChangeHandler(browserHistoryHandler);

		clipboard = new ClipBoard(new RootPanel[]{clipboardLabel, clipboardRoot});
		history = new HistoryPanel(new RootPanel[]{historyLabel, historyRoot});
		clipboardRoot.add(clipboard);
		historyRoot.add(history);
		
		searchArrow.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "searchSimple = " + searchSimpleCheck.getElement().getPropertyString("checked") + " ; " + searchSimpleCheck.getElement().getPropertyBoolean("checked"), this);
				App.processCommand("SEARCH_" + (searchSimpleCheck.getElement().getPropertyBoolean("checked") ? "SIMPLE_" : "") + searchBox.getElement().getPropertyString("value"));
			}
		}, ClickEvent.getType());
		
		searchBox.addDomHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(ClientTools.hasEnterKeyBeenPressed(event)) {
					AtomTools.log(Log.LOG_LEVEL_DEBUG, "searchSimple = " + searchSimpleCheck.getElement().getPropertyString("checked") + " ; " + searchSimpleCheck.getElement().getPropertyBoolean("checked"), this);
					App.processCommand("SEARCH_" + (searchSimpleCheck.getElement().getPropertyBoolean("checked") ? "SIMPLE_" : "") + searchBox.getElement().getPropertyString("value"));
				}
			}
		}, KeyPressEvent.getType());

		homeLogo.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				App.processCommand("WELCOME");
			}
		}, ClickEvent.getType());
		
		appLogo.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				openCISMenue();
			}
		}, MouseMoveEvent.getType());
				
		helpLogo.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				App.processCommand("HELP");
			}
		}, ClickEvent.getType());
		
		clipboardCollapse.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				toggleSidePanelCollapseRestore(clipboardCollapse);
				updateSidePanelCollapseState();
			}
		}, ClickEvent.getType());
		
		historyClear.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				history.clear();
			}
		}, ClickEvent.getType());
		
		historyCollapse.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				toggleSidePanelCollapseRestore(historyCollapse);
				updateSidePanelCollapseState();
			}
		}, ClickEvent.getType());
		
		sidePanelRestore.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				historyCollapse.removeStyleName("restore");
				clipboardCollapse.removeStyleName("restore");
				updateSidePanelCollapseState();
			}
		}, ClickEvent.getType());
				
		sliderNS.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "sliderNS.onMouseDown", this);
				startSidePanelNSDrag();
				event.preventDefault();
			}
		}, MouseDownEvent.getType());
		
		sliderEW.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "sliderEW.onMouseDown", this);
				startSidePanelEWDrag();
				event.preventDefault();
			}
		}, MouseDownEvent.getType());
		
		// frameTitle.getElement().setInnerHTML("gugu");

		Window.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "resize event occured!", this);
				App.this.resize(event);
			}
		});

		RPCCaller.getSinglton().getServerInfo(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "received serverInfo -> " + result, this);
				singleton.testarea.getElement().setInnerHTML(result);
				RootPanel.get("serverinfo").getElement().setInnerHTML(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AtomTools.log(Log.LOG_LEVEL_WARN, "could not get serverInfo -> " + caught.getMessage(), this);
			}
		});
		
		centerHeader.setHeader("Seite geladen, prüfe Identität..");
		checkLoginState();
		Window.setTitle("DIA");
		AtomTools.log(Log.LOG_LEVEL_INFO, "finished ATOM onModuleLoad", this);
	}
	
	/**
	 * 
	 * @param arrow
	 * @return true if the action that needs to be performed is restore and false for collapse
	 */
	private boolean toggleSidePanelCollapseRestore(RootPanel arrow) {
		if(arrow.getStyleName().contains("restore")) {
			arrow.removeStyleName("restore");
			return true;
		}
		else {
			arrow.addStyleName("restore");
			return false;
		}
	}
	
	private boolean sidePanelHidden = false;
	private void updateSidePanelCollapseState() {
		boolean clipboard = clipboardCollapse.getStyleName().contains("restore");
		boolean history = historyCollapse.getStyleName().contains("restore");

		if(clipboard && history) {
			sidePanelHidden = true;
			sidePanel.getElement().getStyle().setDisplay(Display.NONE);
			frameArea.getElement().getStyle().setRight(0, Unit.PX);
			sliderEW.getElement().getStyle().setDisplay(Display.NONE);
			sidePanelRestore.getElement().getStyle().setDisplay(Display.BLOCK);
			lastShownFrame.resize(lastResizeEvent);
		}
		else {
			if(sidePanelHidden) {
				sidePanelHidden = false;
				sidePanel.getElement().getStyle().clearDisplay();
				frameArea.getElement().getStyle().setRight(sidePanel.getOffsetWidth()+5, Unit.PX);
				sliderEW.getElement().getStyle().clearDisplay();
				sidePanelRestore.getElement().getStyle().clearDisplay();
				
				if(lastShownFrame != null)
					lastShownFrame.resize(lastResizeEvent);
			}
			
			if(!clipboard && !history) {
				clipboardPanel.setHeight((100*sidePanelPct)+"%");
				historyPanel.setHeight(100-(100*sidePanelPct)+"%");	
			}
			else if(clipboard && !history) {
				double minPct = getSidePanelMinPct();
				clipboardPanel.setHeight((100*minPct)+"%");
				historyPanel.setHeight(100-(100*minPct)+"%");
			}
			else if(!clipboard && history) {
				double minPct = 1-getSidePanelMinPct();
				clipboardPanel.setHeight((100*minPct)+"%");
				historyPanel.setHeight(100-(100*minPct)+"%");
			}
		}
	}
	
	public static int getFrameWidth() {
		return singleton.frameArea.getOffsetWidth();
	}
	
	private double getSidePanelMinPct() {
		return (double)75 / sidePanel.getOffsetHeight();
	}
	
	private HandlerRegistration rootMouseUpHR, rootMouseMoveHR;
	private void startSidePanelNSDrag() {
		
		rootMouseUpHR = topRoot.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "topRoot.onMouseUp", this);
				if(stopSidePanelDrag()) {
					
					boolean clipboard = clipboardCollapse.getStyleName().contains("restore");
					boolean history = historyCollapse.getStyleName().contains("restore");
					if(!clipboard && !history) {
						sidePanelPct = sidePanelPctDD;
					}
					
					event.preventDefault();
				}
			}
		}, MouseUpEvent.getType());
		
		rootMouseMoveHR = topRoot.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				int relMousePos = event.getClientY() - sidePanel.getAbsoluteTop();
				sidePanelPctDD = (double)(relMousePos) / (sidePanel.getOffsetHeight());
				AtomTools.log(Log.LOG_LEVEL_TRACE, "topRoot.onMouseMove - " 
						+ event.getClientX() + ";" + event.getClientY() + ";" 
						+ (sidePanel.getOffsetHeight())
						+ ";" + sidePanelPctDD, this);
				
				double minPct = getSidePanelMinPct();
				if(sidePanelPctDD < minPct) {
					//clipboard collapsed
					historyCollapse.removeStyleName("restore");
					clipboardCollapse.addStyleName("restore");
					sidePanelPctDD = minPct;
				}
				else if(sidePanelPctDD > 1-minPct) {
					//history collapsed
					historyCollapse.addStyleName("restore");
					clipboardCollapse.removeStyleName("restore");
					sidePanelPctDD = 1-minPct;
				}
				else {
					historyCollapse.removeStyleName("restore");
					clipboardCollapse.removeStyleName("restore");
				}
				
				clipboardPanel.setHeight((100*sidePanelPctDD)+"%");
				historyPanel.setHeight(100-(100*sidePanelPctDD)+"%");
				
				history.resize(lastResizeEvent);
				clipboard.resize(lastResizeEvent);
				
				event.preventDefault();
			}
		}, MouseMoveEvent.getType());
	}
	
	private boolean stopSidePanelDrag() {
		if(rootMouseUpHR != null && rootMouseMoveHR != null) {
			rootMouseUpHR.removeHandler();
			rootMouseUpHR = null;
			rootMouseMoveHR.removeHandler();
			rootMouseMoveHR = null;
			return true;
		}
		else {
			return false;
		}
	}

	private void startSidePanelEWDrag() {
		rootMouseUpHR = topRoot.addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "topRoot.onMouseUp", this);
				if(stopSidePanelDrag()) {
					event.preventDefault();
				}
			}
		}, MouseUpEvent.getType());
		
		rootMouseMoveHR = topRoot.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "topRoot.onMouseMove - " 
						+ event.getClientX() + ";" + event.getClientY() + ";" 
						+ (sidePanel.getOffsetHeight())
						+ ";" + topRoot.getOffsetWidth() , this);
				
				int sidePanelWidth = topRoot.getOffsetWidth() - event.getClientX();
				
				if(sidePanelWidth <130) {
					stopSidePanelDrag();
					historyCollapse.addStyleName("restore");
					clipboardCollapse.addStyleName("restore");
					updateSidePanelCollapseState();
				}
				else {
					sidePanelWidth = Math.max(sidePanelWidth, 179);
					sidePanelWidth = Math.min(sidePanelWidth, topRoot.getOffsetWidth()/2);
					sidePanel.setWidth(sidePanelWidth-8 + "px");
					frameArea.getElement().getStyle().setRight(sidePanelWidth-3, Unit.PX);
					sliderEW.getElement().getStyle().setRight(sidePanelWidth-1, Unit.PX);
					
					lastShownFrame.resize(lastResizeEvent);
					centerHeader.setHeader(lastShownFrame.getLongTitle());
					history.resize(lastResizeEvent);
					clipboard.resize(lastResizeEvent);
				}
				event.preventDefault();
			}
		}, MouseMoveEvent.getType());
	}

	private void checkLoginState() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "atom-client.App starting checkExistingSession request to server", this);
		RPCCaller.getSinglton().checkExistingSession(null, new Notifiable<String>() {
			public void doNotify(String notifyReason) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "atom-client.App checkExistingSession response recieved: " + notifyReason, this);
				if (notifyReason.startsWith("session valid;")) {
					updateLoginState(true, notifyReason.substring("session valid;".length()));
				} else {
					updateLoginState(false, null);
				}
			}
		});
	}

	/**
	 * Makes the GUI represent the given login-state
	 * 
	 * @param state
	 *            null = please wait, false = not logged in, true = logged in
	 * @param userString
	 *            represents the user or the error that happened.
	 */
	private void updateLoginState(Boolean state, String userString) {
		loginInfoPanel.loginState(state, userString);
		if (state == null) {
			loggedIn = false;
			centerHeader.setHeader(AtomTools.getMessages().login_pruefen());
			centerHeader.setState(CenterHeader.State.EMPTY);
			frameSpace.clear();
		} else if (state == false) {
			loggedIn = false;
			centerHeader.setHeader(AtomTools.getMessages().login_aufforderung());
			centerHeader.setState(CenterHeader.State.EMPTY);

			if (loginFrame == null) {
				loginFrame = new LoginFrame();
			}
			frameSpace.clear();
			frameSpace.add(loginFrame);
			loginFrame.setError(userString);
			centerHeader.setLoading(false);
		} else {
			if (loginFrame != null) {
				loginFrame.removeFromParent();
				loginFrame = null;
			}
			loggedIn = true;

			// load domainTree and history & clipboard
			RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
				
				@Override
				public void requestFailed(String reason) {
				}
				
				@Override
				public void recieve(DomainClass instance) {
					history.loadEntriesFromServer(instance.getDomainClassNamed(FrameVisit.class.getName()));
					clipboard.loadEntriesFromServer(instance.getDomainClassNamed(ClipBoardEntry.class.getName()));
				}
			});
			

			if (!processCommand(History.getToken()))
				actionHome();
		}

		searchVisible(loggedIn);
	}
	
	private void searchVisible(boolean visible) {
		searchArrow.setVisible(visible);
		searchBox.setVisible(visible);
		searchSimpleLabel.setVisible(visible);
		searchSimpleCheck.setVisible(visible);
	}

	private void tryLogin() {
		updateLoginState(null, null);
		String userName = loginFrame.getEnteredUserName();
		String password = loginFrame.getEnteredPassword();
		AtomTools.log(Log.LOG_LEVEL_TRACE, "atom-client.App starting login request to server", this);
		RPCCaller.getSinglton().loginUser(userName, password, new Notifiable<String>() {
			public void doNotify(String notifyReason) {
				AtomTools.log(Log.LOG_LEVEL_TRACE, "atom-client.App recieved login response from server: " + notifyReason, this);
				if (notifyReason.startsWith("login successful;")) {
					updateLoginState(true, notifyReason.substring("login successful;".length()));
				} else {
					updateLoginState(false, notifyReason);
				}
			}
		});
	}

	public static void startCreatingNewState() {
		singleton.browserHistoryHandler.setCreatingNewState(true);
	}

	public static void finishedCreatingNewState() {
		singleton.browserHistoryHandler.setCreatingNewState(false);
	}

	public static void actionLogin() {
		singleton.tryLogin();
	}

	public static void actionLogout() {
		Cookies.removeCookie("CAMPUSAUTH");
		RPCCaller.getSinglton().logout();
		singleton.updateLoginState(false, null);
	}

	public static void actionNew() {
		final Frame newClicker = singleton.lastShownFrame;
		newClicker.actionNewInfoOnly();
		openDetailView(null, newClicker.getRepresentedClass(), true, new WaitingFor<DomainObject>() {

			@Override
			public void recieve(DomainObject instance) {
				newClicker.actionObjectDelivered(instance);
			}

			@Override
			public void requestFailed(String reason) {
			}
		});
	}

	public static void actionFilter() {
		singleton.lastShownFrame.actionFilter();
	}

	public static void actionEdit() {
		singleton.lastShownFrame.actionEdit();
	}

	public static void actionDelete() {
		singleton.lastShownFrame.actionDelete();
	}

	public static void actionSave() {
		singleton.lastShownFrame.actionSave();
	}

	public static void actionCancel() {
		singleton.lastShownFrame.actionCancel();
	}

	public static void actionDuplicate() {
		DomainObject sourceInstance = singleton.lastShownFrame.getRepresentedObject();
		DomainClass sourceClass = singleton.lastShownFrame.getRepresentedClass();
		
		Frame frame = singleton.history.findFrame(null, sourceClass, null, null, FrameType.DETAIL_VIEW);
		if(frame == null)
			frame = new DomainObjectDetailFrame(null, sourceClass, true);
		
		((DomainObjectDetailFrame)frame).applyValuesOf(sourceInstance, false);
		singleton.history.showFrame(frame);
	}

	public static void actionExport() {
//		AtomTools.log(Log.LOG_LEVEL_INFO, "Window.Location.getQueryString() = " + Window.Location.getQueryString(), singleton);
//		AtomTools.log(Log.LOG_LEVEL_INFO, "Window.Location.getHref() = " + Window.Location.getHref() , singleton);
//		AtomTools.log(Log.LOG_LEVEL_INFO, "Window.Location.getPath() = " + Window.Location.getPath() , singleton);
//		AtomTools.log(Log.LOG_LEVEL_INFO, "Window.Location.getPath() = " + Window.Location.getHost() , singleton);
		
		String url = Window.Location.getHref();
		AtomTools.log(Log.LOG_LEVEL_TRACE, "originalUrl = " + url , singleton);
		
		//String commingFrom = url.substring(url.indexOf("#")+1);
		
		if(url.contains("?"))
			url = url.substring(0, url.indexOf("?"));
		else if(url.contains("#"))
			url = url.substring(0, url.indexOf("#"));
		
		AtomTools.log(Log.LOG_LEVEL_TRACE, "cutUrl = " + url , singleton);
		
		url = AtomTools.ensureEndsWithSlash(url);
		
		//export servlet (and every other servlet but the gwt-rpc one) are defined to be on the root folder
		// @ atom/atom-server/src/main/webapp/WEB-INF/web.xml
		
//		if(!(url.endsWith("app/")))
//			url += "app/";
			
		url += "export?class=" + singleton.lastShownFrame.getRepresentedClass().getName();
		url += "&filter=" + AtomTools.getFilterString(singleton.lastShownFrame.getDataFilters());
		AtomTools.log(Log.LOG_LEVEL_TRACE, "finishedUrl = " + url , singleton);
		
		Window.open(url, "_blank", "");
	}

	public static void actionImport() {
		AtomTools.log(Log.LOG_LEVEL_INFO, "importAction called", singleton);
		DomainClass domainClass = singleton.lastShownFrame.getRepresentedClass();
		if (!singleton.history.activateFrameIfExists(null, domainClass, null, null, AtomConfig.FrameType.IMPORT)) {
			singleton.centerHeader.setLoading(true);
			singleton.history.addFrame(new ImportFrame(domainClass));
		}
	}

	public static void actionOpenProfile() {
		openDetailView(RPCCaller.getSinglton().getClientSession().getUser(), null, false);
	}
	
	public static void actionHome() {
		if (singleton.loggedIn) {
			if (!singleton.history.activateFrameIfExists(null, null, null, null, FrameType.WELCOME))
				singleton.history.addFrame(new WelcomeFrame());
		}
	}

	public static boolean isActiveFrame(Frame frame) {
		if (singleton.lastShownFrame == frame) {
			return true;
		}
		return false;
	}

	public static void frameChanged(Frame frame) {
		if (isActiveFrame(frame)) {
			singleton.history.addFrame(frame);
			
			//gets called by history //showFrame(frame);
		}
	}

	public static void refreshActiveFrame() {
		singleton.lastShownFrame.goingVisible();
	}

	public static void openDetailView(final DomainObject object, DomainClass classOfObject, final boolean editable) {
		openDetailView(object, classOfObject, editable, null);
	}

	public static void openDetailView(final DomainObject object, DomainClass classOfObject, final boolean editable, final WaitingFor<DomainObject> reciever) {

		if (singleton.loggedIn) {
			if (classOfObject == null) {
				if (object == null) {
					// wenn weder Klasse noch Objekt übergeben wird -->
					// Klasse des gerade aktiven Frames verwenden
					classOfObject = singleton.lastShownFrame.getRepresentedClass();
				} else {
					// falls nur das zu oeffnende Objekt, nicht aber
					// seine Klasse bekannt ist, finde die Klasse
					RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {

						@Override
						public void requestFailed(String reason) {
							AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
						}

						@Override
						public void recieve(DomainClass domainTree) {
							DomainClass foundClass = domainTree.getDomainClassNamed(object.getConcreteClass());
							singleton.openDetailViewConfirmedClass(object, foundClass, editable, reciever);
						}
					});
				}
			}
			if (classOfObject != null)
				singleton.openDetailViewConfirmedClass(object, classOfObject, editable, reciever);
		}
	}

	private void openDetailViewConfirmedClass(DomainObject object, DomainClass classOfObject, boolean editable, WaitingFor<DomainObject> reciever) {
		
		Set<String> accessTypes = null;
		if(object != null)
			accessTypes = object.getUserPermissions();
		if(accessTypes == null)
			accessTypes = classOfObject.getAccessHandler().getNoRelationRequiredAccess(RPCCaller.getSinglton().getClientSession());
			//AtomTools.getNoRelationRequiredAccess(classOfObject.getUsersAccess());
		
		if (editable && !
				(
					accessTypes.contains(AtomConfig.accessReadWrite) || 
					(
						object == null && accessTypes.contains(AtomConfig.accessCreateNew)
					)
				)
			) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "User does not have write access to this object, set editable=false!", this);
			editable = false;
		}

		if (object == null && !editable) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "It makes absolutly no sense to create a read-only DetailView for a new instance!", this);
			return;
		}

		if (history.activateFrameIfExists(object, classOfObject, null, null, AtomConfig.FrameType.DETAIL_VIEW)) {
			
			//don't cancel an ongoing edit, the user could possibly loose data by that!
			if(editable)
				((DomainObjectDetailFrame) lastShownFrame).setEditable(editable);
			
			((DomainObjectDetailFrame) lastShownFrame).setObjectReciever(reciever);
		} else {
			centerHeader.setLoading(true);
			history.addFrame(new DomainObjectDetailFrame(object, classOfObject, editable, reciever));
		}
	}

	public static String getShortenedString(String text, String style, int targetPixs) {

		if (targetPixs <= 0)
			return text;

		int highPixs = style == null ? getTextWidth(text) : getTextWidth(text, style);
		if(highPixs <= targetPixs)
			return text;
		
		int highLength = text.length();
		int lowLength = 0;
		int lowPixs = 0;

		while(true) {
			
			int testLength = lowLength + (Math.max((highLength-lowLength)/2, 1));
			String testText = text.substring(0,testLength) + "…";
			int testPixs = style == null ? getTextWidth(testText) : getTextWidth(testText, style);
			
			if(testPixs > targetPixs) {
				highLength = testLength;
				highPixs = testPixs;
			}
			else {
				lowLength = testLength;
				lowPixs = testPixs;
			}
			if(highLength-lowLength <= 1 || targetPixs-lowPixs <= 3) {
				return text.substring(0,Math.max(lowLength,2)) + "…";
			}
		}
	}

	public static int getTextWidth(String text) {
		singleton.testarea.getElement().setInnerHTML(text);
		int returnValue = singleton.testarea.getOffsetWidth();
		singleton.testarea.getElement().setInnerHTML("");
		
		AtomTools.log(Log.LOG_LEVEL_TRACE, "found pixel width = " + returnValue + " for string '" + text + "' ; maxWidth = " + getTextWidthTestAreaMaxWidth(), singleton);
		return returnValue;
	}

	public static int getTextWidth(String text, String style) {
		singleton.testarea.setStyleName(style);
		int returnValue = getTextWidth(text);
		singleton.testarea.setStyleName(null);
		return returnValue;
	}
	
	public static int getTextWidthTestAreaMaxWidth() {
		return singleton.navPan2.getElement().getOffsetWidth();
	}

	public static void registerDndDropController(AbstractDropController abstractDropController) {
		singleton.dragController.registerDropController(abstractDropController);
	}

	public static void unregisterDndDropController(AbstractDropController abstractDropController) {
		singleton.dragController.unregisterDropController(abstractDropController);
	}

	public static void registerDndWidget(Widget widget) {
		if (widget instanceof AtomDNDWidget)
			singleton.dragController.makeDraggable(widget);
		else
			AtomTools.log(Log.LOG_LEVEL_ERROR, "only Widgets that implement the interface AtomDNDWidget are allowed to be made dragable in ATOM! given widget='"
					+ widget + "'", singleton);
	}

	public static void unRegisterDndWidget(Widget widget) {
		try {
			singleton.dragController.makeNotDraggable(widget);
		} catch (Throwable t) {
			AtomTools.log(Log.LOG_LEVEL_ERROR, "makeNotDraggable failed: " + t.toString(), singleton);
			t.printStackTrace();
		}
	}
	
	private boolean currentlyDragging = false;
	public static void dragStart() {
		singleton.currentlyDragging = true;
	}
	public static void dragEnd() {
		singleton.currentlyDragging = false;
		App.suggestDND(null);
	}

	public static void openList(DomainClass classToOpen, boolean onlyRelated) {
		if (singleton.loggedIn) {
			if (!singleton.history.activateFrameIfExists(null, classToOpen, null, null, onlyRelated ? AtomConfig.FrameType.LIST_RELATED : AtomConfig.FrameType.LIST_ALL)) {
				singleton.history.addFrame(new DomainObjectListFrame(classToOpen, null, true, null, onlyRelated));
			}
		}
	}

	/**
	 * 
	 * @param command
	 * @return true if the command was understood and can/could be executed.
	 */
	public static boolean processCommand(String command) {
		if (!singleton.loggedIn) {
			return false;
		} else {
			singleton.centerHeader.setLoading(true);
			if (command.startsWith("DETAIL_VIEW_")) {
				if (command.startsWith("DETAIL_VIEW_new_")) {
					final String className = command.substring("DETAIL_VIEW_new_".length());
					RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
	
						@Override
						public void requestFailed(String reason) {
							AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
						}
	
						@Override
						public void recieve(DomainClass domainTree) {
							App.openDetailView(null, domainTree.getDomainClassNamed(className), true);
						}
					});
				} else {
					String details = command.substring("DETAIL_VIEW_".length());
					String[] det = details.split("_");
					String objectId = det[0];
					String className = null;
					final boolean editable;
					if(det.length > 1) {
						if("true".equalsIgnoreCase(det[1]) || "1".equalsIgnoreCase(det[1])) {
							editable = true;
						}
						else if("false".equalsIgnoreCase(det[1]) || "0".equalsIgnoreCase(det[1])) {
							editable = false;
						}
						else {
							className = det[1];
							editable = false;
						}
						if(className == null && det.length > 2) {
							className = det[2];
						}
					}
					else {
						editable = false;
					}
	
					DomainObject object = RPCCaller.getSinglton().getLoadedObject(Integer.valueOf(objectId));
					if (object != null) {
						App.openDetailView(object, null, editable);
					} else {
						RPCCaller.getSinglton().loadDomainObject(Integer.valueOf(objectId), className, new WaitingFor<DomainObject>() {
	
							@Override
							public void requestFailed(String reason) {
								Window.alert(reason);
								actionHome();
							}
	
							@Override
							public void recieve(DomainObject instance) {
								App.openDetailView(instance, null, editable);
							}
						});
					}
				}
				return true;
			} else if (command.startsWith("LIST_")) {
				final String className = command.substring(command.lastIndexOf("_") + 1);
				final boolean onlyRelated;
				if (command.startsWith("LIST_ALL_")) {
					onlyRelated = false;
				} else if (command.startsWith("LIST_RELATED_")) {
					onlyRelated = true;
				} else {
					onlyRelated = false;
					AtomTools.log(Log.LOG_LEVEL_ERROR, "unknown LIST history String format: " + command, singleton);
				}
	
				RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
	
					@Override
					public void requestFailed(String reason) {
						AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
					}
	
					@Override
					public void recieve(DomainClass domainTree) {
						DomainClass domainClass = domainTree.getDomainClassNamed(className);
						if (domainClass != null)
							App.openList(domainClass, onlyRelated);
						else {
							Window.alert("No class with name '" + className + "' found.");
							singleton.history.addFrame(new WelcomeFrame());
						}
					}
				});
				return true;
	
			} else if (command.startsWith("IMPORT_")) {
				final String className = command.substring(command.lastIndexOf("_") + 1);
				RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {
	
					@Override
					public void requestFailed(String reason) {
						AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
					}
	
					@Override
					public void recieve(DomainClass domainTree) {
						DomainClass domainClass = domainTree.getDomainClassNamed(className);
						if (domainClass != null) {
							if (!singleton.history.activateFrameIfExists(null, domainClass, null, null, AtomConfig.FrameType.IMPORT)) {
								singleton.centerHeader.setLoading(true);
								singleton.history.addFrame(new ImportFrame(domainClass));
							}
						}
						else {
							Window.alert("No class with name '" + className + "' found.");
							singleton.history.addFrame(new WelcomeFrame());
						}
					}
				});
				return true;
	
			} else if (command.startsWith("SEARCHCLASS_")) {
				processClassSearch(command);
				return true;
				
			} else if (command.startsWith("FILTERCLASS_")) {
				processFilterSearch(command);
				return true;
				
			} else if (command.startsWith("SEARCH_")) {
				boolean simple = (command.startsWith("SEARCH_SIMPLE_"));
				FrameType frameType = simple ? FrameType.SEARCH_SIMPLE : FrameType.SEARCH;
				final String searchTerm = command.substring(frameType.toString().length() + 1);
				if (!singleton.history.activateFrameIfExists(null, null, searchTerm, null, frameType))
					singleton.history.addFrame(new DomainObjectSearchFrame(searchTerm, simple, false));
				return true;
			} else if ("WELCOME".equals(command)) {
				if (!singleton.history.activateFrameIfExists(null, null, null, null, FrameType.WELCOME))
					singleton.history.addFrame(new WelcomeFrame());
				return true;
			} else if ("HELP".equals(command)) {
				if (!singleton.history.activateFrameIfExists(null, null, null, null, FrameType.HELP))
					singleton.history.addFrame(new Frame("Hilfe leider noch nicht verfügbar.", "Hilfe", CenterHeader.State.EMPTY, null, null, null,
						AtomConfig.FrameType.HELP));
				return true;
			}
		}
		return false;
	}
	
	private static void processFilterSearch(String c) {
		c = c.substring("FILTERCLASS_".length());
		String className = c.substring(0, c.indexOf("_"));
		c = c.substring(className.length()+1);
		ArrayList<DataFilter> filters = AtomTools.parseFilterString(c);
		
		processClassSearchPart2(className, filters);
	}

	private static void processClassSearch(String command) {
		
		boolean simple = (command.startsWith("SEARCHCLASS_SIMPLE_"));
		FrameType frameType = simple ? FrameType.SEARCHCLASS_SIMPLE : FrameType.SEARCHCLASS;

		int classNameBeginIndex = frameType.toString().length() + 1;
		
		if(command.substring(classNameBeginIndex+1).contains("_")) {
			int classNameEndIndex = command.indexOf("_", classNameBeginIndex+1);
			int searchTermBeginIndex = classNameEndIndex + 1;
			
			String className = command.substring(classNameBeginIndex, classNameEndIndex);
			String searchTerm = command.substring(searchTermBeginIndex);
						
			String filterName = null;
			if(simple)
				filterName = AtomConfig.specialFilterQuickSearch;
			else
				filterName = AtomConfig.specialFilterDeepSearch;
			
			HashSet<DataFilter> filterConfig =  new HashSet<DataFilter>(1);
			filterConfig.add(new DataFilter(filterName, searchTerm, null, null));
			//.put(simple ? "Schnellsuche" : "durchsuche alle Attribute", searchTerm);
			processClassSearchPart2(className, filterConfig);
		}
		else {
			final String className = command.substring(classNameBeginIndex);
			// new InputDialogBox(null, null, "Bitte Suchbegriff eingeben:", true, "Schnellsuche", null,
			ClientTools.getFilterConfigFromUser(className, false, new WaitingFor<Collection<DataFilter>>() {
				
				@Override
				public void recieve(Collection<DataFilter> instance) {
					processClassSearchPart2(className, instance);
				}

				@Override
				public void requestFailed(String reason) {
					actionHome();
				}
			});
		}
	}
	
	private static void processClassSearchPart2(final String className,
			final Collection<DataFilter> filters) {
				
		RPCCaller.getSinglton().getDomainTree(new WaitingFor<DomainClass>() {

			@Override
			public void requestFailed(String reason) {
				AtomTools.log(Log.LOG_LEVEL_FATAL, "could not get domainTree! -> " + reason, this);
			}

			@Override
			public void recieve(DomainClass domainTree) {
				DomainClass domainClass = domainTree.getDomainClassNamed(className);
				if (domainClass != null) {
					if (!singleton.history.activateFrameIfExists(null, domainClass, null, filters, FrameType.FILTERCLASS))
						singleton.history.addFrame(new DomainObjectListFrame(domainClass, null, false, filters.toArray(new DataFilter[filters.size()]), false));
				} else {
					Window.alert("No class with name '" + className + "' found.");
					singleton.history.addFrame(new WelcomeFrame());
				}
			}
		});
	}

	/**
	 * 
	 * @param frame
	 *            to be shown
	 * @return false, if the currently active frame can't be closed. true, if
	 *         the frame is already shown or was successfully put on top
	 */
	public static boolean showFrame(Frame frame) {
		return singleton.showFrameI(frame);
	}

	/**
	 * 
	 * @param frame
	 *            to be shown
	 * @return false, if the currently active frame can't be closed. true, if
	 *         the frame is already shown or was successfully put on top
	 */
	private boolean showFrameI(Frame frame) {
		if (lastShownFrame != frame) {
			if (lastShownFrame != null) {
				if (!lastShownFrame.goingInvisible())
					return false;
			}
			lastShownFrame = frame;
			lastShownFrame.goingVisible();
		}
		frameSpace.clear();
		centerHeader.setRepresentedDomainObject(frame.getRepresentedObject());
		centerHeader.setState(frame.getCenterHeaderButtonPanelState());
		if(frame.getContent() != null)
			frameSpace.add(frame.getContent());
		centerHeader.setHeader(frame.getLongTitle());
		centerHeader.setLoading(false);
		resizeTimer.schedule(50);
		return true;
	}

	private Timer resizeTimer = new Timer() {
		@Override
		public void run() {
			lastShownFrame.resize(lastResizeEvent);
			centerHeader.setHeader(lastShownFrame.getLongTitle());
		}
	};

	private ResizeEvent lastResizeEvent;
	private boolean secondResizeRequested = false;

	private void resize(ResizeEvent event) {
		secondResizeRequested = false;
		lastResizeEvent = event;

		if (lastShownFrame != null) {
			lastShownFrame.resize(event);
			centerHeader.setHeader(lastShownFrame.getLongTitle());

			if (secondResizeRequested) {
				// lastShownFrame.resize(event);
				frameSpace.fireEvent(event);
				// Window.resizeBy(0, 0);
			}
		}

		history.resize(event);
		clipboard.resize(event);
	}

	public static void requestSecondResize() {
		singleton.secondResizeRequested = true;
		AtomTools.log(Log.LOG_LEVEL_TRACE, "secondResizeRequested", singleton);
	}

	public static void setLoadingState(boolean isCurrentlyLoading, Frame frame) {
		if (frame == null || frame.equals(singleton.lastShownFrame))
			singleton.centerHeader.setLoading(isCurrentlyLoading);
	}

	public static void removeFromUI(DomainObject representedObject) {
		singleton.history.removeFromUI(representedObject);
		singleton.clipboard.removeFromUI(representedObject);
	}

	public static void addFrame(Frame frame) {
		singleton.history.addFrame(frame);
	}

	public static void closeFrame(Frame frame) {
		singleton.history.closeFrame(frame);
	}
	
	private DomainClass currentlySuggesting = null;
	public static void suggestDND(DomainClass dc) {
		if(singleton.currentlySuggesting != dc && !singleton.currentlyDragging) {
			singleton.currentlySuggesting = dc;
			singleton.history.suggestObjectsForDND(dc);
			singleton.clipboard.suggestObjectsForDND(dc);
			if(singleton.lastShownFrame != null)
				singleton.lastShownFrame.suggestDND(dc);
		}
	}

	private PopupPanel cISObjectMenu = null;
	private void openCISMenue() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "opening cISObjectMenu", this);
		if (loggedIn) {
			if (cISObjectMenu == null) {
				makeCISMenue();
			} else {
				ObjectSelectorMenue.prepareViewing();
				cISObjectMenu.show();
				cISObjectMenu.setPixelSize(180, 115);
			}
		}
	}

	private void closeOpenMenue() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "closing cISObjectMenu", this);
		cISObjectMenu.hide();
	}

	private void makeCISMenue() {
		AtomTools.log(Log.LOG_LEVEL_TRACE, "making cISObjectMenu", this);
		if (cISObjectMenu == null) {
			cISObjectMenu = new PopupPanel(true);
			ObjectSelectorMenue popupObjectSelector = ObjectSelectorMenue.getSinglton();
			cISObjectMenu.setPopupPosition(appLogo.getAbsoluteLeft(), appLogo.getAbsoluteTop() + appLogo.getOffsetHeight());
			cISObjectMenu.setStyleName("");
			cISObjectMenu.add(popupObjectSelector);

			// Workaround for smartgwt:
			// http://forums.smartclient.com/showthread.php?t=5526
			cISObjectMenu.getElement().getStyle().setZIndex(300);

			cISObjectMenu.show();
			cISObjectMenu.setPixelSize(180, 1);

			Timer timer = new Timer() {
				@Override
				public void run() {
					AtomTools.log(Log.LOG_LEVEL_TRACE, "running cISObjectMenu Timer", this);
					closeOpenMenue();
					openCISMenue();
				}
			};
			timer.run();
			AtomTools.log(Log.LOG_LEVEL_TRACE, "scheduling cISObjectMenu Timer", this);
			timer.schedule(200);
		}
	}

}
