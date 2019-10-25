package org.academiadecodigo.stringrays.concurrentwebserver;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(1000, 58080);
        server.start();
    }
}
