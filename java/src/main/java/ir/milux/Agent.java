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
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Agent {
    Logger logger = Logger.getLogger (Agent.class);
    public void runServer() throws IOException {
        Server server = new Server ();
        final String uuid = new String (Files.readAllBytes (Paths.get (Properties.getProperty ("uuid.path"))));
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
                Request request =  httpClient.POST (Properties.getProperty ("otpw.master.login"));
                request.header(HttpHeader.CONTENT_TYPE, "application/json");
                request.content(new StringContentProvider ("{\"username\":"+username
                        +",\"challenge\":"+challenge
                        +",\"uuid\":"+uuid+
                        "}","utf-8"));
                try {
                    ContentResponse response = request.send();
                    response.getContent ();
                    httpClient.stop();
                }catch (Exception e){
                }
            }
        }),"/login");
    }
    public Password getPassword(String user, boolean force) throws Exception {
        File file = new File (Properties.getProperty (user+".homedir")+"/.otpw");
        if (! force && file.exists () ){
            return null;
        }
        HttpClient httpClient = new HttpClient ();
        httpClient.start ();
        ContentResponse response = httpClient.GET (Properties.getProperty ("otpw.master.getpass"));
        Gson gson = new Gson ();
        Password password = gson.fromJson (response.getContentAsString (),Password.class);
        BufferedWriter writer = new BufferedWriter (new FileWriter (file));
        for (String p :
                password.list) {
            writer.write (p+"\n");
        }
        writer.close ();
        httpClient.stop ();
return null;

    }
}
