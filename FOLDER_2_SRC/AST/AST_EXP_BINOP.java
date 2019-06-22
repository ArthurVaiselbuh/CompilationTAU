package AST;

import TYPES.*;
import Globals.*;
import TEMP.*;
import IR.*;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;
	public boolean isString = false;
	public boolean isClass = false;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right,int OP)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		//System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP="";
		
		/*********************************/
		/* CONVERT OP to a printable sOP */
		/*********************************/
		if (OP == 0) {sOP = "=";}
		if (OP == 1) {sOP = "<";}
		if (OP == 2) {sOP = ">";}
		if (OP == 3) {sOP = "+";}
		if (OP == 4) {sOP = "-";}
		if (OP == 5) {sOP = "*";}
		if (OP == 6) {sOP = "/";}


		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");
		System.out.format("BINOP EXP(%s)\n",sOP);

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)",sOP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		if (left  != null) t1 = left.SemantMe();
		if (right != null) t2 = right.SemantMe();

		/* Equality Testing */
		if (this.OP == 0){
			/* Primitive types testing */
			if (t1 == TYPE_INT.getInstance() || t1 == TYPE_STRING.getInstance() || t2 == TYPE_INT.getInstance() || t2 == TYPE_STRING.getInstance()) {
				if (t1 != t2) {
					System.out.format(">> ERROR [%d:%d] non-matching types in equality testing\n", this.lineNum, this.charNum);
					Globals.error(this.lineNum);
				}
				if (t1 == TYPE_STRING.getInstance() || t2 == TYPE_STRING.getInstance())
					this.isString = true;
				return TYPE_INT.getInstance();
			}
			if (t1 instanceof TYPE_CLASS && t2 == TYPE_NIL.getInstance())
				return TYPE_INT.getInstance();
			if (t2 instanceof TYPE_CLASS && t1 == TYPE_NIL.getInstance())
				return TYPE_INT.getInstance();

			TYPE_CLASS o;
			if (t1 instanceof TYPE_CLASS && t2 instanceof TYPE_CLASS){
				boolean q = false;
				o = (TYPE_CLASS) t2;
				while (o != null) {
					if (o.name.equals(((TYPE_CLASS) t1).name)) {
						q = true;
						break;
					}
					o = o.father;
				}
				if (q) {
					return TYPE_INT.getInstance();
				}
				o = (TYPE_CLASS) t1;
				while (o != null) {
					if (o.name.equals(((TYPE_CLASS) t2).name)) {
						q = true;
						break;
					}
					o = o.father;
				}
				if (q) {
					return TYPE_INT.getInstance();
				}
			}
			if (t1 instanceof TYPE_ARRAY || t2 instanceof TYPE_ARRAY){
				if (t1 instanceof TYPE_ARRAY && t2 == TYPE_NIL.getInstance())
					return TYPE_INT.getInstance();
				if (t2 instanceof TYPE_ARRAY && t1 == TYPE_NIL.getInstance())
					return TYPE_INT.getInstance();
				if (t1 != t2) {
					System.out.format(">> ERROR [%d:%d] non-matching types arrays in equality testing\n", this.lineNum, this.charNum);
					Globals.error(this.lineNum);
				}
				return TYPE_INT.getInstance();
			}
			//both nil?
			if (t1 instanceof  TYPE_NIL && t2 instanceof  TYPE_NIL){
				return TYPE_INT.getInstance();
			}
			System.out.format(">> ERROR [%d:%d] non-matching types in equality testing\n", this.lineNum, this.charNum);
			Globals.error(this.lineNum);
		}
		/* Binary Operations */
		if (this.OP == 3 && (t1 == TYPE_STRING.getInstance()) && (t2 == TYPE_STRING.getInstance()))
		{
			//System.out.format(">> Problem 1\n",2,2);
			this.isString = true;
			return TYPE_STRING.getInstance();
		}
		if ((t1 == TYPE_INT.getInstance()) && (t2 == TYPE_INT.getInstance()))
		{
			//System.out.format(">> Problem 2\n",2,2);
			return TYPE_INT.getInstance();
		}
		System.out.format(">> ERROR [%d:%d] non-matching types in binary operation\n",this.lineNum, this.charNum);
		Globals.error(this.lineNum);
		return null;
	}


	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (left  != null) t1 = left.IRme();
		if (right != null) t2 = right.IRme();

		if (OP == 0) {
			if (!isString)
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Eq(dst,t1,t2));
			else
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Eq_Strings(dst,t1,t2));
		}
		else if (OP == 1) {
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2));
		}
		else if (OP == 2) {
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_GT_Integers(dst,t1,t2));
		}
		else if (OP == 3) {
			if (!isString)
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Integers(dst,t1,t2));
			else
				IR.getInstance().Add_IRcommand(new IRcommand_Binop_Add_Strings(dst,t1,t2));
		}
		else if (OP == 4) {
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Sub_Integers(dst,t1,t2));
		}
		else if (OP == 5) {
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Mul_Integers(dst,t1,t2));
		}
		else if (OP == 6) {
			IR.getInstance().Add_IRcommand(new IRcommand_Binop_Div_Integers(dst,t1,t2));
		}
		return dst;
	}

}
