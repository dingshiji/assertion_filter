package org.example.testJavaParser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class TestJavaParser {

    private static void showReferenceTypeDeclaration(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration){

        System.out.println(String.format("== %s ==",
                resolvedReferenceTypeDeclaration.getQualifiedName()));
        System.out.println(" fields:");
        resolvedReferenceTypeDeclaration.getAllFields().forEach(f ->
                System.out.println(String.format("    %s %s", f.getType(), f.getName())));
        System.out.println(" methods:");
        resolvedReferenceTypeDeclaration.getAllMethods().forEach(m ->
                System.out.println(String.format("    %s", m.getQualifiedSignature())));
        System.out.println();
    }

    private static void showAncestors(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration){
        System.out.println(String.format("== %s ==",
                resolvedReferenceTypeDeclaration.getName()));
        System.out.println(" ancestors:");
        resolvedReferenceTypeDeclaration.getAllAncestors().forEach(a ->
                System.out.println(String.format("    %s", a.getQualifiedName())));

    }

    private static String[] get_fqm(String name){
        String[] fqm = new String[2];
        fqm[0] = "java.lang." + name;
        fqm[1] = "java.util." + name;
        return fqm;
    }

    private static boolean is_super(String parent, String child){
        try{
        TypeSolver typeSolver = new ReflectionTypeSolver();
        ResolvedReferenceTypeDeclaration parent_type = typeSolver.solveType(parent);
        ResolvedReferenceTypeDeclaration child_type = typeSolver.solveType(child);
        for(ResolvedReferenceType ancestor : child_type.getAllAncestors()){
            if(ancestor.getQualifiedName().equals(parent_type.getQualifiedName())){
                return true;
            }
        }}catch(Exception e){
            return false;
        }
        return false;
    }

    private static void test1(String[] args){
        TypeSolver typeSolver = new ReflectionTypeSolver();

//        showReferenceTypeDeclaration(typeSolver.solveType("java.lang.Object"));
        showAncestors(typeSolver.solveType("java.util.Collection"));
//        System.out.println(is_super("java.lang.obj", "java.lang.String"));
//        showReferenceTypeDeclaration(typeSolver.solveType("java.util.List"));
    }

    private static void test2(String[] args){
        String str = "List<Double>";
        StaticJavaParser.parseClassOrInterfaceType(str).resolve().describe();
//        System.out.println(exp.asTypeExpr().getType());
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(str);
        /*
            这里需要补习一下语法知识
         */
    }

    public static void main(String[] args) {
        test2(args);
    }
}
