package ir.milux;

import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageSender {
    Logger logger = Logger.getLogger (MessageRequest.class);
    public void send(String id,String password){
        Runtime runtime = Runtime.getRuntime ();
        String messageCommand = Properties.getProperty ("master.messagecommand") +" '" + id + "' '" + password+"'";
        try {
            runtime.exec (messageCommand);
        } catch (IOException e) {
            logger.error (e.getMessage ());
        }
    }
}
