/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

/**
 * @author kaefert
 * 
 */
public class NewLogForEachRunFileAppender extends FileAppender {

	public NewLogForEachRunFileAppender() {
	}

	public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
		super(layout, filename, append, bufferedIO, bufferSize);
	}

	public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
	}

	public NewLogForEachRunFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
	}

	public void activateOptions() {
		if (fileName != null) {
			try {
				fileName = getNewLogFileName();
				setFile(fileName, fileAppend, bufferedIO, bufferSize);
			} catch (Exception e) {
				errorHandler.error("Error while activating log options", e, ErrorCode.FILE_OPEN_FAILURE);
			}
		}
	}

	private String getNewLogFileName() {
		if (fileName != null) {
			final String DOT = ".";
			final String HIPHEN = "-";
			final String DASH = "_";
			final File logFile = new File(fileName);
			final String fileName = logFile.getName();
			String newFileName = "";

			Date currentDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm-ss"); //#SSS
			String dateString = dateFormat.format(currentDate);

			final int dotIndex = fileName.indexOf(DOT);
			if (dotIndex != -1) {
				// the file name has an extension. so, insert the time stamp
				// between the file name and the extension
				newFileName = fileName.substring(0, dotIndex) + DASH + dateString + DOT + fileName.substring(dotIndex + 1);
			} else {
				// the file name has no extension. So, just append the timestamp
				// at the end.
				newFileName = fileName + HIPHEN + dateString;
			}
			return logFile.getParent() + File.separatorChar + newFileName;
		}
		return null;
	}

//	public String getPathToWebApp() {
//		
//	}
//	
//	public void contextInitialized(ServletContextEvent event) {
//		ServletContext context = event.getServletContext();
//		System.setProperty("rootPath", context.getRealPath("/"));
//	}

}
