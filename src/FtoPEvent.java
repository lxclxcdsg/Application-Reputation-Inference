/**
 * Created by fang on 6/20/17.
 */
public class FtoPEvent extends Event{
    private FileEntity source;
    private Process sink;
    private long size;
    private String event;

    public FtoPEvent(String type, String startS, String startMs,
                     FileEntity source,Process sink,String event,long id){
        super(type,startS,startMs, id);
        this.source = source;
        this.sink = sink;
        this.size = 0;
        this.event = event;
    }

    public FileEntity getSource(){
        return source;
    }

    public Process getSink(){
        return sink;
    }

    public String getEvent(){
        return event;
    }

    public void updateSize(long i){
        size +=i;
    }

    public long getSize(){
        return size;
    }


}
