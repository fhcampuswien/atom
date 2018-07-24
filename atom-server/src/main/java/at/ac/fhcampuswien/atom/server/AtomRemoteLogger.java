package at.ac.fhcampuswien.atom.server;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;

public class AtomRemoteLogger extends RemoteServiceServlet implements RemoteLoggingService {

	private static final long serialVersionUID = -517252482581344374L;

	private static Logger logger;
	private StackTraceDeobfuscator deobfuscator;
	private ServerSingleton server;

	private File codeserverTarget = new File("target/gwt/codeserver/at.ac.fhcampuswien.atom.App/");
	private File latestCompileDir = null;

	public AtomRemoteLogger() {
		logger = Logger.getLogger("atom");
		refreshDeobfuscator();
		server = ServerSingleton.getInstance();
	}

	/**
	 * Logs a Log Record which has been serialized using GWT RPC on the server.
	 * 
	 * @return either an error message, or null if logging is successful.
	 */
	public final String logOnServer(LogRecord logRecord) {
		if(logRecord.getThrown() != null) {
			refreshDeobfuscator();
			try {
				deobfuscator.deobfuscateStackTrace(logRecord.getThrown(), getPermutationStrongName());
			}
			catch (Throwable t) {
				logger.log(Level.SEVERE, "error while deobfuscating StackTrace", t);
			}
		}
		//logRecord = RemoteLoggingServiceUtil.deobfuscateLogRecord(deobfuscator, logRecord, getPermutationStrongName());
		ClientSession session = null;
		try {
			session = server.getAuth().getSessionFromCookie(getThreadLocalRequest(), false);
		} catch (AuthenticationException e) {
			// exception for not logged in user.
		}
		if (session != null) {
			logRecord.setMessage("Client user " + session.getUsername() + " : " + logRecord.getMessage());
		} else {
			logRecord.setMessage("Client " + getThreadLocalRequest().getRemoteAddr() + " : " + logRecord.getMessage());
		}
		logger.log(logRecord);
		return null;
	}
	
	private void refreshDeobfuscator() {

		boolean changed = false;
		if(codeserverTarget.exists()) for(File file : codeserverTarget.listFiles()) {
			if(latestCompileDir == null) {
				changed = true;
				latestCompileDir = file;
			}	
			else if (file.lastModified() > latestCompileDir.lastModified()) {
				changed = true;
				latestCompileDir = file;
			}
		}
		if(changed) {
			try {
				deobfuscator = StackTraceDeobfuscator.fromFileSystem(latestCompileDir.getCanonicalPath() + "/extras/app/symbolMaps");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
