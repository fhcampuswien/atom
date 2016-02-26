/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

import java.util.logging.Level;

/**
 * @author kaefert
 *
 * This is a generic Importer. It can process any Excel xls Document that can 
 * be read by the library we use to read them: http://poi.apache.org/ 
 *
 * The first row is expected to contain the attribute names.
 * The second row is ignored, the exporter will print the display names of the
 * attributes here. 
 * row is interpreted as data = content --> one row = one instance
 * 
 * WARNING: If an xls file is uploaded, that does not honor this
 * contract, the consequences are unforeseeable.
 * 
 * If two (ore more) columns with the same header (=attribute-name) exist, each
 * row will accept the first non empty value and ignore the content of the
 * following columns with the same header.
 * 
 * If a column with the header "objectID" exists, all rows that have an non-empty
 * value in this column will be matched to an existing DomainObject instance.
 * If no such instance exists, the row will be ignored.
 * If such an instance is found, it will be update, or:
 * 
 * If a column with the header "DELETE" exists, all rows that can be matched to
 * an existing DomainObject instance (see above) which have the boolean value
 * "true" in the "DELETE" column, will be deleted instead of beeing updated.
 * 
 * Any column that does not contain an attribute name in its first row, that can
 * be matched to an attribute of the DomainClass defined by the Http Parameter
 * "class" will be ignored.
 * 
 * If no column with the header "objectID" exist, or a row does contain an empty
 * value in this column, than a new instance of the DomainClass defined 
 * by the Http Parameter "class" will be created and filled with the values of
 * all columns representing valid attributes of this DomainClass.
 * 
 */
public class UploadImportServlet extends HttpServlet {

	private static final long serialVersionUID = 4462590076733757851L;

	private ServerSingleton server;

	public UploadImportServlet() {
		server = ServerSingleton.getInstance();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ClientSession session = server.getAuth().getSessionFromCookie(req, false);
		if (session == null) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "session cookie not present or incorrect value!");
			return;
		}

		String className = req.getParameter("class");
		AtomTools.log(Level.INFO, "UploadImportServlet doPost (changed), class=" + className, this);

		// process only multipart requests
		if (ServletFileUpload.isMultipartContent(req)) {

			AtomTools.log(Level.INFO, "UploadImportServlet doPost isMultipartContent = true", this);

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			AtomTools.log(Level.INFO, "UploadImportServlet doPost created FileItemFactory instance", this);

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			AtomTools.log(Level.INFO, "UploadImportServlet doPost created ServletFileUpload instance", this);

			// List<FileItem> items = new ServletFileUpload(new
			// DiskFieleItemFactory()).parseRequest(req);

			// Parse the request
			try {
				AtomTools.log(Level.INFO, "UploadImportServlet doPost start upload.parseRequest(req)", this);

				List<?> items = upload.parseRequest(req);

				AtomTools.log(Level.INFO, "UploadImportServlet doPost FileItems count = " + items.size(), this);
				for (Object itemobj : items)
				if (itemobj instanceof FileItem) {

					FileItem item = (FileItem) itemobj;
					// process only file upload - discard other form item types

					if (item.isFormField()) {
						// Process regular form field (input
						// type="text|radio|checkbox|etc", select, etc).
						String fieldname = item.getFieldName();
						String fieldvalue = item.getString();
						AtomTools.log(Level.INFO, "UploadImportServlet doPost FormField:" + fieldname + " ; " + fieldvalue, this);
						// ... (do your job here)
					} else {
						// Process form file field (input type="file").
						String fieldname = item.getFieldName();
						String filename = item.getName();
						AtomTools.log(Level.INFO, "UploadImportServlet doPost File: " + fieldname + " ; " + filename, this);
						// String filename =
						// FilenameUtils.getName(item.getName());
						InputStream filecontent = item.getInputStream();
						processXls(filecontent, session, className);
					}
				}

			} catch (Throwable t) {
				AtomTools.log(Level.SEVERE, "UploadImportServlet doPost error happened: " + t.getClass() + " - " + t.getMessage(), this);
				AtomTools.logStackTrace(Level.SEVERE, t, this);
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while creating the file : " + t.getMessage());
				// org.apache.commons.io.output.DeferredFileOutputStream
				if(t instanceof AtomException)
					throw (AtomException) t;
				else
					throw new AtomException(t);
			}

		} else {
			AtomTools.log(Level.INFO, "UploadImportServlet doPost isMultipartContent = false", this);
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Request contents type is not supported by this servlet.");
		}

		AtomTools.log(Level.INFO, "UploadImportServlet doPost end of method", this);
		// super.doPost(req, resp);
	}

	private void processXls(InputStream stream, ClientSession session, String className) {
		try {
			HSSFWorkbook myWorkBook = new HSSFWorkbook(stream);
			FormulaEvaluator evaluator = myWorkBook.getCreationHelper().createFormulaEvaluator();
			evaluator.evaluateAll();

			HSSFSheet sheet = myWorkBook.getSheetAt(0);
			
			ArrayList<ArrayList<HSSFCell>> sheetData = new ArrayList<ArrayList<HSSFCell>>();
			for (Iterator<Row> ir = sheet.rowIterator(); ir.hasNext();) {
				HSSFRow row = (HSSFRow) ir.next();
				ArrayList<HSSFCell> rowData = new ArrayList<HSSFCell>();
								
				int minColIx = row.getFirstCellNum();
				int maxColIx = row.getLastCellNum();
				for (int colIx = minColIx; colIx < maxColIx; colIx++) {
					HSSFCell cell = row.getCell(colIx);
					rowData.add(cell);
//					if (cell == null) {
//						continue;
//					}
					// ... do something with cell
				}
				
//				for (Iterator<Cell> ic = row.cellIterator(); ic.hasNext();) {
//					HSSFCell myCell = (HSSFCell) ic.next();
//					// AtomTools.log(Level.INFO, "" +
//					// myCell.getStringCellValue(), this);
//					// rowData.add(myCell.getStringCellValue());
//					rowData.add(myCell);
//				}
				sheetData.add(rowData);
			}

			processData(sheetData, session, className);

		} catch (Throwable t) {
			AtomTools.log(Level.SEVERE, "UploadImportServlet doPost uploaded file could not be parsed as HSSFWorkbook!! " + t.getMessage(), this);
			AtomTools.logStackTrace(Level.SEVERE, t, this);
			throw new AtomException(t);
		}
	}

	private void processData(ArrayList<ArrayList<HSSFCell>> data, ClientSession session, String className) {
		
		ArrayList<String> headers = null;
		int i = 0;
		for (ArrayList<HSSFCell> row : data) {
			if (i == 0) {
				headers = new ArrayList<String>();
				int emptyHeader = 1;
				for (HSSFCell cell : row) {
					if(cell != null)
						headers.add(cell.toString());
					else
						headers.add("emptyHeader#" + emptyHeader++);
				}
			}
			else if(i == 1) {
				//do nothing, second line are display names!
			}
			else {
				processRow(headers, row, session, className);
			}
			i++;
		}
	}

	private void processRow(ArrayList<String> headers, ArrayList<HSSFCell> data, ClientSession session, String className) {
		HashMap<String, HSSFCell> fields = new HashMap<String, HSSFCell>();
		for (int i = 0; i < data.size(); i++) {
			HSSFCell existing = fields.get(headers.get(i));
			String exVal = null;
			if(existing != null) {
				exVal = existing.toString();
			}
			// if there are more than one column with the same header, keep the value of the first if it is a "good" value, otherwise take the next value.
			// --> similar to behavior of COALESCE
			if(exVal == null || exVal.length() <= 0 || exVal.equals(" ") || exVal.startsWith("#ERR"))
				fields.put(headers.get(i), data.get(i));
		}

		DomainObject instance = null;
		Double objectID = getDoubleValueOfCell(fields.get("objectID"));
		if (objectID != null) {
			instance = server.getDomainObject(session, objectID.intValue(), ServerTools.getClassForName(className));
			if (instance == null)
				AtomTools.log(Level.WARNING, "Importer: cannot update DomainObject with ID " + objectID + ", since it does not exist. ", this);
			else {
				Boolean delete = getBooleanValueOfCell(fields.get("DELETE"));
				if (delete != null && delete.booleanValue() == true) {
					AtomTools.log(Level.INFO, "Importer: Deleting DomainObject " + objectID, this);
					server.deleteDomainObject(session, instance);
				} else {
					AtomTools.log(Level.INFO, "Importer: Updating DomainObject " + objectID, this);
					updateFields(instance, fields, className, session);
					server.saveDomainObject(session, instance);
				}
			}
		} else {
			instance = createInstance(className);
			AtomTools.log(Level.INFO, "Importer: Created new DomainObject with ID " + instance.getObjectID(), this);
			updateFields(instance, fields, className, session);
			server.saveDomainObject(session, instance);
		}
	}

	private void updateFields(DomainObject instance, HashMap<String, HSSFCell> fields, String className, ClientSession session) {
		DomainClass domainClass = DomainAnalyzer.getDomainClass(className);
		for (Map.Entry<String, HSSFCell> entry : fields.entrySet()) {
			String key = entry.getKey();
			DomainClassAttribute attribute = domainClass.getAttributeNamed(key);
			if (attribute != null && attribute.isWriteAble()) {
				setValue(instance, attribute, entry.getValue(), session);
			}
		}
	}

	private void setValue(DomainObject instance, DomainClassAttribute attribute, HSSFCell cell, ClientSession session) {

		if(cell == null)
			return;
		
		try {
			Class<?> reflClass = instance.getClass();
			Method getAttribute = reflClass.getMethod("get" + AtomTools.upperFirstChar(attribute.getName()), new Class[] {});
			Class<?> attributeClass = getAttribute.getReturnType();
			Method setAttribute = reflClass.getMethod("set" + AtomTools.upperFirstChar(attribute.getName()), new Class[] { attributeClass });
			if (attributeClass == String.class) {
				setAttribute.invoke(instance, new Object[] { getStringValueOfCell(cell) });
			} else if (attributeClass == Date.class) {
				setAttribute.invoke(instance, new Object[] { getDateValueOfCell(cell) });
			} else if (attributeClass == Long.class || attributeClass == long.class) {
				Double d = getDoubleValueOfCell(cell);
				Long l = d == null ? null : d.longValue();
				setAttribute.invoke(instance, new Object[] { l });
			} else if (attributeClass == Boolean.class || attributeClass == boolean.class) {
				setAttribute.invoke(instance, new Object[] { getBooleanValueOfCell(cell) });
			} else if (attributeClass == Integer.class || attributeClass == int.class) {
				Double d = getDoubleValueOfCell(cell);
				Integer r = d == null ? null : d.intValue();
				setAttribute.invoke(instance, new Object[] { r });
			} else if (attributeClass.getName().startsWith("at.ac.fhcampuswien.atom.shared.domain.")) {
				setAttribute.invoke(instance, new Object[] { server.getDomainObject(session, getStringValueOfCell(cell), attributeClass) });

			} else if(attribute.getType().contains("Set<at.ac.fhcampuswien.atom.shared.domain.")) {
				HashSet<DomainObject> objects = new HashSet<DomainObject>();
				processCollection(objects, getStringValueOfCell(cell), attribute, session);
				if(objects.size() > 0)
					setAttribute.invoke(instance, new Object[] { objects });
				
			} else if(attribute.getType().contains("List<at.ac.fhcampuswien.atom.shared.domain.")) {
				ArrayList<DomainObject> objects = new ArrayList<DomainObject>();
				processCollection(objects, getStringValueOfCell(cell), attribute, session);
				if(objects.size() > 0)
					setAttribute.invoke(instance, new Object[] { objects });
				
			} else {
				AtomTools.log(Level.SEVERE, "setting of attributes with this Class not implemented: " + attributeClass.toString(), this);
			}

		} catch (NoSuchMethodException e) {
			AtomTools.log(Level.SEVERE, "reflClass.getMethod failed, " + e.getMessage(), this);
		} catch (IllegalAccessException e) {
			AtomTools.log(Level.SEVERE, "setAttribute.invoke failed, " + e.getMessage(), this);
		} catch (IllegalArgumentException e) {
			AtomTools.log(Level.SEVERE, "setAttribute.invoke failed, " + e.getMessage(), this);
		} catch (InvocationTargetException e) {
			AtomTools.log(Level.SEVERE, "setAttribute.invoke failed, " + e.getMessage(), this);
		}
	}
	
	private void processCollection(Collection<DomainObject> collection, String clues, DomainClassAttribute attribute, ClientSession session) {
		if(clues == null || clues.length() <= 0)
			return;
		
		if(clues.startsWith("[") && clues.endsWith("]"))
			clues = clues.substring(1, clues.length()-1);
		
		for(String clue : clues.split(";")) {
			if(clue.length() <= 0)
				continue;
			
			DomainObject found = server.getDomainObject(session, clue, AtomTools.getListedType(attribute.getType()));
			if(found != null)
				collection.add(found);
		}
	}
	
	private DomainObject createInstance(String className) {
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> con = cls.getConstructor(new Class[] {});
			Object instance = con.newInstance(new Object[] {});

			return (DomainObject) instance;

		} catch (ClassNotFoundException e) {
			AtomTools.log(Level.SEVERE, "Class.forName failed! - " + e.getMessage(), this);
		} catch (NoSuchMethodException e) {
			AtomTools.log(Level.SEVERE, "Class.getConstructor failed! - " + e.getMessage(), this);
		} catch (SecurityException e) {
			AtomTools.log(Level.SEVERE, "Class.getConstructor failed! - " + e.getMessage(), this);
		} catch (InstantiationException e) {
			AtomTools.log(Level.SEVERE, "Constructor.newInstance failed! - " + e.getMessage(), this);
		} catch (IllegalAccessException e) {
			AtomTools.log(Level.SEVERE, "Constructor.newInstance failed! - " + e.getMessage(), this);
		} catch (IllegalArgumentException e) {
			AtomTools.log(Level.SEVERE, "Constructor.newInstance failed! - " + e.getMessage(), this);
		} catch (InvocationTargetException e) {
			AtomTools.log(Level.SEVERE, "Constructor.newInstance failed! - " + e.getMessage(), this);
		}
		return null;
	}

	private Double getDoubleValueOfCell(HSSFCell cell) {
		if (cell == null)
			return null;
		return getDoubleValueOfCell(cell, cell.getCellType());
	}

	private Double getDoubleValueOfCell(HSSFCell cell, int cellType) {

		switch (cellType) {

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() ? new Double(1) : new Double(0);

		case Cell.CELL_TYPE_FORMULA:
			return getDoubleValueOfCell(cell, cell.getCachedFormulaResultType());

		case Cell.CELL_TYPE_NUMERIC:
			// TO DO apply the dataformat for this cell
			// if (HSSFDateUtil.isCellDateFormatted(cell)) {
			// DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			// return sdf.format(cell.getDateCellValue());
			// }
			return cell.getNumericCellValue();
			// return value.intValue();

		case Cell.CELL_TYPE_STRING:
			return Double.parseDouble(cell.getStringCellValue());

		default:
			AtomTools.log(Level.SEVERE, "unknown celltype: " + cellType + "; content of cell = " + cell.toString(), this);
			return null;
		}
	}

	private Date getDateValueOfCell(HSSFCell cell) {
		if (cell == null)
			return null;
		return getDateValueOfCell(cell, cell.getCellType());
	}

	private Date getDateValueOfCell(HSSFCell cell, int cellType) {
		switch (cellType) {

		case Cell.CELL_TYPE_FORMULA:
			return getDateValueOfCell(cell, cell.getCachedFormulaResultType());

		case Cell.CELL_TYPE_NUMERIC:

			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			break;

		case Cell.CELL_TYPE_STRING:

			String cellValue = cell.getStringCellValue();
			if (cellValue == null || cellValue.length() <= 0)
				return null;
			else
				try {
					return ServerTools.dateFormat.parse(cell.getStringCellValue());
				} catch (ParseException e) {
					AtomTools.log(Level.SEVERE, "SimpleDateFormat.getInstance().parse(value) failed, " + e.getMessage(), this);
				}
		}
		AtomTools.log(Level.SEVERE, "unknown celltype: " + cellType + "; content of cell = " + cell.toString(), this);
		return null;
	}

	private Boolean getBooleanValueOfCell(HSSFCell cell) {
		if (cell == null)
			return null;
		return getBooleanValueOfCell(cell, cell.getCellType());
	}

	private Boolean getBooleanValueOfCell(HSSFCell cell, int cellType) {

		switch (cellType) {

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();

		case Cell.CELL_TYPE_FORMULA:
			return getBooleanValueOfCell(cell, cell.getCachedFormulaResultType());

		case Cell.CELL_TYPE_NUMERIC:

			return cell.getNumericCellValue() <= 0 ? false : true;

		case Cell.CELL_TYPE_STRING:

			String v = cell.getStringCellValue();
			
			if(v == null || v.length() <= 0)
				return null;
			
			v = v.toLowerCase(Locale.GERMAN);
			if(v.contains("k.a.") || v.contains("n.a."))
				return null;
			
			return (v.toLowerCase().contains("yes") || v.toLowerCase().contains("true") || v.toLowerCase().contains("ja") || v.contains("wahr") || v.equals("1"));
		}
		AtomTools.log(Level.SEVERE, "unknown celltype: " + cellType + "; content of cell = " + cell.toString(), this);
		return null;
	}


	private String getStringValueOfCell(HSSFCell cell) {
		if (cell == null)
			return null;
		return getStringValueOfCell(cell, cell.getCellType());
	}

	private String getStringValueOfCell(HSSFCell cell, int cellType) {
		
		switch (cellType) {

		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() ? "Ja" : "Nein";

		case Cell.CELL_TYPE_FORMULA:
			return getStringValueOfCell(cell, cell.getCachedFormulaResultType());

		case Cell.CELL_TYPE_NUMERIC:
			double number = cell.getNumericCellValue();
			if(Double.isInfinite(number) || Double.isNaN(number) || Math.floor(number) != number)
				return Double.toString(number);
			else
				return String.valueOf(new Double(number).intValue());

		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
			
		}
		AtomTools.log(Level.SEVERE, "unknown celltype: " + cellType + "; content of cell = " + cell.toString(), this);
		return null;
		
	}
}