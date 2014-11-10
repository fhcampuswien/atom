/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import at.ac.fhcampuswien.atom.shared.AtomTools;

public class DownloadFileAttributeServlet extends HttpServlet {

	private static final long serialVersionUID = -7002423293970066768L;

	private ServerSingleton server;

	public DownloadFileAttributeServlet() {
		server = ServerSingleton.getInstance();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// ClientSession session = ss.getSessionFromCookie(req, false);
		String idS = req.getParameter("id");
		Integer id = Integer.decode(idS);
		
		PersistedFileAttribute pfa = server.getFileAttribute(req, id);
		
		if(pfa != null) {
			try {
				InputStream is = pfa.getContent().getBinaryStream();
				resp.setContentType(pfa.getContentType());
				resp.setHeader("Content-Disposition", "attachment; filename=\"" + pfa.getFileName() + "\"");
				IOUtils.copy(is, resp.getOutputStream());
			} catch (SQLException e) {
				e.printStackTrace();
				resp.getWriter().write(AtomTools.getCustomStackTrace(e));
			}
		}
		else {
			resp.getWriter().write("file not found or no permission");
		}
		
	}
}
