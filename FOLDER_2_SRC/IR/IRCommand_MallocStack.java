package IR;
import TEMP.*;
import MIPS.*;

public class IRCommand_MallocStack extends  IRcommand {
    int size;
    TEMP targetReg = null;

    public IRCommand_MallocStack(int size)
    {
        this.size = size;
    }
    public IRCommand_MallocStack(TEMP target, int size){
        this.size = size;
        this.targetReg = target;
    }

    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme()
    {
        if (targetReg!=null) {
            sir_MIPS_a_lot.getInstance().stack_allocate(size, targetReg);
        } else{
            sir_MIPS_a_lot.getInstance().stack_allocate(size);
        }
    }
}
