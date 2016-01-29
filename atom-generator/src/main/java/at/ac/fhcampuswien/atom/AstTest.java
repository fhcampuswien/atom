package at.ac.fhcampuswien.atom;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.Notifiable;

/**
 * 
 * @author kaefert
 *
 * adapted sample copied from:
 * http://www.programcreek.com/2011/01/a-complete-standalone-example-of-astparser/
 *
 * more pointers:
 * http://help.eclipse.org/mars/index.jsp?topic=/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/AST.html
 * http://www.vogella.com/tutorials/EclipseJDT/article.html
 * http://stackoverflow.com/questions/10148802/eclipse-jdt-ast-how-to-write-generated-ast-to-java-file
 * 
 */
public class AstTest {
	public static void main(String args[]){
		
		System.out.println("pre start");
		AtomTools.log(Level.INFO, "starting AstTest main", null);
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource("public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }".toCharArray());
		//parser.setSource("/*abc*/".toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//ASTNode node = parser.createAST(null);
 
 
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
 
		cu.accept(new ASTVisitor() {
 
			Set<String> names = new HashSet<String>();
 
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
				return false; // do not continue to avoid usage info
			}
 
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
				System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
 
		});

		System.out.println("----------------------------------------------");
		System.out.println("----------------------------------------------");
		
		DomainResourceFinder.findResources(true, new Notifiable<String>() {
			
			@Override
			public void doNotify(String reason) {
				System.out.println(reason);
			}
		});
	}
}