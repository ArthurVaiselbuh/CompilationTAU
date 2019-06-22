package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_STRING extends AST_EXP
{
	public String value;
	public boolean done = false;
	public String label;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String value)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> STRING( %s )\n", value);
		this.value = value;
	}

	/******************************************************/
	/* The printing message for a STRING EXP AST node */
	/******************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST STRING EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",value);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING\n%s",value.replace('"','\'')));
	}
	public TYPE SemantMe()
	 {
	 	/* Just checking a label already exists for this string, vtable purposes */
	 	if (!done) {
			// Store string for data sections
			label = IRcommand.getFreshString();
			IR.stringList.add(new String[]{label, value.substring(1, value.length() - 1)});
			done = true;
		}

		return TYPE_STRING.getInstance();
	}
	public TEMP IRme(){
		TEMP ret = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRCommand_LoadAddress(ret, this.label));
		return ret;
	}
}
