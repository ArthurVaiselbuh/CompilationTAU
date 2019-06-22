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

public class IRcommand_AllocateClass extends IRcommand
{
	TEMP dst;
	int size;
	
	public IRcommand_AllocateClass(TEMP dst, int size) {
		this.dst = dst;
		this.size = size;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().heap_allocate(size, dst);
	}
}
