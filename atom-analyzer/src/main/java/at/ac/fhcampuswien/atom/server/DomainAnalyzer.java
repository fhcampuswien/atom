/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.server;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Where;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.ClientSession;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListOrgEinheiten;
import at.ac.fhcampuswien.atom.shared.annotations.AccessListRoles;
import at.ac.fhcampuswien.atom.shared.annotations.AccessLists;
import at.ac.fhcampuswien.atom.shared.annotations.AnalyzerIgnore;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeDisplayName;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeGroup;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeLoadingPolicy;
import at.ac.fhcampuswien.atom.shared.annotations.AttributePlacement;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeTooltip;
import at.ac.fhcampuswien.atom.shared.annotations.AttributeValidators;
import at.ac.fhcampuswien.atom.shared.annotations.BooleanAttributeMeaning;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNamePlural;
import at.ac.fhcampuswien.atom.shared.annotations.ClassNameSingular;
import at.ac.fhcampuswien.atom.shared.annotations.DefaultAttributeGroupName;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromDetailGui;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromGui;
import at.ac.fhcampuswien.atom.shared.annotations.HideFromListGui;
import at.ac.fhcampuswien.atom.shared.annotations.LinkAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.ListBoxDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.ObjectImage;
import at.ac.fhcampuswien.atom.shared.annotations.OrderedAttributeGroups;
import at.ac.fhcampuswien.atom.shared.annotations.OrderedAttributesList;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinition;
import at.ac.fhcampuswien.atom.shared.annotations.RelationDefinitions;
import at.ac.fhcampuswien.atom.shared.annotations.RelationEssential;
import at.ac.fhcampuswien.atom.shared.annotations.Searchable;
import at.ac.fhcampuswien.atom.shared.annotations.SliderAttribute;
import at.ac.fhcampuswien.atom.shared.annotations.SortColumn;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

public class DomainAnalyzer {

	private static final String domainPackageName = "at.ac.fhcampuswien.atom.shared.domain";
	private static String domainPackagePath = domainPackageName.replace('.', '/');
	private static DomainClass domainTree = null;
	private static final Object domainTreeBuildSynchronizeHandle = new Object();

	public static DomainClass getDomainTree() {
		synchronized (domainTreeBuildSynchronizeHandle) {
			if (domainTree == null)
				buildDomainTree();
			return domainTree;
		}
	}

	public static DomainClass getDomainClass(String name) {
		getDomainTree();
		return getDomainClassForName(name);
	}

	private DomainAnalyzer() {
	}

	private static void buildDomainTree() {
		AtomTools.log(Level.INFO, "starting to build DomainTree", null);
		domainTree = createDomainClassForClass(DomainObject.class, null);

		// String name = domainPackageName;
		// name = name.replace('.', '/');
		// if (!name.startsWith("/")) {
		// name = "/" + name;
		// }

		// Get a File object for the package
		URL url = DomainObject.class.getResource("/" + domainPackagePath);

		if (url != null) {

			AtomTools.log(Level.INFO, "Launcher found url '" + url + "' for package '" + domainPackagePath + "'", null);
			// (new File(url.getFile().replace("%20", " "))).list()

			handleResource(url);

			// find and analyze "the other" project that contains domain classes
			// (atom-core & atom-domain)
			String foundTextUrl = url.toString();
			try {
				if (foundTextUrl.contains("/atom-core")) {
					handleResource(new URL(foundTextUrl.replace("/atom-core", "/atom-domain")));
				} else {
					handleResource(new URL(foundTextUrl.replace("/atom-domain", "/atom-core")));
				}

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				AtomTools.log(Level.SEVERE, "could not open atom-domain URL='" + foundTextUrl + "'", null);
			}

			AtomTools.log(Level.INFO, "finished building DomainTree", null);
		} else {
			// please report a github issue if and when url is null
			AtomTools.log(Level.SEVERE, "could not find url for package '" + domainPackagePath + "'", null);
		}
		// handleDirectoryOrFile(new
		// File("/var/lib/tomcat6/webapps/ATOM_v7/WEB-INF/classes" + name));
	}

	private static void handleResource(URL url) {
		if (url.getProtocol().equals("jar")) {
			handleJarUrl(url);
		} else {
			handleDirectoryOrFile(new File(url.getFile().replace("%20", " ")));
		}
	}

	private static void handleJarUrl(URL url) {

		try {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile archive = con.getJarFile();

			/* Search for the entries you care about. */
			Enumeration<JarEntry> entries = archive.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(domainPackagePath) && (entry.getName().endsWith(".class") || entry.getName().endsWith(".java"))) {
					int i = entry.getName().lastIndexOf("/");
					String name = entry.getName().substring(i + 1);
					String path = entry.getName().substring(0, i);
					AtomTools.log(Level.INFO, " match found: " + entry.getName() + " --> " + name + " ; " + path, DomainAnalyzer.class);
					handleClass(path.replace("/", "."), name);
				} else {
					AtomTools.log(Level.INFO, "non-matching: " + entry.getName(), DomainAnalyzer.class);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleDirectoryOrFile(File directoryOrFile) {
		if (!directoryOrFile.getName().contains("gwtr") && !directoryOrFile.getName().contains("annotations")) {
			if (!directoryOrFile.isFile())
				handleDirectory(directoryOrFile);
			else
				handleFile(directoryOrFile);
		}
	}

	private static void handleDirectory(File directory) {
		File[] directoryContent = directory.listFiles();
		if (directoryContent != null) {
			for (File directoryOrFile : directoryContent) {
				handleDirectoryOrFile(directoryOrFile);
			}
		}
	}

	private static void handleFile(File file) {
		String fileName = file.getName();
		// we are only interested in .class or .java files and not in the root
		// DomainObject
		if ((fileName.toLowerCase().endsWith(".class") || fileName.toLowerCase().endsWith(".java")) && !fileName.startsWith("DomainObject.")) {

			String packagePath = file.getParentFile().getAbsolutePath().replace("\\", ".").replace("/", ".");
			handleClass(packagePath.substring(packagePath.indexOf(domainPackageName)), fileName);
		}
	}

	private static void handleClass(String packageString, String nameInclExtension) {

		int fileExtensionLength = 0;
		if (nameInclExtension.endsWith(".class"))
			fileExtensionLength = 6;
		else if (nameInclExtension.endsWith(".java"))
			fileExtensionLength = 5;
		else
			throw new AtomException("unknown DomainClass file extension!! -> " + nameInclExtension);

		// removes the extension
		String simpleClassName = nameInclExtension.substring(0, nameInclExtension.length() - fileExtensionLength);

		AtomTools.log(Level.INFO, "processing class " + simpleClassName + " in package " + packageString, null);

		integrateClassIntoDomainTree(simpleClassName, packageString);
	}

	private static DomainClass integrateClassIntoDomainTree(String simpleClassName, String packageName) {

		// search if the class is already contained by the tree, and return it
		// if it is.
		String fullClassName = packageName + "." + simpleClassName;
		DomainClass foundDomainClass = getDomainClassForName(fullClassName);
		if (foundDomainClass != null)
			return foundDomainClass;

		try {
			// get the class of the found class definition
			Class<?> foundClass = Class.forName(packageName + "." + simpleClassName);
			foundDomainClass = integrateClassIntoDomainTree(foundClass, false);

		} catch (ClassNotFoundException cnfex) {
			AtomTools.log(Level.SEVERE, "No Class found for " + packageName + "." + simpleClassName + "; exception: " + cnfex, null);
		}
		return foundDomainClass;
	}

	/**
	 * 
	 * 
	 * @param theClass is ensured by the caller methods to be a DomainClass inside the domain's package.
	 * @param checkIfAlreadyExists
	 * @return
	 */
	private static DomainClass integrateClassIntoDomainTree(Class<?> theClass, boolean checkIfAlreadyExists) {
		String simpleClassName = theClass.getSimpleName();
		String thePackage = theClass.getPackage().getName();
		String fullClassName = thePackage + "." + simpleClassName;

		DomainClass foundDomainClass = null;

		if (checkIfAlreadyExists) {
			foundDomainClass = getDomainClassForName(fullClassName);
			if (foundDomainClass != null) {
				AtomTools.log(Level.INFO, "Class " + fullClassName + " already in tree, returning", null);
				return foundDomainClass;
			}
		}

		if (theClass.getAnnotation(AnalyzerIgnore.class) != null) {
			AtomTools.log(Level.INFO, "Ignoring Class " + fullClassName + " because of its AnalyzerIgnore Annotation", null);
		}

		else if (thePackage.contains(domainPackageName)) {
			AtomTools.log(Level.INFO, "Class " + fullClassName + " should be created. will search superclass first.", null);

			DomainClass parentDomainClass = null;
			Class<?> superClass = theClass.getSuperclass();
			if (superClass == DomainObject.class) {
				parentDomainClass = domainTree;
			} else {
				if (superClass != null && superClass.getPackage().getName().contains(domainPackageName)) {
					parentDomainClass = integrateClassIntoDomainTree(theClass.getSuperclass(), true);
				} else {
					AtomTools.log(Level.SEVERE, "Superclass of " + fullClassName + " is not inside of the domainpackage, therefore it isn't part of the tree. ignoring both classes! FIX YOUR DOMAIN HIERARCHY!", null);
					foundDomainClass = null;
				}
			}

			foundDomainClass = createDomainClassForClass(theClass, parentDomainClass);

		} else {
			AtomTools.log(Level.SEVERE, "Class " + fullClassName + " is from outside the domainPackage!", null);
		}
		return foundDomainClass;
	}

	private static DomainClass createDomainClassForClass(Class<?> theClass, DomainClass parentClass) {
		DomainClass domainClass = new DomainClass(theClass.getSimpleName(), theClass.getPackage().getName(), Modifier.toString(theClass.getModifiers()).contains("abstract"), parentClass);
		addClassFeaturesToDomainClass(theClass, domainClass);
		return domainClass;
	}

	private static void addClassFeaturesToDomainClass(Class<?> theClass, DomainClass domainClass) {

		for (Annotation anAnnotation : theClass.getAnnotations()) {
			if (anAnnotation instanceof ClassNamePlural) {
				domainClass.setPluralName(((ClassNamePlural) anAnnotation).value());
			} else if (anAnnotation instanceof ClassNameSingular) {
				domainClass.setSingularName(((ClassNameSingular) anAnnotation).value());
			} else if (anAnnotation instanceof DefaultAttributeGroupName) {
				domainClass.setDefaultAttributeGroup(((DefaultAttributeGroupName) anAnnotation).value());
			} else if (anAnnotation instanceof OrderedAttributeGroups) {
				OrderedAttributeGroups ac = ((OrderedAttributeGroups) anAnnotation);
				domainClass.setOrderedAttributeGroups(ac.value(), ac.attributes());
			} else if (anAnnotation instanceof HideFromGui) {
				domainClass.setHideFromGui(true);
			} else if (anAnnotation instanceof OrderedAttributesList) {
				OrderedAttributesList oalv = (OrderedAttributesList) anAnnotation;
				domainClass.setOrderedAttributesListView(oalv.value());
			} else if (anAnnotation instanceof RelationDefinitions) {
				for (RelationDefinition rd : ((RelationDefinitions) anAnnotation).value()) {
					domainClass.addRelationDefinition(rd);
				}
			} else if (anAnnotation instanceof RelationDefinition) {
				domainClass.addRelationDefinition((RelationDefinition) anAnnotation);
			} else if (anAnnotation instanceof AccessListRoles) {
				AccessListRoles list = (AccessListRoles) anAnnotation;
				domainClass.getAccessHandler().addAccessRoles(list.accessTypes(), list.requiredRelations(), list.value());
			} else if (anAnnotation instanceof AccessListOrgEinheiten) {
				AccessListOrgEinheiten list = (AccessListOrgEinheiten) anAnnotation;
				domainClass.getAccessHandler().addAccessOE(list.accessTypes(), list.requiredRelations(), list.onlyHauptrolle(), list.onlyLeiter(), list.value());
			} else if (anAnnotation instanceof ObjectImage) {
				ObjectImage objectImage = (ObjectImage) anAnnotation;
				domainClass.setObjectLogoImageData(objectImage.value());
			} else if (anAnnotation instanceof SortColumn) {
				SortColumn sortColumn = (SortColumn) anAnnotation;
				domainClass.setSortColumn(sortColumn.value());
			} else if (anAnnotation instanceof Searchable) {
				Searchable searchable = (Searchable) anAnnotation;
				domainClass.setSearchable(searchable.value());
			} else if (anAnnotation instanceof AccessLists) {
				AccessLists accessLists = (AccessLists) anAnnotation;
				for (AccessListRoles list : accessLists.rolesLists()) {
					domainClass.getAccessHandler().addAccessRoles(list.accessTypes(), list.requiredRelations(), list.value());
				}
				for (AccessListOrgEinheiten list : accessLists.orgEinheitenLists()) {
					domainClass.getAccessHandler().addAccessOE(list.accessTypes(), list.requiredRelations(), list.onlyHauptrolle(), list.onlyLeiter(), list.value());
				}
			} else {
				domainClass.addClassAnnotation(anAnnotation.toString());
			}
		}

		for (Field aField : theClass.getDeclaredFields()) {
			if (aField.getAnnotation(AnalyzerIgnore.class) == null && !Modifier.isStatic(aField.getModifiers())) {
				String fieldName = aField.getName();
				// String fieldName = aField.getName().substring(0,
				// 1).toUpperCase() + aField.getName().substring(1);
				DomainClassAttribute newAttribute = domainClass.createAttribute(fieldName, aField.getGenericType().toString(), true);
				for (Annotation anAnnotation : aField.getAnnotations()) {
					handleAttributeAnnotation(newAttribute, fieldName, anAnnotation);
				}
			}
		}

		for (Method aMethod : theClass.getDeclaredMethods()) {
			// check if public
			if (Modifier.isPublic(aMethod.getModifiers()) && !Modifier.isStatic(aMethod.getModifiers()) && aMethod.getAnnotation(AnalyzerIgnore.class) == null) {
				String methodName = aMethod.getName();
				DomainClassAttribute attribute = null;
				if (methodName.startsWith("get")) {
					attribute = domainClass.updateAttribute(methodName.substring(3), aMethod.getGenericReturnType().toString(), false, false);
				} else if (methodName.startsWith("set")) {
					attribute = domainClass.updateAttribute(methodName.substring(3), aMethod.getGenericParameterTypes()[0].toString(), false, true);
				}
				if (methodName.startsWith("get") || methodName.startsWith("set")) {
					for (Annotation anAnnotation : aMethod.getAnnotations()) {
						handleAttributeAnnotation(attribute, methodName.substring(3), anAnnotation);
					}
				}
			}
		}
		
		for(Constructor<?> constructor : theClass.getConstructors()) {
			try{
				if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() <= 0) {
					domainClass.setHasPublicEmptyConstructor();
				}
			}
			catch(Throwable t) {
				AtomTools.log(Level.WARNING, "throwable cought while searching for public empty constructor in class " + domainClass.getName(), null, t);
			}
		}
	}

	private static void handleAttributeAnnotation(DomainClassAttribute attribute, String fieldName, Annotation anAnnotation) {
		if (attribute == null) {
			AtomTools.log(Level.SEVERE, "cannot process annotation\"" + anAnnotation + "\" of field \"" + fieldName + "\" --> DomainClassAttribute not found!", null);
			return;
		}

		if (anAnnotation instanceof AttributePlacement) {
			AttributePlacement ap = (AttributePlacement) anAnnotation;
			if (ap.value() != Double.MAX_VALUE)
				attribute.setPosition(ap.value(), ap.type());
			if (ap.inGroup() != Double.MAX_VALUE)
				attribute.setPositionInGroup(ap.inGroup());
			if (ap.overall() != Double.MAX_VALUE)
				attribute.setPositionOverall(ap.overall());
		} else if (anAnnotation instanceof AttributeGroup) {
			attribute.setAttributeGroup(((AttributeGroup) anAnnotation).value());
		} else if (anAnnotation instanceof Transient) {
			attribute.setTransient();
		} else if (anAnnotation instanceof AttributeDisplayName) {
			attribute.setDisplayName(((AttributeDisplayName) anAnnotation).value());
		} else if (anAnnotation instanceof AttributeTooltip) {
			attribute.setTooltip(((AttributeTooltip) anAnnotation).value());
			
		} else if (anAnnotation instanceof AttributeValidators) {
			attribute.setValidators(((AttributeValidators) anAnnotation).value());
		} else if (anAnnotation instanceof HideFromListGui) {
			attribute.setHideFromListGui(((HideFromListGui) anAnnotation).value());
		} else if (anAnnotation instanceof HideFromDetailGui) {
			attribute.setHideFromDetailGui(((HideFromDetailGui) anAnnotation).value());
		} else if (anAnnotation instanceof HideFromGui) {
			attribute.setHideFromDetailGui(true);
			attribute.setHideFromListGui(true);
		} else if (anAnnotation instanceof BooleanAttributeMeaning) {
			BooleanAttributeMeaning ann = (BooleanAttributeMeaning) anAnnotation;
			attribute.setBooleanMeaning(true, ann.trueValue());
			attribute.setBooleanMeaning(false, ann.falseValue());
			attribute.setBooleanMeaning(null, ann.nullValue());
		} else if (anAnnotation instanceof AttributeLoadingPolicy) {
			AttributeLoadingPolicy ann = (AttributeLoadingPolicy) anAnnotation;
			attribute.setLoadedWithLists(ann.withLists());
			attribute.setLoadedWhenNotPrimary(ann.whenNotPrimary());
			attribute.setRequiredForStringRepresentation(ann.requiredForStringRepresentation());
		} else if (anAnnotation instanceof RelationEssential) {
			attribute.setRelationEssential(true);
		} else if (anAnnotation instanceof ListBoxDefinition) {
			ListBoxDefinition def = (ListBoxDefinition) anAnnotation;
			attribute.setListBox(def.keys(), def.display(), def.sql(), def.useHql(), def.viewType(), def.multiSelectSeperator(), def.allowOtherValues(), def.anyExistingValue(), def.hideNonSelectedInReadMode());
		} else if (anAnnotation instanceof SliderAttribute) {
			SliderAttribute ann = (SliderAttribute) anAnnotation;
			attribute.setSlider(ann.minValue(), ann.maxValue(), ann.defaultValue(), ann.stepSize(), ann.roundTo());
		} else if (anAnnotation instanceof AccessListRoles) {
			AccessListRoles list = (AccessListRoles) anAnnotation;
			attribute.getAccessHandler().addAccessRoles(list.accessTypes(), list.requiredRelations(), list.value());
		} else if (anAnnotation instanceof AccessListOrgEinheiten) {
			AccessListOrgEinheiten list = (AccessListOrgEinheiten) anAnnotation;
			attribute.getAccessHandler().addAccessOE(list.accessTypes(), list.requiredRelations(), list.onlyHauptrolle(), list.onlyLeiter(), list.value());
		} else if (anAnnotation instanceof AccessLists) {
			AccessLists accessLists = (AccessLists) anAnnotation;
			for (AccessListRoles list : accessLists.rolesLists()) {
				attribute.getAccessHandler().addAccessRoles(list.accessTypes(), list.requiredRelations(), list.value());
			}
			for (AccessListOrgEinheiten list : accessLists.orgEinheitenLists()) {
				attribute.getAccessHandler().addAccessOE(list.accessTypes(), list.requiredRelations(), list.onlyHauptrolle(), list.onlyLeiter(), list.value());
			}
		} else if (anAnnotation instanceof LinkAttribute) {
			LinkAttribute linkAttribute = (LinkAttribute) anAnnotation;
			attribute.setLinkPrefix(linkAttribute.prefix());
			attribute.setLinkSuffix(linkAttribute.suffix());
			attribute.addAnnotation(anAnnotation.toString());
		} else if (anAnnotation instanceof OneToMany) {
			OneToMany oneToMany = (OneToMany) anAnnotation;
			attribute.setMappedBy(oneToMany.mappedBy());
		} else if (anAnnotation instanceof ManyToMany) {
			ManyToMany manyToMany = (ManyToMany) anAnnotation;
			attribute.setMappedBy(manyToMany.mappedBy());
		} else if (anAnnotation instanceof Where) {
			Where where = (Where) anAnnotation;
			attribute.setWhere(where.clause());
		} else {
			attribute.addAnnotation(anAnnotation.toString());
		}
	}

	private static DomainClass getDomainClassForName(String classname) {
		return getDomainClassForNameBelowDomainClass(classname, domainTree);
	}

	private static DomainClass getDomainClassForNameBelowDomainClass(String classname, DomainClass belowThis) {
		if (belowThis.getName().equals(classname))
			return belowThis;
		for (DomainClass subClass : belowThis.getSubClasses()) {
			DomainClass classForName = getDomainClassForNameBelowDomainClass(classname, subClass);
			if (classForName != null)
				return classForName;
		}
		return null;
	}

	// public static List<DomainClass> getPermissibleDomainClasses(ClientSession
	// session) {
	// ArrayList<DomainClass> permissableClasses = new ArrayList<DomainClass>();
	// DomainClass clonedTree = getDomainTree().clone();
	// permissableClasses.addAll(getPermissibleDomainClassesBelow(session,
	// clonedTree, false));
	// return permissableClasses;
	// }
	//
	// private static List<DomainClass>
	// getPermissibleDomainClassesBelow(ClientSession session, DomainClass
	// domainClass, boolean superIsAccessible) {
	// ArrayList<DomainClass> permissableClasses = new ArrayList<DomainClass>();
	// // boolean isAccessible = domainClass.isAccessAllowed(session,
	// AtomConfig.accessLinkage);
	// boolean isAccessible = AtomTools.isAccessAllowed(session,
	// AtomConfig.accessMenue, domainClass, null);
	// if (isAccessible) {
	// if (!superIsAccessible) {
	// permissableClasses.add(domainClass);
	// }
	// } else {
	// if (domainClass.getSuperClass() != null) {
	// domainClass.getSuperClass().getSubClasses().remove(domainClass);
	// }
	// }
	// if (domainClass.getSubClasses() != null &&
	// domainClass.getSubClasses().size() > 0) {
	// for (DomainClass subClass : domainClass.getSubClasses()) {
	// permissableClasses.addAll(getPermissibleDomainClassesBelow(session,
	// subClass, isAccessible));
	// }
	// }
	// return permissableClasses;
	// }

	public static DomainClass getUsersDomainTree(ClientSession session) {
		DomainClass tree = getDomainTree();
		synchronized (tree) {
			tree.fillUsersAccessRecursive(session);
			return tree;
		}
	}
}
