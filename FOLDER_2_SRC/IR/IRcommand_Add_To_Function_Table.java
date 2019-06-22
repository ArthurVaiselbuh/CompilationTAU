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

public class IRcommand_Add_To_Function_Table extends IRcommand {
    public String tableLabel;
    public String functionLabel;
    public int funcOffset;

    public IRcommand_Add_To_Function_Table(String tableLabel, String functionLabel, int funcOffset){
        this.tableLabel = tableLabel;
        this.functionLabel = functionLabel;
        this.funcOffset = funcOffset;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme(){
        TEMP tableAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP functionAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
        sir_MIPS_a_lot.getInstance().addToFuncTable(tableLabel, functionLabel, tableAddress, functionAddress, funcOffset);
    }
}
