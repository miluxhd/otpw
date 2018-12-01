package ir.milux;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main (String[] args) throws InterruptedException, SQLException, ClassNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool (10);
        executorService.submit (new Runnable () {
            @Override
            public void run () {
                Master master = new Master ();
                try {
                    master.runMaster ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        });

        Thread.sleep (5000);
        executorService.submit (new Runnable () {
            @Override
            public void run () {
            Agent agent = new Agent ();
                try {
                    agent.startLocalService ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        });

    }
}
