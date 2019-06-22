package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.REG_T;

public class IRCommand_PopTemps extends IRcommand{
    // pop temporary registers from the stack
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().pop_temps();
    }
}
