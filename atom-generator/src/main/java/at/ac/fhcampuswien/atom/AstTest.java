package at.ac.fhcampuswien.atom;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import at.ac.fhcampuswien.atom.server.DomainAnalyzer;
import at.ac.fhcampuswien.atom.shared.AtomTools;
import at.ac.fhcampuswien.atom.shared.DomainClass;
import at.ac.fhcampuswien.atom.shared.DomainClassAttribute;
import at.ac.fhcampuswien.atom.shared.exceptions.AtomException;
import net.lightoze.gwt.i18n.server.LocaleProxy;

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
		LocaleProxy.initialize();
		
		adaptLogger();
		loadAndImplementDomainReflectionEmulator();
	}
	
	private enum GenerationPhase {
		get, set, make;
	}
	
	/**
	 * http://stackoverflow.com/questions/6315699/why-are-the-level-fine-logging-messages-not-showing
	 */
	private static void adaptLogger() {
		AtomTools.log(Level.INFO, "will set log level now", null);
		Logger applog = Logger.getLogger("atom");

		// Create and set handler
		Handler systemOut = new ConsoleHandler();
		systemOut.setLevel( Level.FINER );
		applog.addHandler( systemOut );
		applog.setLevel( Level.FINER );

		// Prevent logs from processed by default Console handler.
		applog.setUseParentHandlers( false ); // Solution 1
		Logger.getLogger("").setLevel( Level.OFF ); // Solution 2
		
		AtomTools.log(Level.INFO, "have set log level", null);
	}

	private static void loadAndImplementDomainReflectionEmulator() {
		// final String relStubPath = "atom-core/src/main/java-stubs/DomainReflectionEmulator.java";
		// final String relOutPath = "atom-domain/target/generated-sources/ast/at/ac/fhcampuswien/atom/shared/DomainReflectionEmulator.java";
		// target-survives-clean
		// "atom-core/src/main/java/at/ac/fhcampuswien/atom/shared/AtomTools.java");
		
		final String relStubPath = "atom-domain/src/main/java-stubs/DomainReflectionEmulator.java";
		final String relOutPath = "atom-generator/target-survives-clean/DomainReflectionEmulator.java";
		final String relOutPath2 = "atom-domain/target/generated-sources/ast/at/ac/fhcampuswien/atom/shared/DomainReflectionEmulator.java";

		try {
			// String workdir = System.getProperty("user.dir");
			
			// "/" = root of classes, outside of packages.
			URI classRootUri = new URI(AstTest.class.getResource("/").getPath());
			URI parentProject = classRootUri.resolve("../../..");
			File srcFile = new File(parentProject + relStubPath);

			AtomTools.log(Level.FINER, "classRootUri: " + classRootUri, null);
			AtomTools.log(Level.FINER, "parentProject: " + parentProject, null);
			AtomTools.log(Level.FINER, "DomainReflectionEmulator stub: " + srcFile, null);
			
			String stubContent = new String(Files.readAllBytes(srcFile.toPath()), StandardCharsets.UTF_8);
			AtomTools.log(Level.FINEST, "stubContent content = " + stubContent, null);

			// http://stackoverflow.com/questions/13453811/eclipse-ast-variable-binding-on-standalone-java-application
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(stubContent.toCharArray());

			// parser.setKind(ASTParser.K_COMPILATION_UNIT);
			// parser.setEnvironment(classpath, new String[] { rootDir }, new String[] { "UTF8" }, true);
			// parser.setResolveBindings(true);
			// parser.setBindingsRecovery(true);
			
			CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
			AST ast = astRoot.getAST();
			ASTRewrite rewriter = ASTRewrite.create(ast);
			
			DomainClass domainTree = DomainAnalyzer.getDomainTree();
			
			//////// http://www.programcreek.com/2012/06/insertadd-statements-to-java-source-code-by-using-eclipse-jdt-astrewrite/
			TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
			for( MethodDeclaration methodDecl : typeDecl.getMethods()) {
				AtomTools.log(Level.FINEST, "found method: " + methodDecl.getName(), null);
				Block block = methodDecl.getBody();
				ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				
				if("getAttributeValue".equals(methodDecl.getName().toString())) {
					addCodeForClassTree(domainTree, ast, listRewrite, true);
					
					//// results in: throw AtomException().new MISSING();
//					SimpleName atomExceptionName = ast.newSimpleName(AtomException.class.getSimpleName());
//					MethodInvocation methodInvocation = ast.newMethodInvocation();
//					methodInvocation.setName(atomExceptionName);
//					classInstanceCreation.setExpression(methodInvocation);
					
					listRewrite.insertLast(getNotFoundExceptionThrow(ast, GenerationPhase.get), null);
					
//					ReturnStatement returnStatement = ast.newReturnStatement();
//					returnStatement.setExpression(ast.newNullLiteral());
//					listRewrite.insertLast(returnStatement, null);
				}
				else if("setAttributeValue".equals(methodDecl.getName().toString())) {
					addCodeForClassTree(domainTree, ast, listRewrite, false);
					listRewrite.insertLast(getNotFoundExceptionThrow(ast, GenerationPhase.set), null);
				}
				else if("makeInstance".equals(methodDecl.getName().toString())) {
					addInvocationsForClassTree(domainTree, ast, listRewrite);
					listRewrite.insertLast(getNotFoundExceptionThrow(ast, GenerationPhase.make), null);
				}
			}
			//Block block = methodDecl.getBody();
	 
			// create new statements for insertion
//			MethodInvocation newInvocation = ast.newMethodInvocation();
//			newInvocation.setName(ast.newSimpleName("add"));
//			Statement newStatement = ast.newExpressionStatement(newInvocation);
	 
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
			Document document = new Document(stubContent);
			TextEdit edits = rewriter.rewriteAST(document, null);
			edits.apply(document);
			AtomTools.log(Level.FINEST, "result: " + document.get(), null);
			
			File file = new File(parentProject + relOutPath);
			file.getParentFile().mkdirs();
			PrintWriter out = new PrintWriter(file);
			
			out.print(document.get());
			out.close();
			
			file = new File(parentProject + relOutPath2);
			file.getParentFile().mkdirs();
			out = new PrintWriter(file);
			
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
			AtomTools.log(Level.SEVERE, "IllegalArgumentException!", null, e);
		} catch (MalformedTreeException e) {
			AtomTools.log(Level.SEVERE, "MalformedTreeException!", null, e);
		} catch (BadLocationException e) {
			AtomTools.log(Level.SEVERE, "BadLocationException!", null, e);
		}
	}
	
	private static void addInvocationsForClassTree(DomainClass currentClass, AST ast, ListRewrite listRewrite) {
		if(!currentClass.getIsAbstract() && currentClass.getHasPublicEmptyConstructor()) {
			IfStatement ifClass = ast.newIfStatement();
			SimpleName simpleName = ast.newSimpleName("className");
			MethodInvocation equals = ast.newMethodInvocation();
			equals.setName(ast.newSimpleName("equals"));
			StringLiteral stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(currentClass.getName());
			equals.setExpression(stringLiteral);
			equals.arguments().add(simpleName);
			
			ReturnStatement returnStatement = ast.newReturnStatement();
			ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
			Name name = ast.newName(currentClass.getName());
			SimpleType atomExceptionType = ast.newSimpleType(name);
			classInstanceCreation.setType(atomExceptionType);
			returnStatement.setExpression(classInstanceCreation);
			
			ifClass.setThenStatement(returnStatement);		
			
			listRewrite.insertLast(ifClass, null);
			
			ifClass.setExpression(equals);
		}
		
		for(DomainClass subClass : currentClass.getSubClasses()) {
			addInvocationsForClassTree(subClass, ast, listRewrite);
		}
	}
	
	private static void addCodeForClassTree(DomainClass currentClass, AST ast, ListRewrite listRewrite, boolean get) {
		IfStatement ifClass;
		SimpleName simpleName;
		MethodInvocation equals;
		StringLiteral stringLiteral;
		Block classBlock = ast.newBlock();
		
		HashMap<String, DomainClassAttribute> allAttributes = currentClass.getAllAttributes();
		for (DomainClassAttribute attribute : allAttributes.values()) {
			if(get && !attribute.isReadAble())
				continue;
			if(!get && !attribute.isWriteAble())
				continue;
			
			IfStatement ifAttribute = ast.newIfStatement();
			simpleName = ast.newSimpleName("attributeName");
			equals = ast.newMethodInvocation();
			equals.setName(ast.newSimpleName("equals"));
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(attribute.getName());
			equals.setExpression(stringLiteral);
			equals.arguments().add(simpleName);
			ifAttribute.setExpression(equals);
			

			CastExpression castExpression = ast.newCastExpression();
			simpleName = ast.newSimpleName("domainObject");
			castExpression.setExpression(simpleName);

			Name name = ast.newName(currentClass.getName());
			SimpleType simpleType = ast.newSimpleType(name);
			castExpression.setType(simpleType);
			
			ParenthesizedExpression parenthesizedExpression = ast.newParenthesizedExpression();
			parenthesizedExpression.setExpression(castExpression);
			
			if(get) {
				ReturnStatement returnStatement = ast.newReturnStatement();
				MethodInvocation methodInvocation = ast.newMethodInvocation();
				simpleName = ast.newSimpleName("get" + AtomTools.upperFirstChar(attribute.getName()));
				methodInvocation.setName(simpleName);
				methodInvocation.setExpression(parenthesizedExpression);
				returnStatement.setExpression(methodInvocation);
				ifAttribute.setThenStatement(returnStatement);
			}
			else {
				Block block = ast.newBlock();
				
				MethodInvocation methodInvocation = ast.newMethodInvocation();
				simpleName = ast.newSimpleName("set" + AtomTools.upperFirstChar(attribute.getName()));
				methodInvocation.setName(simpleName);
				methodInvocation.setExpression(parenthesizedExpression);
				
				castExpression = ast.newCastExpression();
				simpleName = ast.newSimpleName("value");
				castExpression.setExpression(simpleName);
				
				String attributeType = attribute.getType();
				if(attributeType.contains("<")) {
					attributeType = attributeType.substring(0,attributeType.indexOf('<'));
					//need to deal with parametized type
					AtomTools.log(Level.WARNING, "found parameterized type: " + attribute.getType(), null);
				}
				name = ast.newName(attributeType);
				simpleType = ast.newSimpleType(name);
				castExpression.setType(simpleType);

				methodInvocation.arguments().add(castExpression);
				block.statements().add(ast.newExpressionStatement(methodInvocation));
				block.statements().add(ast.newReturnStatement());
				
				ifAttribute.setThenStatement(block);
				//ast.newExpressionStatement(methodInvocation)
			}
			
			classBlock.statements().add(ifAttribute);
		}
		
		if(!classBlock.statements().isEmpty()) {
			ifClass = ast.newIfStatement();
			simpleName = ast.newSimpleName("className");
			equals = ast.newMethodInvocation();
			equals.setName(ast.newSimpleName("equals"));
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(currentClass.getName());
			equals.setExpression(stringLiteral);
			equals.arguments().add(simpleName);
			ifClass.setExpression(equals);
			
			ifClass.setThenStatement(classBlock);		
			listRewrite.insertLast(ifClass, null);
		}
		
		for(DomainClass subClass : currentClass.getSubClasses()) {
			addCodeForClassTree(subClass, ast, listRewrite, get);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Statement getNotFoundExceptionThrow(AST ast, GenerationPhase get) {

		ThrowStatement throwStatement = ast.newThrowStatement();
		ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation();
		Name atomExceptionName = ast.newName(AtomException.class.getSimpleName());
		SimpleType atomExceptionType = ast.newSimpleType(atomExceptionName);
		classInstanceCreation.setType(atomExceptionType);
		
		StringLiteral stringLiteral;
		InfixExpression infixExpression, infixExpression2;
		
		SimpleName localClassName = ast.newSimpleName("className");
		
		if(get == GenerationPhase.get || get == GenerationPhase.set) {
			SimpleName localAttributeName = ast.newSimpleName("attributeName");
			
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue("Attribute ");
			
			infixExpression = ast.newInfixExpression();
			infixExpression.setOperator(InfixExpression.Operator.PLUS);
			infixExpression.setLeftOperand(stringLiteral);
			infixExpression.setRightOperand(localClassName);
			
			infixExpression2 = ast.newInfixExpression();
			infixExpression2.setOperator(InfixExpression.Operator.PLUS);
			infixExpression2.setLeftOperand(infixExpression);
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(".");
			infixExpression2.setRightOperand(stringLiteral);
			
			infixExpression = ast.newInfixExpression();
			infixExpression.setOperator(InfixExpression.Operator.PLUS);
			infixExpression.setLeftOperand(infixExpression2);
			infixExpression.setRightOperand(localAttributeName);

			infixExpression2 = ast.newInfixExpression();
			infixExpression2.setOperator(InfixExpression.Operator.PLUS);
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(" not found. Could not " + get.toString() + " value!");
			infixExpression2.setLeftOperand(infixExpression);
			infixExpression2.setRightOperand(stringLiteral);

			classInstanceCreation.arguments().add(infixExpression2);
		}
		else {
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue("DomainClass ");
			infixExpression = ast.newInfixExpression();
			infixExpression.setOperator(InfixExpression.Operator.PLUS);
			infixExpression.setLeftOperand(stringLiteral);
			infixExpression.setRightOperand(localClassName);

			infixExpression2 = ast.newInfixExpression();
			infixExpression2.setOperator(InfixExpression.Operator.PLUS);
			stringLiteral = ast.newStringLiteral();
			stringLiteral.setLiteralValue(" not found. Could not create instance!");
			infixExpression2.setLeftOperand(infixExpression);
			infixExpression2.setRightOperand(stringLiteral);
			
			classInstanceCreation.arguments().add(infixExpression2);
		}
		
		throwStatement.setExpression(classInstanceCreation);
		
		return throwStatement;
	}
}