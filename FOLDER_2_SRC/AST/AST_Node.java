package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	public int lineNum;
	public int charNum;
	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	/***********************************************/
	/* The default type for an AST node */
	/***********************************************/
/*
	public TYPE SemantMe(){return null;}
*/

	/*****************************************/
	/* The default IR action for an AST node */
	/*****************************************/
	public TEMP IRme()
	{
		return null;
	}
}
