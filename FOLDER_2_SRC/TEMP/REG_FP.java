/***********/
/* PACKAGE */
/***********/
package TEMP;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

// this is for precolored $ra register
public class REG_FP extends TEMP
{
	public static String name = "RegFP";
	private static REG_FP reg=null;

	public REG_FP(int serial)
	{
		super(serial);
	}

	public static REG_FP getInstance(){
		if (reg == null) {
			reg = new REG_FP(-1);
		}
		return reg;
	}
}
