/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface SpriteImagesAttributeGroupTabHeader extends ClientBundle {

	@Source("images/tabViewCornerActive.png")
	public ImageResource tabViewCornerActive();
	
	@Source("images/tabViewCornerInactive.png")
	public ImageResource tabViewCornerInactive();
	
	public interface SpriteImagesAttributeGroupTabHeaderStyle extends CssResource {
		String tabViewCornerActive();
		String tabViewCornerInactive();
	}
	
	@Source("SpriteImagesAttributeGroupTabHeader.css")
    public SpriteImagesAttributeGroupTabHeaderStyle css();
	
}
