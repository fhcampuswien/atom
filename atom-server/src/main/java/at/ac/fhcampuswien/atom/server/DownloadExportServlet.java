/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DataFilter;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.DomainObjectList;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

import com.allen_sauer.gwt.log.client.Log;

public class DownloadExportServlet extends HttpServlet {

	private static final long serialVersionUID = -7002423293970066768L;

	private ServerSingleton server;

	public DownloadExportServlet() {
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
		String nameOfClass = req.getParameter("class");
		String filterString = req.getParameter("filter");
		ArrayList<DataFilter> filters = AtomTools.parseFilterString(filterString);
		
		DomainClass requestedClass = DomainAnalyzer.getDomainClass(nameOfClass);
		// Set<String> accessTypes = requestedClass.getAccessTypes(session);

		DomainObjectList list = server.getListOfDomainObjects(req, nameOfClass,
				0, Integer.MAX_VALUE, filters, null, null, false, false);

		// getListOfDomainObject(session, exportClass, 0, Integer.MAX_VALUE,
		// null, null, null, false);
		String[][] data = transformData(list);

		resp.addHeader("Content-Disposition", "attachment; filename="
				+ requestedClass.getPluralName() + ".xls");
		createXls(data).write(resp.getOutputStream());

		// Response.AddHeader "Content-Disposition", "attachment; filename=""" &
		// alternativeFilename & """; size=" & Filesize

		// OutputStream out = resp.getOutputStream();
		// FileInputStream in = new FileInputStream("asdf");
		//
		// byte[] buffer = new byte[4096];
		// int length;
		// while ((length = in.read(buffer)) > 0) {
		// out.write(buffer, 0, length);
		// }
		// in.close();
		// out.flush();

		// super.doGet(req, resp);
	}

	private String[][] transformData(DomainObjectList list) {
		DomainClass domainClass = list.getDomainClass();
		ArrayList<DomainClassAttribute> attributes = domainClass.getSortedAttributesListView(false);
		int columns = attributes.size();
		int lines = list.getTotalSize();
		String[][] rv = new String[lines + 2][columns];
		String[] attrNames = new String[columns];

		int j = 0;
		for (DomainClassAttribute attr : attributes) {
			attrNames[j] = attr.getName();
			rv[0][j] = attr.getName();
			// we need to names of the variables to find the attributes again
			// when importing the xls, therefore use a second header for users:
			rv[1][j] = attr.getDisplayName();
			j++;
		}
		j = 0;
		int i = 2;
		for (DomainObject obj : list.getDomainObjects()) {
			Class<?> reflClass = obj.getClass();
			for (DomainClassAttribute attribute : attributes) {
				try {
					Method getAttribute = reflClass.getMethod(
							"get"
									+ AtomTools.upperFirstChar(attribute
											.getName()), new Class[] {});
					Object val = getAttribute.invoke(obj, new Object[] {});
					String s = val != null ? val.toString(): "";
					
					//workaround excel bug: "damaged file"
					s = s.replaceAll("(?s)<!--.*?-->", "");  
					
					rv[i][j] = s;
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				j++;
			}
			i++;
			j = 0;
		}

		return rv;
	}

	private HSSFWorkbook createXls(String[][] data) {

		HSSFWorkbook myWorkBook = new HSSFWorkbook();
		HSSFSheet mySheet = myWorkBook.createSheet();
		HSSFRow myRow = null;
		HSSFCell myCell = null;

		for (int rowNum = 0; rowNum < data.length; rowNum++) {
			myRow = mySheet.createRow(rowNum);

			for (int cellNum = 0; cellNum < data[0].length; cellNum++) {
				myCell = myRow.createCell(cellNum);
				try {
					String s = data[rowNum][cellNum];
//					if(s != null && s.contains("<b>") || s.contains("<i>")) {
//
//						while(s.contains("<b>") || s.contains("<i>")) {
//							
//						}
//						HSSFRichTextString rts = new HSSFRichTextString();
//						myCell.setCellValue(rts);
//					}
//					else {
						myCell.setCellValue(s);
//					}
				}
				catch (java.lang.IllegalArgumentException e) {
					e.printStackTrace();
					AtomTools.log(Log.LOG_LEVEL_ERROR, "this is bad (data?)! data = " + data[rowNum][cellNum], this);
				}
				catch(Throwable t) {
					t.printStackTrace();
					AtomTools.log(Log.LOG_LEVEL_ERROR, "this is bad! - data = " + data[rowNum][cellNum], this);
				}

			}
		}

		// try {
		// myWorkBook.write(out);
		// out.flush();
		// // out.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return myWorkBook;
	}

	// /** Prepare some demo data as excel file content **/
	// public static String[][] preapreDataToWriteToExcel() {
	// String[][] excelData = new String[4][4];
	// excelData[0][0] = "First Name";
	// excelData[0][1] = "Last Name";
	// excelData[0][2] = "Telephone";
	// excelData[0][3] = "Address";
	//
	// excelData[1][0] = "Kushal";
	// excelData[1][1] = "Paudyal";
	// excelData[1][2] = "000-000-0000";
	// excelData[1][3] = "IL,USA";
	//
	// excelData[2][0] = "Randy";
	// excelData[2][1] = "Ram Robinson";
	// excelData[2][2] = "111-111-1111";
	// excelData[2][3] = "TX, USA";
	//
	// excelData[3][0] = "Phil";
	// excelData[3][1] = "Collins";
	// excelData[3][2] = "222-222-2222";
	// excelData[3][3] = "NY, USA";
	//
	// return excelData;
	//
	// }
}
