import org.jgrapht.graph.DirectedPseudograph;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by fang on 4/6/18.
 */
public class ProcessOneLog {

    public static void main(String[] args){
        ArgParser ap = new ArgParser(args);
        Map<String,String> argMap = ap.parseArgs();
//        String path = "/home/lcl/logs/file_manipulation/1.txt";
//        String detection = "/home/lcl/wget-1.19/INSTALL";
//        String[] highRP = {};
//        String[] lowRP = {"192.168.29.234:54764->208.118.235.20:80"};//"/media/lcl/LCL/bad.zip"};//"192.168.29.125:10289->192.168.29.234:22"}
        String path = argMap.get("path");
        String detection = argMap.get("detection");
        String highRPs = argMap.get("high");
        String[] highRP = highRPs==null?new String[]{}:highRPs.split(",");
        String neutralRPs = argMap.get("neutral");
        String[] neutralRP = neutralRPs==null?new String[]{}:neutralRPs.split(",");
        ArrayList<String> midRP2 = new ArrayList<>();
        midRP2.addAll(Arrays.asList(MetaConfig.midRP));
        midRP2.addAll(Arrays.asList(neutralRP));
        String lowRPs = argMap.get("low");
        String[] lowRP = lowRPs==null?new String[]{}:lowRPs.split(",");
        String resultDir = argMap.get("res");
        String suffix = argMap.get("suffix");
        double threshold = Double.parseDouble(argMap.get("thresh"));
        boolean trackOrigin = argMap.containsKey("origin");

        String[] paths = path.split("/");
        process(resultDir, suffix, threshold, trackOrigin, path,MetaConfig.localIP,detection,highRP,midRP2.toArray(new String[midRP2.size()]),lowRP, paths[paths.length-1]);
    }

    public static void process(Map<String,String> argMap){
        String path = argMap.get("path");
        String detection = argMap.get("detection");
        String highRPs = argMap.get("high");
        String[] highRP = highRPs==null?new String[]{}:highRPs.split(",");
        String neutralRPs = argMap.get("neutral");
        String[] neutralRP = neutralRPs==null?new String[]{}:neutralRPs.split(",");
        ArrayList<String> midRP2 = new ArrayList<>();
        midRP2.addAll(Arrays.asList(MetaConfig.midRP));
        midRP2.addAll(Arrays.asList(neutralRP));
        String lowRPs = argMap.get("low");
        String[] lowRP = lowRPs==null?new String[]{}:lowRPs.split(",");
        String resultDir = argMap.get("res");
        String suffix = argMap.get("suffix");
        double threshold = Double.parseDouble(argMap.get("thresh"));
        boolean trackOrigin = argMap.containsKey("origin");

        String[] paths = path.split("/");
        process(resultDir, suffix, threshold, trackOrigin, path,MetaConfig.localIP,detection,highRP,midRP2.toArray(new String[midRP2.size()]),lowRP, paths[paths.length-1]);

    }

    public static void process(String resultDir, String suffix, double threshold, boolean trackOrigin, String logfile, String[] IP, String detection,String[] highRP,String[] midRP,String[] lowRP, String filename){
        //String resultDir =  "/home/lcl/results/exp/";
        //String suffix = "";
        OutputStream os = null;
        OutputStream weightfile = null;
        try{
            os = new FileOutputStream(resultDir+filename+suffix+"_stats");
            GetGraph getGraph = new GetGraph(logfile, IP);
            getGraph.GenerateGraph();
            DirectedPseudograph<EntityNode, EventEdge> orignal = getGraph.getJg();
            System.out.println("Original vertex number:" + orignal.vertexSet().size() + " edge number : " + orignal.edgeSet().size());
            os.write(("Original vertex number:" + orignal.vertexSet().size() + " edge number : " + orignal.edgeSet().size()+"\n").getBytes());

            BackTrack backTrack = new BackTrack(orignal);
            backTrack.backTrackPOIEvent(detection);
            System.out.println("After Backtrack vertex number is: "+ backTrack.afterBackTrack.vertexSet().size() + " edge number: " + backTrack.afterBackTrack.edgeSet().size());
            os.write(("After Backtrack vertex number is: "+ backTrack.afterBackTrack.vertexSet().size() + " edge number: " + backTrack.afterBackTrack.edgeSet().size()+"\n").getBytes());


            IterateGraph out = new IterateGraph(backTrack.afterBackTrack);
            out.exportGraph(resultDir+"BackTrack_"+filename+suffix);
            //backTrack.exportGraph("backTrack");
            CasualityPreserve CPR = new CasualityPreserve(backTrack.afterBackTrack);
            CPR.CPR();
            System.out.println("After CPR vertex number is: "+ CPR.afterMerge.vertexSet().size() + " edge number: " + CPR.afterMerge.edgeSet().size());
            os.write(("After CPR vertex number is: "+ CPR.afterMerge.vertexSet().size() + " edge number: " + CPR.afterMerge.edgeSet().size()+"\n").getBytes());

            out = new IterateGraph(CPR.afterMerge);
            out.exportGraph(resultDir+"AfterCPR_"+filename+suffix);
            GraphSplit split = new GraphSplit(CPR.afterMerge);
            split.splitGraph();
            System.out.println("After Split vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size());


            InferenceReputation infer = new InferenceReputation(split.inputGraph);

            os.write(("After Split vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size()+"\n").getBytes());
            weightfile = new FileOutputStream(resultDir+"weights_"+filename+suffix);
            for(EventEdge e: infer.graph.edgeSet()){
                weightfile.write((String.valueOf(e.weight)+",").getBytes());
            }

            infer.calculateWeights();
            System.out.println("OTSU: "+infer.OTSUThreshold());

            infer.filterGraphBasedOnAverageWeight(threshold);

            if(!trackOrigin)
                infer.removeIsolatedIslands(detection);
            else
                infer.trimSiblings(detection);

            //infer.normalizeWeightsAfterFiltering();
            infer.initialReputation(highRP,midRP,lowRP);
            infer.PageRankIteration2(highRP,midRP,lowRP,detection);
            //infer.PageRankIteration(detection);
            //infer.fixReputation(highRP);


            System.out.println("After Filter vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size());
            os.write(("After Filter vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size()).getBytes());




//        //infer.onlyPrintHeightestWeights(detection);
            infer.exportGraph(resultDir+"Weight_"+filename+suffix);
//        IterateGraph iterGraph = new IterateGraph(infer.graph);
            infer.extractSuspects(0.5);
            infer.exportGraph(resultDir+"Suspect_"+filename+suffix);

//        iterGraph.filterGraphBasedOnVertexReputation();
//        iterGraph.removeSingleVertex();
//        iterGraph.exportGraph("FilteredInstallMongodb");
//        List<DirectedPseudograph<EntityNode, EventEdge>> paths = iterGraph.getHighWeightPaths(detection);
//        for(int i=0; i< paths.size();i++){
//            IterateGraph iter = new IterateGraph(paths.get(i));
//            String fileName = String.valueOf(i) + "path";
//            iter.exportGraph(fileName);
//        }
            //iterGraph.printEdgesOfVertex("11035dpkg");

//        infer.checkWeightsAfterCalculation();
//        infer.exportGraph("UnrarReputation");
            Runtime rt = Runtime.getRuntime();
            String[] cmd = {"/bin/sh","-c","dot -T svg "+resultDir+"AfterCPR_"+filename+suffix+".dot"
                    + " > "+resultDir+"AfterCPR_"+filename+suffix+".svg"};
            rt.exec(cmd);
            cmd = new String[]{"/bin/sh", "-c","dot -T svg "+resultDir+"Weight_"+filename+suffix+".dot"
                    + " > "+resultDir+"Weight_"+filename+suffix+".svg"};
            rt.exec(cmd);
            cmd = new String[]{"/bin/sh", "-c","dot -T svg "+resultDir+"Suspect_"+filename+suffix+".dot"
                    + " > "+resultDir+"Suspect_"+filename+suffix+".svg"};
            rt.exec(cmd);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                os.close();
                weightfile.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }




    }
}
