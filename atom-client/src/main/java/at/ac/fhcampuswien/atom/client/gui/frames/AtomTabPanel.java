/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class AtomTabPanel extends TabLayoutPanelCopy {

	public AtomTabPanel() {
		this(19, Unit.PX);
	}
	
	public AtomTabPanel(double barHeight, Unit barUnit) {
		super(barHeight, barUnit);
//		this.setStyleName("");
		Style style = this.getStyleElement().getStyle();
		style.setWidth(100, Unit.PCT);
		style.setHeight(100, Unit.PCT);
		
//		this.
	}

	@Override
	public void selectTab(int index) {
		int selectedBefore = getSelectedIndex();
		if (selectedBefore != -1) {
			Widget oldTab = this.getTabWidget(selectedBefore);
			((AttributeGroupTabHeader) oldTab).setInactive();
		}

		if (index != -1) {
			Widget oldTab = this.getTabWidget(index);
			((AttributeGroupTabHeader) oldTab).setActive();
		}

		super.selectTab(index);
	}

	// public void add(Widget child, AttributeGroupTabHeader tab) {
	// // int newWidth = 18 + tab.label.getText().length() * 6;
	// // int newWidthNW = tab.label.getOffsetWidth() + 5;
	// // tab.setWidth(newWidth + "px");
	// // tab.label.setWidth(newWidth + "px");
	// insert(child, tab, getWidgetCount());
	// }
}