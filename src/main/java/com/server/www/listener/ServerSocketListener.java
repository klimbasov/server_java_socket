package com.server.www.listener;

import com.server.www.pool.SocketPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import static com.server.www.config.Config.JOIN_TIMEOUT;
import static java.util.Objects.nonNull;

public class ServerSocketListener implements AutoCloseable, Runnable{
    private final Thread listenerThread;
    private final Logger logger;
    private final SocketPool socketPool;
    private final ServerSocket serverSocket;
    private boolean runnable;

    public ServerSocketListener(final ServerSocket serverSocket, final SocketPool socketPool){
        this.listenerThread = new Thread(this);
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

    public void start(){
        listenerThread.start();
        logger.info("Listener thread started.");
    }
    private void listen() throws IOException, InterruptedException {
        logger.info("start listening");
        while (runnable){
            Socket socket = getSocket();
            logger.info("socket " + socket);
            addToPoolNullable(socket);
        }
        logger.info("end listening");
    }

    private Socket getSocket() {
        Socket socket = null;
        try {
             socket = serverSocket.accept();
        }catch (Exception ignored){}
        return socket;
    }

    private void addToPoolNullable(Socket socket) throws InterruptedException {
        if(nonNull(socket)){
            logger.info("get socket from " + socket.getInetAddress().toString());
            while (!socketPool.add(socket) && runnable){
                synchronized (this){
                    this.wait(1000);
                }
            }
        }
    }

    public synchronized void stop(){
        runnable = false;
    }

    public synchronized boolean isRunnable(){
        return runnable;
    }

    @Override
    public void close() throws InterruptedException, IOException {
        stop();
        listenerThread.join(JOIN_TIMEOUT);
        serverSocket.close();
    }
}
