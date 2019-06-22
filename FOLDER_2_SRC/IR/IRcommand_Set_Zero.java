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
import TEMP.*;
import MIPS.*;

public class IRcommand_Set_Zero extends IRcommand
{
	public TEMP t;
	
	public IRcommand_Set_Zero(TEMP t)
	{
		this.t = t;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().set_zero( t );
	}
}

