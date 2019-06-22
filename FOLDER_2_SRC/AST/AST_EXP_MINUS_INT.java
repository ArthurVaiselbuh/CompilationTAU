package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;



public class AST_EXP_MINUS_INT extends AST_EXP
{
    public int value;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_EXP_MINUS_INT(int value)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== exp -> MINUS INT( %d )\n", value);

        /*******************************/
        /* COPY INPUT DATA NENBERS ... */
        /*******************************/
        this.value = value;
    }

    /************************************************/
    /* The printing message for an MINUS INT EXP AST node */
    /************************************************/
    public void PrintMe()
    {
        /*******************************/
        /* AST NODE TYPE = AST MINUS INT EXP */
        /*******************************/
        System.out.format("AST NODE MINUS INT( %d )\n",value);

        /*********************************/
        /* Print to AST GRAPHIZ DOT file */
        /*********************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                String.format("MINUS\nINT(%d)",value));
    }

    public TYPE SemantMe()
    {

        return TYPE_INT.getInstance();
    }

    public TEMP IRme()
    {
        // I suspect this is not used anywhere might as well delete this file.
        assert false;
        /**
        TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRcommand(new IRcommandConstInt(t,value));
        return t;
         */
        return null;
    }
}
