package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_DEC_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC head;
	public AST_DEC_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_LIST(AST_DEC head,AST_DEC_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.head = head;
		this.tail = tail;
	}

	public TYPE SemantMe()
	{		
		/*************************************/
		/* RECURSIVELY SEMANT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.SemantMe();
		if (tail != null) tail.SemantMe();
		
		return null;	
	}

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		System.out.print("AST NODE DEC LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"DEC\nLIST\n");
				
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}

	public TEMP IRme()
	{
		//first need to run IRMe for globals:
		IR.irForGlobals=true;
		SYMBOL_TABLE.globalsDict.forEach((name,node)->node.IRme());
		IR.irForGlobals=false;
		if (head != null) head.IRme();
		if (tail != null) tail.IRme();

		return null;
	}

	public void IRAllocateFuncTables(){
		if(head != null) {
			if ( head instanceof AST_DEC_CLASS ) {
				AST_DEC_CLASS c = (AST_DEC_CLASS) head;
				c.allocateFuncTable();
			}
		}
		if (tail != null) tail.IRAllocateFuncTables();
		return;
	}

	public void IRWriteData(){
		if(head != null) {
			if ( head instanceof AST_DEC_CLASS ) {
				AST_DEC_CLASS c = (AST_DEC_CLASS) head;
				c.writeFuncTable();
			}
			/*else if (head instanceof AST_DEC_VAR) {
				AST_DEC_VAR var = (AST_DEC_VAR) head;
				TEMP t = null;

				if ( var.initialValue == null && var.newValue == null) {
					t = TEMP_FACTORY.getInstance().getFreshTEMP();
					IR.getInstance().Add_IRcommand( new IRcommand_Set_Zero( t ) );
				}
				else if (var.newValue == null){
					t = var.initialValue.IRme();
				}
				else
					t = var.newValue.IRme();

				IR.getInstance().Add_IRcommand( new IRcommand_StoreGlobalVar(var.name, t) );
			}*/

			}
		if (tail != null)
			tail.IRWriteData();

		return;
	}
}
