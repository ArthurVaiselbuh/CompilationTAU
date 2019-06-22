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

// this register shall always point to the global variables table
public class REG_Globals extends TEMP
{
	public static String name = "RegGlobals";
	private static REG_Globals reg=null;

	public REG_Globals(int serial)
	{
		super(serial);
	}

	public static REG_Globals getInstance(){
		if (reg == null) {
			reg = new REG_Globals(0);
		}
		return reg;
	}

	@Override
	public String toString() {
		return "$gp";
	}
}
