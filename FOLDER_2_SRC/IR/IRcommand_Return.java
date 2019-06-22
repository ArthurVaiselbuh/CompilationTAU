/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Return extends IRcommand
{
    TEMP data;
    boolean isMain;
    String label;

    /**
     * returns from the function and does the epilogue
     * @param data to return, ZERO_REG if its void or null
     */

    public IRcommand_Return(TEMP data, String label)
    {
        this.data = data;
        this.isMain = false;
        this.label = label;
    }

    public IRcommand_Return(boolean isMain, String label)
    {
        this.data = null;
        this.isMain = isMain;
        this.label = label;

    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        if (data != null){
            sir_MIPS_a_lot.getInstance().storeReturnValue(data);
        }
        sir_MIPS_a_lot.getInstance().jump( label );

        //TODO: maybe handle special case of main? reference has no special treatment
    }
}