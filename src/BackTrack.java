import org.jgrapht.graph.DirectedPseudograph;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fang on 3/23/18.
 * the input is the original graph
 */
public class BackTrack {
    DirectedPseudograph<EntityNode,EventEdge> originalGraph;
    IterateGraph iterateGraph;
    DirectedPseudograph<EntityNode, EventEdge> afterBackTrack;

    BackTrack(DirectedPseudograph<EntityNode, EventEdge>input){
        originalGraph = input;
        iterateGraph = new IterateGraph(input);
    }

    public DirectedPseudograph<EntityNode, EventEdge> backTrackPOIEvent(String str){
        DirectedPseudograph<EntityNode, EventEdge> backTrack = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
        EntityNode start = iterateGraph.getGraphVertex(str);
        BigDecimal latestOPTime = iterateGraph.getLatestOperationTime(start);
        Set<EntityNode> nodeInTheQueue = new HashSet<>();
        Queue<EntityNode> queue = new LinkedList<>();
        nodeInTheQueue.add(start);
        queue.offer(start);
        while(!queue.isEmpty()){
            EntityNode cur = queue.poll();
            backTrack.addVertex(cur);
            Set<EventEdge> incoming = originalGraph.incomingEdgesOf(cur);
            for(EventEdge e:incoming){
                if(e.getStart().compareTo(latestOPTime)>0) continue;
                EntityNode source = e.getSource();
                backTrack.addVertex(source);
                backTrack.addEdge(source,cur, e);
                if(!nodeInTheQueue.contains(source)){
                    nodeInTheQueue.add(source);
                    queue.offer(source);
                }
            }

            Set<EventEdge> outgoing = originalGraph.outgoingEdgesOf(cur);
            for(EventEdge e:outgoing){
                if(e.getStart().compareTo(latestOPTime)>0) continue;
                EntityNode target = e.getSink();
                backTrack.addVertex(target);
                backTrack.addEdge(cur, target,e);
                if(!nodeInTheQueue.contains(target)){
                    nodeInTheQueue.add(target);
                    queue.offer(target);
                }
            }
        }
        afterBackTrack = backTrack;
        return backTrack;
    }

    public void printGraph(){
        assert afterBackTrack != null;
        IterateGraph iter = new IterateGraph(afterBackTrack);
        iter.exportGraph("backtrackTest");
    }

    public void exportGraph(String file){
        assert afterBackTrack != null;
        IterateGraph iter = new IterateGraph(afterBackTrack);
        iter.exportGraph(file);
    }


}
