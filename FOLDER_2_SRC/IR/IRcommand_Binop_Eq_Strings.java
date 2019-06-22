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
import TEMP.*;

public class IRcommand_Binop_Eq_Strings extends IRcommand {
    public TEMP t1;
    public TEMP t2;
    public TEMP dst;

    public IRcommand_Binop_Eq_Strings(TEMP dst, TEMP t1, TEMP t2) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }
    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme() {
        TEMP ch1 = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP ch2 = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP offset1 = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP offset2 = TEMP_FACTORY.getInstance().getFreshTEMP();

        String label_comp_end = getFreshLabel("end");
        String label_return_1 = getFreshLabel("AssignOne");
        String label_return_0 = getFreshLabel("AssignZero");
        String label_comp_loop = getFreshLabel("Comp_Loop");

        sir_MIPS_a_lot.getInstance().move(offset1, t1);
        sir_MIPS_a_lot.getInstance().move(offset2, t2);

        sir_MIPS_a_lot.getInstance().strcmp(ch1, ch2, offset1, offset2, label_comp_loop, label_return_1, label_return_0);
        sir_MIPS_a_lot.getInstance().returnFunction(dst, label_return_1, label_return_0, label_comp_end);

        /*sir_MIPS_a_lot.getInstance().move(REG_A.getInstance(0),t1);
        sir_MIPS_a_lot.getInstance().move(REG_A.getInstance(1),t2);
        sir_MIPS_a_lot.getInstance().jal("strcmp");
        sir_MIPS_a_lot.getInstance().move(dst,REG_V.getInstance(0));*/

    }
}
