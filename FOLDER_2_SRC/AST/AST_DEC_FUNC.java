package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;


public class AST_DEC_FUNC extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String returnTypeName;
	public String name;
	public AST_TYPE_NAME_LIST params;
	public AST_STMT_LIST body;
	public int numParams;
	public boolean isMain = false;
	public boolean isGlobal = false;
	public String myClassName = "";
	public static TYPE_LIST myClassFields = null;
	public static int myClassFieldsNum = 0;

	/////////// For IRme:
	public int numOfLocals=0;
	public String label;
	public String label_end;
	public TEMP funcName_address;
	public String funcName_label;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_FUNC(
		String returnTypeName,
		String name,
		AST_TYPE_NAME_LIST params,
		AST_STMT_LIST body)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.returnTypeName = returnTypeName;
		this.name = name;
		this.params = params;
		this.body = body;
		this.numParams = 0;

		AST_TYPE_NAME_LIST p = this.params;
		while (p != null) {
			this.numParams++;
			p = p.tail;
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
		System.out.format("FUNC(%s):%s\n",name,returnTypeName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) params.PrintMe();
		if (body   != null) body.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FUNC(%s)\n:%s\n",name,returnTypeName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (params != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);		
		if (body   != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);		
	}

	public TYPE SemantMe()
	{
		TYPE t;
		TYPE returnType = null;
		TYPE_LIST type_list = null;

		// check if we are a global function, and if we are main function
		// else, get the name of the class we are in
		if (SYMBOL_TABLE.getInstance().isGlobalScope()) {
			// the function is global
			this.isGlobal = true;
			if (this.name.equals("main")) {
				this.isMain = true;
			}
			else {
				this.label = IRcommand.getFreshFuncLabel(this.name);
				this.label_end = IRcommand.getFreshFuncLabel(this.name + "_end");
				IR.funcList.add(new String[]{this.label, this.name});
			}
		} else {
			//TODO: uncomment?
			this.myClassName = SYMBOL_TABLE.getInstance().getLastScope().name;
//			this.myClassFields = ((TYPE_CLASS) (SYMBOL_TABLE.getInstance().find(this.myClassName))).data_members;
//			this.myClassFieldsNum = 0;
//			TYPE_LIST p = this.myClassFields;
//			while (p != null) {
//				this.myClassFieldsNum++;
//				p = p.tail;
//			}
		}
		/*******************/
		/* [0] return type */
		/*******************/
		returnType = SYMBOL_TABLE.getInstance().find(returnTypeName);
		if (returnType == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing return type %s\n",this.lineNum, this.charNum,returnType);
			Globals.error(this.lineNum);
			System.exit(0);
		}

		if (SYMBOL_TABLE.getInstance().find(name) != null){
			System.out.format(">> ERROR [%d:%d] function %s already exists\n",this.lineNum, this.charNum,name);
			Globals.error(this.lineNum);
			System.exit(0);
		}

		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		SYMBOL_TABLE.getInstance().beginScope('f', this.name,this.returnTypeName);

		/***************************/
		/* [2] Semant Input Params */
		/***************************/
		int localIndex=0;
		for (AST_TYPE_NAME_LIST it = params; it  != null; it = it.tail)
		{
			t = SYMBOL_TABLE.getInstance().find(it.head.type);
			if (t == null)
			{
				System.out.format(">> ERROR [%d:%d] non existing type %s\n",this.lineNum, this.charNum,it.head.type);
				Globals.error(this.lineNum);
			}
			else
			{
				type_list = new TYPE_LIST(t,type_list);
				SYMBOL_TABLE.getInstance().enter(it.head.name,t);
				//Save local index and source for future use
				Globals.debug(String.format("Argument index for func %s for %s is: %d",name,it.head.name, localIndex));
				SYMBOL_TABLE_ENTRY te = SYMBOL_TABLE.getInstance().getTableEntry(it.head.name);
				te.localIndex=localIndex;
				te.source = VarSource.argumnet;
			}
			localIndex++;
		}

		/* Allow recursion */
		SYMBOL_TABLE.getInstance().enter(name,new TYPE_FUNCTION(returnType,name,type_list));

		/*******************/
		/* [3] Semant Body */
		/*******************/
		body.SemantMe();

		/*****************/
		/* [4] End Scope */
		/*****************/
		numOfLocals = SYMBOL_TABLE.getInstance().funcEndScope();



//		// FOR PRINTTRACE:
//		// add the name of the function to stringList
//		label = IRcommand.getFreshString();
//		IR.stringList.add(new String[] {label, this.name});
//
//
//		Globals.debug(String.format("Num of locals for %s:%s\n",name,numOfLocals));

		Globals.debug("Num of locals:"+numOfLocals);


		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enter(name,new TYPE_FUNCTION(returnType,name,type_list));

		/*********************************************************/
		/* [6] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}

	public TEMP IRme()
	{
		// first, we need to check if the function is global. then, check if it's main or not
		// if it's global, the label should be just the name
		// else, give it a label of the name + "_" + class name

		//System.out.println("Current Function: " + name + ", label: " + label);

		String label = null;
		//System.out.println("CLASS: " + this.myClassName);
		if (this.label != null) {
			IR.getInstance().Add_IRcommand(new IRcommand_Label(this.label));
			//System.out.println(this.myClassName);
		}

		else{
			if (this.isMain) {
				label = "__main__";
				//System.out.println("000000");
			}
			else if (this.isGlobal) {
				label = IRcommand.getFreshFuncLabel(this.name);
				//System.out.println("111111 " + label);
			}
			if (label == null){
				label = IRcommand.getFreshFuncLabel(this.name);
				//System.out.println("444444");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Label(label));
			this.label = label;
		}

		//System.out.println("The field myclassName: " + this.myClassName + ", The label recieved is: " + label);
		// assign temps for functions name
		// adding the function real label to stringList
		//IR.funcList.add(new String[] {this.label, this.name});
		// FOR PRINTTRACE:
		// add the name of the function to stringList
		this.funcName_label = IRcommand.getFreshString();
		IR.stringList.add(new String[] {this.funcName_label, this.name});

		funcName_address = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRCommand_LoadAddress(funcName_address, this.funcName_label));

		// PROLOGUE
		IR.getInstance().Add_IRcommand(new IRCommand_Prologue(numOfLocals, this.isMain, this.funcName_address, this.name, this.myClassName));



//		// in case of a class method, we need to get the object's fields
//		// the object's address is saved on $a1
//		if (this.isGlobal == false && this.myClassName != "") {
//			for (int i = 0; i < this.myClassFieldsNum; i++) {
//				// here we want to create a temp for each field of the class:
//				TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
//				IR.getInstance().Add_IRcommand(new IRCommand_LoadWord(t, REG_A.getInstance(1), i));
//				IR.getInstance().Add_IRcommand(new IRCommand_StoreLocal(t, i)); // WARNING: might be -i
//			}
//			IR.getInstance().Add_IRcommand(new IRcommand_Move(REG_S.getInstance(7), REG_A.getInstance(1)));
//		}
//
//		// IR each one of the arguments:
//		AST_TYPE_NAME_LIST p = this.params;
//		while (p != null) {
//			AST_TYPE_NAME arg = p.head;
//			arg.IRme();
//			p = p.tail;
//		}

		String label_end = null;
		if(this.label_end == null){
			if (this.isGlobal && this.isMain) {
				label_end = IRcommand.getFreshFuncLabel("main_end");
			} else if (this.isGlobal) {
				label_end = IRcommand.getFreshFuncLabel(this.name + "_end");
			} /*else if (this.myClassName != "") {
				label_end = IRcommand.getFreshFuncLabel(this.name + "_" + this.myClassName + "_end");
			}*/
			if (label_end == null) {
				label_end = IRcommand.getFreshFuncLabel(this.name + "_end");
			}
			this.label_end = label_end;
		}

		// IR each statement in the body:
		if (body != null){
			this.body.labelEnd = this.label_end;
			//System.out.println("FUNC DEC " + this.name + " HAVE END LABEL: " + this.body.labelEnd);
			body.IRme();
		}

		IR.getInstance().Add_IRcommand(new IRcommand_Label(this.label_end));

		// EPILOGUE
		IR.getInstance().Add_IRcommand(new IRCommand_Epilogue(this.numOfLocals, this.isMain, this.funcName_address, this.numParams));



		return null;
	}

	//public void updateFuncTable()
	
}
