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
public class REG_RA extends TEMP
{
	public static String name = "RegRA";
	private static REG_RA reg=null;

	public REG_RA(int serial)
	{
		super(serial);
	}

	public static REG_RA getInstance(){
		if (reg == null) {
			reg = new REG_RA(-1);
		}
		return reg;
	}
}
