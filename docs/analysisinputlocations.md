# Analysis Input
i.e. What should be analyzed - an `AnalysisInputLocation` points to code input SootUp can analyze.
We ship multiple Subclasses that can handle different code input.

### Java Runtime
- Java <=8: `DefaultRTJaAnalysisInputLocation` current rt.jar (or point to any rt.jar as its just a usual .jar file)
- Java >=9: `JRTFilesystemAnalysisInputLocation`

If you have errors like Java.lang.String, Java.lang.Object, ... you are most likely missing this AnalysisInput.

### Java Bytecode .class, .jar, .war
- `JavaClassPathAnalysisInputLocation` - its the equivalent of the classpath you would pass to the java executable i.e. point to root(s) of package(s).

### Java Sourcecode .java
- `OTFCompileAnalysisInputLocation` - you can point directly to .java files or pass a String with Java sourcecode, SootUp delegates to the `JavaCompiler` and transform the bytecode from the compiler to Jimple
- `JavaSourcePathInputLocation` [***experimental!***]{Has huge problems with exceptional flow!} - points to a directory that is the root source directory (containing the package directory structure).

### Jimple .jimple
- `JimpleAnalysisInputLocation` - needs a Path to a .jimple file or a directory.

### Android Bytecode .dex
- `ApkAnalysisInputLocation` - currenlty uses dex2jar internally - A SootUp solution to directly generate Jimple is WIP!


### Java cli arguments to configure SootUp
We created a [Utility](tool_setup.md) that parses a String of java command line arguments and configures SootUp respectively.