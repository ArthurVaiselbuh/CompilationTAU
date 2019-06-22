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

// this is for precolored $s0...$s7
public class REG_S extends TEMP
{
	public static String name = "SavedReg";
	private static REG_S[] regs=null;

	private int internal_idx;

	public REG_S(int serial)
	{
		super(serial);
	}

	public static REG_S getInstance(int idx){
		if (regs == null) {
			regs = new REG_S[8];
			for (int i = 0; i < 8; i++) {
				regs[i] = new REG_S(i);
				regs[i].internal_idx=i;
			}
		}
		if (idx<0 || idx>7){
			return null;
		}
		return regs[idx];
	}
}
