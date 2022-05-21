package com.server.www.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Logger;

import static java.util.Objects.nonNull;

public class Listener implements Runnable, AutoCloseable{
    private final Logger logger;
    private final Queue<Socket> socketPool;
    private final ServerSocket serverSocket;
    private boolean runnable;

    public Listener(final ServerSocket serverSocket, Queue<Socket> socketPool){
        this.logger = Logger.getLogger(this.getClass().getName());
        this.serverSocket = serverSocket;
        this.socketPool = socketPool;
        this.runnable = true;
    }

    @Override
    public void run() {
        try {
            listen();
        } catch (IOException | InterruptedException e) {
            logger.warning(e.getMessage());
            runnable = false;
        }
    }
    private void listen() throws IOException, InterruptedException {
        logger.info("start listening");
        while (runnable){
            Socket socket = null;
            try {
                 socket = serverSocket.accept();
            }catch (Exception ignored){}
            logger.info("socket " + socket);
            addToPoolNullable(socket);
        }
        logger.info("end listening");
    }

    private void addToPoolNullable(Socket socket) throws InterruptedException {
        if(nonNull(socket)){
            logger.info("get socket from " + socket.getInetAddress().toString());
            while (!socketPool.offer(socket) && runnable){
                synchronized (this){
                    this.wait(1000);
                }
            }
        }
    }

    public synchronized void stop(){
        runnable = false;
    }
    public boolean isRunnable(){
        return runnable;
    }

    @Override
    public void close() {

    }
}
