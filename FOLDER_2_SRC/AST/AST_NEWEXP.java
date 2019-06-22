package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_NEWEXP extends AST_Node
{
	public String name;
	public AST_EXP exp;
	public boolean isArr;
	public String funcTable_label;

	public AST_NEWEXP(String name, AST_EXP exp){
		this.name = name;
		this.exp = exp;
		this.isArr = false;
	}

	public AST_NEWEXP(String name, AST_EXP exp, boolean isArr){
		this.name = name;
		this.exp = exp;
		this.isArr = isArr;
	}

	public TYPE SemantMe()
	{
		TYPE t = SYMBOL_TABLE.getInstance().find(name);
		if(t == null){
			System.out.format(">> ERROR [%d:%d] no such type %s\n",this.lineNum, this.charNum,name);
			Globals.error(this.lineNum);
		}
		if (isArr && exp == null) {
			System.out.format(">> ERROR [%d:%d] allocating an array must be done with an integral size\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}

		if (exp != null){
			if (t.isArray() == false){
				System.out.format(">> ERROR [%d:%d] can't initialize non-array type %s\n",this.lineNum, this.charNum,name);
				Globals.error(this.lineNum);
			}
			if (exp.SemantMe() != TYPE_INT.getInstance()){
				System.out.format(">> ERROR [%d:%d] can't initialize array with non-int size\n",this.lineNum, this.charNum);
				Globals.error(this.lineNum);
			}
		}
		return t;
	}

	public TEMP IRme(){
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP tmp = TEMP_FACTORY.getInstance().getFreshTEMP();
		TYPE t = SYMBOL_TABLE.getInstance().find(name);
		if (isArr){
			// DONT FREAK OUT I DIDNT CHANGE ANYTHING
			TEMP size = exp.IRme();	// array size
			IR.getInstance().Add_IRcommand( new IRcommand_AllocateArray(dst, size) );
			return dst;
		}
		else if (t instanceof TYPE_CLASS){
			TYPE_CLASS tc = (TYPE_CLASS)t;
			int num_of_var_fields = tc.localVars.size();

			/* Allocate enough memory for all variables and function table address */
			IR.getInstance().Add_IRcommand(new IRcommand_AllocateClass(tmp, num_of_var_fields + 1));
			IR.getInstance().Add_IRcommand(new IRcommand_Add_Offset(dst,tmp, 0));

			/* First entry of vtable is the function table's address */
			TEMP t3 = TEMP_FACTORY.getInstance().getFreshTEMP();
			funcTable_label = tc.functionTableLabel;
			IR.getInstance().Add_IRcommand( new IRCommand_LoadAddress(t3,funcTable_label) );
			IR.getInstance().Add_IRcommand( new IRCommand_StoreWord(tmp,t3) );

			String[] breakIt;
			TEMP t2;
			//System.out.println("NEW CLASS INSTANCE NAMED " + name + ", FIELDS INITS:");
			for (int i = 0; i < tc.localVarsValues.size(); i++){
				t2 = TEMP_FACTORY.getInstance().getFreshTEMP();
				breakIt = tc.localVarsValues.get(i).split(" ");
				System.out.println(breakIt[1] + ": " + breakIt[0]);
				if (!(breakIt[1].equals("int") || breakIt[1].equals("string"))){
					//System.out.println("NIL CASE");
					IR.getInstance().Add_IRcommand(new IRcommand_Set_Zero(t2));
				}
				else if (breakIt[1].equals("int")){
					//System.out.println("INT CASE");
					IR.getInstance().Add_IRcommand(new IRcommandConstInt(t2, Integer.parseInt(breakIt[0])));
				}
				else {
					//System.out.println("STRING CASE");
					IR.getInstance().Add_IRcommand(new IRCommand_LoadAddress(t2, breakIt[0]));
				}
				IR.getInstance().Add_IRcommand( new IRcommand_Add_Offset(tmp,tmp,sir_MIPS_a_lot.getInstance().WORD_SIZE) );
				IR.getInstance().Add_IRcommand( new IRCommand_StoreWord(tmp,t2) );
			}
			return dst;
		}
		return dst;
	}
}
