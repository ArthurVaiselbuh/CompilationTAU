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

public class AST_TYPE_NAME extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public AST_DEC_VAR dec_var;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPE_NAME(String type,String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.type = type;
		this.name = name;
		this.dec_var = new AST_DEC_VAR(this.type, this.name, null, null);
	}

	/*************************************************/
	/* The printing message for a type name AST node */
	/*************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST TYPE NAME NODE */
		/**************************************/
		System.out.format("NAME(%s):TYPE(%s)\n",name,type);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NAME:TYPE\n%s:%s",name,type));
	}

	/*****************/
	/* SEMANT ME ... */
	/*****************/
	public TYPE SemantMe()
	{
		TYPE t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null)
		{
			/**************************/
			/* ERROR: undeclared type */
			/**************************/
			System.out.format(">> ERROR [%d:%d] undeclared type %s\n",this.lineNum, this.charNum, type);
			Globals.error(this.lineNum);
			return null;
		}
		else
		{
			/*******************************************************/
			/* Enter var with name=name and type=t to symbol table */
			/*******************************************************/
			SYMBOL_TABLE.getInstance().enter(name,t);
		}

		/****************************/
		/* return (existing) type t */
		/****************************/
		return t;
	}


	public TEMP IRme() {
		return this.dec_var.IRme();
	}
}
