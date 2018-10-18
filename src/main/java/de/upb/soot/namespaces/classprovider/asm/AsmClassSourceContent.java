package de.upb.soot.namespaces.classprovider.asm;

public class AsmClassSourceContent extends org.objectweb.asm.tree.ClassNode
    implements de.upb.soot.namespaces.classprovider.ISourceContent {
  @Override
  public de.upb.soot.core.SootClass resolve(de.upb.soot.core.ResolvingLevel level, de.upb.soot.views.IView view) {

    // everything is resolved
    System.out.println(this.access);
    System.out.println(this.methods);
    // create the soot class....
    // FIXME: or a soot module ...
    de.upb.soot.core.SootClass sootClass = new de.upb.soot.core.SootClass(
            null, (de.upb.soot.namespaces.classprovider.AbstractClassSource) null, de.upb.soot.core.Modifier.ABSTRACT);
    for (org.objectweb.asm.tree.MethodNode methodSource : this.methods) {
      de.upb.soot.core.SootMethod sootMethod = new de.upb.soot.core.SootMethod(null, null, null, null);
      sootMethod.setSource((AsmMethodSource) methodSource);
      sootClass.addMethod(sootMethod);
    }

    return null;
  }

  @Override
  public org.objectweb.asm.ModuleVisitor visitModule(String name, int access, String version) {
    // FIXME: do something here??
    return super.visitModule(name, access, version);
  }

  @Override
  public org.objectweb.asm.MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {

    de.upb.soot.namespaces.classprovider.asm.AsmMethodSource mn
        = new AsmMethodSource(null, access, name, desc, signature, exceptions);
    methods.add(mn);
    return mn;
  }
}