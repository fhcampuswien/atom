import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.reflect.shared.GwtReflect;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.domain.DomainObject;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;

// (new File(url.getFile().replace("%20", " "))).list()
			
			magicResource(url);
			
			//find and analyze "the other" project that contains domain classes (atom-core & atom-domain)
			String foundTextUrl = url.toString();
			try {
				if(foundTextUrl.contains("/atom-core")) {
					magicResource(new URL(foundTextUrl.replace("/atom-core", "/atom-domain")));
				}
				else {
					magicResource(new URL(foundTextUrl.replace("/atom-domain", "/atom-core")));
				}
					
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				AtomTools.log(Log.LOG_LEVEL_ERROR,
						"could not open atom-domain URL='" + foundTextUrl + "'", null);
			}
			
			AtomTools.log(Log.LOG_LEVEL_INFO, "finished building DomainTree",
					null);
		} else {
			// please report a github issue if and when url is null
			AtomTools.log(Log.LOG_LEVEL_FATAL,
					"could not find url for package '" + domainPackagePath + "'", null);
		}
	}
	
	private static void magicResource(URL url) {
		if (url.getProtocol().equals("jar")) {
			magicJarUrl(url);
		} else {
			magicDirectoryOrFile(new File(url.getFile()
					.replace("%20", " ")));
		}
	}
	
	private static void magicJarUrl(URL url) {

		try {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile archive = con.getJarFile();

			/* Search for the entries you care about. */
			Enumeration<JarEntry> entries = archive.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(domainPackagePath) && (entry.getName().endsWith(".class") || entry.getName().endsWith(".java"))) {
					int i = entry.getName().lastIndexOf("/");
					String name = entry.getName().substring(i+1);
					String path = entry.getName().substring(0, i);
					AtomTools.log(Log.LOG_LEVEL_INFO, " match found: " + entry.getName() + " --> " + name + " ; " + path, null);
					magicClass(path.replace("/", "."), name);
				}
				else {
					AtomTools.log(Log.LOG_LEVEL_INFO, "non-matching: " + entry.getName(), null);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void magicDirectoryOrFile(File directoryOrFile) {
		if (!directoryOrFile.getName().contains("gwtr")
				&& !directoryOrFile.getName().contains("annotations")) {
			if (!directoryOrFile.isFile())
				magicDirectory(directoryOrFile);
			else
				magicFile(directoryOrFile);
		}
	}

	private static void magicDirectory(File directory) {
		File[] directoryContent = directory.listFiles();
		if (directoryContent != null) {
			for (File directoryOrFile : directoryContent) {
				magicDirectoryOrFile(directoryOrFile);
			}
		}
	}

	private static void magicFile(File file) {
		String fileName = file.getName();
		// we are only interested in .class or .java files and not in the root
		// DomainObject
		if ((fileName.toLowerCase().endsWith(".class") || fileName
				.toLowerCase().endsWith(".java"))
				&& !fileName.startsWith("DomainObject.")) {

			String packagePath = file.getParentFile().getAbsolutePath()
					.replace("\\", ".").replace("/", ".");
			magicClass(packagePath.substring(packagePath.indexOf(domainPackageName)), fileName);
		}
	}
	
	private static void magicClass(String packageString, String nameInclExtension) {
		
		int fileExtensionLength = 0;
		if (nameInclExtension.endsWith(".class"))
			fileExtensionLength = 6;
		else if (nameInclExtension.endsWith(".java"))
			fileExtensionLength = 5;
		else
			throw new AtomException("unknown DomainClass file extension!! -> " + nameInclExtension);

		// removes the extension
		String simpleClassName = nameInclExtension.substring(0, nameInclExtension.length()
				- fileExtensionLength);

		AtomTools.log(Log.LOG_LEVEL_INFO, "processing class "
				+ simpleClassName + " in package " + packageString, null);

		
		try {
			GwtReflect.magicClass(Class.forName(packageString + "." + simpleClassName));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
// Errors GWT compiler:

//	[INFO]          [ERROR] Line 332: No source code is available for type java.util.jar.JarEntry; did you forget to inherit a required module?
//	[INFO]          [ERROR] Line 328: No source code is available for type java.net.JarURLConnection; did you forget to inherit a required module?
//	[INFO]          [ERROR] Line 329: No source code is available for type java.util.jar.JarFile; did you forget to inherit a required module?
//	[INFO]          [ERROR] Line 320: No source code is available for type java.io.File; did you forget to inherit a required module?
