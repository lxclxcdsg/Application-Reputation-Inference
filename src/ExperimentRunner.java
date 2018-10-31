import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ExperimentRunner {
    String PathToLogs;
    String PathToRes;

    public ExperimentRunner(String pathToLogs, String pathToRes){
        PathToLogs = pathToLogs;
        PathToRes = pathToRes;
    }

    public void run() throws FileNotFoundException{
        File resDir = new File(PathToRes);
        if(!resDir.exists() || !resDir.isDirectory())
            resDir.mkdir();

        File logDir = new File(PathToLogs);
        if(!logDir.exists() || !logDir.isDirectory())
            throw new FileNotFoundException();

        File[] logs= logDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("txt");
            }
        });

        List<Experiment> experiments = new ArrayList<>();
        try{
            for(File log : logs){
                experiments.add(new Experiment(log,new File(log.getAbsolutePath()+".property")));
            }

            for(Experiment e : experiments){
                File oneRes = new File(resDir+"/"+e.log.getName().split("\\.")[0]);
                if(!oneRes.exists())
                    oneRes.mkdir();
                Logger logger = Logger.getLogger(e.log.getName());
                FileHandler fileHandler = new FileHandler(oneRes.getAbsolutePath()+"/"+e.log.getName().split("\\.")[0]+".log");
                fileHandler.setLevel(Level.INFO);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                logger.info("#############Configurations#############");
                logger.info("\tLog File: "+e.log.getAbsolutePath()+"\n");
                logger.info("\tPOI: "+e.POI+"\n");
                logger.info("\tLocal IP: "+formatArray(MetaConfig.localIP)+"\n");
                logger.info("\tHigh RP: "+formatArray(e.highRP)+"\n");
                logger.info("\tMid RP: "+formatArray(e.midRP)+"\n");
                logger.info("\tLow RP: "+formatArray(e.lowRP)+"\n");
                logger.info("\tThreshold: "+String.valueOf(e.threshold)+"\n");
                logger.info("\tTrack Origin: "+String.valueOf(e.trackOrigin)+"\n");
                logger.info("##############Sysdig Parser#############");
                logger.info("\tP2P: "+formatArray(MetaConfig.ptopSystemCall)+"\n");
                logger.info("\tP2F: "+formatArray(MetaConfig.ptofSystemCall)+"\n");
                logger.info("\tF2P: "+formatArray(MetaConfig.ftopSystemCall)+"\n");
                logger.info("\tP2N: "+formatArray(MetaConfig.ptonSystemCall)+"\n");
                logger.info("\tN2P: "+formatArray(MetaConfig.ntopSystemCall)+"\n");
                logger.info("#########################################");
                ProcessOneLog.process(oneRes.getAbsolutePath()+"/", "", e.threshold, e.trackOrigin, e.log.getAbsolutePath(),MetaConfig.localIP,e.POI,e.highRP,e.midRP,e.lowRP, e.log.getName().split("\\.")[0]);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String formatArray(String[] array){
        return String.join(",",array);
    }

    public static void main(String[] args){
        try{
            ExperimentRunner er = new ExperimentRunner("/home/lcl/fine_logs","/home/lcl/results/fine");
            er.run();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
