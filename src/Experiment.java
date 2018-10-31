import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Experiment {
    Properties config;
    File log;
    String POI;
    String suffix;
    String[] highRP;
    String[] lowRP;
    String[] midRP;
    double threshold;
    boolean trackOrigin;

    public Experiment(File logFile, File configFile) throws IOException {
        FileInputStream fi = new FileInputStream(configFile);
        log = logFile;
        config = new Properties();
        config.load(fi);
        digestConfig();
    }

    private void digestConfig(){
        POI = config.getProperty("POI");

        String highRPString = config.getProperty("highRP","");
        highRP = highRPString.split(",");

        String lowRPString = config.getProperty("lowRP","");
        lowRP = lowRPString.split(",");

        String[] defaultMidRP = MetaConfig.midRP;
        String midRPString = config.getProperty("midRP","");
        String[] additionalMidRP = midRPString.split(",");
        List<String> _ = new ArrayList<>();
        _.addAll(Arrays.asList(defaultMidRP));
        _.addAll(Arrays.asList(additionalMidRP));
        midRP = _.toArray(new String[_.size()]);

        threshold = Double.parseDouble(config.getProperty("threshold","0"));

        trackOrigin = Boolean.parseBoolean(config.getProperty("trackOrigin","false"));
    }
}
