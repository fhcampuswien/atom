/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;



public class AtomConfig {

	public enum AccessType {
		menu, readWrite, readOnly, relatedReadOnly, relatedReadWrite, linkage, denied;
	};
	
	public static final boolean updateAllStringRepresentationsOnApplicationStartup = false;

	public static final String accessCreateNew = "accessCreateNew";
	public static final String accessMenue = "accessMenue";
	public static final String accessReadWrite = "accessReadWrite";
	public static final String accessReadOnly = "accessReadOnly";
	public static final String accessSomeReadWrite = "accessSomeReadWrite";
	public static final String accessSomeReadOnly = "accessSomeReadOnly";	
	public static final String accessLinkage = "accessLinkage";
	public static final String accessDenied = "accessDenied";
	
	public static final String[] accessTypes = {accessCreateNew, accessMenue, accessReadWrite, accessReadOnly, accessLinkage, accessDenied};
	
	public static final boolean loadEverythingRelated = false;
	
	public static final Integer nullReasonTruelyNull = 0;
	public static final Integer nullReasonNotRelationEssential = 1;
	public static final Integer nullReasonLazyLoading = 2;
	
	public static final String specialFilterQuickSearch = "stringRepresentation";
	public static final String specialFilterDeepSearch = "specialFilterDeepSearch";
	
	public static DataFilter showResultAsListBooleanTransport = new DataFilter("showResultAsList", "BooleanTransport", null, null);
	
	public static enum FrameType implements com.google.gwt.user.client.rpc.IsSerializable {
		LIST_ALL,
		LIST_RELATED,
		DETAIL_VIEW,
		WELCOME,
		HELP,
		SEARCH,
		SEARCH_SIMPLE,
		SEARCHCLASS,
		SEARCHCLASS_SIMPLE,
		FILTERCLASS,
		OBJECT_SELECTOR, 
		IMPORT
	}
	
	public static String[] comparators4Sets = new String[]{"∋", "∌", "=", "≠"};
	public static String[] comparators4Numbers = new String[]{"<", "≤", "=", "≥", ">"};
}