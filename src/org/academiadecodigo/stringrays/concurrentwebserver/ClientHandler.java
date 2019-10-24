package org.academiadecodigo.stringrays.concurrentwebserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private DataOutputStream output;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        readNewRequest();
    }

    private void readNewRequest() {

        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String requestMessage = input.readLine();

            System.out.println(requestMessage);

            String[] requestMessageSplit = requestMessage.split("\\s+");

            if (!requestMessageSplit[0].equals("GET")) {
                browserOutput(405, null);
                close(clientSocket);
                return;
            }

            checkIfFileExists("root" + requestMessageSplit[1]);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(clientSocket);
        }
    }

    private void checkIfFileExists(String path) {

        File file = new File(path);

        if (!file.isFile()) {
            file = new File(path + "/index.html");
        }

        if (!file.exists()) {
            file = new File("404.html");
            browserOutput(404, file);
            return;
        }

        browserOutput(200, file);
    }

    private void browserOutput(int code, File file) {

        try {

            output = new DataOutputStream(clientSocket.getOutputStream());

            if (code == 405) {
                output.writeBytes("HTTP/1.0 405 METHOD NOT ALLOWED\r\n\r\n");
                return;
            }

            if (code == 404) {
                output.writeBytes("HTTP/1.0 404 NOT FOUND\r\n");
            }

            if (code == 200) {
                output.writeBytes("HTTP/1.0 200 OK\r\n");
            }

            output.writeBytes("Content-Type: " + Files.probeContentType(file.toPath()) + "; charset=UTF-8\r\n");
            output.writeBytes("Content-Length: " + file.length() + "\r\n\r\n");
            fileOutput(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fileOutput(File file) {

        try {

            FileInputStream fileInputStream = new FileInputStream(file);

            fileInputStream.transferTo(output);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(output);
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