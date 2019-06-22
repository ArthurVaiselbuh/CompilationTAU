package AST;

import TYPES.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_EXP_VAR_SUBSCRIPT extends AST_EXP_VAR
{
	public AST_EXP_VAR var;
	public AST_EXP subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_VAR_SUBSCRIPT(AST_EXP_VAR var,AST_EXP subscript)
	{
		System.out.print("====================== var -> var [ exp ]\n");
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		if (var != null) var.PrintMe();
		if (subscript != null) subscript.PrintMe();
	}

	public TYPE SemantMe(){
		TYPE t = null;
		if (var != null) t = var.SemantMe();
		if (!(t instanceof TYPE_ARRAY)){
			System.out.format(">> ERROR [%d:%d] variable must be of type array\n", this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		if (subscript == null || subscript.SemantMe() != TYPE_INT.getInstance()){
			System.out.format(">> ERROR [%d:%d] accessing an array entry is semantically valid only when " +
					"the subscript expression has an integer type\n", this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		System.out.println("subscript type: " + ((TYPE_ARRAY)t).elementsType.name);
		return ((TYPE_ARRAY)t).elementsType;
	}

	public TEMP IRme() {
		TEMP arr = null;
		TEMP idx = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (this.var  != null){
			arr = var.IRme();
		}
		if (this.subscript  != null){
			idx = subscript.IRme();
		}

		IR.getInstance().Add_IRcommand(new IRcommand_loadVarSubscript(dst, arr, idx));
		return dst;
	}

}
