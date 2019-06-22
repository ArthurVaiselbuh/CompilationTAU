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

// this is for precolored $t0...$t7
public class REG_T extends TEMP
{
	public static String name = "Temp";
	private static REG_T[] regs=null;

	private int internal_idx;

	public REG_T(int serial)
	{
		super(serial);
	}

	public static REG_T getInstance(int idx){
		if (regs == null) {
			regs = new REG_T[8];
			for (int i = 0; i < 8; i++) {
				regs[i] = new REG_T(i);
				regs[i].internal_idx=i;
			}
		}
		if (idx<0 || idx>7){
			return null;
		}
		return regs[idx];
	}
}
