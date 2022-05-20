package com.server.www.Impl;// import statements

import com.server.www.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ServerImpl implements Server, Runnable
{
    private static final int SOCKET_POOL_SIZE = 10;
    private static final int POLLING_TIMEOUT = 1000;
    private final Logger logger;
    private final Queue<Socket> socketPool;
    private Listener listener;
    private Thread listenerThread;
    private boolean runnable;

    public ServerImpl(int port)
    {
        logger = Logger.getLogger(this.getClass().getName());
        socketPool = new ArrayBlockingQueue(SOCKET_POOL_SIZE);
        runnable = true;
        try
        {
            ServerSocket serverSocket = getServerSocket(port);
            listener = new Listener(serverSocket, socketPool);
            startListenerThread();
        }
        // handling errors
        catch(IOException i)
        {
            logger.warning("ServerImpl instance initialisation fails.");
        }
    }

    private void startListenerThread() {
        listenerThread = new Thread(listener);
        listenerThread.start();
    }

    private ServerSocket getServerSocket(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        logger.info("ServerSocket initialized");
        return serverSocket;
    }

    private void activity() throws InterruptedException, IOException {
        while (runnable){
            waitRequest();
            Socket socket = socketPool.peek();
            Writer responseWriter = new PrintWriter(socket.getOutputStream());
            Reader requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }

    private void waitRequest() throws InterruptedException {
        while (socketPool.isEmpty() && runnable){
            this.wait(POLLING_TIMEOUT);
        }
    }

    @Override
    public void run() {
        activity();
    }


    @Override
    public void close() throws Exception {

    }
}

class Listener implements Runnable, AutoCloseable{
    private final Logger logger;
    private final Queue<Socket> socketPool;
    private final ServerSocket serverSocket;
    private boolean runnable;

    public Listener(final ServerSocket serverSocket, Queue<Socket> socketPool){
        this.logger = Logger.getLogger(this.getClass().getName());
        this.serverSocket = serverSocket;
        this.socketPool = socketPool;
        this.runnable = false;
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
        while (runnable){
            Socket socket = serverSocket.accept();
            if(nonNull(socket)){
                logger.info("get socket from " + socket.getInetAddress().toString());
                while (!socketPool.offer(socket) && runnable){
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
    public void close() throws Exception {

    }
}