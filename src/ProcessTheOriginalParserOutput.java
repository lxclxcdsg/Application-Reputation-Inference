import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by fang on 7/28/17.
 */
public class ProcessTheOriginalParserOutput {
    private HashMap<String, PtoFEvent> processFileMap;
    private HashMap<String, PtoNEvent> processNetworkMap;  // this event direction is decided by the information flow direction
    private HashMap<String, PtoPEvent> processProcessMap;
    private HashMap<String, NtoPEvent> networkProcessMap;  // this event direction is decided by the information flow direction
    private HashMap<String, FtoPEvent> fileProcessMap;
    private HashMap<String, PtoNEvent> pnmap;              // this one is just according to ip direction(local->remote)
    private HashMap<String, NtoPEvent> npmap;              // this one is just according to ip direction(remote -> local)
    private SysdigOutputParser parser;

    public ProcessTheOriginalParserOutput(String logFilePath, String[]localIP){
        parser = new SysdigOutputParser(logFilePath,localIP);
        try {
            parser.getEntities();
        }catch (IOException e){
            System.out.println("The log file doesn't exist");
        }
        processFileMap = parser.getPfmap();
        pnmap = parser.getPnmap();
        processProcessMap = parser.getPpmap();
        npmap = parser.getNpmap();
        fileProcessMap = parser.getFpmap();
        processNetworkMap = new HashMap<>();
        networkProcessMap = new HashMap<>();
    }
    public HashMap<String, PtoFEvent> getProcessFileMap() {
        return processFileMap;
    }

    public HashMap<String, PtoNEvent> getProcessNetworkMap() {
        return processNetworkMap;
    }

    public HashMap<String, PtoPEvent> getProcessProcessMap() {
        return processProcessMap;
    }

    public HashMap<String, NtoPEvent> getNetworkProcessMap() {
        return networkProcessMap;
    }

    public HashMap<String, FtoPEvent> getFileProcessMap() {
        return fileProcessMap;
    }

    public HashMap<String, PtoNEvent> getPnmap() {
        return pnmap;
    }

    public HashMap<String, NtoPEvent> getNpmap() {
        return npmap;
    }

    /*reverse the network and process event according to the information flow*/
    public void reverseSourceAndSink(){
        Set<String> keys = pnmap.keySet();
        for(String key: keys){
            PtoNEvent event = pnmap.get(key);
            String eventType = event.getEvent();
            if(eventType.equals("recvmsg") || eventType.equals("read")){
                NtoPEvent reverse = new NtoPEvent(event);
                //pnmap.remove(key);
                networkProcessMap.put(key,reverse);
            }else{
                processNetworkMap.put(key,pnmap.get(key));
            }
        }
        keys = npmap.keySet();
        for(String key: keys){
            NtoPEvent event = npmap.get(key);
            String eventType = event.getEvent();
            if(eventType.equals("read") || eventType.equals("recvmsg")){
                PtoNEvent reverse = new PtoNEvent(event);
                //npmap.remove(key);
                processNetworkMap.put(key, reverse);
            }else{
                networkProcessMap.put(key, npmap.get(key));
            }
        }
        //System.out.println(pnmap.keySet().size());
//        Iterator<Map.Entry<String,PtoNEvent>> it = pnmap.entrySet().iterator();
//        while(it.hasNext()){
//            if(it.next().getValue()==null){
//                System.out.println("Event is NUll");
//            }
//            if(it.next().getValue().getEvent() == null){
//                System.out.println("Event type is null");
//            }
//            Map.Entry<String, PtoNEvent> cur = it.next();
//            PtoNEvent event = cur.getValue();
//            String eventType = event.getEvent();
//            String key = cur.getKey();
//            System.out.println(eventType);
//            if(eventType.equals("recvmsg") || eventType.equals("read")){
//                NtoPEvent reverse = new NtoPEvent(event);
//                //pnmap.remove(key);
//                //it.remove();
//                networkProcessMap.put(key,reverse);
//            }else{
//                processNetworkMap.put(key,pnmap.get(key));
//            }
//        }
//
//        Iterator<Map.Entry<String, NtoPEvent>> it2 = npmap.entrySet().iterator();
//        while(it2.hasNext()){
//            Map.Entry<String, NtoPEvent> cur = it2.next();
//            NtoPEvent event = cur.getValue();
//            String key = cur.getKey();
//            String eventType = event.getEvent();
//            if(eventType.equals("read") || eventType.equals("recvmsg")){
//                PtoNEvent reverse = new PtoNEvent(event);
//                //it2.remove();
//                processNetworkMap.put(key, reverse);
//            }else{
//                networkProcessMap.put(key, npmap.get(key));
//            }
//        }
    }

    public static  void main(String[] args){
        String[] localIP={"10.0.2.15"};
        ProcessTheOriginalParserOutput test = new ProcessTheOriginalParserOutput("pipInstall.txt",localIP);

        test.reverseSourceAndSink();
//        Map<String, PtoNEvent> pnmap = test.getPnmap();
//        System.out.println("-----------------------------");
//        for(String key:pnmap.keySet()){
//            System.out.println(pnmap.get(key).getUniqID());
//            System.out.println(pnmap.get(key).getEvent());
//        }
        System.out.println("--------------------------");
        Map<String, PtoNEvent> map = test.getProcessNetworkMap();
        System.out.println(map.size());
        for(String key:map.keySet()){
            System.out.println(map.get(key).getSink().getSrcAddress().equals(localIP[0]));
            System.out.println("Find the local ip");
        }
        Map<String, NtoPEvent> map2 = test.getNetworkProcessMap();
        for(String key:map2.keySet()){
            System.out.println(map2.get(key).getSource().getSrcAddress().equals(localIP[0]));
            System.out.println("Find the local ip");
        }
    }


}
