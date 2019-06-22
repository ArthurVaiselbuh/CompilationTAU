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

public class IRcommand_AllocateArray extends IRcommand
{
	private TEMP dst;
	private TEMP numOfElements;

	public IRcommand_AllocateArray(TEMP address, TEMP numOfElements)
	{
		this.dst = address;
		this.numOfElements = numOfElements;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().comment("Begin array allocation");
		//need to add 1 to the # of elements to store the numOfElements itself
		TEMP totalSize = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().addi(totalSize,numOfElements,1);
		// multiply numofelements by word size
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().li(t, 4);
		sir_MIPS_a_lot.getInstance().mul(totalSize,totalSize,t);
		sir_MIPS_a_lot.getInstance().heap_allocate(totalSize, dst);
		//dst now holds a pointer to an allocated path of the correct size.
		//now store the num of elements in the allocated place
		sir_MIPS_a_lot.getInstance().storeWord(dst, numOfElements, 0);
		sir_MIPS_a_lot.getInstance().comment("end array allocation");
	}
}
