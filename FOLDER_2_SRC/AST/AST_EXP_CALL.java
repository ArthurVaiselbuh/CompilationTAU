package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import java.util.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_CALL extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String funcName;
	public AST_EXP_LIST params;
	public AST_EXP_VAR member;
	public int paramsLength;

	// for method call
	public TYPE_CLASS tc;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_CALL(String funcName,AST_EXP_LIST params)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.funcName = funcName;
		this.params = params;
		this.member = null;

		this.paramsLength = 0;
		if (this.params != null) {
			AST_EXP_LIST p = this.params;
			while (p!=null && p.head != null) {
				this.paramsLength++;
				p = p.tail;
			}
		}
	}

	public AST_EXP_CALL(String funcName,AST_EXP_LIST params, AST_EXP_VAR member)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.funcName = funcName;
		this.params = params;
		this.member = member;

		this.paramsLength = 0;
		if (this.params != null) {
			AST_EXP_LIST p = this.params;
			//System.out.println("IS P NULL: " + p == null);
			while (p != null && p.head != null) {
				System.out.println("IS P NULL BEFORE:");
				this.paramsLength++;
				p = p.tail;
				System.out.println("IS P NULL AFTER:");
			}
			System.out.println("PARAMS LENGTH: " + this.paramsLength);
		}

	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		System.out.format("CALL(%s)\nWITH:\n",funcName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) params.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CALL(%s)\nWITH",funcName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (params != null)
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);
	}

	public TYPE SemantMe(){
		TYPE t = null, y;
		TYPE_LIST l = null;
		TYPE_FUNCTION tmp = null;
		boolean w = false;
		/* Check the function exists */
		if (member != null){
			y = member.SemantMe();
			System.out.format(">> function %s called by variable %s\n",funcName, y);
			if (y.isClass() == false){
				System.out.format(">> ERROR [%d:%d] no class with function %s\n", member.lineNum, this.charNum, funcName);
				Globals.error(member.lineNum);
			}
			ArrayList<String> mems = ((TYPE_CLASS)y).data_members_names;
			String[] arr;
			for (int i = 0; i < mems.size(); i++){
				arr = mems.get(i).split(" ");
				//for (int u = 0; u < arr.length; u++)
				//	System.out.print(arr[u] + " ");
				//System.out.println();
				if (arr[0].equals(funcName)){

					if (arr[1].equals("f")){
						w = true;
						TYPE_LIST o = null;
						for(int k = 3; k < arr.length; k++){
							//System.out.print(arr[k] + ", ");
							o = new TYPE_LIST(SYMBOL_TABLE.getInstance().find(arr[k]),o);
						}
						//System.out.println();
						tmp = new TYPE_FUNCTION(SYMBOL_TABLE.getInstance().find(arr[2]), arr[0], o);
						//return SYMBOL_TABLE.getInstance().find(arr[2]);
					}
				}
			}
			//System.out.println("type of member: " + y.name);
			if(!w) {
				System.out.format(">> ERROR [%d:%d] no class with function %s\n", this.lineNum, this.charNum, funcName);
				Globals.error(this.lineNum);
			}
			this.tc = (TYPE_CLASS)y;
		}
		else {
			t = SYMBOL_TABLE.getInstance().find(this.funcName);
			if (t == null) {
				System.out.format(">> ERROR [%d:%d] non existing function %s\n", this.lineNum, this.charNum, funcName);
				Globals.error(this.lineNum);
			}
			tmp = (TYPE_FUNCTION)t;
			TYPE_FOR_SCOPE_BOUNDARIES tsb = SYMBOL_TABLE.getInstance().getClassType();
			if (tsb != null)
				tc = (TYPE_CLASS)SYMBOL_TABLE.getInstance().find(tsb.cls);
		}

		//if (tmp.params == null) System.out.println("params should be empty");
		if (params == null && tmp.params == null)
			return tmp.returnType;
		if ((params == null && tmp.params != null) || (tmp.params == null && params != null)){
			System.out.format(">> ERROR [%d:%d] non matching parameters to function %s\n",this.lineNum, this.charNum,this.funcName);
			Globals.error(this.lineNum);
		}
		if (tmp.params != null) l = params.SemantMe();
		//System.out.println("name: " + funcName);
		//System.out.println("function paramters: " + tmp.params);
		//System.out.println("got from user: " + l);
		ArrayList<String> r = l.getList();
		Collections.reverse(r);
		ArrayList<String> p = tmp.params.getList();
		//System.out.println("function paramters: " + p.toString());
		//System.out.println("got from user: " + r.toString());
		if (r.size() != p.size()){
			System.out.format(">> ERROR [%d:%d] non matching parameters to function %s\n",this.lineNum, this.charNum,this.funcName);
			Globals.error(this.lineNum);
		}
		TYPE a,b;
		TYPE_CLASS c;
		boolean test = false;
		for (int i = 0; i < r.size(); i++){
			if (p.get(i).equals("int") == false && p.get(i).equals("string") == false){
				a = SYMBOL_TABLE.getInstance().find(p.get(i));
				if (r.get(i).equals("nil")) r.set(i, "NIL");
				b = SYMBOL_TABLE.getInstance().find(r.get(i));
				if (a instanceof TYPE_CLASS && b instanceof TYPE_CLASS){
					c = (TYPE_CLASS)b;
					while (c != null){
						if (c.name.equals(((TYPE_CLASS) a).name)) {
							test = true;
							break;
						}
						c = c.father;
					}
					if (test)
						continue;
				}
				else{
					//System.out.println(a + ", " + b);
					if (a instanceof TYPE_ARRAY && b instanceof TYPE_ARRAY){
						if (a.name.equals(b.name))
							continue;
					}
					if ((a instanceof TYPE_CLASS || a instanceof TYPE_ARRAY) && b == TYPE_NIL.getInstance()) {
						if (r.get(i).equals("NIL"))
							continue;
					}
				}
				System.out.format(">> ERROR [%d:%d] non matching parameters to function %s\n",this.lineNum, this.charNum,this.funcName);
				Globals.error(this.lineNum);
				System.exit(0);
			}
			else{
				if (p.get(i).equals(r.get(i)) == false){
					System.out.format(">> ERROR [%d:%d] non matching parameters to function %s\n",this.lineNum, this.charNum,this.funcName);
					Globals.error(this.lineNum);
					System.exit(0);
				}
			}
		}
		return tmp.returnType;
	}
	public TEMP IRme()
	{
		TEMP t = null;
		//common allocation code
		// save temporaries on stack
		IR.getInstance().Add_IRcommand(new IRCommand_PushTemps());
		// malloc on stack (using sub on sp) and store it on $a0
		IR.getInstance().Add_IRcommand(new IRcommand_comment("Allocating stack for arguments"));
		//we will temporarily hold the new arguments in $a2 until we actually have to call the function,
		// so we can use this functions arguments during the evaluation of the function call.
		IR.getInstance().Add_IRcommand(new IRCommand_MallocStack(REG_A.getInstance(2), this.paramsLength));
		// in case we're calling a class function, and member is not null
		if (this.member != null) {
			TEMP t1 = this.member.IRme();
			//store instance in $a1
			IR.getInstance().Add_IRcommand(new IRcommand_Move(REG_A.getInstance(1), t1));
		}

		// check if the function is PrintInt
		if (this.member == null && funcName.equals("PrintInt")) {
			IR.getInstance().Add_IRcommand(new IRcommand_PrintInt(this.params.head.IRme()));
		}else if (this.member == null && funcName.equals("PrintString")) {
			IR.getInstance().Add_IRcommand(new IRcommand_PrintString(this.params.head.IRme()));
		}else if (this.member == null && funcName.equals("PrintTrace")) {
			IR.getInstance().Add_IRcommand(new IRcommand_PrintTrace());
		}else {
			// user-defined function
			int count = 0;
			if (this.params != null) {
				// for each parameter, IR it before passing it to the function
				AST_EXP_LIST p = this.params;
				while (p != null && p.head != null) {
					AST_EXP h = (AST_EXP) p.head;
					// save register $a2 to stack in case of changes
					IR.getInstance().Add_IRcommand(new IRCommand_Push(REG_A.getInstance(2)));
					// evaluate IR on the parameter
					t = h.IRme();
					// restore register $a2 to hold the array of parameters
					IR.getInstance().Add_IRcommand(new IRCommand_Pop(REG_A.getInstance(2)));
					// pass the parameter to the function
					IR.getInstance().Add_IRcommand(new IRCommand_StoreWord(REG_A.getInstance(2), t, count));

					p = p.tail;
					count++;
				}
			}

			// in case we're calling a class function, and member is not null
			if (this.member != null) {
				//the IRme was already called, the member is now stored in $a1
				TEMP t1 = REG_A.getInstance(1);
				TEMP label_from_func_table = TEMP_FACTORY.getInstance().getFreshTEMP();
				int i = 0;
				for (; i < tc.localFuncs.size(); i++) {
					if (tc.localFuncs.get(i).split(" ")[0].equals(funcName))
						break;
				}
				//first: ensure not null
				IR.getInstance().Add_IRcommand(new IRcommand_Jump_If_Eq_To_Zero(t1, "invalid_pointer"));
				IR.getInstance().Add_IRcommand(new IRcommand_Get_Label_from_FuncTable(label_from_func_table, t1, i));
				//move arguments to a0 and call function
				IR.getInstance().Add_IRcommand(new IRCommand_Push(REG_A.getInstance(0)));
				IR.getInstance().Add_IRcommand(new IRcommand_Move(REG_A.getInstance(0), REG_A.getInstance(2)));
				IR.getInstance().Add_IRcommand(new IRcommand_Jalr(label_from_func_table));
				//restore a0
				IR.getInstance().Add_IRcommand(new IRCommand_Pop(REG_A.getInstance(0)));
			} else {
				String label = null;
				//System.out.println("HEY! WERE HERE WITH " + this.funcName + " IN " + SYMBOL_TABLE.getInstance().getLastScope());
				if (tc != null) {
					String[] breakIt;
					System.out.println("HEY! WERE HERE WITH " + this.funcName + " IN " + tc.name);
					// search for method in parents as well
					TYPE_CLASS cur_cls = tc;
					while (cur_cls != null) {
						for (String s : cur_cls.FuncsLabels) {
							breakIt = s.split("_");
							if (breakIt[3].equals(this.funcName)) {
								label = s;
								break;
							}
						}
						cur_cls = cur_cls.father;
					}
				}
				else {
					// extract the function label from stringList
					String[] breakIt;
					for (String[] pair : IR.funcList) {
						System.out.println(pair[0] + ", " + pair[1]);
						breakIt = pair[0].split("_");
						if (breakIt[2].equals("Func") && breakIt[3].equals(funcName) && breakIt.length == 4) {
							label = pair[0];
							break;
						}
					}
				}
				// jal to function label
				System.out.println("CALL TO FUNCTION WITH LABEL: " + label);
				if (label != null) {
					//before calling the function: save current function registers from $a0, and move $a2(the new args) to $a0
					IR.getInstance().Add_IRcommand(new IRCommand_Push(REG_A.getInstance(0)));
					IR.getInstance().Add_IRcommand(new IRcommand_Move(REG_A.getInstance(0), REG_A.getInstance(2)));
					IR.getInstance().Add_IRcommand(new IRcommand_Jal(label));
					IR.getInstance().Add_IRcommand(new IRCommand_Pop(REG_A.getInstance(0)));
				} else {
					System.out.println("ERROR - trying to jump to a function (label) which does not appear in funcList");
					Globals.error(this.lineNum);
					System.exit(0);
				}
			}
		}
		//common deallocation code
		IR.getInstance().Add_IRcommand(new IRcommand_comment("Deallocating stack for arguments"));
		IR.getInstance().Add_IRcommand(new IRCommand_MallocStack(-this.paramsLength));

		IR.getInstance().Add_IRcommand(new IRCommand_PopTemps());

		// return a temp that holds $v0
		TEMP result = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRCommand_LoadReturnValue(result));


		return result;
	}
}
