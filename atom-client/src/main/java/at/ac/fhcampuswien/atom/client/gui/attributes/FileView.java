/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.attributes;

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnChangeUploaderHandler;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStatusChangedHandler;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import java.util.EnumSet;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.FileAttributeRepresentation;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

// use this:
// https://code.google.com/p/gwtupload/wiki/GwtUpload_GettingStarted
// https://code.google.com/p/gwtupload/wiki/Servlets
// FIXME: continue here FileAttribute

public class FileView extends AttributeView<String, FileView, String> {

	HorizontalPanel panel = null;
	MultiUploader fileUpload = null;
	Anchor downLink = null;
	Image preview = null;
	String fileLink = null;
	
	ClickHandler clickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(fileLink != null) {
				Window.open(fileLink.replace("app/", ""),"_blank","");
			}
		}
	};

	public FileView(String className, String attributeName, Integer instanceID) {
		fileUpload = new MultiUploader(FileInputType.BROWSER_INPUT);
		fileUpload.setMultipleSelection(false);
		fileUpload.setMaximumFiles(1);
		fileUpload.add(new Hidden("className", className), 0);
		fileUpload.add(new Hidden("attributeName", attributeName), 0);
		fileUpload.add(new Hidden("instanceID", instanceID == null ? "null"
				: instanceID.toString()), 0);
		
		fileUpload.addOnFinishUploadHandler(new OnFinishUploaderHandler() {

			@Override
			public void onFinish(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG,
						"SingleUploaderModal.onFinish, getInputName = '"
								+ uploader.getInputName() + "'", this);

				if (uploader.getStatus() == Status.SUCCESS) {
					UploadedInfo info = uploader.getServerInfo();
					AtomTools.log(Log.LOG_LEVEL_INFO, "File name " + info.name, this);
					AtomTools.log(Log.LOG_LEVEL_INFO, "File content-type " + info.ctype, this);
					AtomTools.log(Log.LOG_LEVEL_INFO, "File size " + info.size, this);

					// Here is the string returned in your servlet
					AtomTools.log(Log.LOG_LEVEL_INFO, "Server message = " + uploader.getServerMessage().getMessage(), this);
					FileView.this.value = uploader.getServerMessage().getMessage();
					showValue(); 
				}
			}
		});
		fileUpload.addOnChangeUploadHandler(new OnChangeUploaderHandler() {

			@Override
			public void onChange(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG,
						"SingleUploaderModal.onChange " + "'", this);
			}
		});
		fileUpload.addOnStatusChangedHandler(new OnStatusChangedHandler() {

			@Override
			public void onStatusChanged(IUploader uploader) {
				AtomTools.log(Log.LOG_LEVEL_DEBUG,
						"SingleUploaderModal.onStatusChanged " + "'", this);
				//uploader.getStatus()
				if(Status.DELETED.equals(uploader.getStatus()) && downLink != null && !FileView.this.readOnly) {
					downLink.setHTML("");
					fileLink = null;
				}
			}
		});

		panel = new HorizontalPanel();
		panel.add(fileUpload);
		
		initWidget(panel);
	}

	@Override
	protected boolean createFieldWidget() {
		this.field = this;
		return false;
	}

	@Override
	protected void showValue() {
		//FIXME: This doesn't work as hoped. The Link doesn't show.
		//link to the servlet "getfile" to retrieve the file. for servlet config see web.xml in AtomServer project
		AtomTools.log(Log.LOG_LEVEL_INFO, "FileView.showValue() - value = " + FileView.this.value, this);
		//fileUpload.getFileInput()
		FileAttributeRepresentation far = new FileAttributeRepresentation(value);
		//String linkTargetHTML = "<a rel=\"external\" download=\"" + far.getFileName() + "\" target=\"_blank\" href=\"app/getfile?id=" + far.getFileIDString() + "\">" + far.getFileName() + "</a>";
		fileLink = "app/getfile?id=" + far.getFileIDString();
		String linkTargetHTML = "<a target=\"_blank\" href=\"" + fileLink + "\">" + far.getFileName() + "</a>";
		String previewLink = "app/getfilepreview?id=" + far.getFileIDString();
		if(downLink == null) {
			downLink = new Anchor();
			downLink.getElement().getStyle().setMarginLeft(2, Unit.EM);
			panel.add(downLink);
			//fileUpload.clear();
			//fileUpload.add(downLink);
			
			preview = new Image(previewLink);
			preview.addClickHandler(clickHandler);
			preview.getElement().getStyle().setMarginLeft(2, Unit.EM);
			panel.add(preview);
		}
		downLink.setHTML(linkTargetHTML);
		preview.setUrl(previewLink);
	}

	@Override
	protected void readValue() {
		// don't need to do anything here, the value is directly set after an successful upload by our OnFinishUploadHandler
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		
		super.setReadOnly(readOnly);
		
		if(fileUpload != null) {
			fileUpload.setEnabled(!readOnly);
			if(readOnly) {
				fileUpload.getStatusWidget().setCancelConfiguration(EnumSet.of(IUploadStatus.CancelBehavior.DISABLED));
			} else {
				fileUpload.getStatusWidget().setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
			}
		}
	}
}
