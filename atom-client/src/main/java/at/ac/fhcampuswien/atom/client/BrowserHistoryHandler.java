/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class BrowserHistoryHandler implements ValueChangeHandler<String> {

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String command = event.getValue();
		if (!creatingNewState) {
			AtomTools.log(Log.LOG_LEVEL_INFO, "BrowserHistoryChange happened: value='" + command + "', type='"
					+ event.getAssociatedType() + "'", this);
			App.processCommand(command);
		}
	}

	public void setCreatingNewState(boolean creatingNewState) {
		this.creatingNewState = creatingNewState;
	}

	private boolean creatingNewState = false;

}
