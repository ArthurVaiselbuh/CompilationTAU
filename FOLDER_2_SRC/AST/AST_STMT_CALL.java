package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_CALL extends AST_STMT
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP_CALL callExp;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_CALL(AST_EXP_CALL callExp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.callExp = callExp;
	}
	
	public void PrintMe()
	{
		callExp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STMT\nCALL"));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,callExp.SerialNumber);		
	}
	public TYPE SemantMe(){
		return  callExp.SemantMe();
	}

	public TEMP IRme()
	{
		if (callExp != null) callExp.IRme();

		return null;
	}
}
