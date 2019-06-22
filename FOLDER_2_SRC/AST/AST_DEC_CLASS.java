package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import java.util.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;
//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;


public class AST_DEC_CLASS extends AST_DEC
{
	/********/
	/* NAME */
	/********/
	public String name;

	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELD_LIST data_members;
	public String father;

	public TYPE_CLASS t;
	public ArrayList<String> localVars;
	public ArrayList<String> localFuncs;

	TYPE_CLASS finalClass;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASS(String name,AST_CFIELD_LIST data_members, String father)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (father == null) System.out.format("decClass -> name( %s )\n", name);
		else System.out.format("decClass -> name( %s ) extends name ( %s )\n", name, father);

		this.name = name;
		this.data_members = data_members;
		this.father = father;
		this.localVars = new ArrayList<String>();
		this.localFuncs = new ArrayList<String>();
		//this.funcLabels = new ArrayList<String>();
	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* PRINT FATHER ... */
		/*************************************/
		if (father == null)
			System.out.format("CLASS DEC = %s\n",name);
		else
			System.out.format("CLASS DEC = %s EXTENDS %s\n",name, father);
		if (data_members != null) data_members.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CLASS\n%s",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,data_members.SerialNumber);		
	}
	
	public TYPE SemantMe()
	{
		TYPE_FOR_SCOPE_BOUNDARIES scope = SYMBOL_TABLE.getInstance().getLastScope();
		if (scope != null && scope.scope != 'g'){
			System.out.format(">> ERROR [%d:%d] class must be defined in the outer scope\n",2,2);
			Globals.error(this.lineNum);
			System.exit(0);
		}

		if (SYMBOL_TABLE.getInstance().find(name) != null){
			System.out.format(">> ERROR [%d:%d] class %s already exists\n",6,6,name);
			Globals.error(this.lineNum);
			System.exit(0);
		}

		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope('c', name);

		TYPE_CLASS t;
		if (father != null && SYMBOL_TABLE.getInstance().find(father) != null)
			t = new TYPE_CLASS((TYPE_CLASS)(SYMBOL_TABLE.getInstance().find(father)),name,null, null);
		else
			t = new TYPE_CLASS(null,name,null, null);
		SYMBOL_TABLE.getInstance().enter(name,t);
		SYMBOL_TABLE.getInstance().getTableEntry(name).cls = name;

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		ArrayList<String> names = new ArrayList<String>();
		TYPE_LIST res = null;
		String[] mem;
		ArrayList<String> vals = new ArrayList<>();
		TYPE_CLASS f;
		if (father != null){
			f = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find(father);
			if (f == null){
				System.out.format(">> ERROR [%d:%d] class %s doesn't exist\n",6,6,father);
				Globals.error(this.lineNum);
				System.exit(0);
			}
			for (int i = 0; i < f.data_members_names.size(); i++){
				mem = f.data_members_names.get(i).split(" ");
				names.add(f.data_members_names.get(i));
				if (mem.length == 2)
					SYMBOL_TABLE.getInstance().enter(mem[0], SYMBOL_TABLE.getInstance().find(mem[1]));
			}
			vals.addAll(f.localVarsValues);
			//System.out.println("got from father: " + names);
			boolean finish;
			ArrayList<String> toblerones = new ArrayList<String>();
			for (AST_CFIELD_LIST it = data_members; it  != null; it = it.tail) {
				if (it.head.funcDec != null){
					String jackshit = it.head.funcDec.name + " f " + it.head.funcDec.returnTypeName;
					for (AST_TYPE_NAME_LIST e = it.head.funcDec.params; e != null; e = e.tail)
						jackshit += " " + e.head.type;
					mem = jackshit.split(" ");
					//System.out.println("function description: " + mem);
					finish = false;
					for (String temp : names){
						String[] mem_son = temp.split(" ");
						if (mem_son[0].equals(mem[0])) {
							if (mem.length != mem_son.length) {
								System.out.format(">> ERROR [%d:%d] can't overload methods with different signatures %s\n",6,6,mem_son[0]);
								Globals.error(it.lineNum);
								System.exit(0);
							}
							for (int k = 1; k < mem.length; k++) {
								//System.out.println("comparing parameter: " + mem[k] + " vs " + mem_son[k]);
								if (mem[k].equals(mem_son[k]) == false) {
									System.out.format(">> ERROR [%d] can't overload methods with different signatures %s\n",it.lineNum,mem_son[0]);
									Globals.error(it.lineNum);
									System.exit(0);
								}
							}
							finish = true;
						}
					}
					if (finish){
						//System.out.format(">> function that parent has: %s\n", mem[0]);
						toblerones.add(jackshit);
						//t.FuncsLabels.add(it.head.funcDec.label);
					}
				}
				else{
					String jackshit = it.head.varDec.name + " " + it.head.varDec.type;
					mem = jackshit.split(" ");
					for (String temp : names){
						String[] mem_son = temp.split(" ");
						if (mem_son[0].equals(mem[0])){
							System.out.format(">> ERROR [%d:%d] can't overload variable %s\n",this.lineNum, this.charNum,mem_son[0]);
							Globals.error(it.lineNum);
						}
					}
				}
			}
			for (String funcName:f.data_members_names){
				mem = funcName.split(" ");
				if (mem.length == 2)
					continue;
				boolean help_test = true;
				for (String funcName2: toblerones){
					String[] mem_son = funcName2.split(" ");
					if (mem[0].equals(mem_son[0])){
						help_test = false;
						break;
					}
				}
				if (help_test){
					TYPE_LIST params = null;
					//String func_descp = mem[0] + " f " + mem[2];
					for (int r = 3; r < mem.length; r++){
						params = new TYPE_LIST(SYMBOL_TABLE.getInstance().find(mem[r]), params);
						//func_descp += " " + mem[r];
					}
					SYMBOL_TABLE.getInstance().enter(mem[0], new TYPE_FUNCTION(SYMBOL_TABLE.getInstance().find(mem[2]), mem[0], params));
				}
			}
		}

		String tmp;
		//System.out.println("Setting data_members of " + name);
		data_members.myClassName = name;
		res = data_members.SemantMe();

		TYPE_CLASS tc = (TYPE_CLASS)SYMBOL_TABLE.getInstance().find(name);

		//System.out.println("AHAHAHHA " + res + ", " + names);
		tc.data_members = res;
		tc.data_members_names = names;
		tc.localFuncs = new ArrayList<>();
		tc.localVars = new ArrayList<>();
		//System.out.println("FROM DEC CLASS: " + tc.data_members_names);
		for (int i = 0; i < names.size(); i++){
				String curName = names.get(i);
				String[] break_c = curName.split(" ");
				//System.out.println("ITERATION: " + i + ", " + break_c);
				if (break_c.length > 2){
					tc.localFuncs.add(curName);
				}
				else {
					if (!tc.localVars.contains(curName))
						tc.localVars.add(curName);
				}
		}
		//System.out.println("FROM DEC CLASS: " + tc.localVars);
		tc.localVarsValues = vals;

		//System.out.println("got here 1: " + tc.data_members_names + ", " + tc.localVars + ", " + tc.localVarsValues + ", " +
		//		tc.localFuncs);
		TYPE tmp_ins;
		for (AST_CFIELD_LIST it = data_members; it  != null; it = it.tail)
		{
			if (it.head.funcDec == null){
				//System.out.println("name var: " + it.head.varDec.name);
				if (!names.contains(it.head.varDec.name + " " + it.head.varDec.type))
					names.add(it.head.varDec.name + " " + it.head.varDec.type);
				if (!(it.head.isInt || it.head.isString))
					vals.add(it.head.varDec.name + " nil");
				else if (it.head.isInt)
					vals.add(it.head.intVal + " int");
				else
					vals.add(it.head.strVal + " string");
			}
			else{
				tc.FuncsLabels.add(it.head.funcDec.label);
				tmp = it.head.funcDec.name + " f " + it.head.funcDec.returnTypeName;
				for (AST_TYPE_NAME_LIST e = it.head.funcDec.params; e != null; e = e.tail){
					tmp += " " + e.head.type;
				}
				if (!names.contains(tmp)){names.add(tmp);}
			}
		}

		tc.data_members_names = names;
		for (int i = 0; i < names.size(); i++){
			String curName = names.get(i);
			String[] break_c = curName.split(" ");
			//System.out.println("ITERATION: " + i + ", " + break_c);
			if (break_c.length > 2){
				tc.localFuncs.add(curName);
				//tc.FuncsLabels.add()
			}
			else {
				if (!tc.localVars.contains(curName))
					tc.localVars.add(curName);
			}
		}
		//System.out.println("FROM DEC CLASS: " + tc.localVars);
		tc.localVarsValues = vals;

		for (AST_CFIELD_LIST it = data_members; it  != null; it = it.tail)
		{
			if (it.head.funcDec != null) {
				//System.out.println("name func: " + it.head.funcDec.name);
				SYMBOL_TABLE.getInstance().beginScope('f', it.head.funcDec.name,it.head.funcDec.returnTypeName);
				//TYPE_LIST type_list = null;
				int localIndexParams = 0;
				for (AST_TYPE_NAME_LIST st = it.head.funcDec.params; st  != null; st = st.tail)
				{
					tmp_ins = SYMBOL_TABLE.getInstance().find(st.head.type);
					//type_list = new TYPE_LIST(tmp_ins,type_list);
					SYMBOL_TABLE.getInstance().enter(st.head.name,tmp_ins);
					SYMBOL_TABLE_ENTRY te = SYMBOL_TABLE.getInstance().getTableEntry(st.head.name);
					te.localIndex=localIndexParams++;
					te.source = VarSource.argumnet;
				}
				it.head.funcDec.body.SemantMe();
				it.head.funcDec.numOfLocals = SYMBOL_TABLE.getInstance().funcEndScope();
				tmp = it.head.funcDec.name + " f " + it.head.funcDec.returnTypeName;
				for (AST_TYPE_NAME_LIST e = it.head.funcDec.params; e != null; e = e.tail)
					tmp += " " + e.head.type;
				if (!names.contains(tmp))
					names.add(tmp);
			}
		}

		//System.out.println(vals);

		//System.out.println("got here 2: " + names);

		if (father != null && SYMBOL_TABLE.getInstance().find(father) != null)
			t = new TYPE_CLASS((TYPE_CLASS)(SYMBOL_TABLE.getInstance().find(father)),name,res, names);
		else
			t = new TYPE_CLASS(null,name,res, names);

		//((TYPE_CLASS)SYMBOL_TABLE.getInstance().find(name)).data_members_names = names;



		for (int i = 0; i < names.size(); i++){
			String[] break_c = names.get(i).split(" ");
			if (break_c.length > 2){
				this.localFuncs.add(names.get(i));
			}
			else
				this.localVars.add(names.get(i));
		}
		t.localFuncs = this.localFuncs;
		t.localVars = this.localVars;
		t.localVarsValues = vals;
		this.t = t;
		//System.out.println("got here 3: " + t.data_members_names + ", " + t.localVars + ", " + t.localVarsValues + ", " +
		//		t.localFuncs);
		//System.out.println("ORDERED LOCAL VARIABLES: " + this.localVars);
		//System.out.println("ORDERED LOCAL FUNCTIONS: " + this.localFuncs);

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SYMBOL_TABLE.getInstance().enter(name,t);
		finalClass = t;

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}


	public TEMP IRme() {
        data_members.IRme();
		return null;
    }

    public void allocateFuncTable(){
		t.functionTableLabel = t.name + "_funcTable";
		IR.getInstance().Add_IRcommand_FuncTable(new IRcommand_Create_Function_Table(t.functionTableLabel, t.localFuncs.size()));
	}

	public void writeFuncTable(){
		//ArrayList<String> functionLabels = new ArrayList<String>();
		generateLabels();
		//System.out.println("Functions Labels: " + finalClass.FuncsLabels);
		//System.out.println("Function sigs: " + localFuncs);
		for (int i = 0; i < this.finalClass.FuncsLabels.size(); i++){
			String func = finalClass.FuncsLabels.get(i);
			//IR.getInstance().Add_IRcommand(new IRcommand_Add_To_Function_Table(t.functionTableLabel, func, i));
			IR.getInstance().Add_IRcommand_WriteToTable(new IRcommand_Add_To_Function_Table(t.functionTableLabel, func, i));
		}
	}

	public void generateLabels(){
		TYPE_CLASS f;
		if (father != null){
			f = (TYPE_CLASS)SYMBOL_TABLE.getInstance().find(father);
			finalClass.FuncsLabels.addAll(f.FuncsLabels);
		}
		for (AST_CFIELD_LIST it = data_members; it != null; it = it.tail){
			if (it.head.funcDec != null) {
				//System.out.println(it.head.funcDec.label + " ahahhahaha");
				this.finalClass.FuncsLabels.add(it.head.funcDec.label);
			}
		}
		//System.out.println("LABELS: " + finalClass.FuncsLabels);
		String[] breakIt, breakIt2;
		String name, cls, label, label2, name2, cls2;
		ArrayList<Integer> locations = new ArrayList<>();
		boolean b;
		for (int i = 0; i < finalClass.FuncsLabels.size(); i++) {
			label = finalClass.FuncsLabels.get(i);
			breakIt = label.split("_");
			name = breakIt[3];
			cls = breakIt[4];
			//System.out.println("FIRST: " + name + "_" + cls);
			//System.out.println("CHECK: " + cls + " == " + this.name);
			if (cls.equals(this.name)) {
				b = false;
				for (int j = 0; j < finalClass.FuncsLabels.size(); j++) {
					label2 = finalClass.FuncsLabels.get(j);
					breakIt2 = label2.split("_");
					name2 = breakIt2[3];
					cls2 = father;
					//System.out.println("SECOND: " + name2 + "_" + breakIt2[4]);
					while (cls2 != null) {
						//System.out.println("Checking father: " + cls2);
						if (name.equals(name2) && cls2.equals(breakIt2[4]) && !label.equals(label2)) {
							//System.out.println("FOUND MATCH: " + name + "_" + cls + " and " + name2 + "_" + cls2);
							//System.out.println("REMOVE " + finalClass.FuncsLabels.get(j));
							finalClass.FuncsLabels.set(j, label);
							finalClass.FuncsLabels.remove(i);
							locations.add(j);
							//System.out.println("LABELS NOW: " + finalClass.FuncsLabels);
							b = true;
							break;
						}
						f = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find(cls2);
						if (f.father == null)
							break;
						cls2 = f.father.name;
					}
					if (b) {
						i--;
						break;
					}
				}
			}
		}
		//System.out.println("LABELS END: " + finalClass.FuncsLabels);
	}

}
