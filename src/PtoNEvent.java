/**
 * Created by fang on 7/13/17.
 */
public class PtoNEvent extends Event {
    private Process source;
    private NetworkEntity sink;
    private long size;
    private String event;

    public Process getSource() {
        return source;
    }

    public NetworkEntity getSink() {
        return sink;
    }

    public long getSize() {
        return size;
    }

    public String getEvent() {
        return event;
    }


    public PtoNEvent(String type, String startS, String startMs,Process source,NetworkEntity sink,String event,long id) {
        super(type, startS, startMs, id);
        this.source = source;
        this.sink = sink;
        this.size = 0;
        this.event = event;
    }

    public PtoNEvent(NtoPEvent a){
        super("Process to Network",a.getStart().split("\\.")[0],
                a.getStart().split("\\.")[1],a.getUniqID());
        this.source =a.getSink();
        this.sink = a.getSource();
        this.size = a.getSize();
        this.event = a.getEvent();

    }

    public void updateSize(long i){
        size+=i;
    }


}
