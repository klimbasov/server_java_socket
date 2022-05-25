package com.server.www.Impl;

import com.server.www.Server;
import com.server.www.exception.ServerException;
import com.server.www.handler.impl.DefaultSocketHandler;
import com.server.www.handler.HandlerPoolExecutor;
import com.server.www.listener.ServerSocketListener;
import com.server.www.pool.SocketPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import static com.server.www.config.Config.*;

public class ServerImpl implements Server
{
    private final Logger logger;
    private final SocketPool socketPool;
    private ServerSocketListener listener;
    private final HandlerPoolExecutor handleExecutor;
    private boolean runnable;

    public ServerImpl(int port) {
        logger = Logger.getLogger(this.getClass().getName());
        socketPool = new SocketPool();
        handleExecutor = new HandlerPoolExecutor();
        runnable = true;
        try
        {
            listener = new ServerSocketListener(getServerSocket(port), socketPool);
        }catch(IOException exception)
        {
            logger.warning("ServerImpl instance initialisation failed.");
        }
    }

    @Override
    public void run() throws ServerException {
        try {
            listener.start();
            activity();
        } catch (InterruptedException | IOException e) {
            logger.warning(e.toString());
            throw new ServerException(e);
        }
    }

    @Override
    public void stop(){
        runnable = false;
    }

    @Override
    public void close() throws Exception {
        handleExecutor.close();
        listener.close();
        socketPool.close();
    }

    private ServerSocket getServerSocket(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(SERVER_SOCKET_POLLING_TIMEOUT);
        logger.info("ServerSocket initialized");
        return serverSocket;
    }

    private void activity() throws InterruptedException, IOException {
        while (runnable){
            waitRequest();
            Socket socket = socketPool.poll();
            handleExecutor.execute(new DefaultSocketHandler(socket));   //todo unification socket handling
        }
    }

    private void waitRequest() throws InterruptedException {
        while (socketPool.isEmpty() && runnable) synchronized (this) {
            this.wait(SOCKET_POOL_POLLING_TIMEOUT);
        }
    }
}