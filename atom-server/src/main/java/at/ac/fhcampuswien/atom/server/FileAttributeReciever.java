/**
 * This is an example of how to use UploadAction class.
 *  
 * This servlet saves all received files in a temporary folder, 
 * and deletes them when the user sends a remove request.
 * 
 * @author Manolo Carrasco Moñino
 *
 */

package at.ac.fhcampuswien.atom.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.shared.UConsts;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;

import com.allen_sauer.gwt.log.client.Log;

public class FileAttributeReciever extends UploadAction {

	private static final long serialVersionUID = 3434912841530070247L;

	private HashMap<String, Integer> storedFiles = new HashMap<String, Integer>();
	
	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {

		ClientSession clientSession = ServerSingleton.getInstance().getAuth().getSessionFromCookie(request, false);
		String response = "";
		String fileName = null;
		FileItem fileItem = null;
		
		String className = null;
		String attributeName = null;
		Integer instanceID = null;
		
		for (FileItem item : sessionFiles) {
			if (item.isFormField()) {
				AtomTools.log(Log.LOG_LEVEL_INFO, "got FormField " + item.getFieldName() + " = " + item.getString(), this);

				if(item.getFieldName().contains("className"))
					className = item.getString();
				else if(item.getFieldName().contains("attributeName"))
					attributeName = item.getString();
				else if(item.getFieldName().contains("instanceID")) {
					if(!"null".equals(item.getString()))
						instanceID = Integer.valueOf(item.getString());
				}
				else {
					AtomTools.log(Log.LOG_LEVEL_WARN, "FileAttributeReciever: got unknown FormField " + item.getFieldName() + " = " + item.getString(), this);
				}
				
			} else {
				if(fileItem != null)
					AtomTools.log(Log.LOG_LEVEL_ERROR, "FileAttributeReciever: got more than one fileItem!", this);
				
				fileItem = item;
				fileName = item.getName();
			}
		}
		
		Integer faID = ServerSingleton.getInstance().saveFileAttribute(clientSession, fileItem, className, attributeName, instanceID).getId();
		storedFiles.put(fileItem.getFieldName(), faID);
		response = fileName + " [" + faID + "]";


		// Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}

	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(UConsts.PARAM_SHOW);
		Integer fileID = storedFiles.get(fieldName);
		PersistedFileAttribute pfa = ServerSingleton.getInstance().getFileAttribute(request, fileID);
		
		if (pfa != null) {
			response.setContentType(pfa.getContentType());
			try {
				InputStream is = pfa.getContent().getBinaryStream();
				copyFromInputStreamToOutputStream(is, response.getOutputStream());
			} catch (SQLException e) {
				e.printStackTrace();
				renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
			}
		} else {
			renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
		}
	}

	/**
	 * Remove a file when the user sends a delete request.
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
		//maybe delete file from database?
	}
}