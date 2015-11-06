/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.client.gui.attributes.components.RichTextArea;
import at.ac.fhcampuswien.atom.client.gui.attributes.components.RichTextToolbar;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import java.util.logging.Level;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class StringFormattedLobView extends AttributeView<String, RichTextArea, String> {

	private static StringFormattedLobViewUiBinder uiBinder = GWT.create(StringFormattedLobViewUiBinder.class);

	interface StringFormattedLobViewUiBinder extends UiBinder<Widget, StringFormattedLobView> {
	}

	interface PanelStyle extends CssResource {
		String space();

		String textBox();

		String sensitive();

		String insensitive();
	}

	@UiField
	protected PanelStyle panelStyle;

	@UiField
	protected RichTextArea textArea;

	@UiField
	protected RichTextToolbar toolbar;
	
	@UiField
	protected DockLayoutPanel areaPanel;


	private String prefix = "<span style=\"font-family: Verdana, Arial, Helvetica, sans-serif;\">";
	private String suffix = "</span>";
	
	/**
	 * needs to be overridden for non-string values that might be represented by
	 * StringView because there is no special for its type.
	 */
	@Override
	public void setValue(Object value) {
		if (value != null)
			this.value = value.toString();
		else
			this.value = "";
		showValue();
	}

	@Override
	protected boolean createFieldWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		field = textArea;
		toolbar.initToolBar(textArea);
		
		textArea.addAttachHandler(new Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				AtomTools.log(Level.FINE, "textArea.onAttachOrDetach - " + event.toDebugString(), this);
				fixIFrameDefaultFont(true);
			}
		});
		
		fixIFrameDefaultFont(true);
		
		return false;
	}
	
	private Timer fixIFrameDefaultFontTimer = null;
	
	private void fixIFrameDefaultFont(boolean doLaterIfNotPossibleNow) {
		JavaScriptObject contentDocument = textArea.getElement().getPropertyJSO("contentDocument");
		if(contentDocument != null) {
			if(readOnly)
				fixIFrameDefaultFontReadOnly(contentDocument);
			else
				fixIFrameDefaultFontReadWrite(contentDocument);
		}
		else if(doLaterIfNotPossibleNow) {
			if(fixIFrameDefaultFontTimer == null) {
				fixIFrameDefaultFontTimer = new Timer() {
					
					@Override
					public void run() {
						fixIFrameDefaultFont(false);
					}
				};
			}
			fixIFrameDefaultFontTimer.schedule(50);
		}
	}

	private native void fixIFrameDefaultFontReadOnly(JavaScriptObject frame) /*-{
		frame.body.parentNode.style.fontFamily = "Verdana, Arial, Helvetica, sans-serif";
		frame.body.parentNode.style.fontSize = "11px";
		frame.body.parentNode.style.color = "#848284";
		frame.body.parentNode.style.textShadow = "0.7px 1.4px 0px white";
}-*/; 

	private native void fixIFrameDefaultFontReadWrite(JavaScriptObject frame) /*-{
		frame.body.parentNode.style.fontFamily = "Verdana, Arial, Helvetica, sans-serif";
		frame.body.parentNode.style.fontSize = "11px";
		frame.body.parentNode.style.color = "#575A5F";
		frame.body.parentNode.style.textShadow = "none";
}-*/; 

	@Override
	protected void showValue() {
//		textArea.setHTML(prefix + value + suffix);
		textArea.setHTMLandRemember(value);
		
		fixIFrameDefaultFont(true);
	}

	private boolean fistSetReadOnly = true;
	
	@Override
	public void setReadOnly(boolean readOnly) { 
		super.setReadOnly(readOnly);
		textArea.setEnabled(!readOnly);
		fixIFrameDefaultFont(true);
	}

	@Override
	protected void readValue() {
		
		//		field.getHTML()
		String content = textArea.getHTML();
		if(content.startsWith(prefix) && content.endsWith(suffix))
			this.value = content.substring(prefix.length(), content.length()-suffix.length());
		else
			this.value = content;
	}
}
