package AST;

import TYPES.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_STMT_ASSIGN extends AST_STMT
{
	/***************/
	/*  var := exp */
	/*	var := newExp */
	/***************/
	public AST_EXP_VAR var;
	public AST_EXP exp;
	public AST_NEWEXP newExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_EXP_VAR var,AST_EXP exp, AST_NEWEXP newExp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		//System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
		this.newExp = newExp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************//*
		*//* AST NODE TYPE = AST ASSIGNMENT STATEMENT *//*
		*//********************************************//*
		System.out.print("AST NODE ASSIGN STMT\n");

		*//***********************************//*
		*//* RECURSIVELY PRINT VAR + EXP ... *//*
		*//***********************************//*
		if (var != null) var.PrintMe();
		if (exp != null) exp.PrintMe();

		*//***************************************//*
		*//* PRINT Node to AST GRAPHVIZ DOT file *//*
		*//***************************************//*
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ASSIGN\nleft := right\n");
		
		*//****************************************//*
		*//* PRINT Edges to AST GRAPHVIZ DOT file *//*
		*//****************************************//*
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);*/
	}
	public TYPE SemantMe()
	{
		TYPE t = null;
		TYPE r = null;
		TYPE t3 = null;

		if (exp == null && newExp == null){
			System.out.format(">> ERROR [%d:%d] some trash boy talking shit\n", this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		
		if (var != null) t = var.SemantMe();
		if (exp != null) r = exp.SemantMe();
		else if (newExp != null) r = newExp.SemantMe();

		TYPE_CLASS o;
		if (exp != null) {
			//r = initialValue.SemantMe();
			//System.out.println(t + ", " + r);
			if (r == TYPE_INT.getInstance() || r == TYPE_STRING.getInstance()) {
				if (r != t) {
					System.out.format(">> ERROR [%d:%d] non-matching types\n", this.lineNum, this.charNum);
					Globals.error(this.lineNum);
				}
			}
			else {
				if (t instanceof TYPE_ARRAY){
					//System.out.println(t.name + " = " + r.name);
					if (r instanceof TYPE_ARRAY){
						if (t != r){
							System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array\n", this.lineNum, this.charNum);
							Globals.error(this.lineNum);
						}
						return null;
					}
					if (!r.isClass() && r.name.equals(((TYPE_ARRAY)t).elementsType.name) == false){
						System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array\n", this.lineNum, this.charNum);
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
							System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array\n", this.lineNum, this.charNum);
							Globals.error(this.lineNum);
						}
					}
				}
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
						System.out.format(">> ERROR [%d:%d] non-matching types\n", this.lineNum, this.charNum);
						Globals.error(this.lineNum);
					}
				} else {
					//System.out.println(initialValue);
					//System.out.println(name + ", " + t + ", " + r);
					if (!((t instanceof TYPE_CLASS || t instanceof TYPE_ARRAY) && r == TYPE_NIL.getInstance())) {
						System.out.format(">> ERROR [%d:%d] non-matching types\n", this.lineNum, this.charNum);
						Globals.error(this.lineNum);
					}
				}
			}
		}
		else{
			boolean q = false;
			//System.out.println(t.name + " = " + r.name);
			//r = newValue.SemantMe();
			if (t instanceof TYPE_ARRAY){
				//System.out.println(t.name + " = " + r.name);
				if (!r.isClass() && !r.name.equals(((TYPE_ARRAY) t).elementsType.name)){
					System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array\n", this.lineNum, this.charNum);
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
					if (!test){
						System.out.format(">> ERROR [%d:%d] non-matching types for defintion of array\n", this.lineNum, this.charNum);
						Globals.error(this.lineNum);
					}
				}
			}
			if (t instanceof TYPE_CLASS && r instanceof TYPE_CLASS) {
				o = (TYPE_CLASS)r;
				while (o != null) {
					if (o.name.equals(((TYPE_CLASS) t).name)){
						q = true;
						break;
					}
					o = o.father;
				}
				if (!q){
					System.out.format(">> ERROR [%d:%d] non-matching types\n", this.lineNum, this.charNum);
					Globals.error(this.lineNum);
				}
			}
		}
		return null;
	}

	public TEMP IRme()
	{

		TEMP value;
		TEMP address=null;
		TEMP subscript=null;
		//first evaluate(IR) left side:
		if (this.var instanceof AST_EXP_VAR_SIMPLE) {
			//var simple requires no leftSide calculation
		}
		else if (this.var instanceof AST_EXP_VAR_SUBSCRIPT) {
			AST_EXP_VAR_SUBSCRIPT arr = (AST_EXP_VAR_SUBSCRIPT) this.var;
			if (arr.var != null) {address = arr.var.IRme();}
			if (arr.subscript != null) {subscript = arr.subscript.IRme();}
		}
		else if (this.var instanceof AST_EXP_VAR_FIELD) {
			((AST_EXP_VAR_FIELD) this.var).forAssign=true;
			address = this.var.IRme();
			((AST_EXP_VAR_FIELD) this.var).forAssign=false;
		}
		//now IR expression
		if (exp != null){
			value = exp.IRme();
		} else {
			value = newExp.IRme();
		}

		//finally do the assignment
		if (this.var instanceof AST_EXP_VAR_SIMPLE) {
			AST_EXP_VAR_SIMPLE var_s = (AST_EXP_VAR_SIMPLE) this.var;
			if (var_s.source==VarSource.global) {
				IR.getInstance().Add_IRcommand(new IRcommand_StoreGlobalVar(var_s.name, value));
			} else if (var_s.source==VarSource.local){
				IR.getInstance().Add_IRcommand(new IRCommand_StoreLocalVar(value, var_s.localIndex));
			} else if (var_s.source==VarSource.cfield){
				IR.getInstance().Add_IRcommand(new IRcommand_storeFieldVar(value, REG_A.getInstance(1), var_s.localIndex));
			} else  if (var_s.source==VarSource.argumnet) {
				IR.getInstance().Add_IRcommand(new IRcommand_storeArgument(value, var_s.localIndex));
			}
		}
		else if (this.var instanceof AST_EXP_VAR_SUBSCRIPT) {
			IR.getInstance().Add_IRcommand(new IRCommand_StoreVarSubscript(value, address,subscript));
			return value;
		}
		else if (this.var instanceof AST_EXP_VAR_FIELD) {
			AST_EXP_VAR_FIELD var_f = (AST_EXP_VAR_FIELD) this.var;
			IR.getInstance().Add_IRcommand(new IRcommand_storeFieldVar(value, address,var_f.localIndex));
		}
		else if (this.var instanceof AST_EXP_VAR_ID) {
			// TODO - the same, for eran: accessing methods from virtual table
		}


		return null;
	}

}
