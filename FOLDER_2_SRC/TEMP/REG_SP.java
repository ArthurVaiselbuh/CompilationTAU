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
public class REG_SP extends TEMP
{
	public static String name = "RegSP";
	private static REG_SP reg=null;

	public REG_SP(int serial)
	{
		super(serial);
	}

	public static REG_SP getInstance(){
		if (reg == null) {
			reg = new REG_SP(-1);
		}
		return reg;
	}
}
