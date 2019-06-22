package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public abstract class AST_EXP extends AST_Node
{
	public TYPE SemantMe()
	{
		return null;
	}
	public TEMP IRme()
	{
		return null;
	}
}
