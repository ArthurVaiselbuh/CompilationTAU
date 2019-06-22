package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_storeArgument extends IRcommand{
    private TEMP src;
    private int offset=0;
    // Store value from arrAddr into fp + offset(local's address)
    public IRcommand_storeArgument(TEMP src, int offset) {
        this.src=src;
        this.offset=offset;
    }
    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().storeArgument(src, offset);
    }
}
