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
import TEMP.TEMP;

public class IRcommand_loadLocalVar extends IRcommand
{
	TEMP dst;
	int localOffset;

	public IRcommand_loadLocalVar(TEMP dst, int offset_)
	{
		this.dst      = dst;
		this.localOffset = offset_;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadLocalVar(dst, localOffset);
	}
}
