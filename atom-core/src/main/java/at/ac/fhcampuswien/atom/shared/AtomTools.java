/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

import at.ac.fhcampuswien.atom.shared.annotations.AttributeValidators;
import at.ac.fhcampuswien.atom.shared.dateformat.SimpleDateFormatGwt;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AuthenticationException;
import at.ac.fhcampuswien.atom.shared.exceptions.ValidationError;
import net.lightoze.gwt.i18n.client.LocaleFactory;

public class AtomTools {

	static Logger logger = Logger.getLogger("atom");
	static UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES);
	static EmailValidator emailValidator = EmailValidator.getInstance(true, true);
	
	private AtomTools() {
	}

	public static int getIntInRange(int min, int value, int max) {
		if (min >= max)
			throw new IllegalArgumentException("The minimum must not be bigger than the maximum!");
		return Math.min(Math.max(value, min), max);
	}

	public static String fillStringLeading(String value, char fillChar, int aimedLength) {
		while (value.length() < aimedLength)
			value = fillChar + value;
		return value;
	}

	public static String lowerFirstChar(String string) {
		return string.replaceFirst(string.substring(0, 1), string.substring(0, 1).toLowerCase());
	}

	public static String upperFirstChar(String string) {
		return string.replaceFirst(string.substring(0, 1), string.substring(0, 1).toUpperCase());
	}
	
	public static String ensureEndsWithSlash(String source) {
		if(source.endsWith("/"))
			return source;
		else
			return source + "/";
	}

	public static double roundToDecimals(double d, int c) {
		return new BigDecimal(d).round(new MathContext(c)).doubleValue();

		// double ext = Math.pow(10, c);
		// double roundedExt = Math.round(d*ext);
		// double rounded = roundedExt / ext;
		//
		// AtomTools.log(Level.FINER, "rounding " + d + " to " +
		// rounded, null);
		// return rounded;
	}
	
	public static String removeHtmlTags(String html) {
        String regex = "(<([^>]+)>)";
        html = html.replaceAll(regex, "");
        return html;
	}

	/**
	 * Defines a custom format for the stack trace as String.
	 * @param aThrowable the throwable for which the stacktrace should be returned as single string
	 */
	public static String getCustomStackTrace(Throwable aThrowable) {
		// add the class name and any message passed to constructor
		final StringBuilder result = new StringBuilder("Stacktrace: ");
		result.append(aThrowable.toString());
		final String NEW_LINE = " <br /> ";
		result.append(NEW_LINE);

		// add each element of the stack trace
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append(element);
			result.append(NEW_LINE);
		}
		return result.toString();
	}

	public static String domainObjectsString(List<DomainObject> objects, String string) {
		boolean first = true;
		if (objects != null && objects.size() > 0) {
			for (DomainObject object : objects) {
				if (object != null) {
					if (first) {
						first = false;
						string = "";
					} else {
						string += " & ";
					}
					string += object.getStringRepresentation();
				}
			}
		}
		return string;
	}

	public static String domainObjectsString(List<DomainObject> objects) {
		return domainObjectsString(objects, "keine DomainObjekte");
	}

	public static String getListedType(String completeTypeDef) {
		return completeTypeDef.substring(completeTypeDef.indexOf("<") + 1, completeTypeDef.lastIndexOf(">"));
	}

	public static native void speedTracerLog(String msg) /*-{
		var logger = $wnd.console;
		if (logger && logger.markTimeline) {
			logger.markTimeline(msg);
		}
	}-*/;

	public static void log(Level logLevel, String message, Object caller) {
		log(logLevel, message, caller, null);
	}
	
	public static void log(Level logLevel, String message, Object caller, Throwable t) {

		String callerString = "static";
		if (caller != null){
			if(caller instanceof String)
				callerString = (String) caller;
			else
				callerString = caller.getClass().getName() + "@" + caller.hashCode();
		}

		boolean isGwtClient = false;
		try{
			isGwtClient = GWT.isClient();
		} catch(Throwable t1) {
			//in Java standalone calling GWT.* might trigger ClassNotFoundException
			//we don't care. we're not on the client side for sure.
		}

		String logString;
		if (isGwtClient) {
			logString = getCurrentTimeStamp() + " ; " + callerString + ": " + message;
			Window.setStatus("[" + logLevel + "] " + logString);
			logString = callerString + ": " + message;
//			speedTracerLog(logString);
		}
		else {
			logString = callerString + ": " + message;
		}

		// GWT.log(logString);
		logger.log(logLevel, logString, t);
		//Log.log(new LogRecord("gwt-log", logLevel, logString, t));

		// } else {
		// System.out.println("[" + logLevel + "] " + logString);
		// }
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	public static String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	private static String validatorAnnotationPrefix = "@" + AttributeValidators.class.getName() + "(value=";
	
	/**
	 * 
	 * @param value
	 * @param attributeValidators
	 * @return true if validation succeeded
	 * @return false if no validation possible (value or validator not available / empty)
	 * @throws at.ac.fhcampuswien.atom.shared.exceptions.ValidationError if validation failed
	 */
	public static boolean validateAttribute(String attributeName, Object value, String[] attributeValidators) {
		if (attributeValidators == null || attributeValidators.length == 0)
			return false;

		boolean validated = false;
		for(String validator : attributeValidators) {
			validated = validated | validateAttribute(attributeName, value, validator);
		}
		return validated;
	}
	
	private static boolean validateAttribute(String attributeName, Object value, String attributeValidator) {
		if (attributeValidator == null || attributeValidator.length() == 0)
			return false;

		if(attributeValidator.startsWith(validatorAnnotationPrefix))
			attributeValidator = attributeValidator.substring(validatorAnnotationPrefix.length(),
				attributeValidator.length() - 1);

		if (AttributeValidators.email.equals(attributeValidator)) {
			if (value == null || "".equals(value))
				return false;
			if(emailValidator.isValid(value.toString()))
				return true;
			else {
				String[] parts = value.toString().split("@");
				if (parts.length != 2)
					throw new ValidationError("an email-adress needs to contain exaclty one @ symbol!");
				else if (!parts[1].contains("."))
					throw new ValidationError("there needs to be a dot in the second half of an email-adress!");
				else
					throw new ValidationError("Bitte gültige E-Mail-Adresse angeben!");
			}
		}
		else if (AttributeValidators.socialsecurity.equals(attributeValidator)) {

			if (value == null || "".equals(value))
				return false;
			String svnr = value.toString();

			if (svnr.length() != 10)
				throw new ValidationError("Sozialversicherungsnummer muss exakt 10 Stellen lang sein!");

			// Prüfsumme
			if ((3 * Integer.parseInt(svnr.substring(0, 1)) + 7 * Integer.parseInt(svnr.substring(1, 2)) + 9
					* Integer.parseInt(svnr.substring(2, 3)) + 5 * Integer.parseInt(svnr.substring(4, 5)) + 8
					* Integer.parseInt(svnr.substring(5, 6)) + 4 * Integer.parseInt(svnr.substring(6, 7)) + 2
					* Integer.parseInt(svnr.substring(7, 8)) + 1 * Integer.parseInt(svnr.substring(8, 9)) + 6 * Integer.parseInt(svnr
					.substring(9, 10))) % 11 == Integer.parseInt(svnr.substring(3, 4)))
				return true;
			else
				throw new ValidationError(
						"Bitte überprüfen sie die Sozialversicherungsnummer, die Prüfsummenvalidierung war nicht erfolgreich!");

		}
		else if (AttributeValidators.telephone.equals(attributeValidator)) {
			if (value == null || (value instanceof String && ((String) value).length() == 0))
				return false;
			if (!value.toString().matches("\\+[0-9]+ ?[\\(| ][0-9]+[\\)| ][0-9 ]+-? ?[0-9]+")) // "\\+ ?\\([0-9]+\\)[0-9 ]+-?[0-9 ]*"))
				throw new ValidationError(
						"Bitte Telefonnummern im Format +43 (1) 606 68 77 - 4319 eingeben! Durchwahl und Abstände sind optional.");
			else
				return true;
		}
		else if(AttributeValidators.url.equals(attributeValidator)) {
			if (value == null || (value instanceof String && ((String) value).length() == 0))
				return false;
			if(urlValidator.isValid(value.toString()) || urlValidator.isValid("http://" + value.toString()))
				return true;
			else
				throw new ValidationError("Bitte gültige URL angeben!");
		}
		else if (AttributeValidators.notEmpty.equals(attributeValidator)) {
			if (value == null || 
					(value instanceof String && ((String) value).length() == 0) || 
					(value instanceof Collection<?> && ((Collection<?>) value).size() == 0)
			   )
				throw new ValidationError("Feld " + attributeName + " müss befüllt werden!");
			else
				return true;
		}
		return false;
		
	}

//	public static Set<String> checkPermissions(ClientSession session, String accessType, DomainClass requestedClass,
//			DomainObject requestedObject) throws AuthenticationException {
//		if (requestedClass == null) {
//			throw new AuthenticationException("I can only checkPermissions if you give me a non-null class to check!");
//		}
//		Set<String> accessTypes;
//			accessTypes = requestedClass.getAccess(session);
//
//		if (checkPermissionsMatch(accessType, accessTypes, session, requestedClass, requestedObject)) {
//			AtomTools.log(Level.INFO,
//					"User '" + session.getUsername() + "' is permitted access to class '" + requestedClass.getName() + "' instance '"
//							+ (requestedObject != null ? requestedObject.getStringRepresentation() : "null") + "'; permission='"
//							+ accessType + "'", null);
//			return accessTypes;
//		} else
//			throw new AuthenticationException("User '" + session.getUsername() + "' not allowed to access class '"
//					+ requestedClass.getName() + "' instance '"
//					+ (requestedObject != null ? requestedObject.getStringRepresentation() : "null") + "' with right '" + accessType
//					+ "'");
//	}
	
	public static Set<String> getRequiredRelations(String requiredPermission, Set<Access> access) {
		Set<String> ret = new HashSet<String>();
		if(access != null) {
			for(Access a : access) {
				if(isAccessAllowed(requiredPermission, a.getAccessTypes())) {
					java.util.Collections.addAll(ret, a.getRequiredRelations());
				}
			}
		}
		if(ret.size() < 1)
			throw new AuthenticationException("permission not assigned! (" + requiredPermission + ")");
		return ret;
	}
	
	public static void checkPermissionMatch(String requiredPermission, Collection<String> accessTypes) {
		if (accessTypes == null || accessTypes.size() == 0)
			throw new AuthenticationException("no permissions assigned! (requested: '" + requiredPermission + "')");
		else if (accessTypes.contains(AtomConfig.accessDenied))
			throw new AuthenticationException("permission denied!");
		else if (accessTypes.contains(requiredPermission))
			return;
		else if (AtomConfig.accessReadOnly.equals(requiredPermission) && accessTypes.contains(AtomConfig.accessReadWrite))
			return;
		else if (AtomConfig.accessSomeReadWrite.equals(requiredPermission)
				&& (accessTypes.contains(AtomConfig.accessReadWrite)))
			return;
		else if (AtomConfig.accessSomeReadOnly.equals(requiredPermission)
				&& (accessTypes.contains(AtomConfig.accessReadOnly))
						|| accessTypes.contains(AtomConfig.accessReadWrite))
			return;
		else if (AtomConfig.accessSomeLinkage.equals(requiredPermission)
				&& (accessTypes.contains(AtomConfig.accessLinkage)))
			return;
		else if (AtomConfig.accessSomeLinkage.equals(requiredPermission) || (AtomConfig.accessLinkage.equals(requiredPermission) || AtomConfig.accessMenue.equals(requiredPermission))
				&& (accessTypes.contains(AtomConfig.accessReadOnly))
						|| accessTypes.contains(AtomConfig.accessReadWrite))
			return;
		else
			throw new AuthenticationException("permission not assigned! (" + requiredPermission + ")");
	}

	public static boolean isAccessAllowed(String accessType, String... accessTypes) {
		return isAccessAllowed(accessType, Arrays.asList(accessTypes));
	}
	
	public static boolean isAccessAllowed(String accessType, Collection<String> accessTypes) {
		try {
			checkPermissionMatch(accessType, accessTypes);
			return true;
		} catch (AuthenticationException e) {
			AtomTools.log(Level.FINER, e.getMessage(), null);
		}
		return false;
	}

	// public static boolean isAccessAllowed(ClientSession session, String
	// accessType, DomainClass requestedClass,
	// DomainObject requestedObject) {
	// try {
	// checkPermissions(session, accessType, requestedClass, requestedObject);
	// return true;
	// } catch (AuthenticationException e) {
	// AtomTools.log(Level.WARNING, e.getMessage(), null);
	// }
	// return false;
	// }

	private static SimpleDateFormatGwt dateWithTimeMS  = new SimpleDateFormatGwt("dd.MM.yyyy HH:mm:ss.SSS");
	private static SimpleDateFormatGwt dateWithTimeSec = new SimpleDateFormatGwt("dd.MM.yyyy HH:mm:ss");
	private static SimpleDateFormatGwt dateWithTimeMin = new SimpleDateFormatGwt("dd.MM.yyyy HH:mm");
	private static SimpleDateFormatGwt dateWithoutTime = new SimpleDateFormatGwt("dd.MM.yyyy");

	public static String getDateStringWithoutTime(Date date) {
		if (date != null)
			return dateWithoutTime.format(date);
		return "";
	}

	public static String getDateStringWithTimeMin(Date date) {
		if (date != null)
			return dateWithTimeMin.format(date);
		return "";
	}

	public static String getDateStringWithTimeSec(Date date) {
		if (date != null)
			return dateWithTimeSec.format(date);
		return "";
	}

	public static String getCurrentTimeStamp() {
		return dateWithTimeMS.format(new Date());
	}

	@SuppressWarnings("deprecation")
	public static String getDateStringIntelligent(Date date) {
		if (date != null) {
			if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0)
				return dateWithoutTime.format(date);
			else if (date.getSeconds() == 0)
				return dateWithTimeMin.format(date);
			else
				return dateWithTimeSec.format(date);
		}
		return "";
	}

	private static AtomMessages messages;

	public static AtomMessages getMessages() {
		if (messages == null) {
			if (GWT.isClient())
				messages = GWT.create(AtomMessages.class);
			else {
				messages = LocaleFactory.get(AtomMessages.class);
			}
		}
		return messages;
	}
	
	public static boolean arrayContaintsEqualElement(Object[] array, Object test) {
		for(Object element : array) {
			if(element != null && element.equals(test))
				return true;
		}
		return false;
	}
	
	public static boolean arraysContainEqualElements(Object[] array1, Object[] array2) {
		if (array1 == array2 ||
				(
						(array1 == null || array1.length == 0) && 
						(array2 == null || array2.length == 0)
				)
		   ) {
			//both arrays are empty
			return true;
		}
		if(array1 != null && array2 != null && array1.length == array2.length) {
			//both array are non empty and contain the same number of items
			for (int i=0; i < array1.length; i++) {
				if(!array1[i].equals(array2[i]))
					return false;
			}
			return true;
			//this implementation is not the best for the name, since the name suggests that only the elements not the order count. but this should suffice for current needs.
		}
		return false;
	}
	
	public static boolean stringContainsOneOfList(String a, String[] l) {
		for(String s : l) {
			if(a.contains(s))
				return true;
		}
		return false;
	}
	
	public static String[] getComperator4Type(String className) {
		if(AtomConfig.specialFilterDeepSearch.equals(className))
			return new String[] {"∋"};
		
		if(stringContainsOneOfList(className, DomainClassAttribute.ordinalTypes))
			return AtomConfig.comparators4Numbers;

		return AtomConfig.comparators4Sets;
	}
	
	public static String getOneCharFilterType(String fromFilterType) {
		if(fromFilterType == null || fromFilterType.length() < 1 || fromFilterType == "null")
			return "=";
		if(fromFilterType.length() == 1)
			return fromFilterType;
		
		return "=";
	}
	
	public static String getFilterString(DataFilter[] filters) {
		
		if(filters == null || filters.length < 1)
			return "";
		
		String val = "";
		for(DataFilter f : filters) {
			val += "_" + f.getColumn() + "[" + f.getColumnType() + "]" + f.getFilterType() + "_" + f.getValue();
		}
		
		return val.substring(1);
	}
	
	public static String getFilterString(Collection<DataFilter> filters) {
		
		if(filters == null || filters.size() < 1)
			return "";
		
		return getFilterString(filters.toArray(new DataFilter[filters.size()]));
	}
	
	public static ArrayList<DataFilter> parseFilterString(String filterString) {
		
		if(filterString == null || filterString.length() < 1 || !(filterString.contains("[") && filterString.contains("]") && filterString.contains("_")))
			return null;
		
		String c = filterString;
		ArrayList<DataFilter> filters = new ArrayList<DataFilter>();
		
		while(true) {
			String attrName = c.substring(0, c.indexOf("["));
			c = c.substring(attrName.length()+1);
			String attrType = c.substring(0, c.indexOf("]"));
			c = c.substring(attrType.length()+1);
			String fType = c.substring(0, c.indexOf("_"));
			c = c.substring(fType.length()+1);
			String val;
			if(c.contains("_")) {
				val = c.substring(0, c.indexOf("_"));
				c = c.substring(val.length()+1);
			} else {
				val = c;
			}
			filters.add(new DataFilter(attrName, val, fType, attrType));
			
			if(val == c)
				break;
		}
		return filters;
	}
	
	public static Integer parseStringAsIntNullForErrors(String value) {
		Integer i = null;
		try {
			i = Integer.parseInt(value);
		} catch(NumberFormatException e) {
			//don't care
		}
		return i;
	}

	public static int getFrameHashCode(DomainClass representedClass, DomainObject representedObject, String representedSearchTerm, AtomConfig.FrameType frameType, DataFilter[] dataFilters) {
		
		int value = 7*(representedClass == null ? 0 : representedClass.hashCode())
				+ 11*(representedObject == null ? 0 : representedObject.hashCode())
				+ 17*(representedSearchTerm == null ? 0 : representedSearchTerm.hashCode())
				+ 31*(frameType == null ? 0 : frameType.hashCode())
				;
		
		if(dataFilters != null && dataFilters.length > 0)
			for(DataFilter f : dataFilters) {
				value += 71 * f.hashCode();
			}
		
//		AtomTools.log(Level.INFO, "return hashCode = " + value + " for frame " + this.shortTitle, this);
		return value;
	}
	
	public static Throwable getInnerMostCause(Throwable t) {
		if(t == null)
			return null;
		while(t.getCause() != null && t.getCause() != t) {
			t = t.getCause();
		}
		return t;
	}
	
	public static HashSet<String> getCartesianProduct(String[] values, String seperator) {
		HashSet<String> out = new HashSet<String>();
		
		if(values == null || values.length == 0)
			return out;
		for(int i=0 ; i < values.length ; i++) {
			out.add(values[i]);
			
			if(values.length > (i+1)) {
				String[] others = new String[values.length-(i+1)];
				System.arraycopy(values, i+1, others, 0, values.length-(i+1));
				out.addAll(combineStringWithOthers(values[i], others, seperator));
			}
		}
		
		return out;
	}
	
	private static HashSet<String> combineStringWithOthers(String one, String[] others, String seperator) {
		HashSet<String> out = new HashSet<String>();
		
		for(int i=0 ; i < others.length ; i++) {
			String newOne = one + seperator + others[i];
			out.add(newOne);
			if(others.length > (i+1)) {
				String[] othersB = new String[others.length-(i+1)];
				System.arraycopy(others, i+1, othersB, 0, others.length-(i+1));
				out.addAll(combineStringWithOthers(newOne, othersB, seperator));
			}
		}
		return out;
	}
	
	/*
	 * copied from
	 * http://www.java2s.com/Open-Source/Java-Document/6.0-JDK-Modules
	 * -sun/misc/sun/misc/FpUtils.java.htm
	 */

	// /**
	// * Bit mask to isolate the sign bit of a <code>double</code>.
	// */
	// public static final long SIGN_BIT_MASK = 0x8000000000000000L;
	//
	// /**
	// * Returns the floating-point number adjacent to the first
	// * argument in the direction of the second argument. If both
	// * arguments compare as equal the second argument is returned.
	// *
	// * <p>
	// * Special cases:
	// * <ul>
	// * <li> If either argument is a NaN, then NaN is returned.
	// *
	// * <li> If both arguments are signed zeros, <code>direction</code>
	// * is returned unchanged (as implied by the requirement of
	// * returning the second argument if the arguments compare as
	// * equal).
	// *
	// * <li> If <code>start</code> is
	// * &plusmn;<code>Double.MIN_VALUE</code> and <code>direction</code>
	// * has a value such that the result should have a smaller
	// * magnitude, then a zero with the same sign as <code>start</code>
	// * is returned.
	// *
	// * <li> If <code>start</code> is infinite and
	// * <code>direction</code> has a value such that the result should
	// * have a smaller magnitude, <code>Double.MAX_VALUE</code> with the
	// * same sign as <code>start</code> is returned.
	// *
	// * <li> If <code>start</code> is equal to &plusmn;
	// * <code>Double.MAX_VALUE</code> and <code>direction</code> has a
	// * value such that the result should have a larger magnitude, an
	// * infinity with same sign as <code>start</code> is returned.
	// * </ul>
	// *
	// * @param start starting floating-point value
	// * @param direction value indicating which of
	// * <code>start</code>'s neighbors or <code>start</code> should
	// * be returned
	// * @return The floating-point number adjacent to <code>start</code> in the
	// * direction of <code>direction</code>.
	// * @author Joseph D. Darcy
	// */
	// public static double nextAfter(double start, double direction) {
	// /*
	// * The cases:
	// *
	// * nextAfter(+infinity, 0) == MAX_VALUE
	// * nextAfter(+infinity, +infinity) == +infinity
	// * nextAfter(-infinity, 0) == -MAX_VALUE
	// * nextAfter(-infinity, -infinity) == -infinity
	// *
	// * are naturally handled without any additional testing
	// */
	//
	// // First check for NaN values
	// if (Double.isNaN(start) || Double.isNaN(direction)) {
	// // return a NaN derived from the input NaN(s)
	// return start + direction;
	// } else if (start == direction) {
	// return direction;
	// } else { // start > direction or start < direction
	// // Add +0.0 to get rid of a -0.0 (+0.0 + -0.0 => +0.0)
	// // then bitwise convert start to integer.
	// long transducer = Double.doubleToRawLongBits(start + 0.0d);
	//
	// /*
	// * IEEE 754 floating-point numbers are lexicographically
	// * ordered if treated as signed- magnitude integers .
	// * Since Java's integers are two's complement,
	// * incrementing" the two's complement representation of a
	// * logically negative floating-point value *decrements*
	// * the signed-magnitude representation. Therefore, when
	// * the integer representation of a floating-point values
	// * is less than zero, the adjustment to the representation
	// * is in the opposite direction than would be expected at
	// * first .
	// */
	// if (direction > start) { // Calculate next greater value
	// transducer = transducer + (transducer >= 0L ? 1L : -1L);
	// } else { // Calculate next lesser value
	// assert direction < start;
	// if (transducer > 0L)
	// --transducer;
	// else if (transducer < 0L)
	// ++transducer;
	// /*
	// * transducer==0, the result is -MIN_VALUE
	// *
	// * The transition from zero (implicitly
	// * positive) to the smallest negative
	// * signed magnitude value must be done
	// * explicitly.
	// */
	// else
	// transducer = SIGN_BIT_MASK | 1L;
	// }
	//
	// return Double.longBitsToDouble(transducer);
	// }
	// }

}
