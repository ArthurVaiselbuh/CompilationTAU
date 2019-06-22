package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.*;
import MIPS.*;


public class IRCommand_Epilogue extends IRcommand {



    public int numOfLocals;
    public boolean isMain;
    public TEMP funcName_address;
    public int numParams;

    public IRCommand_Epilogue(int numOfLocals, boolean isMain, TEMP funcName_address, int numParams)
    {
        this.numOfLocals = numOfLocals;
        this.isMain = isMain;
        this.funcName_address = funcName_address;
        this.numParams = numParams;
    }

    public void MIPSme() {
        sir_MIPS_a_lot.getInstance().funcEpilogue(this.isMain, this.numOfLocals, this.funcName_address, this.numParams);
    }


}
