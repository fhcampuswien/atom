package at.ac.fhcampuswien.atom;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.Document;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.Notifiable;

/**
 * 
 * @author kaefert
 *
 *
 *         more pointers:
 *         http://help.eclipse.org/mars/index.jsp?topic=/org.eclipse.jdt.doc.isv
 *         /reference/api/org/eclipse/jdt/core/dom/AST.html
 *         http://www.vogella.com/tutorials/EclipseJDT/article.html
 *         http://stackoverflow.com/questions/10148802/eclipse-jdt-ast-how-to-
 *         write-generated-ast-to-java-file
 * 
 */
public class AstTest {
	public static void main(String args[]) {
		
		AtomTools.log(Level.INFO, "parseInlineJavaCodeSample", null);
		parseInlineJavaCodeSample();

		AtomTools.log(Level.INFO, "analyseWorkspaceProjects", null);
		analyseWorkspaceProjects();

		AtomTools.log(Level.INFO, "DomainResourceFinder.findResources", null);
		DomainResourceFinder.findResources(true, new Notifiable<String>() {

			@Override
			public void doNotify(String reason) {
				System.out.println(reason);
			}
		});
	}

	/**
	 * adapted sample copied from:
	 * http://www.programcreek.com/2011/01/a-complete-standalone-example-of-astparser/
	 */
	private static void parseInlineJavaCodeSample() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource("public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }".toCharArray());
		// parser.setSource("/*abc*/".toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		// ASTNode node = parser.createAST(null);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			Set<String> names = new HashSet<String>();

			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '" + name + "' at line" + cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue to avoid usage info
			}

			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
					System.out.println("Usage of '" + node + "' at line " + cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}

		});
	}

	/**
	 * adapted sample copied from:
	 * http://www.vogella.com/tutorials/EclipseJDT/article.html#jdt_example
	 */
	private static void analyseWorkspaceProjects() {

		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		// Loop over all projects
		for (IProject project : projects) {
			try {
				printProjectInfo(project);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printProjectInfo(IProject project) throws CoreException, JavaModelException {
		System.out.println("Working in project " + project.getName());
		// check if we have a Java project
		if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			IJavaProject javaProject = JavaCore.create(project);
			printPackageInfos(javaProject);
		}
	}

	private static void printPackageInfos(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packages = javaProject.getPackageFragments();
		for (IPackageFragment mypackage : packages) {
			// Package fragments include all packages in the
			// classpath
			// We will only look at the package from the source
			// folder
			// K_BINARY would include also included JARS, e.g.
			// rt.jar
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				System.out.println("Package " + mypackage.getElementName());
				printICompilationUnitInfo(mypackage);

			}

		}
	}

	private static void printICompilationUnitInfo(IPackageFragment mypackage) throws JavaModelException {
		for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
			printCompilationUnitDetails(unit);

		}
	}

	private static void printIMethods(ICompilationUnit unit) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			printIMethodDetails(type);
		}
	}

	private static void printCompilationUnitDetails(ICompilationUnit unit) throws JavaModelException {
		System.out.println("Source file " + unit.getElementName());
		Document doc = new Document(unit.getSource());
		System.out.println("Has number of lines: " + doc.getNumberOfLines());
		printIMethods(unit);
	}

	private static void printIMethodDetails(IType type) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {

			System.out.println("Method name " + method.getElementName());
			System.out.println("Signature " + method.getSignature());
			System.out.println("Return Type " + method.getReturnType());

		}
	}
}