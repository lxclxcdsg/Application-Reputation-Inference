import org.jgrapht.graph.DirectedPseudograph;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by fang on 4/6/18.
 */
public class ProcessOneLog {
    public static void main(String[] args){
        String path = "/home/lcl/logs/file_manipulation/4.txt";
        String[]localIP = {"192.168.29.234"};
        String detection = "/home/lcl/upload";
        String[] highRP = {};
        String[] midRP = {};//"/dev/pts/1","/etc/nsswitch.conf","/proc/self/maps","/usr/lib/jvm/java-8-oracle/jre/lib/amd64/libverify.so","/usr/lib/jvm/java-8-oracle/jre/lib/ext/meta-index","/usr/lib/jvm/java-8-oracle/jre/lib/amd64/jvm.cfg","/etc/localtime","/usr/lib/jvm/java-8-oracle/jre/lib/meta-index","/usr/lib/jvm/java-8-oracle/jre/lib/rt.jar","/usr/lib/jvm/java-8-oracle/jre/lib/amd64/libjava.so","/usr/lib/jvm/java-8-oracle/jre/lib/amd64/libzip.so","/usr/share/locale/locale.alias","/dev/pts/0","/proc/filesystems","/etc/security/capability.conf","/etc/nsswitch.conf","/proc/self/loginuid","/dev/urandom","/lib/x86_64-linux-gnu/security/pam_env.so","/usr/lib/x86_64-linux-gnu/libk5crypto.so.3","/lib/x86_64-linux-gnu/security/pam_selinux.so","/lib/x86_64-linux-gnu/libutil.so.1","/lib/x86_64-linux-gnu/libexpat.so.1","/lib/x86_64-linux-gnu/libaudit.so.1","/lib/x86_64-linux-gnu/libpcre.so.3","/lib/x86_64-linux-gnu/security/pam_nologin.so","/lib/x86_64-linux-gnu/libgpg-error.so.0","/lib/x86_64-linux-gnu/libnsl.so.1","/lib/x86_64-linux-gnu/libcom_err.so.2","/lib/x86_64-linux-gnu/libresolv.so.2","/lib/x86_64-linux-gnu/libcap-ng.so.0","/lib/x86_64-linux-gnu/security/pam_permit.so","/lib/x86_64-linux-gnu/libtinfo.so.5","/lib/x86_64-linux-gnu/libkeyutils.so.1","/usr/lib/x86_64-linux-gnu/liblz4.so.1","/lib/x86_64-linux-gnu/librt.so.1","/lib/x86_64-linux-gnu/libgcrypt.so.20","/lib/x86_64-linux-gnu/libnss_systemd.so.2","/lib/x86_64-linux-gnu/security/pam_cap.so","/lib/x86_64-linux-gnu/security/pam_loginuid.so","/lib/x86_64-linux-gnu/libpam.so.0","/lib/x86_64-linux-gnu/libpam_misc.so.0","/usr/lib/x86_64-linux-gnu/libkrb5support.so.0","/usr/lib/x86_64-linux-gnu/libkrb5.so.3","/usr/lib/x86_64-linux-gnu/libgssapi_krb5.so.2","/lib/x86_64-linux-gnu/libcrypt.so.1","/lib/x86_64-linux-gnu/libnss_compat.so.2","/lib/x86_64-linux-gnu/libz.so.1","/usr/lib/x86_64-linux-gnu/libzstd.so.1","/usr/lib/x86_64-linux-gnu/libapt-pkg.so.5.0","/lib/x86_64-linux-gnu/security/pam_keyinit.so","/lib/x86_64-linux-gnu/security/pam_systemd.so","/usr/lib/python3.6/lib-dynload/_csv.cpython-36m-x86_64-linux-gnu.so","/lib/x86_64-linux-gnu/libudev.so.1","/lib/x86_64-linux-gnu/libgcc_s.so.1","/lib/x86_64-linux-gnu/security/pam_deny.so","/lib/x86_64-linux-gnu/liblzma.so.5","/lib/x86_64-linux-gnu/security/pam_gnome_keyring.so","/usr/lib/x86_64-linux-gnu/libcrypto.so.1.0.0","/lib/x86_64-linux-gnu/libsystemd.so.0","/lib/x86_64-linux-gnu/security/pam_umask.so","/lib/x86_64-linux-gnu/libm.so.6","/lib/x86_64-linux-gnu/libcap.so.2","/lib/x86_64-linux-gnu/libbz2.so.1.0","/lib/x86_64-linux-gnu/libpthread.so.0","/lib/x86_64-linux-gnu/libnss_files.so.2","/usr/lib/x86_64-linux-gnu/libstdc++.so.6","/lib/x86_64-linux-gnu/libnss_nis.so.2","/lib/x86_64-linux-gnu/libselinux.so.1","/lib/x86_64-linux-gnu/libdl.so.2","/lib/x86_64-linux-gnu/security/pam_mail.so","/lib/x86_64-linux-gnu/security/pam_limits.so","/lib/x86_64-linux-gnu/libc.so.6","/lib/x86_64-linux-gnu/security/pam_unix.so","/lib/x86_64-linux-gnu/security/pam_motd.so","/usr/lib/x86_64-linux-gnu/libapt-private.so.0.0","/lib/x86_64-linux-gnu/libwrap.so.0"};
        String[] lowRP = {"192.168.29.125:10289->192.168.29.234:22"};
        String[] paths = path.split("/");
        process(path,localIP,detection,highRP,midRP,lowRP, paths[paths.length-1]);
    }

    private static void process(String logfile, String[] IP, String detection,String[] highRP,String[] midRP,String[] lowRP, String filename){
        String resultDir =  "/home/lcl/results/rep/";
        OutputStream os = null;
        OutputStream weightfile = null;
        try{
            os = new FileOutputStream(resultDir+filename+"_stats");
            GetGraph getGraph = new GetGraph(logfile, IP);
            getGraph.GenerateGraph();
            DirectedPseudograph<EntityNode, EventEdge> orignal = getGraph.getJg();
            System.out.println("Original vertex number:" + orignal.vertexSet().size() + " edge number : " + orignal.edgeSet().size());
            os.write(("Original vertex number:" + orignal.vertexSet().size() + " edge number : " + orignal.edgeSet().size()+"\n").getBytes());

            BackTrack backTrack = new BackTrack(orignal);
            backTrack.backTrackPOIEvent(detection);
            System.out.println("After Backtrack vertex number is: "+ backTrack.afterBackTrack.vertexSet().size() + " edge number: " + backTrack.afterBackTrack.edgeSet().size());
            os.write(("After Backtrack vertex number is: "+ backTrack.afterBackTrack.vertexSet().size() + " edge number: " + backTrack.afterBackTrack.edgeSet().size()+"\n").getBytes());


            IterateGraph out = new IterateGraph(backTrack.afterBackTrack);
            out.exportGraph(resultDir+"BackTrack_"+filename);
            //backTrack.exportGraph("backTrack");
            CasualityPreserve CPR = new CasualityPreserve(backTrack.afterBackTrack);
            CPR.CPR();
            System.out.println("After CPR vertex number is: "+ CPR.afterMerge.vertexSet().size() + " edge number: " + CPR.afterMerge.edgeSet().size());
            os.write(("After CPR vertex number is: "+ CPR.afterMerge.vertexSet().size() + " edge number: " + CPR.afterMerge.edgeSet().size()+"\n").getBytes());

            out = new IterateGraph(CPR.afterMerge);
            out.exportGraph(resultDir+"AfterCPR_"+filename);
            GraphSplit split = new GraphSplit(CPR.afterMerge);
            split.splitGraph();
            System.out.println("After Split vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size());


            InferenceReputation infer = new InferenceReputation(split.inputGraph);

            os.write(("After Split vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size()+"\n").getBytes());
            weightfile = new FileOutputStream(resultDir+"weights_"+filename);
            for(EventEdge e: infer.graph.edgeSet()){
                weightfile.write((String.valueOf(e.weight)+",").getBytes());
            }

            infer.calculateWeights();
            infer.filterGraphBasedOnAverageWeight();
            //infer.removeIsolatedIslands(detection);
            infer.removeIrrelaventVertices(detection);
            //infer.calculateWeights();


            infer.initialReputation(highRP,midRP,lowRP);
            infer.PageRankIteration2(highRP,lowRP);
            //infer.fixReputation(highRP);


            System.out.println("After Filter vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size());
            os.write(("After Filter vertex number is: "+ split.inputGraph.vertexSet().size() + " edge number: " + split.inputGraph.edgeSet().size()).getBytes());

            System.out.println("OTSU: "+infer.OTSUThreshold());


//        //infer.onlyPrintHeightestWeights(detection);
            infer.exportGraph(resultDir+"Weight_"+filename);
//        IterateGraph iterGraph = new IterateGraph(infer.graph);

//        iterGraph.filterGraphBasedOnVertexReputation();
//        iterGraph.removeSingleVertex();
//        iterGraph.exportGraph("FilteredInstallMongodb");
//        List<DirectedPseudograph<EntityNode, EventEdge>> paths = iterGraph.getHighWeightPaths(detection);
//        for(int i=0; i< paths.size();i++){
//            IterateGraph iter = new IterateGraph(paths.get(i));
//            String fileName = String.valueOf(i) + "path";
//            iter.exportGraph(fileName);
//        }
            //iterGraph.printEdgesOfVertex("11035dpkg");

//        infer.checkWeightsAfterCalculation();
//        infer.exportGraph("UnrarReputation");
            Runtime rt = Runtime.getRuntime();
            String[] cmd = {"/bin/sh","-c","dot -T svg "+resultDir+"AfterCPR_"+filename+".dot"
                    + " > "+resultDir+"AfterCPR_"+filename+".svg"};
            rt.exec(cmd);
            cmd = new String[]{"/bin/sh", "-c","dot -T svg "+resultDir+"Weight_"+filename+".dot"
                    + " > "+resultDir+"Weight_"+filename+".svg"};
            rt.exec(cmd);
//            cmd = new String[]{"/bin/sh","-c","dot -T svg /home/lcl/results/aaa/BackTrack_"+filename+".dot"
//                    + " > /home/lcl/results/aaa/BackTrack_"+filename+".svg"};
//            rt.exec(cmd);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                os.close();
                weightfile.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }




    }
}
