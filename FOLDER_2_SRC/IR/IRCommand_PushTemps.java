package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.*;

public class IRCommand_PushTemps extends IRcommand{
    // pushes temporary registers onto the stack
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().push_temps();
    }
}
