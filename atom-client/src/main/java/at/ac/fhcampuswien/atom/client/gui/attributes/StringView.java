/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;

public class StringView extends SimpleView<String> {

	@Override
	protected void readValue() {
		AtomTools.log(Log.LOG_LEVEL_DEBUG, "StringView readValue() ; textBox=" + textBox, this);
		this.value = textBox.getText();
	}

}
