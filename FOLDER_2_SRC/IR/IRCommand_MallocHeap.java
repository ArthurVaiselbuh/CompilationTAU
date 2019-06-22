package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRCommand_MallocHeap extends IRcommand{
    int size;
    TEMP targetReg;

    public IRCommand_MallocHeap(TEMP target, int size){
        this.size = size;
        this.targetReg = target;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        sir_MIPS_a_lot.getInstance().heap_allocate(size, targetReg);
    }
}
