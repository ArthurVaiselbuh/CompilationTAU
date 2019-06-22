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
import MIPS.sir_MIPS_a_lot;

public class IRcommand_End_Func_Call extends IRcommand
{
    public TEMP ret;
    public String name;
    public int paramCnt;

    public IRcommand_End_Func_Call(TEMP t, String name, int paramCnt)
    {
        this.ret = t;
        this.name = name;
        this.paramCnt = paramCnt;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        if (name == null ){
            System.out.println("name is null");
            System.exit(0);
        }
        sir_MIPS_a_lot.getInstance().jal( String.format( this.name ) );	// jump and link.
        sir_MIPS_a_lot.getInstance().stack_allocate( -1*this.paramCnt );	// deallocate memory on stack. this is the memory we used for function parameters.
        sir_MIPS_a_lot.getInstance().pop_temps();	// restore temporary registers after function call.
        sir_MIPS_a_lot.getInstance().loadReturnValue( ret ); // load return value from register $v0 to TEMP t.
    }
}