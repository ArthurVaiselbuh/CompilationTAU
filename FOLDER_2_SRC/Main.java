   
import java.io.*;
import java.io.PrintWriter;


import java_cup.runtime.Symbol;
import AST.*;
import IR.*;
import MIPS.*;
import Globals.*;
import RegisterAllocation.RegisterAllocator;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AST_DEC_LIST AST;
		FileReader file_reader;
		PrintWriter file_writer;
		String inputFilename = argv[0];
		String outputFilename = argv[1];
		
		try
		{
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			file_writer = new PrintWriter(outputFilename);
			Globals.file_writer = file_writer;
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);
			
			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			AST = (AST_DEC_LIST) p.parse().value;
			
			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			AST.PrintMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			AST.SemantMe();

			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			AST.IRAllocateFuncTables();
			// TODO: AST.IRWriteStrings();
			AST.IRWriteData();
			AST.IRme();
			
			/***********************/
			/* [9] MIPS the IR ... */
			/***********************/
			IR.getInstance().MIPSme();

			/**************************************/
			/* [10] Finalize AST GRAPHIZ DOT file */
			/**************************************/
			AST_GRAPHVIZ.getInstance().finalizeFile();			

			/***************************/
			/* [11] Finalize MIPS file */
			/***************************/
			sir_MIPS_a_lot.getInstance().finalizeFile();			

			/**************************/
			/* [12] Close output file */
			/**************************/
			RegisterAllocator.garphColor(outputFilename);

			file_writer.close();
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}


