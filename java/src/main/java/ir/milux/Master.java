package ir.milux;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.stream.Collectors;


public class Master {
    private Logger logger = Logger.getLogger (Master.class);
    public void runMaster() throws Exception {
        Server server = new Server ();
        ServerConnector http = new ServerConnector (server);
        http.setPort(Integer.parseInt (Properties.getProperty ("master.port")));
        server.addConnector(http);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet (new ServletHolder (new HttpServlet () {
            @Override
            protected void doPost (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
                String stringRequest = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                logger.info (stringRequest);
            }
        }),"/login");

        handler.addServlet (new ServletHolder (new HttpServlet () {
            @Override
            protected void doGet (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
                Password passowrds = new Password ();
                try {
                    passowrds.generate ();
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                Gson gson = new Gson ();
                resp.setStatus(HttpStatus.OK_200);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");
                resp.getWriter ().write (gson.toJson (passowrds));

                // TODO : save original passwords into database
            }
        }),"/getpass");
        server.start ();
    }
}
