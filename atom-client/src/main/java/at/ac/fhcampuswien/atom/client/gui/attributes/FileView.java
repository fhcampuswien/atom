/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FileUpload;

// use this:
// https://code.google.com/p/gwtupload/wiki/GwtUpload_GettingStarted
// FIXME: continue here

public class FileView extends AttributeView<String, FileView, String> {

	FileUpload fileUpload;
	
	public FileView() {
		fileUpload = new FileUpload();
		fileUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "file upload changed; eventDebugString='" + event.toDebugString() + "', fileUpload.filename='" + fileUpload.getFilename() + "'", this);
			}
		});
		initWidget(fileUpload);
	}
	
	@Override
	protected boolean createFieldWidget() {
		this.field = this;
		return false;
	}

	@Override
	protected void showValue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readValue() {
		// TODO Auto-generated method stub
		
	}

	

}
