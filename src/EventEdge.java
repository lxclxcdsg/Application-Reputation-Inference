import java.math.BigDecimal;

/**
 * Created by fang on 3/21/18.
 */
public class EventEdge{
    private EntityNode source;
    private EntityNode sink;
    public long id;
    private BigDecimal startTime;
    private BigDecimal endTime;
    private String type;
    private String event;
    private long size;
    public double weight;

    EventEdge(PtoFEvent pf){
        source = new EntityNode(pf.getSource());
        sink = new EntityNode(pf.getSink());
        id = pf.getUniqID();
        startTime = new BigDecimal(pf.getStart());
        endTime = new BigDecimal(pf.getEnd());
        type = pf.getType();
        event = pf.getEvent();
        size = pf.getSize();
    }

    EventEdge(PtoPEvent pp){
        source = new EntityNode(pp.getSource());
        sink = new EntityNode(pp.getSink());
        id = pp.getUniqID();
        startTime = new BigDecimal(pp.getStart());
        endTime = new BigDecimal(pp.getEnd());
        type = pp.getType();
        event = pp.getEvent();
        size = 0;
    }

    EventEdge(FtoPEvent fp){
        source = new EntityNode(fp.getSource());
        sink = new EntityNode(fp.getSink());
        id = fp.getUniqID();
        startTime = new BigDecimal(fp.getStart());
        endTime = new BigDecimal(fp.getEnd());
        type = fp.getType();
        event = fp.getEvent();
        size = fp.getSize();
    }

    EventEdge(NtoPEvent np){
        source = new EntityNode(np.getSource());
        sink = new EntityNode(np.getSink());
        id = np.getUniqID();
        startTime = new BigDecimal(np.getStart());
        endTime = new BigDecimal(np.getEnd());
        type = np.getType();
        event = np.getEvent();
        size = np.getSize();
    }

    EventEdge(PtoNEvent pn){
        source = new EntityNode(pn.getSource());
        sink = new EntityNode(pn.getSink());
        id = pn.getUniqID();
        startTime = new BigDecimal(pn.getStart());
        endTime = new BigDecimal(pn.getEnd());
        type = pn.getType();
        event = pn.getEvent();
        size = pn.getSize();
    }

    public EventEdge(EventEdge edge){
        this.source = edge.getSource();
        this.sink = edge.getSink();
        this.id = edge.getID();
        this.startTime = edge.getStart();
        this.endTime = edge.getEnd();
        this.type = edge.getType();
        this.size = edge.getSize();
    }

    public EventEdge(String type, BigDecimal starttime, BigDecimal endtime, long amount, EntityNode from, EntityNode to, long id){
        source = from;
        sink = to;
        this.type = type;
        this.size = amount;
        this.startTime = starttime;
        this.endTime = endtime;
        this.id = id;
    }
    @Deprecated
    public EventEdge(EventEdge edge, long id){                   //for split edge
        this.source = edge.getSource();
        this.sink = edge.getSink();
        this.id = id;
        this.startTime = edge.getStart();
        this.endTime = edge.getEnd();
        this.type = edge.getType();
        this.size = edge.getSize();
    }

    public EventEdge(EventEdge edge, EntityNode from, EntityNode to, long id){
        this.source = from;
        this.sink = to;
        this.id = id;
        this.startTime = edge.getStart();
        this.endTime = edge.getEnd();
        this.type = edge.getType();
        this.size = edge.getSize();
    }

    public EventEdge merge(EventEdge e2){
        this.endTime = e2.endTime;
        this.size += e2.size;
        return this;
    }

    public void printInfo(){
        System.out.println("id: "+this.id+" Source:"+this.source.getSignature()+" Target:"+this.getSink().getSignature()+" End time:"+
                this.endTime.toString()+" Size:"+ this.size);
    }

    long getID(){return id;}

    public void setId(long id){
        this.id = id;
    }

    EntityNode getSource(){return source;}

    EntityNode getSink(){ return sink;}

    BigDecimal getStart(){
        return startTime;
    }

    BigDecimal getEnd(){
        return endTime;
    }

    BigDecimal[] getInterval(){
        BigDecimal[] res = {startTime,endTime};
        return res;
    }

    String getType(){
        return type;
    }

    String getEvent(){return event;}

    long getSize(){return size;}

    public BigDecimal getDuration(){
        return endTime.subtract(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventEdge)) return false;

        EventEdge eventEdge = (EventEdge) o;

        if (id != eventEdge.id) return false;
        if (!source.equals(eventEdge.source)) return false;
        if (!sink.equals(eventEdge.sink)) return false;
        if (!startTime.equals(eventEdge.startTime)) return false;
        return endTime.equals(eventEdge.endTime);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + sink.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + startTime.hashCode();
//        result = 31 * result + endTime.hashCode();
        return result;
    }


}
