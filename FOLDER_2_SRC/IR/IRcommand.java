/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */

import java.util.ArrayList;
import java.util.List;

/*******************/

public abstract class IRcommand
{
	int MAXINT = 32767;
	int MININT = -32768;

	/*****************/
	/* Label Factory */
	/*****************/
	protected static int label_counter=0;
	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	public static String getFreshIfLabel(){ return getFreshLabel("End_if_block");}
	public static String getFreshWhileStartLabel(){return getFreshLabel("While_Start_block");}
	public static String getFreshWhileCondLabel(){return getFreshLabel("While_Block_Cond");}
	public static String getFreshFuncLabel(String name){return String.format("Label_%d_Func_%s",label_counter++,name);}
	public static String getFreshLegal(){return String.format("Label_%d_Legal",label_counter++);}
	public static String getFreshGlobal(int place) {return String.format("Label_%d_Global_Init_%d",label_counter++,place);}
	public static String getFreshString(){return getFreshLabel("StringLiteral");}
	public boolean isData() {return false;}
	/***************/
	/* MIPS me !!! */
	/***************/
	public abstract void MIPSme();
}
