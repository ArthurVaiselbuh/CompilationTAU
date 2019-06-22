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

// this is for precolored $a0...$a3
public class REG_A extends TEMP
{
	public static String name = "ArgReg";
	private static REG_A[] regs=null;

	private int internal_idx;

	public REG_A(int serial)
	{
		super(serial);
	}

	public static REG_A getInstance(int idx){
		if (regs == null) {
			regs = new REG_A[4];
			for (int i = 0; i < 4; i++) {
				regs[i] = new REG_A(i);
				regs[i].internal_idx=i;
			}
		}
		if (idx<0 || idx>=4){
			return null;
		}
		return regs[idx];
	}

	public String toString(){
		return String.format("$a%d",internal_idx);
	}
}
