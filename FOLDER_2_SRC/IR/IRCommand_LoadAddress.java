package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_LoadAddress extends IRcommand{
    private TEMP target;
    private String label;

    // Load the address of label into target register
    public IRCommand_LoadAddress(TEMP trgt, String label_) {
        target = trgt;
        label = label_;
    }
    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().loadAddress(target, label);
    }
}
