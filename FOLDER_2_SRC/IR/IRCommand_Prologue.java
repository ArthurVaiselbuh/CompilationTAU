package IR;

import TEMP.*;
import MIPS.*;


public class IRCommand_Prologue extends IRcommand {



    public int numOfLocals;
    public boolean isMain;
    public TEMP funcName_address;
    public String name;
    public String cls;

    public IRCommand_Prologue(int numOfLocals, boolean isMain, TEMP funcName_address, String name, String cls)
    {
        this.numOfLocals = numOfLocals;
        this.isMain = isMain;
        this.funcName_address = funcName_address;
        this.name = name;
        this.cls = cls;
    }

    public void MIPSme() {
        //System.out.println("class is " + cls.equals(""));
        sir_MIPS_a_lot.getInstance().funcPrologue(this.isMain, this.numOfLocals, this.funcName_address, name, cls);

    }


}
