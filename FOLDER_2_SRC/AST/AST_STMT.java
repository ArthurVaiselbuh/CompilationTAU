package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public abstract class AST_STMT extends AST_Node
{
	public String label;
	/*********************************************************/
	/* The default message for an unknown AST statement node */
	/*********************************************************/
	public void PrintMe()
	{
		System.out.print("UNKNOWN AST STATEMENT NODE\n");
	}
	public TYPE SemantMe()
	{
		return null;
	}
}
