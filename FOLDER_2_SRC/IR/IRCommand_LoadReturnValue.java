package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_LoadReturnValue extends IRcommand {

    public TEMP target;

    public IRCommand_LoadReturnValue(TEMP target)
    {
        this.target = target;
    }


    public void MIPSme() {

        sir_MIPS_a_lot.getInstance().moveReturnValue(target);
    }
}
