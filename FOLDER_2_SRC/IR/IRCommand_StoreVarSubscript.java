package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.*;

public class IRCommand_StoreVarSubscript extends IRcommand{
    private TEMP src;
    private TEMP arrAddr;
    private TEMP subscript;
    // Store value from arrAddr into fp + subscript(local's address)
    public IRCommand_StoreVarSubscript(TEMP src, TEMP arrAddr, TEMP subscript) {
        this.src = src;
        this.subscript = subscript;
        this.arrAddr = arrAddr;
    }
    public void MIPSme() {
        //this will hold the array size, and then be used as a temp
        TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

        // illegal access if subscript > size
        sir_MIPS_a_lot.getInstance().loadWord(t, arrAddr, 0);
        sir_MIPS_a_lot.getInstance().bge(subscript, t, "access_violation");
        // also illegal access if subscript < 0
        sir_MIPS_a_lot.getInstance().blt(subscript, REG_ZERO.getInstance(),"access_violation");

        //no error! to get the right element, add 1 to the subscript(because arrAddr holds size, not the 0'th element)
        sir_MIPS_a_lot.getInstance().addi(t, subscript, 1);
        // multiply by word size
        TEMP wsize = TEMP_FACTORY.getInstance().getFreshTEMP();
        sir_MIPS_a_lot.getInstance().li(wsize, sir_MIPS_a_lot.getInstance().WORD_SIZE);
        sir_MIPS_a_lot.getInstance().mul(t, t, wsize);
        sir_MIPS_a_lot.getInstance().add(t, arrAddr, t);
        sir_MIPS_a_lot.getInstance().storeWord(t, src, 0);
    }
}
