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

public class IRcommand_Binop_LT_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_LT_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end        = getFreshLabel("end");
		String label_AssignOne  = getFreshLabel("AssignOne");
		String label_AssignZero = getFreshLabel("AssignZero");
		
		/******************************************/
		/* [2] if (t1< t2) goto label_AssignOne;  */
		/*     if (t1>=t2) goto label_AssignZero; */
		/******************************************/
		sir_MIPS_a_lot.getInstance().blt(t1,t2,label_AssignOne);
		sir_MIPS_a_lot.getInstance().bge(t1,t2,label_AssignZero);

		sir_MIPS_a_lot.getInstance().returnFunction(dst, label_AssignOne, label_AssignZero, label_end);
	}
}
