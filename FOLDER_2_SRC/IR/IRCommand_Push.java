package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.REG_T;
import TEMP.TEMP;

public class IRCommand_Push extends IRcommand{
    private TEMP target;
    // pushes a register onto the stack
    public IRCommand_Push(TEMP t){
        target = t;
    }
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().stack_allocate(1);
        sir_MIPS_a_lot.getInstance().save_on_stack(target,0);
    }
}
