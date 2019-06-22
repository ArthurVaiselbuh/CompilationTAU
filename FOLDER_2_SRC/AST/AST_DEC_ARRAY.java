package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

/*
	this node holds array declarations of type:
		int x;  int x = a;  int x = new id;  int x = new id[exp];
 */


public class AST_DEC_ARRAY extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/* array ID = ID[] */
	/****************/
	public String typeArr;
	public String name;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_ARRAY(String name,String typeArr)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("arrayDec ->  ARRAY ID( %s ) EQ ID(%s)\n", name,typeArr);

		this.typeArr = typeArr;
		this.name = name;
	}

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		System.out.format("AST NODE ARRAY ID( %s ) EQ ID(%s)", name, typeArr);

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("Array Declaration NAME1(%s) NAME2(%s)", name, typeArr));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		//if (initialValue != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValue.SerialNumber);
			
	}

	public TYPE SemantMe()
	{
		TYPE t;

		/****************************/
		/*[0] Check Its Outer Scope */
		/****************************/
		TYPE_FOR_SCOPE_BOUNDARIES scope = SYMBOL_TABLE.getInstance().getLastScope();
		if (scope != null && scope.scope != 'g'){
			System.out.format(">> ERROR [%d:%d] array must be defined in the outer scope\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SYMBOL_TABLE.getInstance().find(typeArr);
		//System.out.format("Check If Type %s exists, res: %s\n", typeArr, t);
		if (t == null || t.isArray() == false || !(typeArr.equals("int") || typeArr.equals("string") || t instanceof TYPE_CLASS || t instanceof TYPE_ARRAY))
		{
			System.out.format(">> ERROR [%d:%d] non existing array type %s\n",this.lineNum, this.charNum,typeArr);
			Globals.error(this.lineNum);
		}

		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		//System.out.format("Check That Name %s does NOT exist\n", name);
		if (SYMBOL_TABLE.getInstance().find(name) != null)
		{
			System.out.format(">> ERROR [%d:%d] name %s already exists in scope\n",this.lineNum, this.charNum,name);
			Globals.error(this.lineNum);
		}


		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enter(name, new TYPE_ARRAY(name, t));

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}



	// Do nothing


	public TEMP IRme() {
		return null;
	}
	
}
