/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.time.temporal.Temporal;
import IR.IRcommand;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class sir_MIPS_a_lot
{
	public int WORD_SIZE=4;
	private int MAXINT = 32767;
	private int MININT = -32768;

	public static String pathToMIPSFile = "./FOLDER_5_OUTPUT/MIPS.txt";

	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	/***********************/
	/* The file writer ... */
	/***********************/

	/* Exit (syscall 10) */
	public void finalizeFile()
	{
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();
	}

	/* Exit (syscall 10) */
	public void exit()
	{
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
	}

	/* Print the integer in t (syscall 1) followed by a space (syscall 11) */
	public void print_int(TEMP t) {
		// fileWriter.format("\taddi $a0,Temp_%d,0\n",idx);
		fileWriter.format("\tmove $a0,%s\n",t);
		fileWriter.format("\tli $v0,1\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tli $a0,32\n");
		fileWriter.format("\tli $v0,11\n");
		fileWriter.format("\tsyscall\n");
	}

	/* Print the string in t (syscall 4) */
	public void print_string(TEMP t) {
		fileWriter.format("\taddi $a0,%s,0\n",t);
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
	}

	// print string whose label is 'label'
	public void print_string_by_label(String label){
		fileWriter.format("\tlw $a0, %s\n", label);
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
	}

	public void print_trace(String label) {
		// create a temp to hold current frame pointer
		TEMP cur_fp = TEMP_FACTORY.getInstance().getFreshTEMP();
		int cur_fp_num = cur_fp.getSerialNumber();
		// create a temp to hold the value of 0
		TEMP zero = TEMP_FACTORY.getInstance().getFreshTEMP();
		int zero_num = zero.getSerialNumber();
		// cur_fp will hold the current frame pointer on each loop level
		fileWriter.format("\tmove %s, $fp\n", cur_fp);
		// zero will hold the value of 0
		fileWriter.format("\tsub %s, %s, %s\n", zero, cur_fp, cur_fp);
		// Print_Trace: // start a loop
		fileWriter.format("%s:\n", label);
		// load the pointer to the function name into a temp
		TEMP t_funcname = TEMP_FACTORY.getInstance().getFreshTEMP();
		fileWriter.format("\tlw %s, -4(%s)\n", t_funcname, cur_fp);
		// print this function's name
		print_string(t_funcname);
		// load into cur_fp the new fp (of the previos function)
		fileWriter.format("\tlw %s, 0(%s)\n", cur_fp, cur_fp);
		// jump back to loop, only if $t0 is not equal to 0
		// important: we initialize the first $fp to be 0 so we'll know we reached main function
		fileWriter.format("\tbne %s, %s, %s\n", cur_fp, zero, label);

	}

	//public TEMP addressLocalVar(int serialLocalVarNum)
	//{
	//	TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
	//	int idx = t.getSerialNumber();
	//
	//	fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
	//
	//	return t;
	//}
	public void allocate(String var_name)
	{
		fileWriter.format(".data\n");
		fileWriter.format("\tglobal_%s: .word 721\n",var_name);
	}

	/* Allocate size words on the stack */
	public void stack_allocate(int size)
	{
		fileWriter.format("\taddi $sp, $sp, %d\n", -size*WORD_SIZE);
	}

	/* Allocate size words on the stack and set target to point to top of the stack */
	public void stack_allocate(int size, TEMP target)
	{
		fileWriter.format("\taddi $sp, $sp, %d\n", -size*WORD_SIZE);
		fileWriter.format("\tmove %s, $sp\n", target);
	}

	/* Allocate size words on the heap, and return a pointer to the allocated memory */
    public void heap_allocate(int size, TEMP dest){
		//save a0 first
		stack_allocate(1);
		save_on_stack(REG_A.getInstance(0),0);
        fileWriter.format("\tli $v0,9\n");
        fileWriter.format("\tli $a0 ,%d\n",size*WORD_SIZE);
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s,$v0\n", dest);
		//pop a0
		load_from_stack(REG_A.getInstance(0),0);
		stack_allocate(-1);
    }
	public void heap_allocate(TEMP size, TEMP dest){
		//multiply by word size? NO. this should be handled beforehand in the mipsme.
		//save a0 first
		stack_allocate(1);
		save_on_stack(REG_A.getInstance(0),0);
		fileWriter.format("\taddi $a0, %s, 0\n", size);
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\taddi %s, $v0, 0\n", dest);
		//pop a0
		load_from_stack(REG_A.getInstance(0),0);
		stack_allocate(-1);
	}

	public void declareGlobalVar(String name){
    	//create a label: global_name and initially set value to zero.
    	fileWriter.printf("global_%s: .word 0\n",name);
	}

	public void loadGlobalVar(TEMP dst, String var_name)	{
		fileWriter.format("\tla %s, %s\n", dst, "global_" + var_name);
		fileWriter.format("\tlw %s,0(%s)\n",dst,dst);
	}

	public void storeGlobalVar(String var_name, TEMP src)	{
    	TEMP addr = TEMP_FACTORY.getInstance().getFreshTEMP();
		fileWriter.format("\tla %s, global_%s\n",addr,var_name);
		fileWriter.format("\tsw %s, 0(%s)\n",src, addr);
	}
	public void loadAddress(TEMP dst, String label){
		fileWriter.format("\tla %s, %s\n", dst,label);
	}
	public void loadWord(TEMP dst, TEMP src, int offset){
		fileWriter.format("\tlw %s, %d(%s)\n", dst,offset*WORD_SIZE, src);
	}
	public void storeWord(TEMP addr, TEMP src, int offset){
		//store word src into addr+offset
		fileWriter.format("\tsw %s, %d(%s)\n", src, offset*WORD_SIZE, addr);
	}
	public void storeLocalVar(TEMP src, int offset){
		//store local temp src into into it's local addr: fp+offset
		//NOTE: we need the 2 because of the PrintTrace and to not override ebp
		fileWriter.format("\tsw %s, %d($fp)\n", src, -(2+offset)*WORD_SIZE);
	}
	public void storeArgument(TEMP src, int offset){
		//store temp src into into it's location in the argument list
		fileWriter.format("\tsw %s, %d(%s)\n", src, (offset)*WORD_SIZE, REG_A.getInstance(0));
	}

	public void load(TEMP dst, TEMP src){
    	//load [src] to dst
    	fileWriter.format("\tlw %s, 0(%s)\n", dst, src);
	}
	public void loadLocalVar(TEMP target, int offset){
		// epb - 4 - 4 * offset (another word for the PrintTrace entry)
		fileWriter.format("\tlw %s, %d($fp)\n", target, -(2+offset)*WORD_SIZE);
	}

	//this is probably not used
	public void loadParamVar(TEMP dst, int offset){
    	//ebp + 8 + 4 * offset
    	fileWriter.format("\tlw %s, %d($fp)\n", dst, WORD_SIZE * (offset + 2));
	}
	public void storeParamVar(TEMP value, int offset){
		fileWriter.format("\tsw %s, %d($fp)\n", value, WORD_SIZE * (offset + 2));
	}

	public void loadArgument(TEMP target, int offset){
		//load argument index "offset" into target Temp.
		fileWriter.format("\tlw %s, %d(%s)\n", target, (offset)*WORD_SIZE, REG_A.getInstance(0));
	}
	public void push_temps(){
    	stack_allocate(10);
    	for(int i=0;i<8;i++){
			fileWriter.format("\tsw $t%d, %d($sp)\n", i, i*WORD_SIZE);
		}
		fileWriter.format("\tsw $a0, %d($sp)\n", 8*WORD_SIZE);
		//store $a1 which holds class instance during method calls
		fileWriter.format("\tsw $a1, %d($sp)\n", 9*WORD_SIZE);
	}
	public void pop_temps(){
		for(int i=0;i<8;i++){
			fileWriter.format("\tlw $t%d, %d($sp)\n", i, i*WORD_SIZE);
		}
		fileWriter.format("\tlw $a0, %d($sp)\n", 8*WORD_SIZE);
		fileWriter.format("\tlw $a1, %d($sp)\n", 9*WORD_SIZE);
		stack_allocate(-10);
	}
	public void save_on_stack(TEMP value, int offset){
		fileWriter.format("\tsw %s, %d($sp)\n", value, offset*WORD_SIZE);
    }
	public void load_from_stack(TEMP value, int offset){
		fileWriter.format("\tlw %s, %d($sp)\n", value, offset*WORD_SIZE);
	}

	/* Load immidiate to t */
	public void li(TEMP t,int value) {
		fileWriter.format("\tli %s,%d\n",t,value);
	}

	/**** Arithmetic Instructions ****/
	/* dst = oprnd1 + oprnd2 */
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		fileWriter.format("\tadd %s,%s,%s\n",dst,oprnd1,oprnd2);
	}
	public void addi(TEMP dst, TEMP oprnd1,int i)
	{
		fileWriter.format("\taddi %s,%s,%d\n",dst,oprnd1,i);
	}

	/* dst = oprnd1 - oprnd2 */
	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		fileWriter.format("\tsub %s,%s,%s\n",dst,oprnd1,oprnd2);
	}

	/* dst = oprnd1 * oprnd2 */
	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2) {
		fileWriter.format("\tmult %s,%s\n",oprnd1,oprnd2);
		fileWriter.format("\tmflo %s\n", dst);
	}

	/* dst = oprnd1 / oprnd2 */
	public void div(TEMP dst,TEMP oprnd1,TEMP oprnd2) {
		fileWriter.format("\tdiv %s,%s,%s\n",dst,oprnd1,oprnd2);
		fileWriter.format("\tmflo %s\n", dst);
	}

	/* Create a new label */
	public void label(String inlabel)
	{
		fileWriter.format("%s:\n",inlabel);
	}

	/**** Jump Instructions ****/
	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
	}
	public void jal(String target){fileWriter.format("\tjal %s\n",target);}
	public void jalr(TEMP target){fileWriter.format("\tjalr %s\n",target);}

	/**** Branch Instructions ****/
	/* Branch to label if oprnd1 < oprnd2 */
	public void blt(TEMP oprnd1,TEMP oprnd2,String label) {
		fileWriter.format("\tblt %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 > oprnd2 */
	public void bgt(TEMP oprnd1,TEMP oprnd2,String label) {
		fileWriter.format("\tbgt %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 >= oprnd2 */
	public void bge(TEMP oprnd1,TEMP oprnd2,String label) {
		fileWriter.format("\tbge %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 <= oprnd2 */
	public void ble(TEMP oprnd1, TEMP oprnd2, String label){
		fileWriter.format("\tble %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 != oprnd2 */
	public void bne(TEMP oprnd1,TEMP oprnd2,String label) {
		fileWriter.format("\tbne %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 != 0 */
	public void bnez(TEMP oprnd1,String label) {
		fileWriter.format("\tbnez %s,%s\n",oprnd1,label);
	}

	/* Branch to label if oprnd1 = oprnd2 */
	public void beq(TEMP oprnd1,TEMP oprnd2,String label) {
		fileWriter.format("\tbeq %s,%s,%s\n",oprnd1,oprnd2,label);
	}

	/* Branch to label if oprnd1 = 0 */
	public void beqz(TEMP oprnd1,String label) {
		fileWriter.format("\tbeq %s,$zero,%s\n",oprnd1,label);
	}

	/**** Error Messages ****/
	public void printDivByZero(){
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tla $a0, string_illegal_div_by_0\n");
		fileWriter.format("\tsyscall\n");
	}

	public void printIllegalPointer(){
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tla $a0, string_invalid_ptr_dref\n");
		fileWriter.format("\tsyscall\n");
	}

	public void printAccessViolation() {
		fileWriter.format("\tli $v0, 4\n");
		fileWriter.format("\tla $a0, string_access_violation\n");
		fileWriter.format("\tsyscall\n");
	}

	public void move(TEMP dst, TEMP src) {
		fileWriter.format("\tmove %s,%s\n", dst, src);
	}

	public void moveReturnValue(TEMP dst) {
		fileWriter.format("\tmove %s, $v0\n", dst);
	}

	public void storeStringLiteral(String label, String value){
		fileWriter.format("%s: .asciiz \"%s\"\n", label, value);
	}

	public void funcPrologue(boolean isMain, int numOfLocals, TEMP funcName_address, String name, String cls){
		// allocate old-ebp and return address on stack
        fileWriter.format("\taddi $sp, $sp, %d\n", -2*WORD_SIZE);
        // now esp stands on return address
        fileWriter.format("\tsw $ra, 4($sp)\n");
        // now esp stands on old-ebp
		// if we are in main function, we save old-ebp to be 0 for print_trace
		if(isMain) {
			fileWriter.format("\tsub $fp, $fp, $fp\n");
		}
        fileWriter.format("\tsw $fp, 0($sp)\n");
        // now set new ebp to hold current esp
        fileWriter.format("\tmove $fp, $sp\n");
        // PRINTTRACE: we want to save the name of the function between ebp and the locals
		// allocate room on the stack for the pointer
		fileWriter.format("\taddi $sp, $sp, %d\n", -1*WORD_SIZE);
		// save the string address of the function name
		int funcName = funcName_address.getSerialNumber();
		fileWriter.format("\tsw %s, 0($sp)\n", funcName_address);
        // allocate stack frame for the func name and locals
        fileWriter.format("\taddi $sp, $sp, %d\n", -WORD_SIZE*(numOfLocals));

		//this label isn't(currently) being used, it's just for mips readability. can remove this.
		//System.out.println("CREATING LABEL FOR " + name);
		if (!cls.equals("")) {
			label(IRcommand.getFreshLabel("prolog_end_" + name + "_" + cls));
		}
		else {
			label(IRcommand.getFreshLabel("prolog_end_" + name));
		}
		comment("func prolog end");

	}

	public void funcEpilogue(boolean isMain, int numOfLocals, TEMP funcName_address, int numParams){
		// de-allocate locals and arguments/temporaries from the frame
		fileWriter.format("\taddi $sp, $sp, %d\n",WORD_SIZE*(numOfLocals+1));
		// now esp stands on epb // restore ebp
		fileWriter.format("\tlw	$fp, ($sp)\n");
		// now esp stands on return address // restore return address
		fileWriter.format("\tlw	$ra, 4($sp)\n");
		// de-allocate old-ebp and return address from the stack
		fileWriter.format("\taddi $sp, $sp, %d\n", WORD_SIZE*2);
		// in case we are in main function, we don't want to jump to the return address
		if (isMain){
			return;
		}
		fileWriter.format("\tjr $ra\n");

	}

	/**** Important Functions ****/

	/* A function to set a temp dst to 0 or 1, depending on return value of a function */
	public void returnFunction(TEMP dst, String label_return_1, String label_return_0, String end_label){
		//label(label_return_1);
		fileWriter.format("%s:\n", label_return_1);
		fileWriter.format("\tli %s, 1\n", dst);
		fileWriter.format("\tj %s\n", end_label);
		//label(label_return_0);
		fileWriter.format("%s:\n", label_return_0);
		fileWriter.format("\tli %s, 0\n", dst);
		//label(end_label);
		fileWriter.format("%s:\n", end_label);
	}

	/* A function to compare 2 strings */
	public void strcmp(TEMP ch1, TEMP ch2, TEMP offset1, TEMP offset2, String label_comp_loop,
					   String label_return_1, String label_return_0){

		fileWriter.format("%s:\n", label_comp_loop);
		//label(label_comp_loop);
		fileWriter.format("\tlb %s, 0(%s)\n", ch1, offset1);
		fileWriter.format("\tlb %s, 0(%s)\n", ch2, offset2);
		/* s1[i] =? s2[i] */
		fileWriter.format("\tbne %s, %s, %s\n", ch1, ch2, label_return_1);
		/* String are equal */
		fileWriter.format("\tbeq %s, $zero, %s\n", ch1, label_return_0);
		/* i++ */
		fileWriter.format("\taddi %s, %s, 1\n", offset1, offset1);
		fileWriter.format("\taddi %s, %s, 1\n", offset2, offset2);
		fileWriter.format("\tj %s\n", label_comp_loop);
	}

	/* The function checks whther an arithmetic action resulted in an overflow or underflow */
	public void check_int_extreme(TEMP dst, String label_overflow_max, String label_overflow__min, String label_end){

		/* Allocate temporary variables for the extreme values */
		TEMP intMax = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP intMin = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().li(intMax, MAXINT);
		sir_MIPS_a_lot.getInstance().li(intMin, MININT);

		/* Check if the resulting value overflows */
		sir_MIPS_a_lot.getInstance().blt(intMax,dst,label_overflow_max); // overflow when intMax < dst
		sir_MIPS_a_lot.getInstance().bgt(intMin,dst,label_overflow__min); // overflow when inMin > dst
		sir_MIPS_a_lot.getInstance().jump(label_end); // no overflow, just proceed to the next instruction

		/* Max overflow case */
		sir_MIPS_a_lot.getInstance().label(label_overflow_max);
		sir_MIPS_a_lot.getInstance().li(dst, MAXINT);
		sir_MIPS_a_lot.getInstance().jump(label_end);

		/* Min overflow case */
		sir_MIPS_a_lot.getInstance().label(label_overflow__min);
		sir_MIPS_a_lot.getInstance().li(dst, MININT);

		/* End label */
		sir_MIPS_a_lot.getInstance().label(label_end);
	}

	/* The function counts a string's length and puts the result in length TEMP */
	public void strlen(TEMP length, TEMP charTemp, TEMP src, String inlabel){
		fileWriter.format("%s:\n", inlabel);
		fileWriter.format("\tlb %s, 0(%s)\n", charTemp, src);
		fileWriter.format("\taddi %s, %s, 1\n", length, length);
		fileWriter.format("\taddi %s, %s, 1\n", src, src);
		fileWriter.format("\tbnez %s, %s\n", charTemp, inlabel);
		fileWriter.format("\taddi %s, %s, -1\n", length, length);
	}

	/* The function copies the string in src to dst */
	public void strcpy(TEMP dst, TEMP charTemp, TEMP src, String inlabel){
		fileWriter.format("%s:\n", inlabel);
		fileWriter.format("\tlb %s, 0(%s)\n", charTemp, src);
		fileWriter.format("\tsb %s, 0(%s)\n", charTemp, dst);
		fileWriter.format("\taddi %s, %s, 1\n", src, src);
		fileWriter.format("\taddi %s, %s, 1\n", dst, dst);
		fileWriter.format("\tbnez %s, %s\n", charTemp, inlabel);
		fileWriter.format("\taddi %s, %s, -1\n", dst, dst);
	}

	/* Allocate space for the function table */
	public void allocateDataSpace(String name, int size){
		fileWriter.format("%s: .space %d\n", name, size*WORD_SIZE);
	}

	/* The function adds a given function to the function table */
	public void addToFuncTable(String tableLabel, String functionLabel, TEMP tableAdress, TEMP functionAdress, int offset){
		fileWriter.format("\tla %s, %s\n", tableAdress, tableLabel);
		fileWriter.format("\tla %s, %s\n", functionAdress, functionLabel);
		fileWriter.format("\taddi %s, %s, %d\n", tableAdress, tableAdress, WORD_SIZE * offset);
		fileWriter.format("\tsw %s, (%s)\n", functionAdress, tableAdress);

	}

	public void set_zero(TEMP t){
		fileWriter.format("\taddi %s, $zero, 0\n",t);
	}

	public void saveRAonStack() {
	    fileWriter.format("\tsw $ra, 0($sp)\n");
    }
    public void savePrologueOnStack(int numParams) {
		// push ebp
		fileWriter.format("\tsub $sp, $sp, %d\n", WORD_SIZE);
		fileWriter.format("\tsw $fp, 0($sp)\n");
		fileWriter.format("\tmove $fp, $sp\n");

		// allocate stack for arguments
		fileWriter.format("\tsub $sp, $sp, %d\n", numParams*WORD_SIZE);

    }


    public void comment(String msg) {
		fileWriter.format("\t# %s\n", msg);
	}

	public void storeReturnValue(TEMP t) {
		fileWriter.format("\tmove $v0, %s\n", t);
	}

	public void loadReturnValue(TEMP t){fileWriter.format("\taddi %s, $v0, 0\n", t );}

	public void endData(){
		fileWriter.format(".text\n");
	}

	public void align(){fileWriter.format(".align 2\n");}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static sir_MIPS_a_lot instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected sir_MIPS_a_lot() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static sir_MIPS_a_lot getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new sir_MIPS_a_lot();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(pathToMIPSFile);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.fileWriter.print(".data\n");
			instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Division By Zero\"\n");
			instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}
