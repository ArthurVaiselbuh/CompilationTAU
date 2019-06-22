package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_CFIELD_LIST extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELD head;
	public AST_CFIELD_LIST tail;
	public String myClassName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_LIST(AST_CFIELD head,AST_CFIELD_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("cfields -> cfield cfields\n");
		if (tail == null) System.out.print("cfields -> cfield\n");

		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a CFIELD LIST AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST CFIELD LIST */
		/**************************************/
		System.out.print("AST NODE CFIELD LIST\n");

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
			"CFIELD\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}

	public TYPE_LIST SemantMe()
	{
		//System.out.println("Settnig head class to " + this.myClassName);
		this.head.className = this.myClassName;
		if (tail == null)
		{
			return new TYPE_LIST(
				head.SemantMe(),
				null);
		}
		else
		{
			this.tail.myClassName = this.myClassName;
			return new TYPE_LIST(
				head.SemantMe(),
				tail.SemantMe());
		}
	}


	public TEMP IRme()
	{
		if (head != null) head.IRme();
		if (tail != null) tail.IRme();

		return null;
	}
}
