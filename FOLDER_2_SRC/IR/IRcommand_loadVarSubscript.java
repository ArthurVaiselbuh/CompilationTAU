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

//access array index
public class IRcommand_loadVarSubscript extends IRcommand
{
	TEMP dst, arrAddr;
	TEMP subscript;

	public IRcommand_loadVarSubscript(TEMP dst, TEMP arrAddr, TEMP subscript)
	{
		this.dst      = dst;
		this.arrAddr = arrAddr;
		this.subscript = subscript;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		//first ensure array is not nil
		sir_MIPS_a_lot.getInstance().beq(arrAddr, REG_ZERO.getInstance(), "invalid_pointer");

		//this will hold the array size, and then be used as a temp
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		// illegal access if subscript > size
		sir_MIPS_a_lot.getInstance().loadWord(t, arrAddr, 0);
		sir_MIPS_a_lot.getInstance().bge(subscript, t, "access_violation");
		// also illegal access if subscript < 0
		sir_MIPS_a_lot.getInstance().blt(subscript, REG_ZERO.getInstance(),"access_violation");

		//no error! to get the right element, add 1 to the subscript(because arrAddr holds size, not the 0'th element)
		sir_MIPS_a_lot.getInstance().addi(t, subscript, 1);
		// multiply by word size, using dst as temp
		sir_MIPS_a_lot.getInstance().li(dst, sir_MIPS_a_lot.getInstance().WORD_SIZE);
		sir_MIPS_a_lot.getInstance().mul(t, t, dst);
		sir_MIPS_a_lot.getInstance().add(t, arrAddr, t);
		sir_MIPS_a_lot.getInstance().loadWord(dst, t, 0);
	}
}
