package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_Pop extends IRcommand{
    private TEMP target=null;
    // Pop from stack, with the option to pop to a register
    public IRCommand_Pop(TEMP t){
        target = t;
    }
    public IRCommand_Pop(){ //poped value can be ignored
    }
    public void MIPSme()
    {
        if (target!=null) {
            sir_MIPS_a_lot.getInstance().load_from_stack(target, 0);
        }
        sir_MIPS_a_lot.getInstance().stack_allocate(-1);
    }
}
