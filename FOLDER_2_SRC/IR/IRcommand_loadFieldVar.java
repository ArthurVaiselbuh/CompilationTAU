/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

import MIPS.sir_MIPS_a_lot;
import TEMP.*;

public class IRcommand_loadFieldVar extends IRcommand
{
	//var holds the var which we try to get the field of: var.field . dst is the temp we want to load this into
	TEMP dst, var;
	int classOffset;

	public IRcommand_loadFieldVar(TEMP dst, TEMP var, int offset_)
	{
		this.dst = dst;
		this.var = var;
		this.classOffset = offset_;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		//test for null deref
		sir_MIPS_a_lot.getInstance().beq(var, REG_ZERO.getInstance(), "invalid_pointer");
		//get the field.
		sir_MIPS_a_lot.getInstance().loadWord(dst, var, (1+classOffset));
	}
}
