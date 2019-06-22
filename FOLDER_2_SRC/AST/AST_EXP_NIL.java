package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_NIL extends AST_EXP
{
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NIL()
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> NIL\n");
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE NIL\n");

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NIL"));
	}
	public TYPE SemantMe()
	{
		return TYPE_NIL.getInstance();
	}
	public TEMP IRme(){
		return REG_ZERO.getInstance();
	}
}
