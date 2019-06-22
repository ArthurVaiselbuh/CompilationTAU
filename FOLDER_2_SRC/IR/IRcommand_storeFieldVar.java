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

public class IRcommand_storeFieldVar extends IRcommand
{
	//var is the var.field we try to access. value is the value to store.
	TEMP value, var;
	int classOffset;

	public IRcommand_storeFieldVar(TEMP value, TEMP var, int offset_)
	{
		this.value = value;
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
		//set the field.
		sir_MIPS_a_lot.getInstance().storeWord(var, value, 1+classOffset);
	}
}
