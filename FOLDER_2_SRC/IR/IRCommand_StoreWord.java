package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.*;

public class IRCommand_StoreWord extends IRcommand{
    private TEMP target, src;
    private int offset=0;
    // Store value from arrAddr into target + offset
    public IRCommand_StoreWord(TEMP trgt, TEMP src){
        this(trgt, src, 0);
    }
    public IRCommand_StoreWord(TEMP trgt, TEMP src, int offset) {
        target = trgt;
        this.src=src;
        this.offset=offset;
    }
    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().storeWord(target, src, offset);
    }
}
