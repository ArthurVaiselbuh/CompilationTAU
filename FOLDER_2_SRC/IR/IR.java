/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */

import MIPS.sir_MIPS_a_lot;
import SYMBOL_TABLE.SYMBOL_TABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*******************/

public class IR
{
	private IRcommand head=null;
	private IRcommandList tail=null;

	//globals for ir.
	//Tuples of (label, string)
	// add all strings to stringList so at the beginning they will load to the data section
	public static List<String[]> stringList = new ArrayList<>();
	// This list holds the real labels of each function, for jump on call
	// (label, string - name of function)
	public static List<String[]> funcList = new ArrayList<>();
	//When IRme is called for globals, need to add to a different list:
	public static boolean irForGlobals = false;
	private IRcommand globalsHead = null;
	private IRcommandList globalsTail = null;

	private IRcommandList funcTables = null;
	private IRcommandList writeToTables = null;

	/******************/
	/* Add IR command */
	/******************/
	public void Add_IRcommand(IRcommand cmd)
	{
		if (!irForGlobals){
			if ((head == null) && (tail == null))
			{
				this.head = cmd;
			}
			else if ((head != null) && (tail == null))
			{
				this.tail = new IRcommandList(cmd,null);
			}
			else
			{
				IRcommandList it = tail;
				while ((it != null) && (it.tail != null))
				{
					it = it.tail;
				}
				it.tail = new IRcommandList(cmd,null);
			}
		} else{
			//for globals!
			if ((globalsHead== null) && (globalsTail == null))
			{
				this.globalsHead = cmd;
			}
			else if ((globalsHead != null) && (globalsTail == null))
			{
				this.globalsTail = new IRcommandList(cmd,null);
			}
			else
			{
				IRcommandList it = globalsTail;
				while ((it != null) && (it.tail != null))
				{
					it = it.tail;
				}
				it.tail = new IRcommandList(cmd,null);
			}
		}
	}

	public void Add_IRcommand_FuncTable(IRcommand_Create_Function_Table cmd){
		if (funcTables == null){
			funcTables = new IRcommandList(cmd, null);
			return;
		}
		funcTables = new IRcommandList(cmd, funcTables);
	}

	public void Add_IRcommand_WriteToTable(IRcommand_Add_To_Function_Table cmd){
		if (writeToTables == null){
			writeToTables = new IRcommandList(cmd, null);
			return;
		}
		writeToTables = new IRcommandList(cmd, writeToTables);
	}

	public void ReverseFuncTables(){
		ArrayList<IRcommand_Create_Function_Table> arr = new ArrayList<>();
		for (IRcommandList it = this.funcTables; it != null; it = it.tail){
			arr.add((IRcommand_Create_Function_Table)it.head);
		}
		//Collections.reverse(arr);
		this.funcTables = null;
		for (IRcommand_Create_Function_Table ir: arr
			 ) {
			this.funcTables = new IRcommandList(ir, this.funcTables);
		}
	}

	public void ReverseWriteToTable(){
		ArrayList<IRcommand_Add_To_Function_Table> arr = new ArrayList<>();
		for (IRcommandList it = this.writeToTables; it != null; it = it.tail){
			arr.add((IRcommand_Add_To_Function_Table)it.head);
		}
		//Collections.reverse(arr);
		this.writeToTables = null;
		for (IRcommand_Add_To_Function_Table ir: arr
		) {
			this.writeToTables = new IRcommandList(ir, this.writeToTables);
		}
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		// First order of business: Create strings
		for (String[] pair:IR.stringList) {
			//tuples of label:string
			sir_MIPS_a_lot.getInstance().storeStringLiteral(pair[0], pair[1]);
		}
		//allocate globals:
		SYMBOL_TABLE.globalsDict.forEach((name,node)->sir_MIPS_a_lot.getInstance().declareGlobalVar(name));

		if (funcTables != null){
			ReverseFuncTables();
			funcTables.MIPSme();
		}
		sir_MIPS_a_lot.getInstance().endData();
		//start "main" label - this is not the program main,but the  compiler main function that inits globals
		sir_MIPS_a_lot.getInstance().label("main");

		if (writeToTables != null) {
			ReverseWriteToTable();
			this.writeToTables.MIPSme();
		}

		//now initialize globals:
		if (globalsHead != null) globalsHead.MIPSme();
		if (globalsTail != null) globalsTail.MIPSme();

		//Now: jump to main!
		sir_MIPS_a_lot.getInstance().jump("__main__");
		/**************************************
		 *********** error handlers ***********
		 **************************************/
		sir_MIPS_a_lot.getInstance().comment("Begin error handlers:");
		/**access violation(illegal  index)*/
		sir_MIPS_a_lot.getInstance().label("access_violation");
		sir_MIPS_a_lot.getInstance().printAccessViolation();
		sir_MIPS_a_lot.getInstance().exit();

		sir_MIPS_a_lot.getInstance().label("invalid_pointer");
		sir_MIPS_a_lot.getInstance().printIllegalPointer();
		sir_MIPS_a_lot.getInstance().exit();

		if (head != null) head.MIPSme();
		if (tail != null) tail.MIPSme();
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static IR instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected IR() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static IR getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new IR();
		}
		return instance;
	}
}
