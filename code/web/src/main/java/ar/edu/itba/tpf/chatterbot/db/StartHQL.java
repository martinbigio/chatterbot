package ar.edu.itba.tpf.chatterbot.db;

import org.hsqldb.Server;

public class StartHQL {

    public static void main(String[] args) {
        Server s = new Server();
        s.setDatabaseName(0, "chatterbot");
        s.setDatabasePath(0, "../db/chatterbot");
        s.start();
    }
}
