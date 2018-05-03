import org.jgrapht.ext.DOTImporter;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.traverse.GraphIterator;

import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by fang on 3/26/18.
 */
public class ReadGraphFromDot {
    IterateGraph graphIterator;
    private BigDecimal rel = new BigDecimal(43333);
    public DirectedPseudograph<EntityNode, EventEdge> readGraph(String file){

        DOTImporter<EntityNode, EventEdge> importer = new DOTImporter<EntityNode, EventEdge>(new EntityProvider(), new eventEdgeProvider());
        DirectedPseudograph<EntityNode, EventEdge> g = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
        try {
            importer.importGraph(g, new File(file));
        }catch (Exception e){
            e.printStackTrace();
        }
        graphIterator = new IterateGraph(g);
        return g;
    }

    public void exportGraph(String file){
        graphIterator.exportGraph(file);
    }

    public static void main(String[] args){
        String file = "/home/fang/thesis2/Data/TestSampleAndResult/chrome1.dot";
        ReadGraphFromDot test = new ReadGraphFromDot();
        DirectedPseudograph<EntityNode, EventEdge>original = test.readGraph(file);
        test.graphIterator.exportGraphAmountAndTime("original");
        GraphSplit split = new GraphSplit(original);
        split.splitGraph();
        split.outputGraph("splitTest");
        InferenceRuputation testReputation = new InferenceRuputation(split.inputGraph);
        try {
            testReputation.calculateWeights();
            testReputation.PageRankIteration();
            test.graphIterator.exportGraph("SampleWeights");
        }catch (Exception e){
            e.printStackTrace();
        }

        testReputation.printReputation();

//        try {
//            testReputation.inferRuputation();
//            testReputation.printWeights();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        IterateGraph iterateGraph = new IterateGraph(split.inputGraph);
//        iterateGraph.exportGraph("testSample");
    }

}
