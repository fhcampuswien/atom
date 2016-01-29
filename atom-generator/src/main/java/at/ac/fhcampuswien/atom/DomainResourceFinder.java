package at.ac.fhcampuswien.atom;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.Notifiable;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;

public class DomainResourceFinder {

	private static final String domainPackageName = "at.ac.fhcampuswien.atom.shared.domain";
	private static String domainPackagePath = domainPackageName.replace('.', '/');


	public static void findResources(boolean getLocation, Notifiable<String> receiver) {

		AtomTools.log(Level.INFO, "starting to build DomainTree", null);

		// Get a File object for the package
		URL url = DomainObject.class.getResource("/" + domainPackagePath);

		if (url != null) {

			AtomTools.log(Level.INFO, "Launcher found url '" + url + "' for package '" + domainPackagePath + "'", null);
			// (new File(url.getFile().replace("%20", " "))).list()

			handleResource(url, receiver, getLocation);

			// find and analyze "the other" project that contains domain classes
			// (atom-core & atom-domain)
			String foundTextUrl = url.toString();
			try {
				if (foundTextUrl.contains("/atom-core")) {
					handleResource(new URL(foundTextUrl.replace("/atom-core", "/atom-domain")), receiver, getLocation);
				} else {
					handleResource(new URL(foundTextUrl.replace("/atom-domain", "/atom-core")), receiver, getLocation);
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
	
	private static void handleResource(URL url, Notifiable<String> receiver, boolean getLocation) {
		if (url.getProtocol().equals("jar")) {
			handleJarUrl(url, receiver, getLocation);
		} else {
			handleDirectoryOrFile(new File(url.getFile().replace("%20", " ")), receiver, getLocation);
		}
	}

	private static void handleJarUrl(URL url, Notifiable<String> receiver, boolean getLocation) {

		try {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile archive = con.getJarFile();

			/* Search for the entries you care about. */
			Enumeration<JarEntry> entries = archive.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(domainPackagePath) && (entry.getName().endsWith(".class") || entry.getName().endsWith(".java"))) {
					if(getLocation)
						receiver.doNotify(entry.getName());
					else {
						int i = entry.getName().lastIndexOf("/");
						String name = entry.getName().substring(i + 1);
						String path = entry.getName().substring(0, i);
						AtomTools.log(Level.INFO, " match found: " + entry.getName() + " --> " + name + " ; " + path, DomainResourceFinder.class);
						receiver.doNotify(path.replace("/", ".") + name);
					}
				} else {
					AtomTools.log(Level.INFO, "non-matching: " + entry.getName(), DomainResourceFinder.class);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleDirectoryOrFile(File directoryOrFile, Notifiable<String> receiver, boolean getLocation) {
		if (!directoryOrFile.getName().contains("gwtr") && !directoryOrFile.getName().contains("annotations")) {
			if (!directoryOrFile.isFile())
				handleDirectory(directoryOrFile, receiver, getLocation);
			else
				handleFile(directoryOrFile, receiver, getLocation);
		}
	}

	private static void handleDirectory(File directory, Notifiable<String> receiver, boolean getLocation) {
		File[] directoryContent = directory.listFiles();
		if (directoryContent != null) {
			for (File directoryOrFile : directoryContent) {
				handleDirectoryOrFile(directoryOrFile, receiver, getLocation);
			}
		}
	}

	private static void handleFile(File file, Notifiable<String> receiver, boolean getLocation) {
		String fileName = file.getName();
		// we are only interested in .class or .java files
		if (fileName.toLowerCase().endsWith(".class") || fileName.toLowerCase().endsWith(".java")) {

			int fileExtensionLength = 0;
			if (fileName.endsWith(".class"))
				fileExtensionLength = 6;
			else if (fileName.endsWith(".java"))
				fileExtensionLength = 5;

			if(getLocation)
				receiver.doNotify(file.getAbsolutePath());
			else {
				String packagePath = file.getParentFile().getAbsolutePath().replace("\\", ".").replace("/", ".");
				String simpleClassName = fileName.substring(0, fileName.length() - fileExtensionLength);
				receiver.doNotify(packagePath.substring(packagePath.indexOf(domainPackageName)) + "." + simpleClassName);
			}	
		}
	}
}
