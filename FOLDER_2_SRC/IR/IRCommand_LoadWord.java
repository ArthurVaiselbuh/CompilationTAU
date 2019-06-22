package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_LoadWord extends IRcommand{
    private TEMP target, src;
    private int offset=0;
    // Load value from address held in addr into target temp
    public IRCommand_LoadWord(TEMP trgt, TEMP src){
        this(trgt, src, 0);
    }

    public IRCommand_LoadWord(TEMP trgt, TEMP src, int offset) {
        target = trgt;
        this.src=src;
        this.offset=offset;

    }
    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().loadWord(target, src, offset);
    }
}
