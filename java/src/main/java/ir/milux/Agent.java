package ir.milux;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class Agent {
    Logger logger = Logger.getLogger (Agent.class);
    public void startLocalService() throws Exception {
        File uuidFile = new File (Properties.getProperty ("agent.uuid.path"));
        if (!uuidFile.exists ()){
            FileWriter writer = new FileWriter (uuidFile);
            String uuid = UUID.randomUUID ().toString ();
            writer.write (uuid);
            writer.close ();
        }
        List<String> users = Arrays.asList (Properties.getProperty ("agent.otwp.users").split (","));
        for (String user: users
             ) {
            getPassword (user,false);
        }

        Server server = new Server ();
        final String uuid = new String (Files.readAllBytes (uuidFile.toPath ()));
        ServerConnector http = new ServerConnector (server);
        http.setPort(8080);
        server.addConnector(http);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet (new ServletHolder (new HttpServlet () {
            @Override
            protected void doGet (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
                String username = req.getHeader ("Username");
                String challenge = req.getHeader ("Challenge");
                HttpClient httpClient = new HttpClient ();
                try {
                    httpClient.start();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                Request request =  httpClient.POST (Properties.getProperty ("agent.otpw.login"));
                request.header(HttpHeader.CONTENT_TYPE, "application/json");
                request.content(new StringContentProvider ("{\"username\":"+username
                        +",\"challenge\":"+challenge
                        +",\"uuid\":"+uuid+
                        "}","utf-8"));
                try {
                    logger.info ("sending challenge");
                    ContentResponse response = request.send();
                    httpClient.stop();
                }catch (Exception e){
                    e.printStackTrace ();
                }
            }
        }),"/login");
server.start ();
    }
    public void getPassword(String user, boolean force) throws Exception {
        File file = new File ("/home/"+user+"/.otpw");
        if (! force && file.exists () ){
            return;
        }
        HttpClient httpClient = new HttpClient ();
        httpClient.start ();
        ContentResponse response = httpClient.GET (Properties.getProperty ("agent.otpw.getpass"));
        Gson gson = new Gson ();
        Password password = gson.fromJson (response.getContentAsString (),Password.class);
        BufferedWriter writer = new BufferedWriter (new FileWriter (file));
        for (String p :
                password.list) {
            writer.write (p+"\n");
        }
        writer.close ();
        httpClient.stop ();
return;

    }
}
