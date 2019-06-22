package AST;


import Globals.*;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_VAR_ID extends AST_EXP_VAR
{
    /************************/
    /* simple variable name */
    /************************/
    public AST_EXP_VAR var;
    public String field;
    public AST_EXP_LIST exp_lst;
    public boolean isFunction;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_EXP_VAR_ID(AST_EXP_VAR var, String field, AST_EXP_LIST exp_lst)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        System.out.format("====================== exp -> var DOT ID( %s ) LPAREN expList RPAREN\n", field);
        this.var = var;
        this.field = field;
        this.exp_lst = exp_lst;
        this.isFunction = true;
    }

    /**************************************************/
    /* The printing message for a simple var AST node */
    /**************************************************/
    public void PrintMe()
    {
        /**********************************/
        /* AST NODE TYPE = AST SIMPLE VAR */
        /**********************************/
        System.out.format("AST NODE EXP VAR DOT ID( %s ) LPAREN expList RPAREN\n", field);

        if (var != null) var.PrintMe();
        System.out.format("FIELD NAME( %s )\n",field);
        if (exp_lst != null) exp_lst.PrintMe();
        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                String.format("EXP\nVAR.ID(%s)\n(expList)", field));

        if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
        if (exp_lst != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp_lst.SerialNumber);
    }
    public TYPE SemantMe()
    {

        TYPE t = null;
        if (var != null) t = var.SemantMe();
        if (t.isClass() == false || !(t instanceof TYPE_CLASS))
        {
            System.out.format(">> ERROR [%d] variable must be a class or object of class\n",this.lineNum);
            Globals.error(this.lineNum);
        }

        exp_lst.SemantMe();


        return null;
    }

    public TEMP IRme() {
        // TODO: only afetr eran finishes index function
        return null;
    }
}
