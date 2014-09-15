/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.dnd;

import at.ac.fhcampuswien.atom.client.App;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class AtomDragController extends PickupDragController {

	public AtomDragController(AbsolutePanel boundaryPanel) {
		super(boundaryPanel, false);
		this.setBehaviorDragProxy(true);
	    setBehaviorMultipleSelection(false);
		this.setBehaviorDragStartSensitivity(1);
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		Widget widget = context.selectedWidgets.get(0);

		int widgetLeft = widget.getAbsoluteLeft();
		int widgetTop = widget.getAbsoluteTop();
		int diffX = context.mouseX - widgetLeft;
		int diffY = context.mouseY - widgetTop;

		Widget proxy = ((AtomDNDWidget) widget).getDndProxy(diffX, diffY);
		Style proxyStyle = proxy.getElement().getStyle();

		int proxyPaddingLeft = Math.max(diffX - 50, 0);
		int proxyPaddingTop = Math.max(diffY - 5, 0);
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "settingProxyPaddingLeft='" + proxyPaddingLeft + "'", this);
		proxyStyle.setPaddingLeft(proxyPaddingLeft, Unit.PX);
		proxyStyle.setPaddingTop(proxyPaddingTop, Unit.PX);
		return proxy;

		// proxyStyle.setBackgroundColor("green");
	}

	@Override
	public void previewDragStart() throws VetoDragException {
//		disableTextSelection();
		Widget widget = context.selectedWidgets.get(0);

		int widgetLeft = widget.getAbsoluteLeft();
		int widgetTop = widget.getAbsoluteTop();
		int diffX = context.mouseX - widgetLeft;
		int diffY = context.mouseY - widgetTop;

		((AtomDNDWidget) widget).previewDragStart(diffX, diffY);
		App.dragStart();
	}
	
	@Override
	public void dragEnd() {
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "dragEnd called", this);
//		enableTextSelection();
		App.dragEnd();
		super.dragEnd();
	}
	
	private static native void enableTextSelection() /*-{
	  document.onselectstart = function(){ return true; }
	}-*/;
	
	private static native void disableTextSelection() /*-{
	  document.onselectstart = function(){ return false; }
	}-*/;
}
