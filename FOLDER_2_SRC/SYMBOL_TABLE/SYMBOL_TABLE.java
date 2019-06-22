/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import AST.AST_DEC_VAR;
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	private int hashArraySize = 13;
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;


	///Post-IR additions
	/** This will hold the amount of locals(variables only - not fields and functions/methods) at the current stack level.
	 * Value is incremented in AST_DEC_VAR
	 * */
	public static Stack<Integer> localsStack=new Stack<>();
	// This is a mapping of every global variable name to it's index in the globals table.
	public static HashMap<String, AST_DEC_VAR> globalsDict;
	
	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name,TYPE t)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name,t,hashValue,next,top,top_index++);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name)
	{
		SYMBOL_TABLE_ENTRY e;
				
		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		
		return null;
	}
	//get table entry for variable 'name'
	public SYMBOL_TABLE_ENTRY getTableEntry(String name){
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e;
			}
		}

		return null;
	}
	/**
	 * Get scope where variable "name" was declared
	 * Note: this assumes name is a properly declared variable.
	 * null is global scope.
	 */
	public TYPE_FOR_SCOPE_BOUNDARIES getScopeForVar(String name){
		if (top == null || top_index == 0)
			return null;
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				break;
			}
		}
		//found the variable. now get last scope starting from e:
		while (e.prevtop_index > 0){
			if (e.name.equals("SCOPE-BOUNDARY")) {
				TYPE_FOR_SCOPE_BOUNDARIES tmp = (TYPE_FOR_SCOPE_BOUNDARIES) (e.type);
				return tmp;
			}
			e = e.prevtop;
		}
		return null;
	}

	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope(char scope)
	{
		//Possible scope types: 'b', 'c', 'f', though f is used in the overloaded function only.
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
				"SCOPE-BOUNDARY",
				new TYPE_FOR_SCOPE_BOUNDARIES("NONE", scope));


		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}

	public void beginScope(char scope, String cls)
	{
		// Scope type here is: scope='c'
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
				"SCOPE-BOUNDARY",
				new TYPE_FOR_SCOPE_BOUNDARIES("NONE", scope, cls));

		// save current amount of local variables
		localsStack.push(localsStack.peek());

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}


	public void beginScope(char scope, String f, String returnType)
	{
		// Scope type here is: scope='f'
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
				"SCOPE-BOUNDARY",
				new TYPE_FOR_SCOPE_BOUNDARIES("NONE", scope, f, returnType));

		// save current amount of local variables
		localsStack.push(localsStack.peek());

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}


	public TYPE_FOR_SCOPE_BOUNDARIES getLastScope(){
		if (top == null || top_index == 0)
			return null;
		SYMBOL_TABLE_ENTRY e = top;
		while (e.prevtop_index > 0){
			if (e.name.equals("SCOPE-BOUNDARY")) {
				TYPE_FOR_SCOPE_BOUNDARIES tmp = (TYPE_FOR_SCOPE_BOUNDARIES) (e.type);
				return tmp;
			}
			e = e.prevtop;
		}
		return null;
	}


	public TYPE_FOR_SCOPE_BOUNDARIES getFunction(){
		if (top == null || top_index == 0)
			return null;
		SYMBOL_TABLE_ENTRY e = top;
		while (e.prevtop_index > 0){
			if (e.name.equals("SCOPE-BOUNDARY")) {
				TYPE_FOR_SCOPE_BOUNDARIES tmp = (TYPE_FOR_SCOPE_BOUNDARIES) (e.type);
				if (tmp.scope == 'f')
					return tmp;
			}
			e = e.prevtop;
		}
		return null;
	}

	public TYPE_FOR_SCOPE_BOUNDARIES getClassType(){
		if (top == null || top_index == 0)
			return null;
		SYMBOL_TABLE_ENTRY e = top;
		while (e.prevtop_index > 0){
			if (e.name.equals("SCOPE-BOUNDARY")) {
				TYPE_FOR_SCOPE_BOUNDARIES tmp = (TYPE_FOR_SCOPE_BOUNDARIES) (e.type);
				if (tmp.scope == 'c')
					return tmp;
			}
			e = e.prevtop;
		}
		return null;
	}



	public boolean isInLastScope(String name){
		if (top == null || top_index == 0)
			return false;
		//System.out.println("SYMBOL_TABLE is not empty");
		if (top.name.equals(name))
			return true;
		SYMBOL_TABLE_ENTRY e = top;
		//System.out.println("iterate in SYMBOL_TABLE:");
		while (e.prevtop_index > 0 && !e.name.equals("SCOPE-BOUNDARY")){
			//System.out.println("entry: " + e.name);
			if (e.name.equals(name)) {
				//TYPE_FOR_SCOPE_BOUNDARIES tmp = (TYPE_FOR_SCOPE_BOUNDARIES) (e.type);

				return true;
			}
			e = e.prevtop;
		}
		return false;

		/*SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null && e.name != "SCOPE-BOUNDARY"; e = e.next)
		{
			if (name.equals(e.name))
			{
				return true;
			}
		}

		return false;*/
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		while (!top.name.equals("SCOPE-BOUNDARY"))
		{
			table[top.index] = top.next;
			top_index = top_index-1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		table[top.index] = top.next;
		top_index = top_index-1;
		top = top.prevtop;

		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		PrintMe();
	}

	public int funcEndScope(){
		// line endScope but for function. Returns number of used locals.
		endScope();
		int cur_locals = localsStack.pop();
		return cur_locals - localsStack.peek();
	}
	
	public static int n=0;
	
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./FOLDER_5_OUTPUT/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtop_index);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TYPE_INT.getInstance());
			instance.enter("string",TYPE_STRING.getInstance());
			instance.enter("void",TYPE_VOID.getInstance());
			instance.enter("NIL",TYPE_NIL.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter(
					"PrintInt",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintInt",
							new TYPE_LIST(
									TYPE_INT.getInstance(),
									null)));

			instance.enter(
					"PrintString",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintString",
							new TYPE_LIST(
									TYPE_STRING.getInstance(),
									null)));

			instance.enter(
					"PrintTrace",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintTrace",
							null));
			globalsDict=new LinkedHashMap<>();
			localsStack=new Stack<>();
			localsStack.push(0);

		}
		return instance;
	}
	// Return whether the current scope is global
	public boolean isGlobalScope(){
		TYPE_FOR_SCOPE_BOUNDARIES last = getLastScope();
		return last == null;
	}
	// Increments internal representation of number of local variables(call when declaring a new local)
	// Returns number of local vars before the incrementation(effectively the index of the new var)
	public int addLocal(){
		int temp = localsStack.pop();
		localsStack.push(temp+1);
		return temp;
	}
}
