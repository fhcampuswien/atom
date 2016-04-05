package at.ac.fhcampuswien.atom.server;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;

public class AtomRemoteLogger extends RemoteServiceServlet implements RemoteLoggingService {

	private static final long serialVersionUID = -517252482581344374L;

	private static Logger logger = Logger.getLogger("atom");
	private StackTraceDeobfuscator deobfuscator = StackTraceDeobfuscator.fromFileSystem("WEB-INF/app/symbolMaps/");
	private ServerSingleton server = ServerSingleton.getInstance();
	
	/**
	 * Logs a Log Record which has been serialized using GWT RPC on the server.
	 * 
	 * @return either an error message, or null if logging is successful.
	 */
	public final String logOnServer(LogRecord logRecord) {
		logRecord = RemoteLoggingServiceUtil.deobfuscateLogRecord(deobfuscator, logRecord, getPermutationStrongName());
		ClientSession session = null;
		try {
			session = server.getAuth().getSessionFromCookie(getThreadLocalRequest(), false);
		} catch(AuthenticationException e) {
			//exception for not logged in user.
		}
		if(session != null) {
			logRecord.setMessage("Client user " + session.getUsername() + " : " + logRecord.getMessage());
		}
		else {
			logRecord.setMessage("Client " + getThreadLocalRequest().getRemoteAddr() + " : " + logRecord.getMessage());
		}
		logger.log(logRecord);
		return null;
	}
}
