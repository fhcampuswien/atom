/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.client.gui.frames;

import at.ac.fhcampuswien.atom.client.gui.CenterHeader;
import at.ac.fhcampuswien.atom.shared.AtomConfig.FrameType;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;

import java.util.logging.Level;
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
	formPanel.setAction("import?class=" + domainClass.getName());
	fileUpload.setName("upload");
	
	formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	    
	    @Override
	    public void onSubmitComplete(SubmitCompleteEvent event) {
	    	String result = event.getResults();
		    if(result == null || result.length() < 1)
		    	setError("import complete!");
		    else
		    	setError(result.replace(
		    			 "<h1>HTTP Status 500 - </h1><hr size=\"1\" noshade=\"noshade\"><p><b>type</b> Exception report</p><p><b>message</b> <u></u></p><p><b>description</b> <u>The server encountered an internal error that prevented it from fulfilling this request.</u></p><p><b>exception</b> </p><pre>at.ac.fhcampuswien.atom.shared.exceptions.AtomException: \n"
		    			,"<h1>Fehler beim Import</h1>\n</p><pre>").replace(
		    			 "\n\tat.ac.fhcampuswien.atom.server.UploadImportServlet.processData(UploadImportServlet.java:288)\n\tat.ac.fhcampuswien.atom.server.UploadImportServlet.processXls(UploadImportServlet.java:224)\n\tat.ac.fhcampuswien.atom.server.UploadImportServlet.doPost(UploadImportServlet.java:164)\n\tjavax.servlet.http.HttpServlet.service(HttpServlet.java:647)\n\tjavax.servlet.http.HttpServlet.service(HttpServlet.java:728)\n\torg.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)"
		    			, ""));
	    	//
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
    void onClick_uploadButton(ClickEvent event) {
	formPanel.submit();
	AtomTools.log(Level.INFO, "pressed upload import file button - " + fileUpload.getFilename(), this);
    }

    // public String getUploadFilePath() {
    // return fileUpload.
    // }

    public void setError(String userString) {
	errorLabel.setHTML(userString);
    }
}
