package ast_visitors;

/** 
 * CheckTypes
 * 
 * This AST visitor traverses a MiniJava Abstract Syntax Tree and checks
 * for a number of type errors.  If a type error is found a SymanticException
 * is thrown
 * 
 * CHANGES to make next year (2012)
 *  - make the error messages between *, +, and - consistent <= ??
 *
 * Bring down the symtab code so that it only does get and set Type
 *  for expressions
 */

import ast.node.*;
import ast.visitor.DepthFirstVisitor;
import java.util.*;

import symtable.*;
import exceptions.*;
public class CheckTypes extends DepthFirstVisitor
{
    
   private SymTable mCurrentST;
   private boolean error;
   private String errorMessage;
   private String currentClass; 
   public CheckTypes(SymTable st) {
     if(st==null) {
          throw new InternalException("unexpected null argument");
      }
      this.mCurrentST = st;
      this.error=false;
      this.errorMessage="";
      this.currentClass=null;
   }
   
   //========================= Overriding the visitor interface

   public void defaultOut(Node node) {
       //System.err.println("Node not implemented in CheckTypes, " + node.getClass());
   }
   
   public void outAndExp(AndExp node)
   {
     if(this.lookup(node.getLExp()) != Type.BOOL.toString()) {
	this.error=true;
     //  System.out.println(
       errorMessage += "Invalid left operand type for operator && ["+
         node.getLExp().getLine()+" , "+  node.getLExp().getPos()+"]\n";
     }

     if(this.lookup(node.getRExp()) != Type.BOOL.toString()) {
	this.error=true;       
	//System.out.println(
         errorMessage +="Invalid right operand type for operator && ["+
         node.getRExp().getLine()+" , "+  node.getRExp().getPos()+"]\n";
     }

     this.mCurrentST.setExpType(node, Type.BOOL);
   }

   public void outEqualExp(EqualExp node)
   {
	String LType = this.lookup(node.getLExp());
	String RType = this.lookup(node.getRExp());

     if(LType != RType) {
	this.error=true;       
	//System.out.println(
         errorMessage+="Different types for operator == ["+
         node.getLExp().getLine()+" , "+ node.getLExp().getPos()+"]\n";
     }

     this.mCurrentST.setExpType(node, Type.BOOL);
   }
  
   public void outLtExp(LtExp node)
   {
	String LType = this.lookup(node.getLExp());
	String RType = this.lookup(node.getRExp());

     if((LType==Type.INT.toString()  || LType==Type.BYTE.toString()) &&
           (RType==Type.INT.toString()  || RType==Type.BYTE.toString())
	) {
	this.error=true;       
	//System.out.println(
         errorMessage+="Different types for operator < ["+
         node.getLExp().getLine()+" , "+ node.getLExp().getPos()+"]\n";
     }

     this.mCurrentST.setExpType(node, Type.BOOL);
   }
   public void outPlusExp(PlusExp node)
   {
       String lexpType = this.lookup(node.getLExp());
       String rexpType = this.lookup(node.getRExp());
       if ((lexpType==Type.INT.toString()  || lexpType==Type.BYTE.toString()) &&
           (rexpType==Type.INT.toString()  || rexpType==Type.BYTE.toString())
          ){
           this.mCurrentST.setExpType(node, Type.INT);
	System.out.println("+ is correct");
       } else {
	this.error=true;       
	           
		//System.out.println(
                  errorMessage+= "Operands to + operator must be INT or BYTE ["+
                   node.getLExp().getLine()+" , "+ 
                   node.getLExp().getPos()+"]\n";
       }

   }
   public void outCallExp(CallExp node)
   {
	// look scope
	// look return parameters --done
	// match all paramters passed or not -- done
       

// get MethodSTE 
// check reurn type of methodSTE
// assign return type
	boolean breakFlag=false;
 	String methodName = node.getId();
	STE methodSTE = this.mCurrentST.lookup(methodName);
	List<Type> argsList = ((MethodSTE)methodSTE).getSignature().getFormals();
	List<IExp> callParam = node.getArgs();
	//getIType();
	if(argsList.size()!=callParam.size()){
		this.error=true;       
	        breakFlag=true;
		//System.out.println(
                  errorMessage+= "Required # of Args for methodCall "+node.getId()+" is # "+argsList.size()+
		   " but found only #"+callParam.size() +" arguements ["+
                   node.getLine()+" , "+ 
                   node.getPos()+"]\n";
	}
	Iterator<Type> args=argsList.iterator();
	Iterator<IExp> param=callParam.iterator();
	int i=0;
	while(args.hasNext()&&param.hasNext()&&!breakFlag){
		Type lexp = args.next();//this.mCurrentST.getExpType(node.getLExp());
      	 	IExp rexp = param.next();//this.mCurrentST.getExpType(node.getRExp());
		String rexpType = this.mCurrentST.getExpType(rexp);
		i++;
		if(!rexpType.equalsIgnoreCase(lexp.toString())){
			this.error=true;       
	           	
		//System.out.println(
                  errorMessage+= "Args for methodCall "+node.getId()+" is not as expected, check argument # "+i+" ["+
                   node.getLine()+" , "+ 
                   node.getPos()+"]\n";
		   breakFlag=true;		  
		   break;		
		}

	}
	if(!breakFlag){ // all arguement type matched so set return tpe for an expression	
		this.mCurrentST.setExpType(node,((MethodSTE)methodSTE).getSignature().getReturnType());
	}
   }
   public void outCallStatement(CallStatement node)
   {	// look scope
	// look return parameters --done
	// match all paramters passed or not -- done
       

// get MethodSTE 
// check reurn type of methodSTE
// assign return type

try
{
	/*if(node.getExp() instanceof ThisLiteral)
	{
	 
	}
	

	else if(node.getExp() instanceof NewExp)
*/
	
	
	boolean breakFlag=false;
 	String methodName = node.getId();
	System.out.println("Method name we are looking for is "+ methodName);
	STE classSTE = null;
	if(node.getExp() instanceof ThisLiteral){
	 	classSTE = this.mCurrentST.lookup(this.currentClass);
	}else if(node.getExp() instanceof NewExp)
		classSTE = this.mCurrentST.lookup(((NewExp)node.getExp()).getId());
	
	STE methodSTE = ((ClassSTE)classSTE).getScope().lookup(methodName);
	System.out.println("methSTE is " + methodSTE);
	List<Type> argsList = ((MethodSTE)methodSTE).getSignature().getFormals();
	List<IExp> callParam = node.getArgs();
System.out.println("Size");
	if(argsList.size()!=callParam.size()){
System.out.println("error");
		this.error=true;       
	        breakFlag=true;
		//System.out.println(
                  errorMessage+= "Required # of Args for methodCall "+node.getId()+" is # "+argsList.size()+
		   " but found only #"+callParam.size() +" arguements ["+
                   node.getLine()+" , "+ 
                   node.getPos()+"]\n";
	}

	Iterator<Type> args=argsList.iterator();
	Iterator<IExp> param=callParam.iterator();
	int i=0;
        System.out.println(args + " " + param + " condition "+(args.hasNext()&&param.hasNext()&&!breakFlag));
	while(args.hasNext()&&param.hasNext()&&!breakFlag){
		String rexpType="";
		Type lexp = args.next();//this.mCurrentST.getExpType(node.getLExp());
      	 	IExp rexp = param.next();//this.mCurrentST.getExpType(node.getRExp());



		rexpType = this.mCurrentST.getExpType(rexp);

if(rexpType==null){
System.out.println("we are broken "+rexp);
rexpType = ((VarSTE)this.mCurrentST.lookup(""+rexp)).getType().toString();	}	

i++;
		System.out.println(i+". rexpType is "+rexpType+ " && lexp is "+lexp+" are equal ? "+rexpType.equalsIgnoreCase(lexp.toString()));
		if(!rexpType.equalsIgnoreCase(lexp.toString())){
			this.error=true;       
	           System.out.println("*** inside while and error");	
		//System.out.println(
                  errorMessage+= "Args for methodCall "+node.getId()+" is not as expected, check argument # "+i+" ["+
                   node.getLine()+" , "+ 
                   node.getPos()+"]\n";
		   breakFlag=true;		  
		   break;		
		}


	}
	if(!breakFlag){ // all arguement type matched so set return tpe for an expression	
		this.mCurrentST.setExpType(node,((MethodSTE)methodSTE).getSignature().getReturnType());
	}
}
	catch(Exception e){
	System.out.println("Caught in call stmt:" +  e.getMessage() );
	}
   }
   
   public void outMinusExp(MinusExp node)
   {
       String lexpType = this.lookup(node.getLExp());
       String rexpType = this.lookup(node.getRExp());
       if ((lexpType==Type.INT.toString()  || lexpType==Type.BYTE.toString()) &&
           (rexpType==Type.INT.toString()  || rexpType==Type.BYTE.toString())
          ){
           this.mCurrentST.setExpType(node, Type.INT);
	System.out.println("- is correct");
       } else {
	this.error=true;       
	  //         System.out.println(
            errorMessage +=      "Operands to - operator must be INT or BYTE ["+
                   node.getLExp().getLine()+" , "+ 
                   node.getLExp().getPos()+"]\n";
       }

   }

   public void outMulExp(MulExp node)
   {
       String lexpType = this.lookup(node.getLExp());
       String rexpType = this.lookup(node.getRExp());
       if ((lexpType==Type.INT.toString()  || lexpType==Type.BYTE.toString()) &&
           (rexpType==Type.INT.toString()  || rexpType==Type.BYTE.toString())
          ){
           this.mCurrentST.setExpType(node, Type.INT);
	System.out.println("* is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to * operator must be INT or BYTE ["+
                   node.getLExp().getLine()+" , "+ 
                   node.getLExp().getPos()+"]\n";
       }

   }

  

    public void outNegExp(NegExp node)
   {
       String expType = this.lookup(node.getExp());

       if ((expType==Type.INT.toString())
          ){
           this.mCurrentST.setExpType(node, Type.INT);
	System.out.println("UMin is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to UMin operator must be INT ["+
                   node.getExp().getLine()+" , "+ 
                   node.getExp().getPos()+"]\n";
       }
  }


   public void outByteCast(ByteCast node)
   {
       String expType = this.lookup(node.getExp());

       if ((expType==Type.INT.toString()  || expType==Type.BYTE.toString())
          ){
           this.mCurrentST.setExpType(node, Type.BYTE);
	System.out.println("Byte is correct");
       } else {
	this.error=true;       
//	           System.out.println(
 	errorMessage+=             "Operands to Byte operator must be INT or BYTE ["+
                   node.getExp().getLine()+" , "+ 
                   node.getExp().getPos()+"]\n";
       }

   }

  public void outNotExp(NotExp node)
   {
       String expType = this.lookup(node.getExp());

       if ((expType==Type.BOOL.toString())
          ){
           this.mCurrentST.setExpType(node, Type.BOOL);
	System.out.println("NOT is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to NOT operator must be BOOL ["+
                   node.getExp().getLine()+" , "+ 
                   node.getExp().getPos()+"]\n";
       }
 }

  public void outMeggyDelay(MeggyDelay node)
   {
       String expType = this.lookup(node.getExp());

       if ((expType==Type.INT.toString())
          ){
           this.mCurrentST.setExpType(node, Type.VOID);
	System.out.println("MeggyDelay is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to Delay operator must be INT ["+
                   node.getExp().getLine()+" , "+ 
                   node.getExp().getPos()+"]\n";
       }
 }
   public void outMeggyToneStart(MeggyToneStart node)
   {
       String toneExpType = this.lookup(node.getToneExp());
       String durationExpType = this.lookup(node.getDurationExp());

       if ((toneExpType==Type.INT.toString())&&(durationExpType==Type.INT.toString())
          ){
           this.mCurrentST.setExpType(node, Type.VOID);
	System.out.println("ToneStart is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to ToneStart operator must be INT ["+
                   node.getToneExp().getLine()+" , "+ 
                   node.getToneExp().getPos()+"]\n";
       }
   }
   public void outMeggySetPixel(MeggySetPixel node)
   {


       String lexpType = this.lookup(node.getYExp());
       String rexpType = this.lookup(node.getXExp());
       String colorexpType = this.lookup(node.getColor());


	if(colorexpType==null){
	System.out.println("we are broken "+node.getColor());
	colorexpType = ((VarSTE)this.mCurrentST.lookup(""+node.getColor())).getType().toString();	}	



       if ((lexpType==Type.INT.toString()  || lexpType==Type.BYTE.toString()) &&
           (rexpType==Type.INT.toString()  || rexpType==Type.BYTE.toString()) &&
	   (colorexpType == Type.COLOR.toString())
          ){
           this.mCurrentST.setExpType(node, Type.VOID);
	System.out.println("SetPixel is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to MeggySetPixel operator must be INT or BYTE,INT or BYTE & MeggyColor ["+
                   node.getXExp().getLine()+" , "+ 
                   node.getXExp().getPos()+"]\n";
       }

   }

   public void outMeggyCheckButton(MeggyCheckButton node)
   {
       String expType = this.lookup(node.getExp());
  	System.out.println(expType);

       if (expType==Type.BUTTON.toString())
	{
           this.mCurrentST.setExpType(node, Type.BOOL);
	System.out.println("CheckButton is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to MeggyCheckButton operator must be ButtonLiteral ["+
                   node.getExp().getLine()+" , "+ 
                   node.getExp().getPos()+"]\n";
       }

   }


   public void outMeggyGetPixel(MeggyGetPixel node)
   {
       String lexpType = this.lookup(node.getXExp());
       String rexpType = this.lookup(node.getYExp());


       if ((lexpType==Type.INT.toString()  || lexpType==Type.BYTE.toString()) &&
           (rexpType==Type.INT.toString()  || rexpType==Type.BYTE.toString()) 
          ){
           this.mCurrentST.setExpType(node, Type.COLOR);
	System.out.println("GetPixel is correct");
       } else {
	this.error=true;       
	           //System.out.println(
                   errorMessage+="Operands to MeggyGetPixel operator must be INT or BYTE ["+
                   node.getXExp().getLine()+" , "+ 
                   node.getXExp().getPos()+"]\n";
       }

   }

    public void inTopClassDecl(TopClassDecl topClassDecl) {
        this.currentClass = topClassDecl.getName();
        this.mCurrentST.pushScope(this.currentClass);// find class and push the scope

	System.out.println("in Top Decl");
    } // this. class and push scope 

    public void outTopClassDecl(TopClassDecl topClassDecl) {
        this.mCurrentST.popScope();
    } // pop scope 

    public void inMethodDecl(MethodDecl methodDecl) {
        this.mCurrentST.pushScope(methodDecl.getName()); //find method and push scope
    }// push method scope

    public void outMethodDecl(MethodDecl methodDecl) {
        this.mCurrentST.popScope();
    } //pop method scope 

  public void outNewExp(NewExp newExp) {


        STE sTE = this.mCurrentST.lookup(newExp.getId());// search Class
        if (sTE == null) {
	    this.error = true;
            //System.out.println(
	    errorMessage+="Undeclared class type "+ newExp.getId()+" at ["+ newExp.getLine()+" , "+ newExp.getPos()+ "]\n";
        }

	System.out.println("new Exp:" +sTE.getName());
        this.mCurrentST.setExpType(newExp, Type.getClassType(newExp.getId()));
    }

  
    public void outThisExp(ThisLiteral thisLiteral) {
        if (this.currentClass == null) {
	    this.error = true;
            //System.out.println(
	    errorMessage+="This : CurrentClass is null at ["+thisLiteral.getLine()+" , "+thisLiteral.getPos()+"]\n";
        }
        this.mCurrentST.setExpType((Node)thisLiteral, Type.getClassType(this.currentClass));
    }


public String lookup(IExp exp)
{
	 String expType = this.mCurrentST.getExpType(exp);
	if(expType==null){
	System.out.println("we are broken "+exp);
	expType = ((VarSTE)this.mCurrentST.lookup(""+exp)).getType().toString();	
	}

	return expType;
	


}

  public boolean getError(){
	return this.error;
  }
  public void getErrorMessage(){
	System.out.println(this.errorMessage);
  }
  public Type getType (String type){

  if(type.equalsIgnoreCase("INT") )
    {
      return Type.INT;
    }

    if(type.equalsIgnoreCase("BOOL"))
    {
      return Type.BOOL;
    }

    if(type.equalsIgnoreCase("BYTE"))
    {
      return Type.BYTE;
    }

    if(type.equalsIgnoreCase("COLOR"))
    {
      return Type.COLOR;
    }

    if(type.equalsIgnoreCase("BUTTON"))
    {
      return Type.BUTTON;
    }

   return Type.VOID;

  }

   public Type getIType(IType node){
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
 
}
