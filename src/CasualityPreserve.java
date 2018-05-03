import org.jgrapht.graph.DirectedPseudograph;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fang on 3/23/18.
 */
public class CasualityPreserve {
    DirectedPseudograph<EntityNode, EventEdge> input;
    IterateGraph graphIter;
    DirectedPseudograph<EntityNode, EventEdge> afterMerge;

    CasualityPreserve(DirectedPseudograph<EntityNode, EventEdge> input){
        this.input = input;
    }
    public DirectedPseudograph<EntityNode, EventEdge> CPR(){
        Set<EventEdge> edgeSet = input.edgeSet();
        List<EventEdge> edgeList = new LinkedList<>(edgeSet);
        Collections.sort(edgeList,(a,b)->a.getStart().compareTo(b.getStart()));
        Iterator iter = edgeList.iterator();
        Map<EntityNode, Map<EntityNode, Deque<EventEdge>>> mapOfStack = new HashMap<>();
        while(iter.hasNext()){
            EventEdge cur = (EventEdge)iter.next();
            EntityNode u = cur.getSource();
            EntityNode v = cur.getSink();
            if(mapOfStack.containsKey(u)){
                Map<EntityNode, Deque<EventEdge>> values = mapOfStack.get(u);
                if(values.containsKey(v)){
                    Deque<EventEdge> edgeStack = values.get(v);
                    if(edgeStack.isEmpty()){
                        System.out.println("CPR function is not correct");
                        break;
                    }
                    EventEdge earlyEdge = edgeStack.pop();
                    if(forwardCheck(earlyEdge, cur, v) && backwardCheck(earlyEdge, cur, u)){  // event type need to be checked
                        earlyEdge.merge(cur);
                        edgeStack.push(earlyEdge);
                    }else{
                        edgeStack.push(cur);
                    }
                }else{
                    Deque<EventEdge> stack = new ArrayDeque<>();
                    stack.push(cur);
                    values.put(v, stack);
                }
            }else{
                Deque<EventEdge> stack = new ArrayDeque<>();
                stack.push(cur);
                Map<EntityNode, Deque<EventEdge>> value = new HashMap<>();
                value.put(v, stack);
                mapOfStack.put(u,value);
            }
        }
        DirectedPseudograph<EntityNode, EventEdge> res = getCPR(mapOfStack);
        afterMerge = res;
        return res;
    }

    public void exportGraph(String fileName){
        if(afterMerge == null){
            CPR();
        }
        graphIter = new IterateGraph(afterMerge);
        graphIter.exportGraph(fileName);
    }

    private DirectedPseudograph<EntityNode, EventEdge> getCPR(Map<EntityNode, Map<EntityNode, Deque<EventEdge>>> mapOfStacks){
        DirectedPseudograph<EntityNode, EventEdge> res = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
        for(EntityNode u:mapOfStacks.keySet()){
            Map<EntityNode, Deque<EventEdge>> cur = mapOfStacks.get(u);
            for(EntityNode v:cur.keySet()){
                res.addVertex(u);
                res.addVertex(v);
                while(!cur.get(v).isEmpty()){
                    EventEdge edge = cur.get(v).pop();
                    res.addEdge(u,v,edge);
                }
            }
        }
        return res;
    }

    private boolean backwardCheck(EventEdge p,EventEdge l, EntityNode u){
        Set<EventEdge> incoming = input.incomingEdgesOf(u);
        BigDecimal[] endTimes = {p.getEnd(),l.getEnd()};
        Arrays.sort(endTimes);
        for(EventEdge edge:incoming){
            BigDecimal[] timeWindow = edge.getInterval();
            if(isOverlap(timeWindow,endTimes)){
                return false;
            }
        }
        return true;
    }

    private boolean forwardCheck(EventEdge p, EventEdge l, EntityNode u){
        BigDecimal[] startTime = {p.getStart(), l.getStart()};
        Set<EventEdge> outgoing = input.outgoingEdgesOf(u);
        Arrays.sort(startTime);
        for(EventEdge edge:outgoing){
            BigDecimal[] timeWindow = edge.getInterval();
            if(isOverlap(timeWindow,startTime)){
                return false;
            }
        }
        return true;
    }

    private boolean isOverlap(BigDecimal[]a, BigDecimal[]b){
        if(a[1].compareTo(b[0])>=0 && a[1].compareTo(b[1])<=0 || a[0].compareTo(b[0])>=0 && a[0].compareTo(b[1])<=0){
            return true;
        }
        return false;
    }



}
