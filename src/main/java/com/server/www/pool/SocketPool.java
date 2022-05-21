package com.server.www.pool;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static com.server.www.config.Config.SOCKET_POOL_SIZE;
import static java.util.Objects.isNull;

public class SocketPool implements AutoCloseable{
    private final Queue<Socket> socketPool;

    public SocketPool(){
        socketPool = new ArrayBlockingQueue<>(SOCKET_POOL_SIZE);
    }

    public Socket poll(){
        Socket socket = null;
        if(!socketPool.isEmpty()){
            socket = socketPool.poll();
        }
        return socket;
    }

    public boolean add(Socket socket){
        if(isNull(socket)){
            throw new IllegalArgumentException("Null socket passed.");
        }
        return !socketPool.offer(socket);
    }

    public boolean isEmpty(){
        return socketPool.isEmpty();
    }

    private static void accept(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() {
        socketPool.forEach(SocketPool::accept);
    }
}
