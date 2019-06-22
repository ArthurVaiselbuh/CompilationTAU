package RegisterAllocation;
import java.util.HashSet;

public class TempLivenessRange {
    int tmpIdx; //original index of the register, in a world of infinite registers.
    int assignedReg;//assigned register for this temp
    int defLine, endLine;
    HashSet<Integer> interferingWith; //set of temps that interfere with this one.

    //partial init(only index, define start and end lines later)
    public TempLivenessRange(int tmpIdx){
        interferingWith = new HashSet<>();
        this.tmpIdx = tmpIdx;
        this.defLine = -1;
        this.endLine = -1;
    }
    public void assignRegister(int regIdx) {
        this.assignedReg = regIdx;
    }

    public void addInterferance(TempLivenessRange t){
        interferingWith.add(t.tmpIdx);
    }

    //return whether this register interferes with other(they have a non empty cross range)
    public boolean checkInterference(TempLivenessRange other){
        if (other.tmpIdx == tmpIdx){
            //a temp does not interfere with itself
            return false;
        }
        //all possible intersections between two ranges:
        return  (this.defLine >= other.defLine && this.defLine <= other.endLine) ||
                (other.endLine >= this.defLine && other.endLine <= this.endLine) ||
                (other.defLine >= this.defLine && other.defLine <= this.endLine) ||
                (this.endLine >= other.defLine && this.endLine <= other.endLine);
    }

}
