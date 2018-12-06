package ir.milux;

import org.apache.commons.cli.*;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main (String[] args) throws Exception {
        Options options = new Options ();
        options.addOption("m" , "master", false, "master");
        options.addOption ("a" , "agent",false , "agent");
        CommandLineParser parser = new DefaultParser ();
        CommandLine cmd = null;
        HelpFormatter formatter = new HelpFormatter();

        try {
            cmd = parser.parse( options, args);
        }catch (Exception e){
            formatter.printHelp("java -jar otpw-service.jar ", options);
            System.exit (1);
        }
        if (cmd.hasOption ("master") || cmd.hasOption ("m")){
            Master master = new Master ();
            master.runMaster ();
        }
        else if (cmd.hasOption ("agent") || cmd.hasOption ("a")){
            Agent agent = new Agent ();
            agent.startLocalService ();
        }
        else {
            formatter.printHelp("java -jar otpw-service.jar ", options);
            System.exit (1);
        }
    }
}
