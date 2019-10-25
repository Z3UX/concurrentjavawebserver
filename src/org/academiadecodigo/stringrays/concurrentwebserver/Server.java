package org.academiadecodigo.stringrays.concurrentwebserver;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private ServerSocket serverSocket;
    private int portNumber;
    private int maxConnections;


    public Server(int maxConnections, int portNumber) {
        this.maxConnections = maxConnections;
        this.portNumber = portNumber;
    }

    public void start() {


        ExecutorService fixedPool = Executors.newFixedThreadPool(maxConnections);

        try {

            serverSocket = new ServerSocket(portNumber);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                fixedPool.execute(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(serverSocket);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}