package de.upb.swt.soot.test.java.bytecode.minimaltestsuite.java6;

import categories.Java8Test;
import de.upb.swt.soot.core.model.SootClass;
import de.upb.swt.soot.core.model.SootMethod;
import de.upb.swt.soot.core.signatures.MethodSignature;
import de.upb.swt.soot.test.java.bytecode.minimaltestsuite.MinimalBytecodeTestSuiteBase;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** @author Kaustubh Kelkar */
@Category(Java8Test.class)
public class DeclareInnerClassTest extends MinimalBytecodeTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        "methodDisplayOuter", getDeclaredClassSignature(), "void", Collections.emptyList());
  }

  public MethodSignature getInnerMethodSignature() {
    return identifierFactory.getMethodSignature(
        "methodDisplayInner", getDeclaredClassSignature(), "void", Collections.emptyList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    //        loadMethod(expectedBodyStmts1(), getStaticMethodSignature());
    //        SootMethod staticMethod = loadMethod(expectedBodyStmts1(),
    // getStaticMethodSignature());
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    SootMethod sootMethod = loadMethod(getMethodSignature());
    assertJimpleStmts(sootMethod, expectedBodyStmts());
    System.out.println(getInnerMethodSignature());
    /**
     * SootMethod sootMethod1= loadMethod(getInnerMethodSignature()); //java.lang.AssertionError: No
     * matching method signature found assertJimpleStmts(sootMethod1,expectedInnerClassBodyStmts());
     */
    /** TODO check for inner class inside method body */
    // assertTrue(sootClass.getFields().stream().allMatch(sootField -> sootField.getModifiers().));
  }

  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: DeclareInnerClass",
            "$stack1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack1.<java.io.PrintStream: void println(java.lang.String)>(\"methodDisplayOuter\")",
            "return")
        .collect(Collectors.toList());
  }

  public List<String> expectedInnerClassBodyStmts() {
    return Stream.of(
            "l0 := @this: DeclareInnerClass$InnerClass",
            "$stack1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack1.<java.io.PrintStream: void println(java.lang.String)>(\"methodDisplayOuter\")",
            "return")
        .collect(Collectors.toList());
  }
}