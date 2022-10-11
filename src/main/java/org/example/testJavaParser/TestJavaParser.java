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

    // 这里认为类型相同也算对


    private static String[] split_generic(String name){
        String[] result = new String[2];
        result[0] = name.substring(0, name.indexOf("<"));
        result[1] = name.substring(name.indexOf("<") + 1, name.indexOf(">"));
        return result;
    }

    private static void test1(String[] args){
        TypeSolver typeSolver = new ReflectionTypeSolver();

//        showReferenceTypeDeclaration(typeSolver.solveType("java.lang.Object"));
        showAncestors(typeSolver.solveType("java.util.Queue"));
        showAncestors(typeSolver.solveType("java.util.Vector"));
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

    private static void test3(String[] args){
        String str = "List";
//        String[] result = split_generic(str);
//        System.out.println(result[0]);
//        System.out.println(result[1]);

        System.out.println(remove_generic(str));
    }


    private static boolean is_super(String parent, String child){
        if(parent.equals(child)) return true;
        if(parent.equals("java.lang.Object")||child.equals("java.lang.Object")) return true;
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
    private static String remove_generic(String name){
        try{
            return name.substring(0, name.indexOf("<"));
        }catch(Exception e){
            return name;
        }
    }

    private static boolean isSameType(String a, String b){
        a = remove_generic(a);
        b = remove_generic(b);
        String a1 = "java.lang."+a;
        String a2 = "java.util."+a;
        String b1 = "java.lang."+b;
        String b2 = "java.util."+b;
        return is_super(a1, b1) || is_super(b1, a1) || is_super(a2, b2) || is_super(b2, a2);
    }

    private static void test_one_case(String[] args){
        String a = "Vector";
        String b = "Queue";
        System.out.println(isSameType(a, b));
    }

    public static void main(String[] args) {
//        test1(args);
//        test2(args);
//        test3(args);
//        test_one_case(args);
        test_on_truth(args);
    }

    private static void test_on_truth(String[] args){
        String[] a = {"JsonValue", "JsonArray", "Integer", "Object", "Collection<Integer>", "List<Integer>", "String", "Object", "Collection<Long>", "List<Long>", "ImmutableSet<Integer>", "MutableSet<Integer>", "List<TesterBeanA>", "Object", "Object", "Integer", "Geometry", "Point", "List<SimpleNavigationPosition>", "List<NavigationPosition>", "HyperUniquesAggregatorFactory", "MutableByteList", "ByteArrayList", "String", "OtpErlangObject", "String", "Collection<Long>", "List<Long>", "Collection<Double>", "List<Double>", "XCardOutputProperties", "Map<String, String>", "Object", "Long", "Object", "Date", "Date", "Time", "BigDecimal", "Object", "Object", "JAXBBean", "HasCssName", "IconType", "long", "Object", "MutableShortList", "ShortArrayList", "BadRequestException", "Collection<Double>", "List<Double>", "Iterable<TmfAbstractAnalysisRequirement>", "Set<TmfAbstractAnalysisRequirement>", "Binary", "Term", "ObjectWrapper<IROI>", "ObjectWrapper<?>", "JsonArray", "JsonValue", "JsonObject", "JsonElement", "JsonElement", "JsonObject", "int", "Object", "Binary", "Term", "List<String>", "Object", "Revision", "Object", "Object", "IArchimateElement", "LocalTime", "Object", "Object", "long", "Queue<String>", "Vector<String>", "HashMap<Object, Object>", "Object", "List<MutationDetails>", "Object", "float", "List", "Term", "int", "Object", "String", "Object", "Geometry", "Point", "Node", "SimpleKeywordNode", "BigInteger", "Object", "BigInteger", "Object", "Atom", "Term", "Instant", "Object", "Collection<Long>", "List<Long>", "Collection<Float>", "List<Float>", "int", "Object", "int", "Object", "List<TesterBeanA>", "Object", "Component", "TextComponent", "Collection<Double>", "List<Double>"};
        String[] b = {"JsonArray", "JsonValue", "Object", "Integer", "List<Integer>", "Collection<Integer>", "Object", "String", "List<Long>", "Collection<Long>", "MutableSet<Integer>", "ImmutableSet<Integer>", "Object", "List<TesterBeanA>", "Integer", "Object", "Point", "Geometry", "List<NavigationPosition>", "List<SimpleNavigationPosition>", "AggregatorFactory", "ByteArrayList", "MutableByteList", "Object", "String", "OtpErlangObject", "List<Long>", "Collection<Long>", "List<Double>", "Collection<Double>", "Map<String, String>", "XCardOutputProperties", "Long", "Object", "Date", "Object", "Time", "Date", "Object", "BigDecimal", "JAXBBean", "Object", "IconType", "HasCssName", "Object", "long", "ShortArrayList", "MutableShortList", "CosmoDavException", "List<Double>", "Collection<Double>", "Set<TmfAbstractAnalysisRequirement>", "Iterable<TmfAbstractAnalysisRequirement>", "Term", "Binary", "ObjectWrapper<?>", "ObjectWrapper<IROI>", "JsonValue", "JsonArray", "JsonElement", "JsonObject", "JsonObject", "JsonElement", "Object", "int", "Term", "Binary", "Object", "List<String>", "Object", "Revision", "IArchimateElement", "Object", "Object", "LocalTime", "long", "Object", "Vector<String>", "Queue<String>", "Object", "HashMap<Object, Object>", "Collection<MutationDetails>", "float", "Object", "Term", "List", "Object", "int", "Object", "String", "Point", "Geometry", "SimpleKeywordNode", "Node", "Object", "BigInteger", "Object", "BigInteger", "Term", "Atom", "Object", "Instant", "List<Long>", "Collection<Long>", "List<Float>", "Collection<Float>", "Object", "int", "Object", "int", "Object", "List<TesterBeanA>", "TextComponent", "Component", "List<Double>", "Collection<Double>"};
        int cnt_same = 0;
        int cnt_diff = 0;
        for(int i=0; i<a.length; i++){
//            System.out.println(isSameType(a[i], b[i]));
            if(isSameType(a[i], b[i])){
                cnt_same++;
                System.out.println("same: "+a[i]+" "+b[i]);
            }else{
                cnt_diff++;
                System.out.println("diff: "+a[i]+" "+b[i]);
            }
        }
        System.out.println("same: "+cnt_same);
        System.out.println("diff: "+cnt_diff);
    }
}
