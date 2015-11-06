/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import java.util.logging.Level;

public class StringView extends SimpleView<String> {

	@Override
	protected void readValue() {
		AtomTools.log(Level.FINE, "StringView readValue() ; textBox=" + textBox, this);
		this.value = textBox.getText();
	}

}
