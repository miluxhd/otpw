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
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
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
        final String uuid = new String (Files.readAllBytes (uuidFile.toPath ()));
        for (String user: users
             ) {
            getPassword (uuid.trim (),user,false);
        }

        Server server = new Server ();
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
                MessageRequest messageRequest = new MessageRequest ();
                messageRequest.setChallenge (challenge);
                messageRequest.setUsername (username);
                messageRequest.setUuid (uuid);
                Gson gson = new Gson ();
                request.content(new StringContentProvider(gson.toJson (messageRequest)));
                try {
                    ContentResponse response = request.send();
                    httpClient.stop();
                }catch (Exception e){
                    e.printStackTrace ();
                }
            }
        }),"/login");
server.start ();
    }
    public void getPassword(String uuid,String user, boolean force) throws Exception {
        File file = new File ("/home/"+user.split (":")[0]+"/.otpw");
        if (! force && file.exists () ){
            return;
        }
        HttpClient httpClient = new HttpClient ();
        httpClient.start ();
        String uri = Properties.getProperty ("agent.otpw.getpass")+"?uuid="+uuid+
                "&user="+user.split (":")[0]+
                "&phone="+user.split (":")[1];
        ContentResponse response = httpClient.GET (uri);
        Gson gson = new Gson ();
        Password password = gson.fromJson (response.getContentAsString (),Password.class);
        BufferedWriter writer = new BufferedWriter (new FileWriter (file));
        for (String p :
                password.list) {
            writer.write (p+"\n");
        }
        writer.close ();
        File home = new File("/home/"+user.split (":")[0]+"/.");
        GroupPrincipal group = Files.readAttributes(home.toPath (),
                PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();
        UserPrincipal owner = Files.readAttributes (home.toPath () ,
                PosixFileAttributes.class , LinkOption.NOFOLLOW_LINKS).owner ();

        PosixFileAttributeView attributeView = Files.getFileAttributeView (file.toPath () , PosixFileAttributeView.class , LinkOption.NOFOLLOW_LINKS);
        attributeView.setGroup (group);
        attributeView.setOwner (owner);
        httpClient.stop ();
return;

    }
}
