/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

import MIPS.*;
import TEMP.*;

import java.time.temporal.Temporal;

public class IRcommand_Binop_Add_Strings extends IRcommand {
    public TEMP t1;
    public TEMP t2;
    public TEMP dst;

    public IRcommand_Binop_Add_Strings(TEMP dst, TEMP t1, TEMP t2) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }
    /***************/
    /* MIPS me !!! */
    /***************/
    public void MIPSme() {

        TEMP charTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP src_offset = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP dst_offset = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP length = TEMP_FACTORY.getInstance().getFreshTEMP();

        String label_length = getFreshLabel("Length_String");
        String label2_length = getFreshLabel("Length_String");
        String label_concat = getFreshLabel("Concat_String1");
        String label2_concat = getFreshLabel("Concat_String1");

        /* Initiallize length to 1, for only the null byte */
        sir_MIPS_a_lot.getInstance().li(length, 1);

        /* Calculate both strings' length */
        sir_MIPS_a_lot.getInstance().move(src_offset, t1);
        sir_MIPS_a_lot.getInstance().strlen(length, charTemp, src_offset, label_length);
        sir_MIPS_a_lot.getInstance().move(src_offset, t2);
        sir_MIPS_a_lot.getInstance().strlen(length, charTemp, src_offset, label2_length);

        /* Allocate len(s1)+len(s2)+1 bytes on the heap */
        sir_MIPS_a_lot.getInstance().heap_allocate(length, dst);
        sir_MIPS_a_lot.getInstance().move(dst_offset, dst);

        /* Copy the strings to the new location */
        sir_MIPS_a_lot.getInstance().move(src_offset, t1);
        sir_MIPS_a_lot.getInstance().strcpy(dst_offset, charTemp, src_offset, label_concat);
        sir_MIPS_a_lot.getInstance().move(src_offset, t2);
        sir_MIPS_a_lot.getInstance().strcpy(dst_offset, charTemp, src_offset, label2_concat);
    }
}
