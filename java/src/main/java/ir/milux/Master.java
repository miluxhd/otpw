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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class Master {
    private Logger logger = Logger.getLogger (Master.class);
    public void runMaster() throws Exception {
        Server server = new Server ();
        DB db = new DB ();
        ServerConnector http = new ServerConnector (server);
        http.setPort(Integer.parseInt (Properties.getProperty ("master.port")));
        server.addConnector(http);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet (new ServletHolder (new HttpServlet () {
            @Override
            protected void doPost (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
                String stringRequest = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                Gson gson = new Gson ();
                System.out.println (stringRequest);
                MessageRequest request = gson.fromJson (stringRequest , MessageRequest.class);
                StringBuilder password = new StringBuilder ();
                try {
                    List<String> challenges = Arrays.asList (request.challenge.split ("/"));
                    for (String ch :
                            challenges) {
                     password.append (db.getPassword (request.username,request.uuid,ch));
                    }
                } catch (SQLException e) {
                    e.printStackTrace ();
                }
                MessageSender sender = new MessageSender ();
                try {
                    sender.send (db.getPhoneNumber (request.username,request.uuid),password.toString ());
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        }),"/login");

        handler.addServlet (new ServletHolder (new HttpServlet () {
            @Override
            protected void doGet (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
                String uuid = req.getParameter ("uuid");
                String user = req.getParameter ("user");
                String phone = req.getParameter ("phone");

                Password passowrds = new Password ();
                HashMap<String, String> plainPass = null;
                try {
                    plainPass = passowrds.generate ();
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                Gson gson = new Gson ();
                resp.setStatus(HttpStatus.OK_200);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");

                try {
                    db.flushUser (user,uuid);
                    db.store (user,phone,uuid,plainPass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace ();
                } catch (SQLException e) {
                    e.printStackTrace ();
                }
                resp.getWriter ().write (gson.toJson (passowrds));
            }
        }),"/getpass/*");
        server.start ();
    }
}
