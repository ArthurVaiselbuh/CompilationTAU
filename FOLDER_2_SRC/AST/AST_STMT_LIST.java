package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_STMT head;
	public AST_STMT_LIST tail;
	public String labelEnd;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_LIST(AST_STMT head,AST_STMT_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		//if (tail != null) System.out.print("====================== stmts -> stmt stmts\n");
		//if (tail == null) System.out.print("====================== stmts -> stmt      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT LIST */
		/**************************************/
		System.out.print("AST NODE STMT LIST\n");

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
			"STMT\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		if (head != null) head.SemantMe();
		if (tail != null) tail.SemantMe();
		
		return null;
	}

	public TEMP IRme()
	{
		//System.out.println("LABEL OF RETURN " + this.labelEnd);
		if (head != null) {
			head.label = this.labelEnd;
			//System.out.println("RUN IRME OF HEAD");
			head.IRme();
		}
		if (tail != null) {
			tail.labelEnd = this.labelEnd;
			//System.out.println("RUN IRME OF TAIL");
			tail.IRme();
		}

		return null;
	}
}
