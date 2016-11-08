package symtable;

import ast.node.TopClassDecl;
import java.io.PrintStream;
import symtable.*;

public class ClassSTE extends STE {
    private boolean mMain;
    private String mSuperClass;
    private Scope mScope;
    
    public ClassSTE(String name, boolean mainClass, String superClass) {
        super(name);
        this.mMain = mainClass;
        this.mSuperClass = superClass;
	this.mScope= new Scope(null);
	//this.setMethodSTE(classDecl);
	this.mScope.setScopeName(name); // everything under class is of scope that class
    }


    public String getScopeName(){
	return this.mScope.getScopeName();// Class method scope
    }
    public void setScope(Scope scope) {
        this.mScope = scope;
    }

    public Scope getScope() {
        return this.mScope;
    }


    public void setMethodSTE(MethodSTE method)//TopClassDecl classDecl){
    {	
		mScope.insert(method);	// add method
    }

    public MethodSTE getMethodSTE(String method)//TopClassDecl classDecl){
    {	
		return (MethodSTE)mScope.lookup(method);	// lookup method
    }

    
    public int outputDot(PrintStream printStream, int n) {
        int n2 = n++;
        String string = "\t" + n2 + " [label=\" <f0> ClassSTE " + "| <f1> mName = " + this.mName + "| <f2> mMain = " + this.mMain + "| <f3> mSuperClass = " + this.mSuperClass + "| <f4> mScope \"];";
        printStream.println(string);
        printStream.println("\t" + n2 + ":<f4> -> " + n + ":<f0>;");
        return this.mScope.outputDot(printStream, n);
    }
}
