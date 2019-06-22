package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_RETURN extends AST_STMT
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP exp;
	//public String label;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.exp = exp;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT RETURN\n");

		/*****************************/
		/* RECURSIVELY PRINT exp ... */
		/*****************************/
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"RETURN");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	public TYPE SemantMe(){
		TYPE_FOR_SCOPE_BOUNDARIES scope = SYMBOL_TABLE.getInstance().getFunction();
		if (scope == null){
			System.out.format(">> ERROR [%d:%d] return must be in a function\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		System.out.format("func: %s, type: (%s)\n", scope.f, scope.returnType);
		TYPE t = null;
		if (exp != null) t = this.exp.SemantMe();
		//System.out.format("ret type: (%s)\n", t.name);
		if (t == null && "void".equals(scope.returnType) == false){
			System.out.format(">> ERROR [%d:%d] return type mismatch\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		TYPE r = SYMBOL_TABLE.getInstance().find(scope.returnType);
		if (t != null && (r instanceof TYPE_CLASS || r instanceof TYPE_ARRAY) && t == TYPE_NIL.getInstance()){
            return null;
        }
		if (t != null && t instanceof TYPE_CLASS && r instanceof TYPE_CLASS){
			TYPE_CLASS tmp = (TYPE_CLASS)r;
			while (tmp != null){
				if (tmp.name.equals(((TYPE_CLASS)t).name))
					return null;
				tmp = tmp.father;
			}
			System.out.format(">> ERROR [%d:%d] return type mismatch\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		if (t != null && t.name.equals(scope.returnType) == false){
			System.out.format(">> ERROR [%d:%d] return type mismatch\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		return null;
	}
	public TEMP IRme(){
		TEMP t = null;
		/* Evaluate the return value if there is one */
		if (exp != null)
			t = exp.IRme();
		IR.getInstance().Add_IRcommand(new IRcommand_Return(t, label));
		return null;
	}
}
