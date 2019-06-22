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

public class IRcommand_Binop_Div_Integers extends IRcommand
{
    public TEMP t1;
    public TEMP t2;
    public TEMP dst;

    public IRcommand_Binop_Div_Integers(TEMP dst,TEMP t1,TEMP t2)
    {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }
    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        /* Allocate labels for the extreme values */
        String label_end         = getFreshLabel("end");
        String label_overflow_max    = getFreshLabel("overflow_max");
        String label_overflow__min    = getFreshLabel("overflow_min");
        String label_error_not = getFreshLabel("div_by_zero_not");

        /* Check if t2 = 0 for zero devision error */
        sir_MIPS_a_lot.getInstance().bnez(t2, label_error_not);
        sir_MIPS_a_lot.getInstance().printDivByZero();
        sir_MIPS_a_lot.getInstance().exit();

        sir_MIPS_a_lot.getInstance().label(label_error_not);

        /* Divide the values */
        sir_MIPS_a_lot.getInstance().div(dst,t1,t2);
        /* Check underflow */
        sir_MIPS_a_lot.getInstance().check_int_extreme(dst, label_overflow_max, label_overflow__min, label_end);


    }
}
