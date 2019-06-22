package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_EXP_VAR_FIELD extends AST_EXP_VAR
{
	public AST_EXP_VAR var;
	public String fieldName;
	public boolean isFunction;
	public TYPE_CLASS myClass;

	public int localIndex =-1;
	//this is used to check if need the variable address or to dereference.
	public boolean forAssign=false;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_VAR_FIELD(AST_EXP_VAR var,String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);
		this.var = var;
		this.fieldName = fieldName;
		this.isFunction = false;
		this.myClass = null;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.format("FIELD\nNAME\n(___.%s)\n",fieldName);

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n___.%s",fieldName));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);		
	}
	public TYPE SemantMe()
	{
		TYPE t = null;
		TYPE_CLASS tc = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe();
		
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		if (t.isClass() == false)
		{
			System.out.format(">> ERROR [%d:%d] access %s field of a non-class variable\n",6,6,fieldName);
			Globals.error(this.lineNum);
			System.exit(0);
		}
		else
		{
			tc = (TYPE_CLASS) t;
			this.myClass = tc;
		}
		
		/************************************/
		/* [3] Look for fiedlName inside tc */
		/************************************/
		String s;
		Globals.debug("fields are: " + tc.data_members_names + " of " + t.name);
		String[] arr;
		int index;
		for (int i = 0; i < tc.data_members_names.size(); i++)
		{
			s = tc.data_members_names.get(i);
			arr = s.split(" ");
			//System.out.println(s);
			if (arr[0].equals(fieldName))
			{
				//System.out.println(SYMBOL_TABLE.getInstance().find(arr[1]));
				if (arr.length > 2 && !isFunction){
					///System.out.format(">> ERROR [%d:%d] missing () in function call %s\n",6,6,fieldName);
					Globals.error(this.lineNum);
					System.exit(0);
				}
				SYMBOL_TABLE_ENTRY data = SYMBOL_TABLE.getInstance().getTableEntry(arr[1]);
				if (arr.length == 2){
					index = tc.localVars.indexOf(s);
				}
				else{
					index = tc.localFuncs.indexOf(s);
				}
				this.localIndex = index;
				data.localIndex = index;
				data.source=VarSource.cfield;
				return SYMBOL_TABLE.getInstance().find(arr[1]);
			}
		}
		
		/*********************************************/
		/* [4] fieldName does not exist in class var */
		/*********************************************/
		System.out.format(">> ERROR [%d:%d] field %s does not exist in class\n",6,6,fieldName);
		Globals.error(this.lineNum);
		System.exit(0);
		return null;
	}

	public TEMP IRme() {
		TEMP var = this.var.IRme();
		if (!forAssign) {
			TEMP t =TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(new IRcommand_loadFieldVar(t, var, this.localIndex));
			return t;
		} else {
			return var;
		}
	}
}
