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

public class TEMP
{
	protected int serial = 0;
	public static String name = "Temp";

	public TEMP(int serial)
	{
		this.serial = serial;
	}
	
	public int getSerialNumber()
	{
		return serial;
	}

	public String toString(){return String.format("%s_%d", name, serial);}
}
