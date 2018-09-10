import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.alg.*;

/**
 * Created by fang on 3/12/18.
 */
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

public class InferenceReputation {
    //GraphSplit splitedGraph;
    DirectedPseudograph<EntityNode, EventEdge> graph;
    /* the input  need to finish split step before this(this parameter need to run relevent funtions first)*/
    private BigDecimal POItime;
    IterateGraph graphiterator;
    HashMap<Long, HashMap<Long, Double>> weights;
    double weight; // used to calculate combinative weight
    double dumpingFactor;
    InferenceReputation(DirectedPseudograph<EntityNode, EventEdge> input){
        //splitedGraph = s;
        graph = input;
        graphiterator = new IterateGraph(graph);
        weights = new HashMap<>();
        POItime = updatePOITime();
        weight = 0.5;
        dumpingFactor = 0.85;
    }


    public void calculateWeights(){
        //HashMap<Long, HashMap<Long, Double>> weights = new HashMap<>();
        Set<EntityNode> vertexSet = graph.vertexSet();
        initalizeWeights();
        for(EntityNode e: vertexSet){
            Set<EventEdge> edges = graph.incomingEdgesOf(e);
            double[] timeweights = new double[edges.size()];
            double[] amountweights = new double[edges.size()];
            boolean nodata = false;
            double amounttotal = 0;
            double timetotal = 0.0;
            double totalWeightBasedOnEdgeNumber = 0.0;
            for(EventEdge i:edges){
                amounttotal += i.getSize();
                timetotal += timeWeight(i);
                totalWeightBasedOnEdgeNumber += getEdgeWeight(i);
            }
            //System.out.println("totalWeightBasedOnEdgeNumber is:" + String.valueOf(totalWeightBasedOnEdgeNumber));
            assert totalWeightBasedOnEdgeNumber!=0.0 && totalWeightBasedOnEdgeNumber!=Double.NaN;
            if(amounttotal == 0) nodata = true;
            boolean someHasData = someEdgesWithoutData(graph.incomingEdgesOf(e));

            double wtotal = 0.0;
            if(!nodata && !someHasData) {

                for (EventEdge i : edges) {
                    EntityNode from = i.getSource();
//                    wtotal += (1/3.0)*(timeWeight(i) / timetotal) + (1/3.0)*(amountWeight(i) / amounttotal)+
//                            (1/3.0)*(getEdgeWeight(i)/totalWeightBasedOnEdgeNumber);  // here is issue some process amounttotal is zero
                    wtotal += getCombineWeight(i, timetotal, amounttotal, totalWeightBasedOnEdgeNumber);
                }
                if(Double.isNaN(wtotal)){
                    System.out.println("Common case total weight is NaN");
                }
                for (EventEdge edge : edges) {
                    EntityNode from = edge.getSource();
//                    double w = ((1/3.0)*(timeWeight(edge)/timetotal)+(1/3.0)*(amountWeight(edge))/amounttotal+
//                            (1/3.0)*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber))/wtotal;
                    double w = getCombineWeight(edge, timetotal,amounttotal,totalWeightBasedOnEdgeNumber)/wtotal;
//                    double testForEdge = (1/3)*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber)/wtotal;
//                    if(Double.isNaN(testForEdge)){
//                        System.out.println("Tst for edge is NAN");
//                    }
                    //System.out.println("Single edge weight is: "+String.valueOf(getEdgeWeight(edge)));
                    if(Double.isNaN(getEdgeWeight(edge))){
                        System.out.println("single edge weight is NAN");
                    }
                    if(totalWeightBasedOnEdgeNumber == 0.0){
                        System.out.println("In common case totalWeigtBaseOnEdge is 0.0");
                    }
                    if(Double.isNaN(w)){
                        System.out.println("Common case weight is Nan");
                    }
                    edge.weight = w;
                    weights.get(e.getID()).put(from.getID(), w);
                }
            }else{
                if(nodata){
                    //wtotal = totalWeightWithOutDataAmount(edges, timetotal);
                    for(EventEdge edge: edges){
                        wtotal += 0.5*(timeWeight(edge)/timetotal) + 0.5*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber);
                    }
                    for(EventEdge edge : edges){
                        EntityNode from = edge.getSource();
                        double w = (0.5*(timeWeight(edge))/timetotal+0.5*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber))/wtotal;
                        if(Double.isNaN(w)){
                            System.out.println("No data weight has NaN");
                        }
                        edge.weight = w;
                        weights.get(e.getID()).put(from.getID(), w);
                    }
                }else{
                    System.out.println("Some has data some doesnt :"+ edges.size());
                    for(EventEdge i : edges){
                        EntityNode from = i.getSource();
//                        wtotal += (1/3.0)*(timeWeight(i) / timetotal) + (1/3.0)*(amountWeight(i) / amounttotal)+
//                                (1/3.0)*(getEdgeWeight(i)/totalWeightBasedOnEdgeNumber);
                        wtotal += getCombineWeight(i, timetotal, amounttotal, totalWeightBasedOnEdgeNumber);
                    }
                    if(Double.isNaN(wtotal)){
                        System.out.println("Some edge having data total weight is NaN");
                    }
                    for(EventEdge edge : edges){

                        double w= 0.0;
                        EntityNode from = edge.getSource();
//                        w = ((1/3.0)*(timeWeight(edge) / timetotal) +(1/3.0)* (amountWeight(edge) / amounttotal)+
//                                (1/3.0)*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber)) / wtotal;
                        w = getCombineWeight(edge, timetotal, amounttotal, totalWeightBasedOnEdgeNumber)/wtotal;
                        edge.weight = w;
                        weights.get(e.getID()).put(from.getID(), w);
                    }
                }
            }



        }
    }

    private double getCombineWeight(EventEdge e, double timetotal, double amounttotal, double edgetotal){
        return (0.1)*(timeWeight(e)/timetotal)+(0.4)*(amountWeight(e))/amounttotal+
                (0.5)*(getEdgeWeight(e)/edgetotal);
    }

    public void calculateWeight(String input){
        Set<EntityNode> vertexSet = graph.vertexSet();
        long data = getDataAmount(input);
        initalizeWeights();
        for(EntityNode e: vertexSet){
            Set<EventEdge> edges = graph.incomingEdgesOf(e);
            double[] timeweights = new double[edges.size()];
            double[] amountweights = new double[edges.size()];
            boolean nodata = false;
            double amounttotal = 0;
            double amountWeightTotal = 0.0;
            double timetotal = 0.0;
            double totalWeightBasedOnEdgeNumber = 0.0;
            for(EventEdge i:edges){
                amounttotal += i.getSize();
                amountWeightTotal = weightSimilar(i.getSize(), data);
                timetotal += timeWeight(i);
                totalWeightBasedOnEdgeNumber += getEdgeWeight(i);
            }
            //System.out.println("totalWeightBasedOnEdgeNumber is:" + String.valueOf(totalWeightBasedOnEdgeNumber));
            assert totalWeightBasedOnEdgeNumber!=0.0 && totalWeightBasedOnEdgeNumber!=Double.NaN;
            if(amounttotal == 0) nodata = true;
            boolean someHasData = someEdgesWithoutData(graph.incomingEdgesOf(e));

            double wtotal = 0.0;
            if(!nodata && !someHasData) {

                for (EventEdge i : edges) {
                    EntityNode from = i.getSource();
                    wtotal += (1/3.0)*(timeWeight(i) / timetotal) + (1/3.0)*(weightSimilar(i.getSize(), data) / amountWeightTotal)+
                            (1/3.0)*(getEdgeWeight(i)/totalWeightBasedOnEdgeNumber);  // here is issue some process amounttotal is zero
                }
                if(Double.isNaN(wtotal)){
                    System.out.println("Common case total weight is NaN");
                }
                for (EventEdge edge : edges) {
                    EntityNode from = edge.getSource();
//                    double w = ((1/3.0)*(timeWeight(edge)/timetotal)+(1/3.0)*(weightSimilar(edge.getSize(), data))/amountWeightTotal+
//                            (1/3.0)*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber))/wtotal;
                    double w = (0.2*(timeWeight(edge)/timetotal)+0.4*(weightSimilar(edge.getSize(), data))/amountWeightTotal+
                            0.4*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber))/wtotal;
                    if(Double.isNaN(getEdgeWeight(edge))){
                        System.out.println("single edge weight is NAN");
                    }
                    if(totalWeightBasedOnEdgeNumber == 0.0){
                        System.out.println("In common case totalWeigtBaseOnEdge is 0.0");
                    }

                    if(Double.isNaN(w)){
                        System.out.println("Common case weight is Nan");
                    }
                    edge.weight = w;
                    weights.get(e.getID()).put(from.getID(), w);
                }
            }else{
                if(nodata){
                    //wtotal = totalWeightWithOutDataAmount(edges, timetotal);
                    for(EventEdge edge: edges){
                        wtotal += 0.4*(timeWeight(edge)/timetotal) + 0.6*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber);
                    }
                    for(EventEdge edge : edges){
                        EntityNode from = edge.getSource();
                        double w = (0.4*(timeWeight(edge))/timetotal+0.6*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber))/wtotal;
                        if(Double.isNaN(w)){
                            System.out.println("No data weight has NaN");
                        }
                        edge.weight = w;
                        weights.get(e.getID()).put(from.getID(), w);
                    }
                }else{
                    System.out.println("Some has data some doesnt :"+ edges.size());
                    for(EventEdge i : edges){
                        EntityNode from = i.getSource();
                        wtotal += (0.2)*(timeWeight(i) / timetotal) + (0.4)*(weightSimilar(i.getSize(), data) / amountWeightTotal)+
                                (0.4)*(getEdgeWeight(i)/totalWeightBasedOnEdgeNumber);
                    }
                    if(Double.isNaN(wtotal)){
                        System.out.println("Some edge having data total weight is NaN");
                    }
                    for(EventEdge edge : edges){

                        double w= 0.0;
                        EntityNode from = edge.getSource();
                        w = (0.2*(timeWeight(edge) / timetotal) +(0.4)* (weightSimilar(edge.getSize(), data) / amountWeightTotal)+
                                (0.4)*(getEdgeWeight(edge)/totalWeightBasedOnEdgeNumber)) / wtotal;
                        edge.weight = w;
                        weights.get(e.getID()).put(from.getID(), w);
                    }
                }
            }



        }
    }

    private double weightSimilar(long cur, long targets){
        if(cur == targets){
            return 1.0;
        }else if(cur > targets){
            return (double)targets/cur;
        }else{
            return (double)cur/targets;
        }
    }

    private double getEdgeWeight(EventEdge e){
        EntityNode source = e.getSource();
        EntityNode target = e.getSink();
        double sizeOfSourceNodeOfCurTarget = graph.incomingEdgesOf(target).size()*1.0;
        double weight = 0.0;
        double sizeOfSourceSource = graph.incomingEdgesOf(source).size()*1.0;
        if(sizeOfSourceSource==0.0){
            weight += 1/sizeOfSourceNodeOfCurTarget;
        }else{
            weight += 1/sizeOfSourceNodeOfCurTarget+1/sizeOfSourceSource;
        }
        if(weight == 0.0){
            System.out.println("sizeofsourcesource: " + String.valueOf(sizeOfSourceSource));
            System.out.println("sizeofSourceofcurtarget: "+ String.valueOf(sizeOfSourceNodeOfCurTarget));
        }
        //System.out.println("edge weight is: "+ String.valueOf(weight));
        return weight;
    }

    private double getWeightAboueEdgesNumber(EntityNode e){
        double weightBasedOnEdgeNumber = 0.0;
        Set<EntityNode> sourceOfIncoming = getSources(e);
        for(EntityNode node: sourceOfIncoming){
            Set<EventEdge> sourceFornode = graph.incomingEdgesOf(node);
            if(sourceFornode.size() == 0){
                weightBasedOnEdgeNumber += 1/(sourceOfIncoming.size()*1.0);
            }else{
                weightBasedOnEdgeNumber += 1/(sourceOfIncoming.size()*1.0) +
                        1/(sourceFornode.size()*1.0);
            }
        }
        return weightBasedOnEdgeNumber;
    }

    public void PageRankIteration(){
        Set<EntityNode> vertexSet = graph.vertexSet();
        double fluctuation = 1.0;
        int iterTime = 0;
        while(fluctuation >= 0.0000000000001){
            double culmativediff = 0.0;
            iterTime++;
            Map<Long, Double> preReputation = getReputation();
            for(EntityNode v: vertexSet){
                Set<EventEdge> edges = graph.incomingEdgesOf(v);
                if(edges.size() == 0) continue;
                double rep = 0.0;
                for(EventEdge edge: edges){
                    EntityNode source = edge.getSource();
                    int numberOfOutEgeFromSource = graph.outDegreeOf(source);
//                    double total_weight = 0.0;
//                    for (EventEdge oe:graph.outgoingEdgesOf(source)){
//                        total_weight += weights.get(graph.getEdgeTarget(oe).getID()).get(source.getID());
//                    }
                    rep+=(preReputation.get(source.getID())*weights.get(v.getID()).get(source.getID())/1);
                }
                rep = rep*dumpingFactor+(1-dumpingFactor)/vertexSet.size();
                culmativediff += Math.abs(rep-preReputation.get(v.getID()));
                v.setReputation(rep);
            }
            fluctuation = culmativediff;
        }
        System.out.println(String.format("After %d times iteration, the reputation of each vertex is stable", iterTime));
    }

    public void PageRankIteration2(String[] highRP, String[] lowRP){
        double dumpingFactor = 0.9;
        Set<EntityNode> vertexSet = graph.vertexSet();
        Set<String> sources = new HashSet<>(Arrays.asList(highRP));
        sources.addAll(Arrays.asList(lowRP));
        double fluctuation = 1.0;
        int iterTime = 0;
        while(fluctuation >= 0.0000000000001){
            double culmativediff = 0.0;
            iterTime++;
            Map<Long, Double> preReputation = getReputation();
            for(EntityNode v: vertexSet){
                if(sources.contains(v.getSignature())) continue;
                Set<EventEdge> edges = graph.incomingEdgesOf(v);
                if(edges.size() == 0) continue;
                double rep = 0.0;
                for(EventEdge edge: edges){
                    EntityNode source = edge.getSource();
                    int numberOfOutEgeFromSource = graph.outDegreeOf(source);
//                    double total_weight = 0.0;
//                    for (EventEdge oe:graph.outgoingEdgesOf(source)){
//                        total_weight += weights.get(graph.getEdgeTarget(oe).getID()).get(source.getID());
//                    }
                    rep+=(preReputation.get(source.getID())*weights.get(v.getID()).get(source.getID())/1);
                }
                rep = rep*dumpingFactor;
                culmativediff += Math.abs(rep-preReputation.get(v.getID()));
                v.setReputation(rep);
            }
            fluctuation = culmativediff;
        }
        System.out.println(String.format("After %d times iteration, the reputation of each vertex is stable", iterTime));
    }

    protected void fixReputation(String[] highRP){
        Map<Long, Double> reputation = getReputation();
        Set<String> s = new HashSet<>(Arrays.asList(highRP));
        double high_rep = 0.0;
        int count = 0;
        for(EntityNode v: graph.vertexSet()){
            if(s.contains(v.getSignature())) {
                high_rep += reputation.get(v.getID());
                count++;
            }
        }
        high_rep /= count;
        for(EntityNode v: graph.vertexSet())
            v.setReputation(Math.min(1-(high_rep-reputation.get(v.getID()))/high_rep,1));
    }

    private Map<Long, Double> getReputation(){
        Set<EntityNode> vertexSet = graph.vertexSet();
        Map<Long, Double> map = new HashMap<>();
        for(EntityNode node:vertexSet){
            map.put(node.getID(), node.getReputation());
        }
        return map;
    }

    private void getFinalWeights(){
        Set<EventEdge> edgeSet = graph.edgeSet();
        for(EventEdge edge:edgeSet){
            EntityNode source = edge.getSource();
            EntityNode sink = edge.getSink();
            if(!weights.containsKey(sink.getID())){
                System.out.println("Sink id issue:"+ sink.getID());
                if(!weights.get(sink.getID()).containsKey(source.getID())){
                    System.out.println("Source id issue: "+source.getID());
                }
            }

            double weight = weights.get(sink.getID()).get(source.getID());
            edge.weight = weight;
        }
    }
    public void exportGraph(String name){
        graphiterator.exportGraph(name);
    }

    private Set<EventEdge> getEdgeWithoutData(Set<EventEdge> edges){
        Set<EventEdge> set = new HashSet<>();
        for(EventEdge e:edges){
            if(e.getSize() == 0){
                set.add(e);
            }
        }
        return set;
    }


    public void initalizeWeights(){
        Set<EntityNode> vertexSet = graph.vertexSet();
        for(EntityNode e:vertexSet){
            weights.put(e.getID(), new HashMap<Long,Double>());
            for(EntityNode e2 : vertexSet){
                weights.get(e.getID()).put(e2.getID(),0.0);
            }

        }
        //System.out.println(weights);
    }

    public void setReliableReputation(String[] strs){
        Set<String> set = new HashSet<String>(Arrays.asList(strs));
        Set<EntityNode> vertexSet = graph.vertexSet();
        for(EntityNode v:vertexSet){
            if(set.contains(v.getSignature())){
                v.setReputation(1.0);
            }
        }
    }

    private double timeWeight(EventEdge edge){
        if(edge.getEnd().equals(POItime)){
            return 1;
        }else{
            BigDecimal diff = POItime.subtract(edge.getEnd());
            //System.out.println("Time diff is: "+ diff.toString());
            if(diff.compareTo(new BigDecimal(1))>0){
                return 1/diff.doubleValue();
            }
            double res = Math.log(1/ diff.doubleValue());
            if(res == 0.0){
                System.out.println("timeWight should not be zero");

            }
            if(res < 0.0) System.out.println("Minus TimeWeight:" + res);
            return res;
        }
    }

    private long amountWeight(EventEdge edge){
        //System.out.println("weight: "+ String.valueOf(edge.getSize()));
        return edge.getSize();
    }
    /* get the latest operation of target */
    private BigDecimal getPOItime(){
        BigDecimal res = BigDecimal.ZERO;
        Set<EventEdge> vertexSet = graph.edgeSet();
        for(EventEdge e : vertexSet){
            if(e.getEnd().compareTo(res)>0){
                res =e.getEnd();
            }
        }
        return res;
    }

    public void printWeights() throws Exception{
        PrintWriter writer = new PrintWriter(String.format("%s.txt", "EdgeWeights"));
        if(weights == null) System.out.println("weithis is null or size equal to zero");
        System.out.println(weights.keySet().size());
        for(Long id:  weights.keySet()){
            Map<Long, Double> sub = weights.get(id);
            for(Long id2: weights.keySet()){
                //writer.println(String.format("%d_%d : %f", id, id2, weights.get(id).get(id2)));
                if(!weights.get(id).get(id2).equals(0.0)) {
                    writer.println(String.format("%d_%d : %f", id, id2, weights.get(id).get(id2)));
                    //System.out.println(String.format("%d_%d : %f", id, id2, weights.get(id).get(id2)));
                }
            }
        }
        writer.close();
    }

    private BigDecimal updatePOITime(){
        BigDecimal res = BigDecimal.ZERO;
        Set<EventEdge> edges = graph.edgeSet();
        for(EventEdge e : edges){
            if(e.getEnd().compareTo(res) > 0){
                res = e.getEnd();
            }
        }
        return res;
    }

    public void printReputation(){
        graphiterator.printVertexReputation();
    }

    public void checkTimeAndAmount(){
        Set<EventEdge> edges = graph.edgeSet();
        for(EventEdge edge : edges){
            if(edge.getDuration().equals(BigDecimal.ZERO)){
                System.out.println("this is because amount is zero");
                System.out.println(edge.getID());
                System.out.println(edge.getSource().getSignature());
                //System.out.println(edge.getSink().getSignature());
            }

            if(edge.getSize() == 0){
                System.out.println("this is because size is zero");
                System.out.println(edge.getID());
                System.out.println(edge.getSource().getSignature());
            }
        }
    }

    private boolean someEdgesWithoutData(Set<EventEdge> set){
        boolean oneEdgeNoData = false;
        boolean edgeWithData = false;
        for(EventEdge e: set){
            if(e.getSize()==0){
                oneEdgeNoData = true;
            }
            if(e.getSize()!=0){
                edgeWithData = true;
            }
            if(oneEdgeNoData && edgeWithData){
                return true;
            }
        }
        return false;
    }

    public void initialReputation(String[] signature_high, String[] signature_neutral, String[] signature_low){
        Set<EntityNode> set = graph.vertexSet();
        Set<String> highReputation = new HashSet<String>(Arrays.asList(signature_high));
        Set<String> midReputation = new HashSet<String>(Arrays.asList(signature_neutral));
        Set<String> lowReputation = new HashSet<String>(Arrays.asList(signature_low));
        for(EntityNode node : set) {
            if(highReputation.contains(node.getSignature())) {
                System.out.println(node.getSignature()+" has high reputation");
                node.reputation = 1.0;
            }else if(midReputation.contains(node.getSignature())) {
                node.reputation = 0.5;
            }else if(lowReputation.contains(node.getSignature())) {
                node.reputation = -1.0;
            }else if(graph.incomingEdgesOf(node).size() == 0) {
                node.reputation =0.0;
            }
        }

    }

    public void printConstantPartOfPageRank(){
        double res = (1-dumpingFactor)/graph.vertexSet().size();
        System.out.println("The constant part of Page Rank:" + res);
    }

    public void checkWeightsAfterCalculation(){
        Set<EntityNode> vertexSet = graph.vertexSet();
        for(EntityNode node : vertexSet){
            Set<EventEdge> incoming = graph.incomingEdgesOf(node);
            double res = 0.0;
            for(EventEdge edge:incoming){
                res += edge.weight;
            }
            if(incoming.size() != 0 && Math.abs(res-1.0) >=0.00001){
                System.out.println("Target: "+ node.getSignature());
                for(EventEdge edge :incoming){
                    edge.printInfo();
                }
                System.out.println("-----------");
            }
        }
    }

    public void onlyPrintHeightestWeights(String start){
        EntityNode v1 = graphiterator.getGraphVertex(start);
        Map<Long, EntityNode> map = new HashMap<>();
        map.put(v1.getID(), new EntityNode(v1));

        DirectedPseudograph<EntityNode, EventEdge> result = new DirectedPseudograph<EntityNode, EventEdge>(EventEdge.class);
        Queue<EntityNode> queue = new LinkedList<>();
        queue.offer(v1);
        while(!queue.isEmpty()){
            EntityNode node = queue.poll();
            Set<EventEdge> incoming = graph.incomingEdgesOf(node);
            Set<EventEdge> outgoing = graph. outgoingEdgesOf(node);
            EventEdge incomingHighestWeight = getHeighestWeightEdge(incoming);
            EventEdge outgoingHighestWeight = getHeighestWeightEdge(outgoing);
            if(incomingHighestWeight!= null){
                if(!map.containsKey(incomingHighestWeight.getSource().getID())) {
                    map.put(incomingHighestWeight.getSource().getID(), new EntityNode(incomingHighestWeight.getSource()));
                    queue.offer(incomingHighestWeight.getSource());
                }
                EventEdge incomingCopy = new EventEdge(incomingHighestWeight);
                EntityNode copy1 = map.get(node.getID());
                EntityNode copy2 = map.get(incomingHighestWeight.getSource().getID());
                result.addVertex(copy1);
                result.addVertex(copy2);
                result.addEdge(copy2, copy1, incomingCopy);
            }
            if(outgoingHighestWeight != null) {
                if (!map.containsKey(outgoingHighestWeight.getSink().getID())) {
                    map.put(outgoingHighestWeight.getSink().getID(), new EntityNode(outgoingHighestWeight.getSink()));
                    queue.offer(outgoingHighestWeight.getSink());
                }
                EventEdge outgoingCopy = new EventEdge(outgoingHighestWeight);
                EntityNode copy1 = map.get(node.getID());
                EntityNode copy3 = map.get(outgoingCopy.getSink().getID());
                result.addVertex(copy1);
                result.addVertex(copy3);
                result.addEdge(copy1, copy3, outgoingCopy);
            }

        }
        System.out.println("dEBUG: " + result.vertexSet().size());
        IterateGraph iter = new IterateGraph(result);
        iter.exportGraph("HighestWeight");
    }

    private EventEdge getHeighestWeightEdge(Set<EventEdge> edges){
        List<EventEdge> edgeList = new ArrayList<>(edges);
        if(edgeList.size() == 0) return null;
        EventEdge res = edgeList.get(0);
        for(int i=1; i< edgeList.size(); i++){
            if(res.weight < edgeList.get(i).weight){
                res = edgeList.get(i);
            }
        }
        return res;
    }
    private Set<EntityNode> getSources(EntityNode e){
        Set<EventEdge> edges  = graph.incomingEdgesOf(e);
        Set<EntityNode> sources = new HashSet<>();
        for(EventEdge edge: edges){
            sources.add(edge.getSource());
        }
        assert sources.size() <= edges.size();
        return sources;
    }

    public double avergeEdgeWeight(){
        int nums = graph.edgeSet().size();
        double sum = 0.0;
        for(EventEdge edge : graph.edgeSet()){
            sum += edge.weight;
        }
        System.out.println("average weight: "+sum/(nums*1.0));
        return sum/(nums*1.0);
    }

    public double sdEdgeWeight(double average){
        int nums = graph.edgeSet().size();
        double sd = 0.0;
        for(EventEdge edge : graph.edgeSet()){
            double diff = edge.weight-average;
            sd+=diff*diff;
        }
        System.out.println("standard deviation: "+Math.sqrt(sd/(1.0*(nums-1))));
        return Math.sqrt(sd/(1.0*(nums-1)));
    }

    /*this need to be tested*/
    public void filterGraphBasedOnAverageWeight(){
        double averageEdgeWeight = avergeEdgeWeight();
        double sd = sdEdgeWeight(averageEdgeWeight);
        List<EventEdge> edges = new ArrayList<>(graph.edgeSet());
        double threshold = 0.138;//averageEdgeWeight/5;
        System.out.println("threshold: "+threshold);
        for(int i=0; i< edges.size(); i++){

            if(edges.get(i).weight < threshold){
                graph.removeEdge(edges.get(i));
            }
        }
        List<EntityNode> list = new ArrayList<>(graph.vertexSet());
        for(int i=0; i< list.size(); i++){
            EntityNode v = list.get(i);
            if(graph.incomingEdgesOf(v).size() == 0 && graph.outgoingEdgesOf(v).size() == 0){
                graph.removeVertex(v);
            }
        }
    }

    public double OTSUThreshold(){
        List<Double> weights  = new ArrayList<>();
        for(EventEdge e: graph.edgeSet())
            weights.add(e.weight);
        Collections.sort(weights);

        double min_sigma = Double.MAX_VALUE, sum = 0.0;
        int min_i = 0, length = weights.size();
        for(int i = 0; i < length; i++) sum += weights.get(i);
        double sum1 = 0.0;
        for(int i = 0; i< length; i++) {
            sum1+=weights.get(i);
            double avg1 = sum1/(i+1);
            double avg2 = (sum-sum1)/(length-i-1);
            double sigma1 = 0.0, sigma2 = 0.0;
            for(int j = 0; j <= i; j++)
                sigma1 += Math.pow(weights.get(i)-avg1,2.0);
            for(int j = i+1; j < length; j++)
                sigma2 += Math.pow(weights.get(i)-avg2,2.0);
            if(sigma1+sigma2 < min_sigma){
                min_sigma = sigma1 + sigma2;
                min_i = i;
            }
        }
        return weights.get(min_i);
    }

    public void removeIsolatedIslands(String POI){
        ConnectivityInspector ci = new ConnectivityInspector(graph);
        Set verticesConnectedToPOI = ci.connectedSetOf(graphiterator.getGraphVertex(POI));
        List<EntityNode> list = new ArrayList<>(graph.vertexSet());
        for(int i=0; i< list.size(); i++){
            EntityNode v = list.get(i);
            if(!verticesConnectedToPOI.contains(v)){
                graph.removeVertex(v);
            }
        }
    }

    public void removeIrrelaventVertices(String POI){
        EntityNode POIVertex = graphiterator.getGraphVertex(POI);
        LinkedList<EventEdge> queue = new LinkedList<>(graph.incomingEdgesOf(POIVertex));
        Set<EntityNode> ancestors = new HashSet<>();
        ancestors.add(POIVertex);
        while(!queue.isEmpty()){
            EntityNode v = graph.getEdgeSource(queue.pollLast());
            ancestors.add(v);
            for(EventEdge e:graph.incomingEdgesOf(v))
                if(!ancestors.contains(graph.getEdgeSource(e)))
                    queue.addFirst(e);
        }

        queue = new LinkedList<>(graph.outgoingEdgesOf(POIVertex));
        Set<EntityNode> children = new HashSet<>();
        children.add(POIVertex);
        while(!queue.isEmpty()){
            EntityNode v = graph.getEdgeTarget(queue.pollLast());
            children.add(v);
            for(EventEdge e:graph.outgoingEdgesOf(v))
                if(!children.contains(graph.getEdgeTarget(e)))
                    queue.addFirst(e);
        }

        ancestors.addAll(children);
        List<EntityNode> list = new ArrayList<>(graph.vertexSet());
        for(int i=0; i< list.size(); i++){
            EntityNode v = list.get(i);
            if(!ancestors.contains(v)){
                graph.removeVertex(v);
            }
        }

    }

    public long getDataAmount(String signature){
        EntityNode node = graphiterator.getGraphVertex(signature);
        long res = 0;
        Set<EventEdge> edges = graph.incomingEdgesOf(node);
        for(EventEdge e:edges){
            res += e.getSize();
        }
        return res;
    }

    public static void main(String[] args){
        String[] locapIPS = {"10.0.2.15"};
        String path = "/home/fang/thesis2/Data/Expdata2/aptgetInstallUnrar.txt";
        ProcessGraph pGraph = new ProcessGraph(path, locapIPS);
        pGraph.backTrack("/usr/bin/unrar-nonfree.dpkg-new", "File");
        pGraph.CPR();
        GraphSplit splitGraph = new GraphSplit(pGraph.afterCPR);
        splitGraph.splitGraph();
        InferenceReputation test = new InferenceReputation(splitGraph.inputGraph);
        String[] entityWithHighReputation = {"10.0.2.15:58250->91.189.91.26:80"};
        test.setReliableReputation(entityWithHighReputation);
        try {
            test.graphiterator.bfs("/usr/bin/unrar-nonfree.dpkg-new");
        }catch (Exception e){
            e.printStackTrace();
        }

        test.checkTimeAndAmount();
//        try {
//            test.inferRuputation();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try{
//            test.printWeights();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        test.printReputation();





    }


}
