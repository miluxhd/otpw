package ir.milux;

import org.apache.log4j.Logger;

public class MessageSender {
    Logger logger = Logger.getLogger (MessageRequest.class);
    public void send(String id,String password){
        logger.info ("id : " + id + "password : " + password);
    }
}
