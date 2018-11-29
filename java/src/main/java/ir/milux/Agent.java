package ir.milux;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Agent {
    Logger logger = Logger.getLogger (Agent.class);
    public void runServer(){
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
                logger.info ("Username : " + username +",challenge : " + challenge);
            }
        }),"/login");
    }
}
