/**
 * Created by fang on 6/28/17.
 */
public class NtoPEvent extends Event {

    private NetworkEntity source;
    private Process sink;
    private long size;
    private String event;

    public NtoPEvent(String type, String startS, String startMs,NetworkEntity source,Process sink,String event,long id){
        super(type,startS,startMs,id);
        this.source = source;
        this.sink = sink;
        this.size = 0;
        this.event = event;
    }

    public NtoPEvent(PtoNEvent a){
        super("Process to Network",a.getStart().split("\\.")[0],
                a.getStart().split("\\.")[1],a.getUniqID());
        this.source = a.getSink();
        this.sink = a.getSource();
        this.size = a.getSize();
        this.event = a.getEvent();
    }

    public NetworkEntity getSource() {
        return source;
    }

    public Process getSink() {
        return sink;
    }

    public long getSize() {
        return size;
    }

    public String getEvent() {
        return event;
    }

    public void updateSize(long i){
        size +=i;
    }


}
