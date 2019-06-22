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

public class IRcommand_Add_Offset extends IRcommand
{
	TEMP dst;
	TEMP t1;
	int offset;
	
	public IRcommand_Add_Offset(TEMP dst, TEMP t1, int offset)
	{
		this.dst = dst;
		this.t1 = t1;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().addi(dst, t1, offset);
	}
}
