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

public class IRcommand_PrintTrace extends IRcommand
{

	public IRcommand_PrintTrace()
	{
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String label = getFreshLabel("Print_Trace");
		sir_MIPS_a_lot.getInstance().print_trace(label);
	}
}
