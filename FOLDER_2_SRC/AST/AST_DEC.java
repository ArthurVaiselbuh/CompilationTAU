package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public abstract class AST_DEC extends AST_Node
{
	public TYPE SemantMe()
	{
		return null;
	}

	public void PrintMe(){System.out.print("UNKNOWN AST DECELERATION NODE");}

	public TEMP IRme() { return null; }
}
