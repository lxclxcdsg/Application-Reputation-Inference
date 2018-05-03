/*The function here include bfs and output methods*/


import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DirectedPseudograph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;

public class IterateGraph {
    DirectedPseudograph<EntityNode, EventEdge> inputgraph;
    DOTExporter<EntityNode, EventEdge> exporter;


    IterateGraph(DirectedPseudograph<EntityNode, EventEdge> graph){

        this.inputgraph = graph;
        exporter = new DOTExporter<EntityNode, EventEdge>(new EntityIdProvider(),new EntityNameProvider(), new EventEdgeProvider(),new EntityAttributeProvider(),null);
    }

    public DirectedPseudograph<EntityNode, EventEdge> bfs(String input){
        EntityNode start = getGraphVertex(input);
        Queue<EntityNode> queue = new LinkedList<EntityNode>();
        if(start!=null){
            return bfs(start);
        }else{
            System.out.println("Your input doesn't exist in the graph");
        }
        return null;

    }

    private DirectedPseudograph<EntityNode, EventEdge> bfs(EntityNode start){
        Queue<EntityNode> queue = new LinkedList<>();
        DirectedPseudograph<EntityNode, EventEdge> newgraph = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
        queue.offer(start);
        Set<EntityNode> nodeInTheQueue = new HashSet<>();
        nodeInTheQueue.add(start);

        while(!queue.isEmpty()){
            EntityNode cur = queue.poll();
            newgraph.addVertex(cur);
            Set<EventEdge> inEdges = inputgraph.incomingEdgesOf(cur);
            for(EventEdge edge: inEdges){
                EntityNode source = edge.getSource();
                newgraph.addVertex(source);
                newgraph.addEdge(source,cur,edge);
                if(!nodeInTheQueue.contains(source)){
                    nodeInTheQueue.add(source);
                    queue.offer(source);
                }
            }
            Set<EventEdge> outEdges = inputgraph.outgoingEdgesOf(cur);
            for(EventEdge edge: outEdges){
                EntityNode target = edge.getSink();
                newgraph.addVertex(target);
                newgraph.addEdge(cur, target, edge);
                if(!nodeInTheQueue.contains(target)){
                    nodeInTheQueue.add(target);
                    queue.offer(target);
                }
            }
        }
        return newgraph;
    }


    /* input is file name  output is a new dot file*/
    public void exportGraph (String fileName){
        try {
            exporter.exportGraph(inputgraph, new FileWriter(String.format("%s.dot", fileName)));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void exportGraph(DirectedPseudograph<EntityNode, EventEdge>graph, String fileName){
        try {
            exporter.exportGraph(graph, new FileWriter(String.format("%s.dot", fileName)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public EntityNode getGraphVertex(String input){
        Set<EntityNode> vertexSet = inputgraph.vertexSet();
        for(EntityNode n:vertexSet){
            System.out.println(n.getSignature());
            if(n.getSignature().equals(input)){
                return n;
            }
        }
        System.out.println("Can't find the vertex");
        return null;
    }

    public void printVertexReputation(){
        Set<EntityNode> vertex = inputgraph.vertexSet();
        int count = 0;
        for(EntityNode v : vertex){
            System.out.print(String.valueOf(v.getReputation())+" ");
            count++;
            if((count +1)%20==0) System.out.println();
        }
    }

    public boolean findProcessNode(DirectedPseudograph<EntityNode, EventEdge>graph,String pname){
        Set<EntityNode> vertex = graph.vertexSet();
        for(EntityNode v:vertex){
            if(v.getP()!=null && v.getP().getName().equals((pname))){
                return true;
            }
        }
        return false;
    }

    public void exportGraphAmountAndTime(String file){
        DOTExporter<EntityNode, EventEdge> export =  new DOTExporter<EntityNode, EventEdge>(new EntityIdProvider(),new EntityNameProvider(), new EdgeAmountTimeProvider(),new EntityAttributeProvider(),null);
        try {
            export.exportGraph(inputgraph, new FileWriter(file));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public BigDecimal getLatestOperationTime(String str){
        EntityNode vertex = getGraphVertex(str);
        assert vertex != null;
        Set<EventEdge> incoming = inputgraph.incomingEdgesOf(vertex);
        BigDecimal res = BigDecimal.ZERO;
        for(EventEdge e:incoming){
            if(res.compareTo(e.getStart())<0){
                res = e.getStart();
            }
        }
        return res;
    }
    // the largest end time of POI vertex
    public BigDecimal getLatestOperationTime(EntityNode node){
        assert node != null;
        Set<EventEdge> edges = inputgraph.incomingEdgesOf(node);
        BigDecimal res = BigDecimal.ZERO;
        for(EventEdge e:edges){
            if(res.compareTo(e.getEnd())<0){
                res = e.getStart();
            }
        }
        return res;
    }


}
