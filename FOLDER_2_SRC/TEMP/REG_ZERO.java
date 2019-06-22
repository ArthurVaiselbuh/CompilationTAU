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

// this is for precolored $zero register
public class REG_ZERO extends TEMP
{
	public static String name = "RegZERO";
	private static REG_ZERO reg=null;

	public REG_ZERO(int serial)
	{
		super(serial);
	}

	public static REG_ZERO getInstance(){
		if (reg == null) {
			reg = new REG_ZERO(-1);
		}
		return reg;
	}
	public String toString(){
		return "$zero";
	}
}
