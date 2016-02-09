package at.ac.fhcampuswien.atom;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import at.ac.fhcampuswien.atom.shared.AtomTools;

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

		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		AtomTools.log(Level.INFO, "parseInlineJavaCodeSample", null);
		parseInlineJavaCodeSample();

		AtomTools.log(Level.INFO, "loadAndModifySourceOfClientTools", null);
		loadAndImplementDomainReflectionEmulator();

		// AtomTools.log(Level.INFO, "analyseWorkspaceProjects", null);
		// analyseWorkspaceProjects();

		// AtomTools.log(Level.INFO, "DomainResourceFinder.findResources",
		// null);
		// DomainResourceFinder.findResources(true, new Notifiable<String>() {
		//
		// @Override
		// public void doNotify(String reason) {
		// System.out.println(reason);
		// }
		// });
	}

//	private static final String 

	private static void loadAndImplementDomainReflectionEmulator() {
		final String stubPath = "atom-core/src/main/java-stubs/DomainReflectionEmulator.java";
		final String outPath = "atom-core/target/generated-sources/gwt/at/ac/fhcampuswien/atom/shared/DomainReflectionEmulator.java";

		try {
			// "/" = root of classes, outside of packages.
			URL classRootUrl = AtomTools.class.getResource("/");
			String classRootPath = classRootUrl.getPath();
			AtomTools.log(Level.INFO, "got url: " + classRootPath, null);

			URI classRootUri = new URI(classRootPath);
			URI parentProject = classRootUri.resolve("../../..");
			AtomTools.log(Level.INFO, "parent: " + parentProject, null);
			File srcFile = new File(parentProject + stubPath);
			// "atom-core/src/main/java/at/ac/fhcampuswien/atom/shared/AtomTools.java");
			AtomTools.log(Level.INFO, "atomToolsSrc: " + srcFile, null);
			
			byte[] encoded = Files.readAllBytes(srcFile.toPath());
			String srcContent = new String(encoded, StandardCharsets.UTF_8);
			Document document = new Document(srcContent);
			// AtomTools.log(Level.INFO, "atomToolsSrc content = " + srcContent,
			// null);

			// http://stackoverflow.com/questions/13453811/eclipse-ast-variable-binding-on-standalone-java-application
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(srcContent.toCharArray());

			// parser.setKind(ASTParser.K_COMPILATION_UNIT);
			// //parser.setEnvironment(classpath, new String[] { rootDir }, new
			// String[] { "UTF8" }, true);
			// parser.setResolveBindings(true);
			// parser.setBindingsRecovery(true);
			CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
			AST ast = astRoot.getAST();
			ASTRewrite rewriter = ASTRewrite.create(ast);
			
			
			//////// http://www.programcreek.com/2012/06/insertadd-statements-to-java-source-code-by-using-eclipse-jdt-astrewrite/
			// for getting insertion position
			TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
			for( MethodDeclaration methodDecl : typeDecl.getMethods()) {
				if("getAttributeValue".equals(methodDecl.getName().toString())) {
					Block block = methodDecl.getBody();
//					block.statements()
					ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
					ReturnStatement returnStatement = ast.newReturnStatement();
					returnStatement.setExpression(ast.newNullLiteral());
					listRewrite.insertLast(returnStatement, null);
				}
				else if("setAttributeValue".equals(methodDecl.getName().toString())) {
					
				}
			}
			//Block block = methodDecl.getBody();
	 
			// create new statements for insertion
			MethodInvocation newInvocation = ast.newMethodInvocation();
			newInvocation.setName(ast.newSimpleName("add"));
			Statement newStatement = ast.newExpressionStatement(newInvocation);
	 
			//create ListRewrite
//			ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
//			listRewrite.insertFirst(newStatement, null);

			
		    //////// sample from ASTRewrite javadoc
//			ImportDeclaration id = ast.newImportDeclaration();
//			id.setName(ast.newName(new String[] { "java", "util", "RegularEnumSet" }));
//			TypeDeclaration td = (TypeDeclaration) astRoot.types().get(0);
//			ITrackedNodePosition tdLocation = rewriter.track(td);
//			ListRewrite lrw = rewriter.getListRewrite(astRoot, CompilationUnit.IMPORTS_PROPERTY);
//			lrw.insertLast(id, null);

			// http://stackoverflow.com/questions/10148802/eclipse-jdt-ast-how-to-write-generated-ast-to-java-file
			TextEdit edits = rewriter.rewriteAST(document, null);
			edits.apply(document);
			AtomTools.log(Level.INFO, "result: " + document.get(), null);
			
			PrintWriter out = new PrintWriter(parentProject + outPath);
			out.print(document.get());
			out.close();

			// RewriteEventStore s;
			// ASTRewriteFlattener f;

			// astRoot.recordModifications();
			//
			// astRoot.accept(new ASTVisitor() {
			//
			// @Override
			// public void endVisit(VariableDeclarationFragment node) {
			// AtomTools.log(Level.INFO, "endVisit node = " + node.toString(),
			// null);
			// super.endVisit(node);
			// }
			// });

			// AST ast = CompilationUnit.getAST();

		} catch (URISyntaxException e) {
			AtomTools.log(Level.SEVERE, "URISyntaxException!", null, e);
		} catch (IOException e) {
			AtomTools.log(Level.SEVERE, "IOException!", null, e);
			// } catch (JavaModelException e) {
			// AtomTools.log(Level.SEVERE, "JavaModelException!", null, e);
		} catch (IllegalArgumentException e) {
			AtomTools.log(Level.SEVERE, "IllegalArgumentE	xception!", null, e);
		} catch (MalformedTreeException e) {
			AtomTools.log(Level.SEVERE, "MalformedTreeException!", null, e);
		} catch (BadLocationException e) {
			AtomTools.log(Level.SEVERE, "BadLocationException!", null, e);
		}

		// File file = new File( classRootUrl.getPath( ) );
		// String parentProjectDir = file.getParent();
		// URI parent = classRootUrl.getPath().endsWith("/") ? uri.resolve("..")
		// : uri.resolve(".")

	}

	/**
	 * adapted sample copied from:
	 * http://www.programcreek.com/2011/01/a-complete-standalone-example-of-
	 * astparser/
	 */
	private static void parseInlineJavaCodeSample() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
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