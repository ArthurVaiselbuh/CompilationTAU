/***********/
/* PACKAGE */
/***********/
package AST;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_CFIELD extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC_VAR varDec;
	public AST_DEC_FUNC funcDec;
	public String className;

	public int intVal;
	public String strVal;
	public boolean isInt;
	public boolean isString;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD(AST_DEC_VAR varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.varDec = varDec;
		this.funcDec = null;
	}

	public AST_CFIELD(AST_DEC_FUNC funcDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.varDec = null;
		this.funcDec = funcDec;
	}

	/*************************************************/
	/* The printing message for a type name AST node */
	/*************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST TYPE NAME NODE */
		/**************************************/
		//System.out.format("NAME(%s):TYPE(%s)\n",name,type);
		if (this.funcDec == null)
			this.varDec.PrintMe();
		else
			this.funcDec.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		/*AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NAME:TYPE\n%s:%s",name,type));*/
	}

	/*****************/
	/* SEMANT ME ... */
	/*****************/
	public TYPE SemantMe()
	{
		if (this.funcDec == null){
			if (this.varDec.initialValue != null) {
				if (this.varDec.initialValue instanceof AST_EXP_INT) {
					//System.out.println("HEY!! INT DEC FIELD HERE!!!! VALUE: " + ((AST_EXP_INT) this.varDec.initialValue).value);
					this.intVal = ((AST_EXP_INT) this.varDec.initialValue).value;
					isInt = true;
					isString = false;
				} else if (this.varDec.initialValue instanceof AST_EXP_STRING) {
					this.varDec.initialValue.SemantMe();
					//System.out.println("HEY!! STRING DEC FIELD HERE!!!! VALUE: " + ((AST_EXP_STRING) this.varDec.initialValue).label);
					this.strVal = ((AST_EXP_STRING) this.varDec.initialValue).label;
					isInt = false;
					isString = true;
				}
			}
			else if (this.varDec.newValue == null){
				//System.out.println("HEY!! EMPTY DEC FIELD HERE!!!! VALUE: EMPTY");
				if (this.varDec.type.equals("int")){
					//System.out.println("HEY!! INT DEC FIELD HERE!!!! VALUE: EMPTY");
					this.intVal = 0;
					isInt = true;
					isString = false;
				}
				else if (this.varDec.type.equals("string")){
					//System.out.println("HEY!! STRING DEC FIELD HERE!!!! VALUE: EMPTY");
					String label = IRcommand.getFreshString();
					IR.stringList.add(new String[]{label, ""});
					this.strVal = label;
					isInt = false;
					isString = true;
				}
				else{
					isInt = false;
					isString = false;
				}
			}
			else
				System.out.println("SHOULDNT GET HERE");
			return this.varDec.SemantMe();
		}
		TYPE t;
		TYPE returnType = null;
		TYPE_LIST type_list = null;

		/*******************/
		/* [0] return type */
		/*******************/
		returnType = SYMBOL_TABLE.getInstance().find(this.funcDec.returnTypeName);
		if (returnType == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing return type %s\n",this.lineNum, this.charNum,returnType);
			Globals.error(this.lineNum);
		}

		if (SYMBOL_TABLE.getInstance().find(funcDec.name) != null && SYMBOL_TABLE.getInstance().isInLastScope(funcDec.name)){
			System.out.format(">> ERROR [%d:%d] function %s already exists\n",this.lineNum, this.charNum,funcDec.name);
			Globals.error(this.lineNum);
		}

		/***************************/
		/* [1] Semant Input Params */
		/***************************/
		for (AST_TYPE_NAME_LIST it = funcDec.params; it  != null; it = it.tail)
		{
			t = SYMBOL_TABLE.getInstance().find(it.head.type);
			if (t == null)
			{
				System.out.format(">> ERROR [%d:%d] non existing type %s\n",this.lineNum, this.charNum,it.head.type);
				Globals.error(this.lineNum);
			}
			else
			{
				type_list = new TYPE_LIST(t,type_list);
				//SYMBOL_TABLE.getInstance().enter(it.head.name,t);
			}
		}

		/***************************************************/
		/* [2] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enter(this.funcDec.name, new TYPE_FUNCTION(returnType,funcDec.name,type_list));

		this.funcDec.label = IRcommand.getFreshFuncLabel(this.funcDec.name + "_" + this.className);
		this.funcDec.label_end = IRcommand.getFreshFuncLabel(this.funcDec.name + "_" + this.className + "_end");
		this.funcDec.myClassName = this.className;

		/*********************************************************/
		/* [3] Return value is irrelevant for class declarations */
		/*********************************************************/
		return returnType;
	}

	public TEMP IRme()
	{
		TEMP t = null;
		if (this.funcDec != null) {
			t = this.funcDec.IRme();
		} else if (this.varDec != null) {
			t = this.varDec.IRme();
		}
		return t;
	}


}
