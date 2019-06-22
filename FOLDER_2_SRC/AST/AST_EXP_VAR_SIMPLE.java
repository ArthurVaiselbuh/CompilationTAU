package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_EXP_VAR_SIMPLE extends AST_EXP_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;

	//global?local?
	public VarSource source;
	public int localIndex=-1;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_VAR_SIMPLE(String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== var -> ID( %s )\n",name);
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}
	public TYPE SemantMe()
	{
		TYPE t = SYMBOL_TABLE.getInstance().find(name);
		if (t == null){
			System.out.format(">> ERROR [%d:%d] variable %s cannot be resolved\n", this.lineNum, this.charNum, name);
			Globals.error(this.lineNum);
		}
		TYPE_FOR_SCOPE_BOUNDARIES ste = SYMBOL_TABLE.getInstance().getClassType();
		boolean is_cfield = false;
		int i = 0;
		//this is within a method. However - local scope takes priority!
		if(SYMBOL_TABLE.getInstance().isInLastScope(name)){
			//this IS a local var! locals take priority over class fields.
			is_cfield=false;
		}else if (ste != null){
			//System.out.println("MY NAME: " + ste.cls);
			TYPE_CLASS tcb = (TYPE_CLASS)(SYMBOL_TABLE.getInstance().find(ste.cls));
			//System.out.println("MY VARS: " + tcb.localVars);
			String[] breakIt;
			for (; i < tcb.localVars.size(); i++){
				breakIt = tcb.localVars.get(i).split(" ");
				if (breakIt[0].equals(name)) {
					is_cfield = true;
					break;
				}
			}
		}
		if (is_cfield){
			localIndex = i;
			source = VarSource.cfield;
			//System.out.println("THIS IS VAR_SIMPLE " + name + " WITH INDEX " + localIndex);
		}
		else{
			//update and get variable type:
			SYMBOL_TABLE_ENTRY te = SYMBOL_TABLE.getInstance().getTableEntry(name);
			source = te.source;
			localIndex = te.localIndex;
		}

		if (t instanceof TYPE_CLASS_VAR_DEC) {
			return ((TYPE_CLASS_VAR_DEC)t).t;
		}

		return t;
	}

	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		//System.out.println("STATUS: " + source);
		switch (source){
			case global:
				IR.getInstance().Add_IRcommand(new IRcommand_loadGlobalVar(t, name));
				break;
			case local:
				IR.getInstance().Add_IRcommand(new IRcommand_loadLocalVar(t, localIndex));
				break;
			case cfield:
				IR.getInstance().Add_IRcommand(new IRcommand_loadFieldVar(t, REG_A.getInstance(1),localIndex));
				break;
			case argumnet:
				IR.getInstance().Add_IRcommand( new IRcommand_loadArgument(t, localIndex) );
				break;
			default:
				Globals.debugError(String.format("Unexpected case in ast_dec_var:%s", source));
		}
		return t;
	}
}
