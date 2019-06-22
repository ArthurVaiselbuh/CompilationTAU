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

public class IRcommand_Get_Label_from_FuncTable extends IRcommand
{
	TEMP dst;
	TEMP src;
	int offset;
	
	public IRcommand_Get_Label_from_FuncTable(TEMP dst, TEMP src, int offset)
	{
		this.dst = dst;
		this.src = src;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		System.out.println("------------------------------- " + offset);
		TEMP label_address = TEMP_FACTORY.getInstance().getFreshTEMP();
		//Load address of function table from object's layout
		sir_MIPS_a_lot.getInstance().load(label_address, src);
		//Add function's offset
		sir_MIPS_a_lot.getInstance().addi(label_address,label_address,sir_MIPS_a_lot.getInstance().WORD_SIZE*offset);
		//Load the function's address
		sir_MIPS_a_lot.getInstance().load(dst,label_address);
	}
}
