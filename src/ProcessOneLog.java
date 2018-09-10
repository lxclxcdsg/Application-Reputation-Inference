import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;

import java.sql.BatchUpdateException;
import java.util.List;

/**
 * Created by fang on 4/6/18.
 */
public class ProcessOneLog {
    public static void main(String[] args){
        String path = "/home/fang/pagerank/pagerank/pagerank/Data/Expdata2/aptgetInstallCurl.txt";
        String[]localIP = {"10.0.2.15"};
        String detection = "/usr/bin/curl.dpkg-new";
        String[] highRP = {"10.0.2.15:52256->172.217.3.46:443"};
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
//        GraphConnectivity connectivity = new GraphConnectivity(CPR.afterMerge);
//        connectivity.testStrongConn("Connectivity.txt");
//        connectivity.testWeakConnectivity("WeakConnectivity.txt");
        InferenceRuputation infer = new InferenceRuputation(CPR.afterMerge);
        infer.calculateWeights();
        infer.initialReputation(highRP);
        infer.PageRankIteration();
        //infer.onlyPrintHeightestWeights(detection);
        IterateGraph iter = new IterateGraph(infer.graph);
        infer.exportGraph("instllCurl");
        IterateGraph iterGraph = new IterateGraph(infer.graph);
        iterGraph.filterGraphBasedOnAverageWeight();
        iterGraph.exportGraph("FilteredInstallCural");
//        List<DirectedPseudograph<EntityNode, EventEdge>> paths = iterGraph.getHighWeightPaths(detection);
//        for(int i=0; i< paths.size();i++){
//            IterateGraph iter = new IterateGraph(paths.get(i));
//            String fileName = String.valueOf(i) + "path";
//            iter.exportGraph(fileName);
//        }
        iterGraph.printEdgesOfVertex("11035dpkg");

//        infer.checkWeightsAfterCalculation();
//        infer.exportGraph("UnrarReputation");



    }
}
