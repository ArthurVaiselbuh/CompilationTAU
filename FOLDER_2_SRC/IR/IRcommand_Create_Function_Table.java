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

public class IRcommand_Create_Function_Table extends IRcommand{
    public String label;
    int numOfFuncs;

    public IRcommand_Create_Function_Table(String label, int numOfFuncs){
        this.label = label;
        this.numOfFuncs = numOfFuncs;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme(){
        sir_MIPS_a_lot.getInstance().align();
        sir_MIPS_a_lot.getInstance().allocateDataSpace(label, numOfFuncs);
    }
}
