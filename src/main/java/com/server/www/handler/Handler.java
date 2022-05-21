package com.server.www.handler;

import com.server.www.response.builder.ResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class Handler implements Runnable{
    private final Logger logger;
    private static final String path = "D:\\trash\\server_logs\\handlers\\";
    private final Socket socket;
    private static final Date date = new Date();

    public Handler(final Socket socket){
        this.socket = socket;
        this.logger = Logger.getLogger(this.getClass().getName()+ this);
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            try {
                logger.warning("exception occurred: " + e);
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void handle() throws IOException {
        String filePath = path+socket.getInetAddress().toString().substring(1).replace(':', '_') + date.toString().replace(':', '_') + ".txt";
        logger.info("log path: " + filePath);
        try(OutputStream responseOutput = socket.getOutputStream();
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Writer fileLogger = new PrintWriter(new FileOutputStream(filePath))){
            String requestLine = null;
            logRequestPacket(requestReader, fileLogger);

            responseOutput.write(ResponseBuilder.buildDefault().getBytes(StandardCharsets.UTF_8));
            logger.info("Handling done");
        }
        socket.close();
    }

    private void logRequestPacket(BufferedReader requestReader, Writer fileLogger) throws IOException {
        String requestLine;
        do {
            requestLine = requestReader.readLine();
            fileLogger.write(requestLine + '\n');
        } while (!requestLine.isEmpty());
    }
}
