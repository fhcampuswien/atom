/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.allen_sauer.gwt.log.client.Log;

import at.ac.fhcampuswien.atom.shared.AtomTools;

public class DownloadFileAttributePreviewServlet extends HttpServlet {

	private static final long serialVersionUID = -7002423293970066768L;
	
	private static final int IMG_WIDTH = 50;
	private static final int IMG_HEIGHT = 50;

	private ServerSingleton server;

	public DownloadFileAttributePreviewServlet() {
		server = ServerSingleton.getInstance();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
				String fileName = pfa.getFileName() + ".preview.jpg";
				
				if(tryDirectResize(is, fileName, resp))
					return;
				
				File tmp = File.createTempFile("atom-tmp_", pfa.getFileName());
				IOUtils.copy(pfa.getContent().getBinaryStream(), new FileOutputStream(tmp));				
				
				File output = File.createTempFile("atom-tmp_", fileName);
				String inputFile = tmp.getAbsolutePath();
				String outputFile = output.getAbsolutePath();
				String[] params = null;
				
				params = new String[] {"convert", inputFile, "-thumbnail", IMG_WIDTH+"x"+IMG_HEIGHT, outputFile};
				if(tryExternal(fileName, resp, params, output))
					return;

				params = new String[] {"totem-video-thumbnailer", "-j", "-s", IMG_WIDTH+"x"+IMG_HEIGHT, inputFile, outputFile};
				if(tryExternal(fileName, resp, params, output))
					return;
				
				
			} catch (SQLException e) {
				e.printStackTrace();
				resp.getWriter().write(AtomTools.getCustomStackTrace(e));
			}
		}
		else {
			resp.getWriter().write("file not found or no permission");
		}	
	}


	private boolean tryDirectResize(InputStream is, String fileName, HttpServletResponse resp) {
    	
		try {
			BufferedImage originalImage = ImageIO.read(is);
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			
			BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
			g.dispose();	
			g.setComposite(AlphaComposite.Src);
		 
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		 

			resp.setContentType("image/jpeg");
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			ImageIO.write(resizedImage, "jpg", resp.getOutputStream());
			
			//return resizedImage;
			return true;
		}
		catch(Throwable t) {
			AtomTools.log(Log.LOG_LEVEL_DEBUG, "I guess this file was no image of a format that java ImageIO knows of, Exception: " + t.getMessage(), this);
		}
    	return false;
    }
	
	private boolean tryExternal(String fileName, HttpServletResponse resp, String[] params, File output) throws IOException {
		
		final ProcessBuilder pb = new ProcessBuilder(params);
		
		try {
			final Process p = pb.start();
			// wait until it's created
			p.waitFor();

			resp.setContentType("image/jpeg");
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			FileInputStream is = new FileInputStream(output);
			IOUtils.copy(is, resp.getOutputStream());
			return true;

		} catch (Throwable t) {
			AtomTools.log(Log.LOG_LEVEL_DEBUG, "ImageMagick failed creating thumbnail, Exception: " + t.getMessage(), this);
		}
		
		return false;
	}
}