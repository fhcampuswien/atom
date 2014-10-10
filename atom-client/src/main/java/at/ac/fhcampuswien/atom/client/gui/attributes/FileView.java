/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnChangeUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.MultiUploader;
import at.ac.fhcampuswien.atom.shared.AtomTools;

import com.allen_sauer.gwt.log.client.Log;

// use this:
// https://code.google.com/p/gwtupload/wiki/GwtUpload_GettingStarted
// FIXME: continue here

public class FileView extends AttributeView<String, FileView, String> {

	MultiUploader fileUpload;
	
	public FileView() {
		fileUpload = new MultiUploader();
		fileUpload.setMaximumFiles(1);
		fileUpload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
			
			@Override
			public void onFinish(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "SingleUploaderModal.onFinish, getInputName = '" + uploader.getInputName() + "'", this);
			}
		});
		fileUpload.addOnChangeUploadHandler(new OnChangeUploaderHandler() {
			
			@Override
			public void onChange(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "SingleUploaderModal.onChange " + "'", this);
			}
		});
		fileUpload.addOnStatusChangedHandler(new OnStatusChangedHandler() {
			
			@Override
			public void onStatusChanged(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG, "SingleUploaderModal.onStatusChanged " + "'", this);
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
