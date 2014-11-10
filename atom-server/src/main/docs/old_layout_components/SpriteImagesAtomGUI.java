package at.ac.fhcampuswien.atom.client.gui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface SpriteImagesAtomGUI extends ClientBundle {

	
	@Source("images/center_header_lower_edge.png")
	public ImageResource centerHeaderLowerEdge();
	
	@Source("images/search_arrow.png")
	public ImageResource searchArrow();
	
	public interface SpriteImagesAtomGUIStyle extends CssResource {
		String centerHeaderLowerEdge();
		String searchArrow();
	}
	
	@Source("SpriteImagesAtomGUI.css")
    public SpriteImagesAtomGUIStyle css();
	
}
