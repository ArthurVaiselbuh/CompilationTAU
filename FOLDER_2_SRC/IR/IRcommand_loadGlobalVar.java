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

public class IRcommand_loadGlobalVar extends IRcommand
{
	TEMP dst;
	String var_name;

	public IRcommand_loadGlobalVar(TEMP dst, String var_name)
	{
		this.dst      = dst;
		this.var_name = var_name;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadGlobalVar(dst,var_name);
	}
}
