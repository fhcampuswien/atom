/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.gxt;

import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

class MyPagingToolBar extends PagingToolBar {

	public MyPagingToolBar(int pageSize) {
		super(pageSize);
	}

	protected boolean refreshing = false;

	@Override
	public void refresh() {
		refreshing = true;
		super.refresh();
		refreshing = false;
	}

}