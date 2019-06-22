package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import Globals.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_WHILE extends AST_STMT
{
	public AST_EXP cond;
	public AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_WHILE(AST_EXP cond,AST_STMT_LIST body)
	{
		this.cond = cond;
		this.body = body;
	}

	public TYPE SemantMe()
	{
		/****************************/
		/* [0] Semant the Condition */
		/****************************/
		if (cond.SemantMe() != TYPE_INT.getInstance())
		{
			System.out.format(">> ERROR [%d:%d] condition inside WHILE is not integral\n",this.lineNum, this.charNum);
			Globals.error(this.lineNum);
			System.exit(0);
		}

		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope('b');

		/***************************/
		/* [2] Semant Data Members */
		/***************************/
		body.SemantMe();

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;
	}

	public TEMP IRme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end   = IRcommand.getFreshLabel("while_statement_end");
		String label_start = IRcommand.getFreshLabel("while_statement_start");

		/*********************************/
		/* [2] entry label for the while */
		/*********************************/
		IR.
				getInstance().
				Add_IRcommand(new IRcommand_Label(label_start));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		TEMP cond_temp = cond.IRme();

		/******************************************/
		/* [4] Jump conditionally to the loop end */
		/******************************************/
		IR.
				getInstance().
				Add_IRcommand(new IRcommand_Jump_If_Eq_To_Zero(cond_temp,label_end));

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.labelEnd = this.label;
		body.IRme();

		/******************************/
		/* [6] Jump to the loop entry */
		/******************************/
		IR.
				getInstance().
				Add_IRcommand(new IRcommand_Jump_Label(label_start));

		/**********************/
		/* [7] Loop end label */
		/**********************/
		IR.
				getInstance().
				Add_IRcommand(new IRcommand_Label(label_end));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}
}