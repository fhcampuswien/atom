/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface SpriteImagesForFrames extends ClientBundle {
	
	@Source("images/centerBorderBottom.png")
	public ImageResource centerBorderBottom();
	
	@Source("images/centerBorderRight.png")
	public ImageResource centerBorderRight();

	public interface SpriteImagesForFramesStyle extends CssResource {
		String centerBorderBottom();
		String centerBorderRight();
	}
	
	@Source("SpriteImagesForFrames.css")
    public SpriteImagesForFramesStyle css();
	
}
