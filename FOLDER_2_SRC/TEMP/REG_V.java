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

// this is for precolored $v0...$v3
public class REG_V extends TEMP
{
	public static String name = "ValueReg";
	private static REG_V[] regs=null;

	private int internal_idx;

	public REG_V(int serial)
	{
		super(serial);
	}

	public static REG_V getInstance(int idx){
		if (regs == null) {
			regs = new REG_V[4];
			for (int i = 0; i < 4; i++) {
				regs[i] = new REG_V(i);
				regs[i].internal_idx=i;
			}
		}
		if (idx<0 || idx>3){
			return null;
		}
		return regs[idx];
	}
}
