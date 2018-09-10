/*The function here include bfs and output methods*/


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DirectedPseudograph;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.*;

public class IterateGraph {
    DirectedPseudograph<EntityNode, EventEdge> inputgraph;
    DOTExporter<EntityNode, EventEdge> exporter;
    Map<String, EntityNode> indexOfNode;

    IterateGraph(DirectedPseudograph<EntityNode, EventEdge> graph){

        this.inputgraph = graph;
        exporter = new DOTExporter<EntityNode, EventEdge>(new EntityIdProvider(),new EntityNameProvider(), new EventEdgeProvider(),new EntityAttributeProvider(),null);
        indexOfNode = new HashMap<>();
        for(EntityNode n : graph.vertexSet()){
            indexOfNode.put(n.getSignature(), n);
        }
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
//        Set<EntityNode> vertexSet = inputgraph.vertexSet();
//        for(EntityNode n:vertexSet){
//            System.out.println(n.getSignature());
//            if(n.getSignature().equals(input)){
//                return n;
//            }
//        }
        if(indexOfNode.containsKey(input)){
            return indexOfNode.get(input);
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

    public void printPathsOfSpecialVertex(String vertex)
    {
        EntityNode v1 = getGraphVertex(vertex);
        assert v1!=null;
        Set<EventEdge> outgoing = inputgraph.outgoingEdgesOf(v1);
        Set<EventEdge> incoming = inputgraph.incomingEdgesOf(v1);
        List<EventEdge> list = new ArrayList<>(outgoing);
        sortEdgesBasedOnWeight(list);
        List<EventEdge> list2 =  new ArrayList<>(incoming);
        sortEdgesBasedOnWeight(list2);
        try{
            FileWriter w = new FileWriter(new File(String.format("%s.txt",vertex)));
            for(int i=0; i< list.size(); i++){
                EventEdge edge = list.get(i);
                w.write("Target: " + edge.getSink().getSignature() + " Data: " + edge.getSize() + " Weight: "+ edge.weight+ System.lineSeparator());
            }
            for(int i=0;i< list2.size(); i++){
                EventEdge edge = list2.get(i);
                w.write("Source: "+ edge.getSource().getSignature()+" Data: " + edge.getSize()+" Weiget: "+edge.weight+System.lineSeparator());
            }
            w.close();
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

    public void OutputPaths(List<GraphPath<EntityNode, EventEdge>> paths){
        System.out.println("Paths size:" + paths.size());
        for(int i=0; i<paths.size(); i++){
            Graph<EntityNode, EventEdge> g = paths.get(i).getGraph();
            String fileName = String.format("Path %d.dot", i);
            try{
                exporter.exportGraph(g, new FileWriter(new File(fileName)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void sortEdgesBasedOnWeight(List<EventEdge> edges){
        Comparator<EventEdge> cp= new Comparator<EventEdge>() {
            @Override
            public int compare(EventEdge a, EventEdge b) {
                if(a.weight >= b.weight){
                    return 1;
                }else{
                    return 0;
                }
            }
        };

        Collections.sort(edges, cp);
    }
    // the largest end time of POI vertex
    public BigDecimal getLatestOperationTime(EntityNode node) {
        assert node != null;
        Set<EventEdge> edges = inputgraph.incomingEdgesOf(node);
        BigDecimal res = BigDecimal.ZERO;
        for (EventEdge e : edges) {
            if (res.compareTo(e.getEnd()) < 0) {
                res = e.getStart();
            }
        }
        return res;
    }

    public List<DirectedPseudograph<EntityNode, EventEdge>> getHighWeightPaths(String s){
        EntityNode start = getGraphVertex(s);
        assert start != null;
        List<DirectedPseudograph<EntityNode, EventEdge>> paths = new ArrayList<DirectedPseudograph<EntityNode, EventEdge>>();
        for(int i=0; i<5; i++){
            DirectedPseudograph<EntityNode, EventEdge> path = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
            Queue<EntityNode> queue = new LinkedList<>();
            queue.offer(start);
            Set<EntityNode> visited = new HashSet<>();
            while(!queue.isEmpty()){
                EntityNode cur = queue.poll();
                visited.add(cur);
                path.addVertex(cur);
                Set<EventEdge> incoming = inputgraph.incomingEdgesOf(cur);
                if(incoming.size() > 0){
                    List<EventEdge> listOfIncoming = sortBasedOnWeight(incoming);
                    if(listOfIncoming.size() == 1){
                        EventEdge inc = listOfIncoming.get(0);
                        path.addVertex(inc.getSource());
                        path.addEdge(inc.getSource(), cur, inc);
                        if(!visited.contains(inc.getSource()))
                            queue.offer(inc.getSource());
                    }else{
                        EventEdge inc = listOfIncoming.get(0);
                        if(!visited.contains(inc.getSource()))
                            queue.offer(inc.getSource());
                        path.addVertex(inc.getSource());
                        path.addEdge(inc.getSource(), cur, inc);
                        inputgraph.removeEdge(inc);
                    }
                }
                Set<EventEdge> outgoing = inputgraph.outgoingEdgesOf(cur);
                if(outgoing.size() > 0){
                    List<EventEdge> listOfOutgoing = sortBasedOnWeight(outgoing);
                    if(listOfOutgoing.size() == 1){
                        EventEdge out = listOfOutgoing.get(0);
                        path.addVertex(out.getSink());
                        path.addEdge(cur, out.getSink(), out);
                        if(!visited.contains(out.getSink()))
                            queue.offer(out.getSink());

                    }else{
                        EventEdge out = listOfOutgoing.get(0);
                        queue.offer(out.getSink());
                        path.addVertex(out.getSink());
                        path.addEdge(cur, out.getSink(), out);
                        if(!visited.contains(out.getSink()))
                            queue.offer(out.getSink());
                        inputgraph.removeEdge(out);
                    }
                }
            }
            paths.add(path);
        }
        return paths;

    }

    public void printEdgesOfVertex(String s){
        EntityNode vertex = getGraphVertex(s);
        assert  vertex != null;
        Set<EventEdge> incoming = inputgraph.incomingEdgesOf(vertex);
        List<EventEdge> list = sortBasedOnWeight(incoming);
        String fileName = "edgesOf"+s;
        Set<EventEdge> outgoing = inputgraph.outgoingEdgesOf(vertex);
        List<EventEdge> list2 = sortBasedOnWeight(outgoing);
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write("Incoming: "+"\n");
            for (int i = 0; i < list.size(); i++) {
                String cur = outputEdge(list.get(i));
                writer.write(cur+"\n");
            }
            writer.write("Outgoing: "+"\n");
            for(int i=0; i< list2.size(); i++){
                String cur = outputEdge(list.get(i));
                writer.write(cur+"\n");
            }
            writer.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String outputEdge(EventEdge e){
        StringBuilder sb = new StringBuilder();
        sb.append("source: ");
        sb.append(e.getSource().getSignature()+" ");
        sb.append("target: ");
        sb.append(e.getSink().getSignature()+" ");
        sb.append("weight: ");
        sb.append(e.weight);
        return sb.toString();
    }

    private List<EventEdge> sortBasedOnWeight(Set<EventEdge> edges){
        List<EventEdge> list = new ArrayList<>(edges);
        Comparator<EventEdge> cmp = new Comparator<EventEdge>() {
            @Override
            public int compare(EventEdge a, EventEdge b) {
                double diff = b.weight - a.weight;
                if (diff == 0){
                    return 0;
                }else if(diff > 0){
                    return 1;
                }else{
                    return -1;
                }
            }
        };

        Collections.sort(list, cmp);
        return list;
    }

    public double avergeEdgeWeight(){
        int nums = inputgraph.edgeSet().size();
        double sum = 0.0;
        for(EventEdge edge : inputgraph.edgeSet()){
            sum += edge.weight;
        }
        return sum/(nums*1.0);
    }
    /*this need to be tested*/
    public void filterGraphBasedOnAverageWeight(){
        double averageEdgeWeight = avergeEdgeWeight()/100.0;
        List<EventEdge> edges = new ArrayList<>(inputgraph.edgeSet());
        for(int i=0; i< edges.size(); i++){
            if(edges.get(i).weight < averageEdgeWeight){
                inputgraph.removeEdge(edges.get(i));
            }
        }
        List<EntityNode> list = new ArrayList<>(inputgraph.vertexSet());
        for(int i=0; i< list.size(); i++){
            EntityNode v = list.get(i);
            if(inputgraph.incomingEdgesOf(v).size() == 0 && inputgraph.outgoingEdgesOf(v).size() == 0){
                inputgraph.removeVertex(v);
            }
        }
    }

    public void filterGraphBasedOnVertexReputation(){
        List<EntityNode> vlist = new ArrayList<>(inputgraph.vertexSet());
        for(int i=0;i<vlist.size(); i++){
            EntityNode v = vlist.get(i);
            if(v.reputation == 0.0){
                List<EventEdge> inc = new ArrayList<>(inputgraph.incomingEdgesOf(v));
                for(int j=0; j< inc.size();j++){
                    inputgraph.removeEdge(inc.get(j));
                }
                List<EventEdge> out = new ArrayList<>(inputgraph.outgoingEdgesOf(v));
                for(int j=0; j<out.size(); j++){
                    inputgraph.removeEdge(out.get(j));
                }
                inputgraph.removeVertex(v);
            }
        }
    }

    public void removeSingleVertex(){
        List<EntityNode> list = new ArrayList<>(inputgraph.vertexSet());
        for(int i=0; i< list.size(); i++){
            EntityNode v = list.get(i);
            if(inputgraph.incomingEdgesOf(v).size() == 0 && inputgraph.outgoingEdgesOf(v).size() == 0){
                inputgraph.removeVertex(v);
            }
        }
    }


}
