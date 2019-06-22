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

public class IRcommand_Start_Func_Call extends IRcommand
{
    public int paramCnt;
    public String name;

    public IRcommand_Start_Func_Call( int paramCnt ,String name)
    {
        this.paramCnt = paramCnt;
        this.name = name;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        if ( !(name.equals("PrintInt") || name.equals("PrintString") ) ){
            sir_MIPS_a_lot.getInstance().push_temps();	// save temporary registers before function call.
        }
        sir_MIPS_a_lot.getInstance().stack_allocate( this.paramCnt );	// make place on the stack for function parameters.
    }
}