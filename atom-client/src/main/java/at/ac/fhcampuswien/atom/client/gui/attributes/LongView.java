/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;

public class LongView extends SimpleView<Long> {

	/**
	 * @see at.ac.fhcampuswien.atom.client.gui.attributes.AttributeView#readValue()
	 */
	@Override
	protected void readValue() {
		try {
			String textValue = textBox.getText();
			if (textValue != null && textValue.length() > 0)
				this.value = Long.decode(textValue);
			else
				this.value = null;
		} catch (NumberFormatException e) {
			throw new ValidationError(e.getMessage());
		}
	}
}
