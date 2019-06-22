package Globals;

import java.io.PrintWriter;

public class Globals {
    public static PrintWriter file_writer;
    //for getting correct line in parser
    public static int parserLine, parserChar;
    public static int lexerChar;

    public static void error(int line){
        file_writer.println(String.format("ERROR(%d)", line));
        file_writer.close();
        new Exception().printStackTrace(System.out);
        System.exit(0);
    }
    public static void ok(){
        file_writer.println("OK");
        file_writer.close();
        new Exception().printStackTrace(System.out);
        System.exit(0);
    }

    public static void debugError(String msg){
        String errstr = String.format("ERROR(%s)", msg);
        System.out.println(errstr);
        file_writer.println(errstr);
        file_writer.close();
        new Exception().printStackTrace(System.out);
        System.exit(0);
    }

    public static void debug(String msg){
        System.out.println(msg);
    }
}