package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_DEC_VAR extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public AST_EXP initialValue;
	public AST_NEWEXP newValue;

	//global?local?
	public VarSource source;
	//The index of the var inside this location(i.e. index in argument list, in global vars, etc.)
	public int localIndex;

	// this is to prevent globals from running IRME twice
	private boolean ranIR = false;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VAR(String type,String name,AST_EXP initialValue, AST_NEWEXP newValue)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
		this.newValue = newValue;
	}

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		if (initialValue != null) System.out.format("VAR-DEC(%s):%s := initialValue\n",name,type);
		if (initialValue == null) System.out.format("VAR-DEC(%s):%s                \n",name,type);

		/**************************************/
		/* RECURSIVELY PRINT initialValue ... */
		/**************************************/
		if (initialValue != null) initialValue.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nDEC(%s)\n:%s",name,type));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (initialValue != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValue.SerialNumber);		
			
	}

	public TYPE SemantMe()
	{
		TYPE t;
	
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		//System.out.println("Check If Type " + type + " exists");

		if ("void".equals(type)){
			System.out.format(">> ERROR [%d:%d] can't decalre void type %s\n",this.lineNum, this.charNum,name);
			Globals.error(this.lineNum);
		}

		t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null)
		{
			System.out.format(">> ERROR [%d:%d] non existing type %s\n",this.lineNum, this.charNum,type);
			Globals.error(this.lineNum);
		}
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		//System.out.format("Check That Name %s does NOT exist in last scope: %s\n", name, SYMBOL_TABLE.getInstance().isInLastScope(name));
		TYPE nameRes = SYMBOL_TABLE.getInstance().find(name);
		if (nameRes != null){
			if (SYMBOL_TABLE.getInstance().isInLastScope(name)){
				System.out.format(">> ERROR [%d:%d] variable %s already exists in scope\n",this.lineNum, this.charNum,name);
				Globals.error(this.lineNum);
			}
			//ensure name is not an existing type. shadowing functions is allowed
			if (name.equals(nameRes.name) && (nameRes.equals("int") || nameRes.equals("string") ||nameRes.isClass()||nameRes.isArray())){
				System.out.format(">> ERROR [%d] cannot create variable with name that hides type: %s\n",this.lineNum, name);
				Globals.error(this.lineNum);
			}
		}

		boolean b = true;
		if (initialValue == null && newValue == null) {
			b = false;
		}

		TYPE r = null;
		TYPE_CLASS o;
		if (b) {
			if (initialValue != null) {
				r = initialValue.SemantMe();
				if (r == TYPE_INT.getInstance() || r == TYPE_STRING.getInstance()) {
					if (r != t) {
						System.out.format(">> ERROR [%d:%d] non-matching types for %s\n", this.lineNum, this.charNum, name);
						Globals.error(this.lineNum);
					}
				} else {
					if (t instanceof TYPE_CLASS && r instanceof TYPE_CLASS) {
						boolean q = false;
						o = (TYPE_CLASS) r;
						while (o != null) {
							if (o.name.equals(((TYPE_CLASS) t).name)) {
								q = true;
								break;
							}
							o = o.father;
						}
						if (!q) {
							System.out.format(">> ERROR [%d:%d] non-matching types for %s\n", this.lineNum, this.charNum, name);
							Globals.error(this.lineNum);
						}
					} else {
						//System.out.println(initialValue);
						//System.out.println(name + ", " + t + ", " + r);
						if (!((t instanceof TYPE_CLASS || t instanceof TYPE_ARRAY) && r == TYPE_NIL.getInstance()) //class and nil
								&& !((t instanceof TYPE_ARRAY && r instanceof  TYPE_ARRAY && t.name.equals(r.name))) //array assignment?
						) {
							System.out.format(">> ERROR [%d:%d] non-matching types for %s\n", this.lineNum, this.charNum, name);
							Globals.error(this.lineNum);
						}
					}
				}
			} else {
				boolean q = false;
				r = newValue.SemantMe();
				if (newValue.isArr == false && (r == TYPE_INT.getInstance() || r == TYPE_STRING.getInstance())){
					System.out.format(">> ERROR [%d:%d] new primitive type is not allowed: %s\n", this.lineNum, this.charNum, name);
					Globals.error(this.lineNum);
				}
				if (t instanceof TYPE_ARRAY){
					//System.out.println(t.name + " = " + r.name);
					if (!r.isClass() && r.name.equals(((TYPE_ARRAY)t).elementsType.name) == false){
						System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array %s\n", this.lineNum, this.charNum, name);
						Globals.error(this.lineNum);
					}
					if (r.isClass()){
						TYPE_CLASS tmp = (TYPE_CLASS)r;
						boolean test = false;
						while (tmp != null){
							if (tmp.name.equals(((TYPE_ARRAY) t).elementsType.name)){
								test = true;
								break;
							}
							tmp = tmp.father;
						}
						test = r.name.equals(((TYPE_ARRAY)t).elementsType.name);
						if (!test){
							System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array %s\n",this.lineNum, this.charNum, name);
							Globals.error(this.lineNum);
						}
					}
				}
				if (t instanceof TYPE_CLASS && r instanceof TYPE_CLASS) {
					o = (TYPE_CLASS) r;
					while (o != null) {
						if (o.name.equals(((TYPE_CLASS) t).name)) {
							q = true;
							break;
						}
						o = o.father;
					}
					if (!q) {
						System.out.format(">> ERROR [%d:%d] non-matching types for %s\n", this.lineNum, this.charNum, name);
						Globals.error(this.lineNum);
					}
				}
			}
		}

		TYPE_FOR_SCOPE_BOUNDARIES scope = SYMBOL_TABLE.getInstance().getLastScope();
		boolean b0, b1, b2, b3, b4, b5;
		//System.out.println(scope.scope);
		if(scope != null && scope.scope == 'c'){
			//System.out.println(t + ", " + initialValue + ", " + (initialValue instanceof AST_EXP_NIL));
			if (initialValue != null && newValue == null){
				if (t instanceof TYPE_ARRAY && initialValue.SemantMe() != TYPE_NIL.getInstance()){
					System.out.format(">> ERROR [%d:%d] data member %s inside a class can be initialized only with " +
							"a constant value\n", this.lineNum, this.charNum, name);
					Globals.error(this.lineNum);
				}
			}
			if (!(initialValue == null && newValue == null)) {
				b1 = newValue == null;
				b2 = initialValue != null;
				b3 = (t == TYPE_INT.getInstance() && initialValue instanceof AST_EXP_INT);
				b4 = (t == TYPE_STRING.getInstance() && initialValue instanceof AST_EXP_STRING);
				//b5 = (t instanceof TYPE_CLASS && initialValue instanceof AST_EXP_NIL);
				b5 = ((t instanceof TYPE_CLASS || t instanceof TYPE_ARRAY) && b2 && initialValue.SemantMe() == TYPE_NIL.getInstance());
				if (!((b1 && b2) && (b3 || b4 || b5))) {
					System.out.format(">> ERROR [%d:%d] data member %s inside a class can be initialized only with " +
							"a constant value\n", this.lineNum, this.charNum, name);
					Globals.error(this.lineNum);
				}
			}
		}

		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		//System.out.format("%s\n", t.name)
		TYPE_CLASS_VAR_DEC res=null;
		if (t instanceof TYPE_CLASS){
			res = new TYPE_CLASS_VAR_DEC((TYPE_CLASS)t, name);
			SYMBOL_TABLE.getInstance().enter(name,res);
		}
		else
			SYMBOL_TABLE.getInstance().enter(name,t);

		//determine the var location type
		if (SYMBOL_TABLE.getInstance().isGlobalScope()){
			source=VarSource.global;
			localIndex = SYMBOL_TABLE.globalsDict.size();
			SYMBOL_TABLE.globalsDict.put(name, this);
		}else {
			TYPE_FOR_SCOPE_BOUNDARIES s_type = SYMBOL_TABLE.getInstance().getLastScope();
			if (s_type.scope == 'c'){
				//class scope - this is a class field.
				//TODO: ERAN
				source = VarSource.cfield;
			} else {
				//Otherwise - this is a local variable!
				source=VarSource.local;
				localIndex=SYMBOL_TABLE.getInstance().addLocal();
				System.out.println("Local variable " + name + " with index: " + localIndex);
			}
		}
		//Save local index and source for future use
		SYMBOL_TABLE_ENTRY te = SYMBOL_TABLE.getInstance().getTableEntry(name);
		te.localIndex=localIndex;
		te.source = source;

		/*********************************************************/
		/* [4] Return value is irrelevant for variable declarations */
		/*********************************************************/
		return t;
	}

	public TEMP IRme()
	{
		if (ranIR){
			return null;
		}
		ranIR=true;
		TEMP exp_value;
		if (initialValue!=null){
			exp_value = initialValue.IRme();
		} else if (newValue!=null){
			exp_value=newValue.IRme();
		} else{
			exp_value=REG_ZERO.getInstance();
		}
		switch (source){
			case global:
				IR.getInstance().Add_IRcommand(new IRcommand_StoreGlobalVar(name, exp_value));
				break;
			case local:
				IR.getInstance().Add_IRcommand(new IRCommand_StoreLocalVar(exp_value, localIndex));
				break;
			case cfield:
				break; //should never actually get here, we have AST_CFIELD for that
			case argumnet:
				//TODO: Still trying to figure this out
				break;
			default:
				Globals.debugError(String.format("Unexpected case in ast_dec_var:%s", source));
		}
		return null;
	}

}
