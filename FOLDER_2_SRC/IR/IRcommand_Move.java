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
import TEMP.*;
import MIPS.*;

public class IRcommand_Move extends IRcommand
{
    TEMP dst;
    TEMP src;

    public IRcommand_Move(TEMP dst, TEMP src)
    {
        this.dst = dst;
        this.src = src;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().move(this.dst, this.src);
    }
}