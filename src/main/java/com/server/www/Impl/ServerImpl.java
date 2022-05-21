package com.server.www.Impl;

import com.server.www.Server;
import com.server.www.exception.ServerException;
import com.server.www.handler.Handler;
import com.server.www.listener.Listener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static com.server.www.config.Config.*;

public class ServerImpl implements Server
{
    private final Logger logger;
    private final Queue<Socket> socketPool;
    private Listener listener;
    private Thread listenerThread;
    private ThreadPoolExecutor handleExecutor;
    private boolean runnable;
    public ServerImpl(int port)
    {
        logger = Logger.getLogger(this.getClass().getName());
        socketPool = new ArrayBlockingQueue<>(SOCKET_POOL_SIZE);
        initHandleExecute();
        runnable = true;
        try
        {
            initListener(port);
            startListening();
        }catch(IOException i)
        {
            logger.warning("ServerImpl instance initialisation failed.");
        }
    }

    private static void accept(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private void initListener(int port) throws IOException {
        ServerSocket serverSocket = getServerSocket(port);
        listener = new Listener(serverSocket, socketPool);
    }

    private void initHandleExecute() {
        handleExecutor = new ThreadPoolExecutor(HANDLER_CORE_POOL_SIZE, HANDLER_MAX_POOL_SIZE, HANDLER_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        handleExecutor.setRejectedExecutionHandler((runnable,executor)-> logger.warning("handlers are full. Connection rejected"));
        logger.info("HandleExecutor initialize.");
    }

    private void startListening() {
        listenerThread = new Thread(listener);
        listenerThread.start();
        logger.info("Listener thread started.");
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
            Socket socket = socketPool.poll();
            handleExecutor.execute(new Handler(socket));
        }
    }

    private void waitRequest() throws InterruptedException {
        while (socketPool.isEmpty() && runnable){
            synchronized (this){
                this.wait(POLLING_TIMEOUT);
            }
        }
    }

    public void run() throws ServerException {
        try {
            activity();
        } catch (InterruptedException | IOException e) {
            throw new ServerException(e);
        }
    }


    @Override
    public void close() throws Exception {
        handleExecutor.shutdown();
        listener.stop();
        listenerThread.join(JOIN_TIMEOUT);
        if(!listener.isRunnable()){
            listener.close();
        }
        socketPool.forEach(ServerImpl::accept);

    }
}