/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;
import java_cup.runtime.Symbol;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; }
	public int getCharPos() { return yycolumn; }

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; }
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator	 = \r|\n|\r\n
WhiteSpace	  	 = {LineTerminator} | [ \t\f]
INTEGER			 = 0 | [1-9][0-9]*
ID				 = [a-zA-Z][a-zA-Z0-9]*
SPECIAL_CHAR     = [\(\)\[\]\?\!\.;{}\+\-\/\*\/]   //This is confusing because of escaping...
CommentChar      = [ \t\f]|[0-9]|[a-zA-Z]|{SPECIAL_CHAR}


%xstate IN_SINGLE_LINE_COMMENT
%xstate IN_MULTI_LINE_COMMENT
/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {
/* process comments */

"/*"                 {yybegin(IN_MULTI_LINE_COMMENT);}
"//"                 {yybegin(IN_SINGLE_LINE_COMMENT);}


/* Keywords */
"class"             { return symbol(TokenNames.CLASS);}
"nil"               { return symbol(TokenNames.NIL);}
"array"             { return symbol(TokenNames.ARRAY);}
"while"             { return symbol(TokenNames.WHILE);}
"extends"           { return symbol(TokenNames.EXTENDS);}
"return"            { return symbol(TokenNames.RETURN);}
"new"               { return symbol(TokenNames.NEW);}
"if"                { return symbol(TokenNames.IF);}


"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"*"				    { return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}
"("					{ return symbol(TokenNames.LPAREN);}
")"					{ return symbol(TokenNames.RPAREN);}
"{"					{ return symbol(TokenNames.LBRACE);}
"}"					{ return symbol(TokenNames.RBRACE);}
"["					{ return symbol(TokenNames.LBRACK);}
"]"					{ return symbol(TokenNames.RBRACK);}
";"					{ return symbol(TokenNames.SEMICOLON);}
","					{ return symbol(TokenNames.COMMA);}
"."					{ return symbol(TokenNames.DOT);}
":="				{ return symbol(TokenNames.ASSIGN);}
"="					{ return symbol(TokenNames.EQ);}
"<"					{ return symbol(TokenNames.LT);}
">"					{ return symbol(TokenNames.GT);}
{INTEGER}			{/*Make sure int is in legal range*/
                       int val = new Integer(yytext());
                       if ((int)val == Math.pow(2, 15))
                        {return symbol(TokenNames.MAXINT, val);}
                       if((int)val > Math.pow(2, 15))
                        {return symbol(TokenNames.error);}
                        return symbol(TokenNames.INT, val);}
{ID}				{ return symbol(TokenNames.ID,  new String( yytext()));}
\"[a-zA-Z]*\"       { return symbol(TokenNames.STRING,  new String( yytext()));}
{WhiteSpace}		{ /* just skip what was found, do nothing */ }
.                   {return symbol(TokenNames.error);}
<<EOF>>				{ return symbol(TokenNames.EOF);}
}

<IN_MULTI_LINE_COMMENT> {
//first priority - exit multiline comment
"*/"                {yybegin(YYINITIAL);}
//consume all other legit chars
({CommentChar}|{LineTerminator})      {}
"*"                            {}//match single star
// Anything else is a lexical error
.                   {return symbol(TokenNames.error);}
}

<IN_SINGLE_LINE_COMMENT> {
//first priority - exit comment
{LineTerminator}   {yybegin(YYINITIAL);}
// consume all other legit chars
({CommentChar}|"*")      {}
//Allow capturing EOF as a terminator to this lexer
<<EOF>>				{ yybegin(YYINITIAL);return symbol(TokenNames.EOF);}
// Anything else is a lexical error
.                   {return symbol(TokenNames.error);}
}
