package RegisterAllocation;

import Globals.Globals;
import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP_FACTORY;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;
import java.nio.file.Paths;


public class RegisterAllocator {
    public static void garphColor(String outputPath) throws IOException, InterruptedException {

        List<TempLivenessRange> tmp_list;
        List<String> outputContent = new ArrayList<>();
        Pattern p = Pattern.compile("Temp_(\\d+)");
        String inputFile = sir_MIPS_a_lot.pathToMIPSFile;
        int tmp_num= TEMP_FACTORY.getInstance().getCount();
        TempLivenessRange[] ranges = new TempLivenessRange[tmp_num];
        for (int i = 0; i < ranges.length; i++) {
            ranges[i] = new TempLivenessRange(i);
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
        int lineNum = 1;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Matcher m = p.matcher(line);
            //iterate over matches:
            while (m.find()) {
                //each match is a temp. Update it's entry in ranges:
                int tmp_idx = Integer.parseInt(m.group(1));
                if (ranges[tmp_idx].defLine == -1 || ranges[tmp_idx].defLine>lineNum){
                    //first appearance or not defined yet
                    ranges[tmp_idx].defLine =lineNum;
                }
                if (ranges[tmp_idx].endLine == -1 || ranges[tmp_idx].endLine<lineNum){
                    //first appearance or not defined yet
                    ranges[tmp_idx].endLine = lineNum;
                }
            }
            lineNum++;
        }

        //convert to list for easy sorting and iteration
        tmp_list = Arrays.asList(ranges);

        //For debugging purposes: print liveness ranges
//        for (TempLivenessRange t : tmp_list) {
//            Globals.debug(String.format("TEMP_%d range:[%d:%d]", t.tmpIdx,t.defLine, t.endLine));
//        }

        //iterate over all temps(V^2) and mark add all interferences to the graph.
        InterferenceGraph interGraph = new InterferenceGraph(tmp_list.size());
        for (TempLivenessRange v : tmp_list) {
            for (TempLivenessRange u : tmp_list) {
                if (u.checkInterference(v)) {
                    u.addInterferance(v);
                    interGraph.addInterference(v.tmpIdx, u.tmpIdx);
                }
            }
        }
        tmp_list.sort(Comparator.comparingInt(tempLivenessRange -> tempLivenessRange.interferingWith.size()));
        interGraph.colorGraph();
        HashMap<Integer, Integer> assignMap = interGraph.colorAssignemnts;
//        Globals.debug(assignMap.toString());

        //restart buffered reader to seek to zero..
        bufferedReader.close();
        bufferedReader = new BufferedReader(new FileReader(sir_MIPS_a_lot.pathToMIPSFile));
        //now: replace all temps!
        while ((line = bufferedReader.readLine()) != null) {
            Matcher m = p.matcher(line);
            StringBuffer line_sb = new StringBuffer();
            while (m.find()) {
                int tmp_idx = Integer.parseInt(m.group(1));
                m.appendReplacement(line_sb, "\\$t"+assignMap.get(tmp_idx));
            }
            m.appendTail(line_sb); // append the rest of the contents
            outputContent.add(line_sb.toString());
        }

        Files.write(Paths.get(outputPath),outputContent);
    }
}
