package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;

import sun.reflect.annotation.AnnotationParser;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.CommentsParser;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class TestCasesTemplate {

	
	private static String getClassName(String srcName) {

		StringBuilder sb = new StringBuilder();
		String className = null;
		for (int i = srcName.length() - 1; i >= 0; i--) {
			if (srcName.charAt(i) != '\\') {
				sb.append(srcName.charAt(i));
			} else {
				break;
			}
		}
		className = sb.reverse().toString();
		String[] names = className.split("\\.");
		className = names[0].toString();
		return className;
	}
	
	private static String getMethodName(String methodDeclaration, boolean lowercase) {

		String methodName = null;
		StringBuilder sb = new StringBuilder();
		for (int i = methodDeclaration.indexOf("("); i >= 0; i--) {
			if (methodDeclaration.charAt(i) != ' ') {
				sb.append(methodDeclaration.charAt(i));
			} else {
				break;
			}
		}
		sb.reverse();
		sb.setLength(sb.length() - 1);
		methodName = sb.toString();
		if(!lowercase)
			methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
		return methodName;
	}
	private static String getMethodName(String methodDeclaration) {
		return getMethodName(methodDeclaration, false) ;
	}
	
	public static CompilationUnit createTestTemplate(ClassDetailVO classDetailObj, String packageName) throws Exception{
		
		CompilationUnit cu = new CompilationUnit();
        // set the package
        cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(packageName)));

        // create the type declaration
		List<ImportDeclaration> idList = new ArrayList<ImportDeclaration>();
        ImportDeclaration id = new ImportDeclaration();
        id.setName(new NameExpr("java.util.ArrayList"));
        idList.add(id);
        ImportDeclaration id1 = new ImportDeclaration();
        id1.setName(new NameExpr("java.util.*"));
        idList.add(id1);
        id1 = new ImportDeclaration();
        id1.setName(new NameExpr("static org.junit.Assert.*"));
        idList.add(id1);
        cu.setImports(idList);
        id1 = new ImportDeclaration();
        id1.setName(new NameExpr("org.junit.Test"));
        idList.add(id1);
        cu.setImports(idList);
        System.out.println("****************************************************");
        System.out.println(cu);
        System.out.println("****************************************************");
        ClassOrInterfaceDeclaration type = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, getClassName(classDetailObj.getTestFileName()));
        
        Comment comment = ((new CommentsParser().parse("//@Test") ).getAll()).get(0);
        
        ASTHelper.addTypeDeclaration(cu, type);
       
        Map<String, ArrayList<String>> methodListMap = classDetailObj.getMethodList();
        
        for (Entry<String, ArrayList<String>> entry : methodListMap.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            
            MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC, ASTHelper.VOID_TYPE, "testSuccess" + getMethodName(entry.getKey()));
            method.setModifiers(ModifierSet.addModifier(method.getModifiers(), 0));
            ASTHelper.addMember(type, method);
           //  method.setAnnotations(anExprList);
            method.setComment(comment);
            System.out.println("Test Veri: " + method.getAnnotations() ) ;
            
            BlockStmt block = new BlockStmt();
            method.setBody(block);
            NameExpr clazz = new NameExpr("System");
            FieldAccessExpr field = new FieldAccessExpr(clazz, "out");
            MethodCallExpr call = new MethodCallExpr(field, "println");
            ASTHelper.addArgument(call, new StringLiteralExpr(getMethodName(entry.getKey())));
            ASTHelper.addStmt(block, call);
            
            
            NameExpr expr2 = ASTHelper.createNameExpr(getClassName(classDetailObj.getSrcFileName()) + " instance = new " + getClassName(classDetailObj.getSrcFileName()) + "()");
           
           
            String newExp = "";
            List<String> varNames = entry.getValue();
            List<NameExpr> nameExprList = new ArrayList<NameExpr>();
            List<String> exprParamList = new ArrayList<String>();
            for(String varObj: varNames){
            	String[] names = varObj.split(" ");
                if(names.length>=3){
                    if(names[0].startsWith("Map")){
                        NameExpr expr = ASTHelper.createNameExpr("Map " + names[2] + " = " + getDefaultValue(names[0]));
                        nameExprList.add(expr);
                        exprParamList.add(names[2]);
                    }
                }else{
            	NameExpr expr = ASTHelper.createNameExpr(names[0] + " " + names[1] + " = " + getDefaultValue(names[0]));
            	nameExprList.add(expr);
            	exprParamList.add(names[1]);
                }
            }
            
            String tempArgValue=exprParamList.toString().replace(" ", "");
            String argValue=tempArgValue.substring(1, tempArgValue.length()-1);
            System.out.println(argValue);
            
            for (NameExpr expr: nameExprList){
            	ASTHelper.addStmt(block, expr);
            }
            String newString = entry.getKey() ;
   
            String returnType=newString.substring(0, newString.indexOf('(')).trim();
            String[] tempReturnType=returnType.split(" ", newString.indexOf('('));
            int length=tempReturnType.length;
            String print_str = tempReturnType[length-2] ;
    
            System.out.println(length);
            System.out.println(tempReturnType[0]);
            System.out.println(tempReturnType[1]);
            System.out.println(tempReturnType[length-2]); 
    		
            System.out.println(print_str);
            Object defValue= getDefaultValue(print_str);
            NameExpr expr3;
            NameExpr expr4;
            if(tempReturnType[1].toString().startsWith("Map")){
            expr3 = ASTHelper.createNameExpr("Map "  + "expresult = " + defValue);
            expr4 = ASTHelper.createNameExpr("Map "  + "result = instance." + getMethodName(entry.getKey(), true)+"(" + argValue + ")");
            }else{
            expr3 = ASTHelper.createNameExpr(print_str+ " expresult = " + defValue);
            expr4 = ASTHelper.createNameExpr(print_str+ " result = instance." + getMethodName(entry.getKey(), true)+"(" + argValue + ")");
            }
            NameExpr expr5 = ASTHelper.createNameExpr("assertEquals(expresult, result)");
            NameExpr expr6 = ASTHelper.createNameExpr("//TODO review the generated test code and remove the default call to fail.");
            NameExpr expr7 = ASTHelper.createNameExpr("fail(\"The test case is a prototype.\") ");
            
            ASTHelper.addStmt(block, expr2);
            ASTHelper.addStmt(block, expr3);
            ASTHelper.addStmt(block, expr4);
            ASTHelper.addStmt(block, expr5);
            ASTHelper.addStmt(block, expr6);
            ASTHelper.addStmt(block, expr7);
            
            
        }
        
        System.out.println(cu.toString());
		
		return cu;
	
	}
	/**
     * creates the Junit Test Cases Template
     * @param classDetailObj 
     */
    public CompilationUnit createTemplate(List<ClassDetailVO> classDetailObj) {
    	
    	
    	
    	CompilationUnit cu = new CompilationUnit(); 
    	Map<String, ArrayList<String>> testClassTemplate = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> sampleList = new ArrayList<String>();
    	System.out.println(classDetailObj.getClass().getName()+ "======+++++++");classDetailObj.getClass().getName();
    	for(ClassDetailVO obj: classDetailObj){
    		
    		System.out.println(getClassName(obj.getSrcFileName())+ "======Src File Name\n");
    		System.out.println(getClassName(obj.getTestFileName())+ "======TestFileName\n");
    		System.out.println(obj.getMethodList()+ "======MethodList\n");
    		try {
    			cu = createTestTemplate(obj,  "");
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		
    		}
    
        return cu;
    }
    
    public static Object getDefaultValue(String dataType){
    	
    	Object defValue = null;
    	if(dataType.equals("int")){
    		defValue = 0;
    	}else if(dataType.equalsIgnoreCase("long")){
    		defValue = 0L;
    	}else if(dataType.equalsIgnoreCase("float")){
    		defValue = 0;
    	}else if(dataType.equals("double")){
    		defValue = 0.0d;
    	}else if(dataType.equals("String")){
    		defValue = null;
    	}else if(dataType.equals("List")){
    		defValue = null;
    	}else if(dataType.startsWith("Map")){
			defValue = null;
		}else if(dataType.startsWith("HashMap")){
			HashMap obj = null;
		}else if(dataType.startsWith("TreeMap")){
			TreeMap obj = null;
		}else if(dataType.startsWith("LinkedHashMap")){
			LinkedHashMap obj = null;
		}else if(dataType.startsWith("ArrayList")){
			HashMap obj = null;
		}
    	
		return defValue;
    
    }

}
