/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ImportFrame extends Frame {

    private static ImportFrameUiBinder uiBinder = GWT.create(ImportFrameUiBinder.class);

    interface ImportFrameUiBinder extends UiBinder<Widget, ImportFrame> {
    }

    private static String getTitlte(DomainClass domainClass) {
	return "Import " + domainClass.getPluralName();
    }

    public ImportFrame(DomainClass domainClass) {
	super(getTitlte(domainClass), getTitlte(domainClass), CenterHeader.State.EMPTY, me, null, domainClass, FrameType.IMPORT);
	initWidget(uiBinder.createAndBindUi(this));
	formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	formPanel.setMethod(FormPanel.METHOD_POST);
	// (GWT.isProdMode() ? "ATOM/" : "") +
	// (GWT.isProdMode() ? "app/" : "") + 
	formPanel.setAction("app/import?class=" + domainClass.getName());
	fileUpload.setName("upload");
	
	formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	    
	    @Override
	    public void onSubmitComplete(SubmitCompleteEvent event) {
		setError("import complete!");
		uploadButton.setEnabled(true);
	    }
	});
	
	formPanel.addSubmitHandler(new SubmitHandler() {
	    
	    @Override
	    public void onSubmit(SubmitEvent event) {
		setError("please wait, xls is beeing processed..");
		uploadButton.setEnabled(false);
	    }
	});
    }

    @UiField
    protected Button uploadButton;
    
    @UiField
    protected FileUpload fileUpload;
    
    @UiField
    protected HTML errorLabel;

    @UiField
    protected FormPanel formPanel;

    @UiHandler("uploadButton")
    void onClick_loginButton(ClickEvent event) {
	formPanel.submit();
	AtomTools.log(Log.LOG_LEVEL_INFO, "pressed upload import file button - " + fileUpload.getFilename(), this);
    }

    // public String getUploadFilePath() {
    // return fileUpload.
    // }

    public void setError(String userString) {
	errorLabel.setHTML(userString);
    }
}
