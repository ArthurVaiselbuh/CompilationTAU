package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_StoreLocalVar extends IRcommand{
    private TEMP src;
    private int offset=0;
    // Store value from arrAddr into fp + offset(local's address)
    public IRCommand_StoreLocalVar(TEMP src, int offset) {
        this.src=src;
        this.offset=offset;
    }
    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().storeLocalVar(src, offset);
    }
}
