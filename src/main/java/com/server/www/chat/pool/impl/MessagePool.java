package com.server.www.chat.pool.impl;

import com.server.www.chat.pool.Pool;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class
MessagePool implements Pool<String> {
    private static final int MAX_POOL_SIZE = 10;
    private final ArrayBlockingQueue<String> messages;

    public MessagePool(){
        this.messages = new ArrayBlockingQueue<>(10);
    }

    public synchronized String poll(){
        return messages.poll();
    }

    @Override
    public boolean isEmpty() {
        return messages.isEmpty();
    }

    @Override
    public void add(String obj) {
        synchronized (this){
            messages.offer(obj);
        }
    }
}
