/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes.components;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import java.util.logging.Level;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

public class RichTextArea extends com.google.gwt.user.client.ui.RichTextArea {

	private boolean enabled = true;
	
	private static final Set<String> harmlessEvents = new HashSet<String>(Arrays.asList(
		     new String[] {"click", "mousedown", "mouseup", "mouseover", "mousemove", "mouseout", "focus", "blur"}
		     //, "mouseup", "mousedown", "mousemove"
		     //TODO: discuss with gregor if the possibility to select and copy text from an readOnly view is more important, then not beeing able to modify text in the readOnly view via drag&drop
		     //if the later is more important, then remove "mousedown", "mouseup" from this list of "harmlessEvents"
		));
	
	private static final Set<String> keyboardEvents = new HashSet<String>(Arrays.asList(
		     new String[] {"keyup", "keydown"}
		));
	
	private boolean strgCurrentlyDown = false;
	
	@Override
	public void onBrowserEvent(final Event event) {
		if (enabled) {
			super.onBrowserEvent(event);
		}
		else {
			String eventType = event.getType();
			
//			if(!"mousemove".equals(eventType))
//				AtomTools.log(Level.FINER, "RichTextArea - toString=[" + event.toString() + "], type=[" + eventType + "]", this);
//			
//			if(eventType.contains("key"))
//				AtomTools.log(Level.FINER, "RichTextArea - key event key=[" + event.getKeyCode() + "]", this);
			
			if(keyboardEvents.contains(eventType)) {
				//  	arrow key navigation; 											ctrl + c --> copying content, allow this!
				if((event.getKeyCode() >= 37 && event.getKeyCode() <= 40) || (strgCurrentlyDown && (event.getKeyCode() == 67 || event.getKeyCode() == 65))) {
					return;
				}
				else if(event.getKeyCode() == 17) { //STRG = CTRL key
					strgCurrentlyDown = "keydown".equals(eventType);						
					return;
				}
				else
					AtomTools.log(Level.INFO, "keycode = " + event.getKeyCode(), this);
			}
			
			if(!harmlessEvents.contains(eventType))
				event.preventDefault();
			else if(!"mousemove".equals(eventType)) {
				AtomTools.log(Level.FINER, "scheduling restoreValue, caused by eventtype: " + eventType, this);
				restoreValueTimer.schedule(30);
			}
		}
	}
	
	private Timer restoreValueTimer = new Timer() {
		@Override
		public void run() {
			
			String currentValue = RichTextArea.this.getHTML();
			if(valueWhenDisabled != null && (!valueWhenDisabled.equals(currentValue))) {
				RichTextArea.this.setHTML(valueWhenDisabled);
				AtomTools.log(Level.FINER, "restoring RichtTextArea value to: " + valueWhenDisabled, this);
			}
			else {
				AtomTools.log(Level.FINER, "RichtTextArea value did not change, do nothing " + currentValue, this);
			}
				
			
			//.getElement().setInnerHTML(currentValue);
			//setPropertyString("value", currentValue);
		}
	};
	
	private String valueWhenDisabled = null;
	private boolean firstTime = true;
	
	@Override
	public void setEnabled(boolean enabled) {
		if(this.enabled == true && enabled == false) {
			//changing from enabled to disabled
			valueWhenDisabled = RichTextArea.this.getHTML();
			
			if(firstTime && valueWhenDisabled != null && valueWhenDisabled.length() <= 0) {
				valueWhenDisabled = null;
				firstTime = false;
			}
		}
		this.enabled = enabled;
		super.setEnabled(enabled);
	}

	public void setHTMLandRemember(String value) {
		valueWhenDisabled = value;
		this.setHTML(value);
	}
}
