/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public interface AtomClientBundle extends ClientBundle {

	public static final AtomClientBundle INSTANCE = GWT.create(AtomClientBundle.class);

	@Source("images/pre_alpha.png")
	public ImageResource preAlphaLogo();
	
	@Source("images/green_plus.png")
	public ImageResource greenPlus();
	
	@Source("images/red_minus.png")
	public ImageResource redMinus();
	
	@Source("images/loginArrow.png")
	public ImageResource loginArrow();
	
	@Source("images/closeFrameButton.png")
	public ImageResource closeFrameButton();
	
	@Source("images/closeFrameButtonInvertiert.png")
	public ImageResource closeFrameButtonInvertiert();
	
	@Source("images/centerCornerTopRight.png")
	public ImageResource centerCornerTopRight();
	
	@Source("images/centerCornerTopRight_t5.png")
	public ImageResource centerCornerTopRight_t5();
	
	@Source("images/centerCornerTopRight_r8.png")
	public ImageResource centerCornerTopRight_r8();
	
	@Source("images/centerCornerBottomRight.png")
	public ImageResource centerCornerBottomRight();
	
	@Source("images/centerCornerBottomRight_b9.png")
	public ImageResource centerCornerBottomRight_b9();
	
	@Source("images/centerCornerBottomRight_r8.png")
	public ImageResource centerCornerBottomRight_r8();
	
	@Source("images/centerCornerBottomLeft.png")
	public ImageResource centerCornerBottomLeft();
	
	@Source("images/clipBoardCornerTop.png")
	public ImageResource clipBoardCornerTop();
	
	@Source("images/clipBoardCornerTopMarked.png")
	public ImageResource clipBoardCornerTopMarked();
	
	@Source("images/clipBoardCornerBottom.png")
	public ImageResource clipBoardCornerBottom();
	
	@Source("images/clipBoardCornerBottomMarked.png")
	public ImageResource clipBoardCornerBottomMarked();
	
	@Source("images/menuPopupNormal.png")
	public ImageResource menuPopupNormal();	
	
	@Source("images/menuPopupOver.png")
	public ImageResource menuPopupOver();

	@Source("images/loading_center_header.gif")
	public ImageResource loadingGif();

	@Source("images/black_minus.png")
	public ImageResource blackMinus();

	@Source("images/black_plus.png")
	public ImageResource blackPlus();

	@Source("images/lupe18px.png")
	public ImageResource lupe();

	@Source("images/lupe12px.png")
	public ImageResource lupe12();

	@Source("images/papierkorb18px.png")
	public ImageResource muelltonne();
	
	@Source("images/link_arrow.gif")
	public ImageResource linkArrow();
	
	public interface AtomClientBundleStylePublic extends AtomClientBundleStyle{} 
	@Source("AtomClientBundleStyle.css")
    public AtomClientBundleStylePublic css();
	
	@Source("logo.svg.core")
	public TextResource svgCore();
	
	public class SaveHtml {
		public SafeHtml svgCore() { 
			return SafeHtmlUtils.fromSafeConstant(AtomClientBundle.INSTANCE.svgCore().getText()); 
		}
	}
}
