import org.jgrapht.graph.DirectedPseudograph;

import java.sql.BatchUpdateException;

/**
 * Created by fang on 4/6/18.
 */
public class ProcessOneLog {
    public static void main(String[] args){
        String path = "/home/fang/thesis2/Data/pythonRW.txt";
        String[]localIP = {"10.0.2.15"};
        String detection = "/home/fang/target.txt";
        process(path,localIP,detection);


    }

    private static void process(String logfile, String[] IP, String detection){
        GetGraph getGraph = new GetGraph(logfile, IP);
        getGraph.GenerateGraph();
        DirectedPseudograph<EntityNode, EventEdge> orignal = getGraph.getJg();
        BackTrack backTrack = new BackTrack(orignal);
        backTrack.backTrackPOIEvent(detection);
        backTrack.exportGraph("backTrack");
        CasualityPreserve cpr = new CasualityPreserve(backTrack.afterBackTrack);
        cpr.CPR();
        cpr.exportGraph("CPR");
    }
}
