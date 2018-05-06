import org.jgrapht.graph.DirectedPseudograph;

import java.sql.BatchUpdateException;

/**
 * Created by fang on 4/6/18.
 */
public class ProcessOneLog {
    public static void main(String[] args){
        String path = "/home/fang/pagerank/pagerank/pagerank/Data/Expdata2/aptgetInstallUnrar.txt";
        String[]localIP = {"10.0.2.15"};
        String detection = "/usr/bin/unrar-nonfree.dpkg-new";
        String[] highRP = {"10.0.2.15:58268->91.189.91.26:80"};
        process(path,localIP,detection,highRP);


    }

    private static void process(String logfile, String[] IP, String detection,String[] highRP){
        GetGraph getGraph = new GetGraph(logfile, IP);
        getGraph.GenerateGraph();
        DirectedPseudograph<EntityNode, EventEdge> orignal = getGraph.getJg();
        BackTrack backTrack = new BackTrack(orignal);
        backTrack.backTrackPOIEvent(detection);
        backTrack.exportGraph("backTrack");
        CasualityPreserve CPR = new CasualityPreserve(backTrack.afterBackTrack);
        CPR.CPR();
        InferenceRuputation infer = new InferenceRuputation(CPR.afterMerge);
        infer.calculateWeights();
        infer.initialReputation(highRP);
        infer.PageRankIteration();
        //infer.printConstantPartOfPageRank();
//        try {
//            infer.printWeights();
//        }catch ( Exception e){
//            e.printStackTrace();
//        }
        infer.checkWeightsAfterCalculation();
        infer.exportGraph("UnrarReputation");


    }
}
