package symtable;
import java.util.*;
import ast.node.*;
import ast.visitor.*;

import exceptions.InternalException;

/** 

 */
public class BuildSymTable extends DepthFirstVisitor   {
	private SymTable symTable; 
	private String currentClass;
	private boolean firstpass;
  private static final Type BOOL = Type.BOOL;
  private static final Type INT = Type.INT;
  private static final Type BYTE = Type.BYTE;
  private static final Type COLOR = Type.COLOR;
  private static final Type BUTTON = Type.BUTTON;
  private static final Type VOID = Type.VOID; 
  private static final Type TONE = Type.TONE;

    public BuildSymTable() {
	this.symTable= new SymTable();
	this.currentClass="";
	this.firstpass=true;
    }
    public boolean getFirstPass()
	{ return this.firstpass;}

 public Type getType(IType node){
   if(node instanceof BoolType){
	return Type.BOOL;
   }
   if(node instanceof IntType)
return Type.INT;

if(node instanceof ByteType)
return Type.BYTE;

if(node instanceof ColorType)
return Type.COLOR;

if(node instanceof ButtonType)
return Type.BUTTON;


if(node instanceof ToneType)
 return Type.TONE;

return Type.VOID;
 }
    
  @Override 
   public void inPlusExp(PlusExp node)
    {
	checkExp(node.getLExp());
	checkExp(node.getRExp());
	System.out.println("plus exp");

    }

  @Override 
   public void inMinusExp(MinusExp node)
    {
	checkExp(node.getLExp());
	checkExp(node.getRExp());
	System.out.println("minus exp");
    }

  @Override 
   public void inMulExp(MulExp node)
    {
	checkExp(node.getLExp());
	checkExp(node.getRExp());
	System.out.println("mul exp");
    }

  @Override 
   public void inAndExp(AndExp node)
    {
	System.out.println("and exp");
	
	checkExp(node.getLExp());
	checkExp(node.getRExp());
    }

  @Override 
   public void inLtExp(LtExp node)
    {
	
	checkExp(node.getLExp());
	checkExp(node.getRExp());
	System.out.println("lt exp");
    }

  @Override 
   public void inNotExp(NotExp node)
    {
	
	checkExp(node.getExp());
	System.out.println("not exp");
    }


 @Override 
   public void inTrueExp(TrueLiteral node)
    {
	
	checkExp(node);
	System.out.println("true exp");
    }

 @Override 
   public void inFalseExp(FalseLiteral node)
    {
	
	checkExp(node);
	System.out.println("false exp");
    }

 @Override
  public void inMeggyCheckButton(MeggyCheckButton node)
    {
        checkExp(node);
    }

 @Override
  public void inColorExp(ColorLiteral node)
    {
        checkExp(node);
    }

 @Override
  public void inIntegerExp(IntLiteral node)
    {
        checkExp(node);
    }

 @Override
  public void inButtonExp(ButtonLiteral node)
    {
        checkExp(node);
    }

   public void inByteCast(ByteCast node)
    {
        checkExp(node.getExp());
    }


   public SymTable getSymTable()
	{
	return this.symTable;
	}


   public void checkExp(IExp exp){
	
	if(!this.getFirstPass())
{

	switch(exp.getClass().getSimpleName()){ 

		case "IntLiteral":
		symTable.setExpType(exp, INT);
		break;

		case "ButtonLiteral" :
		symTable.setExpType(exp, BUTTON);
		break;

		case "TrueLiteral": case "FalseLiteral":
		symTable.setExpType(exp, BOOL);
		break;

		case "ColorLiteral":
		symTable.setExpType(exp, COLOR);
		break;


	}
}

   
	}
/*
 */
	public void inMethodDecl(MethodDecl methodDecl) {


System.out.println("CurrentClass is "+this.currentClass);
       
	if(this.getFirstPass())
{

	System.out.println("First pass "+methodDecl.getName());
        MethodSTE methodSTE;
        Formal formal;
        Iterator iterator = methodDecl.getFormals().iterator();
        LinkedList<Type> linkedList = new LinkedList<Type>();
        while (iterator.hasNext()) {
            formal = (Formal)iterator.next();
            linkedList.add(this.getType(formal.getType()));
        }//get all formal parameters
        Signature sig = new Signature(this.getType(methodDecl.getType()), linkedList);//signature
        if (!this.isDuplicate(methodDecl.getName(), methodDecl.getLine(), methodDecl.getPos())) {
            methodSTE = new MethodSTE(methodDecl.getName(), sig, this.currentClass+ methodDecl.getName());
        //   System.out.println(this.currentClass);
	//ClassSTE ste= (ClassSTE) this.symTable.lookup(this.currentClass);
	//ste.setMethodSTE(methodSTE);
	}else{
	    methodSTE = new MethodSTE(methodDecl.getName()+"*", sig, this.currentClass+ methodDecl.getName()+"*");
		//create duplicate methodSTE
	}


       this.symTable.insert(methodSTE);
System.out.println("MethodSTE "+methodSTE);
       
        methodSTE.setVarSTE("this", Type.getClassType(this.currentClass),1);
        int i=0;
	for (Formal formal2 : methodDecl.getFormals()) {
          methodSTE.setVarSTE(formal2.getName(), this.getType(formal2.getType()), 3+i);
          i++;
	} }

else{

	System.out.println("Second pass "+methodDecl.getName());
	System.out.println("Scope "+this.symTable.getCurrentScope().getScopeName());
	this.symTable.pushScope( methodDecl.getName());
        
        	}

    }


    public void outMethodDecl(MethodDecl methodDecl) {
	if(!this.getFirstPass())
        	this.symTable.popScope();
        
    }

    public void inTopClassDecl(TopClassDecl topClassDecl) {

	
if(this.getFirstPass())
{

	System.out.println("First pass "+topClassDecl.getName());

        if(!this.isDuplicate(topClassDecl.getName(), topClassDecl.getLine(), topClassDecl.getPos())){ 
	        this.currentClass = topClassDecl.getName();
       	}else{			// if duplicate class is found add duplicare to it
		//System.exit(0);
		this.currentClass = topClassDecl.getName()+"**"; 
	}

  	this.symTable.insert(new ClassSTE(this.currentClass, false, null) );
	this.symTable.pushScope(this.currentClass);//inside top class
}
else{
	System.out.println("Second pass "+topClassDecl.getName());
	this.currentClass = topClassDecl.getName();
	this.symTable.pushScope(this.currentClass);//inside top class	
}

    }

    public void outTopClassDecl(TopClassDecl topClassDecl) {

//	if(!this.getFirstPass())
        this.symTable.popScope();
    }

    public void inMainClass(MainClass mainClass) {
System.out.println("CurrentClass is "+this.currentClass);
       
       if(this.getFirstPass())	
{	
	System.out.println("First pass "+mainClass.getName());

 if(!this.isDuplicate(mainClass.getName(), mainClass.getLine(), mainClass.getPos())){ 
	        this.currentClass = mainClass.getName();
       	}else{			// if duplicate class is found add duplicare to it
		//System.exit(0);
		this.currentClass = mainClass.getName()+"**"; 
	}
  	this.symTable.insert(new ClassSTE(this.currentClass, true, null) );
		this.symTable.pushScope(mainClass.getName());//inside main class	

	}else{
		this.currentClass = mainClass.getName();
		this.symTable.pushScope(mainClass.getName());//inside main class	
	}

    }

    public void outMainClass(MainClass mainClass) {
//	if(!this.getFirstPass())	
        	this.symTable.popScope();
    } 

    public void inCallStatement(CallStatement callStatement){
	if(!this.getFirstPass()){

	System.out.println("Second pass "+ callStatement.getId());

	Scope currentScope = this.symTable.getCurrentScope();
	Scope global = this.symTable.getGlobalScope();
	//MethodSTE currentMethod = (MethodSTE)this.symTable.lookup(this.symTable.getCurrentScope().getScopeName()); // get current method {currentScope--> scopename --> lookup STE 	
	
	IExp exp= callStatement.getExp(); // get operation part where call is operation.name(***);
	System.out.println("Method call statement from class "+this.currentClass);
	STE classSTE=null;
	String classname;


	String methodName="";	


	methodName = callStatement.getId();
 
	if(exp instanceof ThisLiteral){ //method from same class {this.name()}
		//method in class which is called
		classSTE = global.lookup(this.currentClass);
	}else{ // method from another class {new class().name()}
		if(exp instanceof NewExp){
		    //NewExp newExp = (NewExp) exp;
		    classname = ((NewExp) exp).getId(); 
		    classSTE = global.lookup(classname);
		    //STE other = this.symTable.lookup(classname);
		     //methodName = classname+"."+methodName; // method in other class which is called
		}
    	}

	if(classSTE!=null)
	{	System.out.println("*** " + methodName + " "+ classSTE.getName());
		currentScope.setmEnclosing(classSTE); // currentMethod references some method
		//System.out.println("mEnclosing "+currentScope.getmEnclosingStr());	
	}
    }
}
    public void outCallStatement(CallStatement callStatement){
    }

    private boolean isDuplicate(String name, int line, int pos) {
	if (this.symTable.lookupInnermost(name) != null) {
            System.err.println("[" +line+ "," +pos+ "] Duplicate name " + name);
            return true;
        }
        return false;
    }

	public void inProgram(Program node) {
	if(this.firstpass==true)
	{
		System.out.println("------------------------First Pass Starts ---------------------------------");
	
	}
	}
	public void outProgram(Program node) {
	if(this.firstpass==true)
	{
		System.out.println("------------------------First Pass Ends ---------------------------------");
		this.firstpass=false;
		this.visitProgram(node);
	}
	}
}
