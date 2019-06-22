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

public class IRcommand_Jalr extends IRcommand
{
    TEMP target;

    public IRcommand_Jalr(TEMP target)
    {
        this.target = target;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().jalr(target);
    }
}