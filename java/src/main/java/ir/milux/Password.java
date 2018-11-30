package ir.milux;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Password {
    String type = "otpw";
    ArrayList<String> list = new ArrayList<> ();
    public void generate() throws IOException, InterruptedException {
        Random random = new Random (System.currentTimeMillis ());
        String otpwPath = Properties.getProperty ("master.otpw.tmpdir")+"/.otpw."+Math.abs (random.nextLong ());
        String passwordsPath = Properties.getProperty ("master.password.tmpdir")+".password."+Math.abs (random.nextLong ());
        String command = "gen "+ otpwPath + " " + passwordsPath;
        Process proc = Runtime.getRuntime ().exec (command);
        proc.waitFor ();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader (otpwPath));
        while ((line=reader.readLine ())!=null){
            list.add (line);
        }
    }
}
