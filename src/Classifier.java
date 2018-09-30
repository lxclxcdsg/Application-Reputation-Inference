import org.jgrapht.graph.DirectedPseudograph;

import java.util.*;

public abstract class Classifier {
    DirectedPseudograph<EntityNode, EventEdge> graph;

    public List<double[]> createTrainingSet(Map<EntityNode,EntityNode> positive){

        Set<EventEdge> s = graph.edgeSet();
        List<double[]> l = new ArrayList<>();
        for(EventEdge e: s){
            double[] weights = new double[4];
            weights[0] = e.amountWeight;
            weights[1] = e.timeWeight;
            weights[2] = e.structureWeight;
            if(positive.containsKey(graph.getEdgeSource(e)) && positive.get(graph.getEdgeSource(e)) == graph.getEdgeTarget(e))
                weights[3] = 1;
            else weights[3] = 0;
            l.add(weights);
        }

        return l;
    }

}
