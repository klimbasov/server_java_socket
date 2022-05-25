package com.server.www.chat.pool.impl;

import com.server.www.chat.pool.Pool;
import com.server.www.chat.user.User;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.Objects.nonNull;

public class UserPool implements Pool<User>, AutoCloseable{
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 10;
    private final Queue<User> users;

    public UserPool(){
        this.users = new ArrayBlockingQueue<>(MAX_SIZE);
    }

    public UserPool(int size){
        if(size < MIN_SIZE){
            throw new IllegalArgumentException("pool size can not be less then " + MIN_SIZE);
        }
        this.users = new ArrayBlockingQueue<>(size);
    }

    public List<User> getUsersSnap(){
        List<User> usersSnapshot;
        synchronized (this){
            usersSnapshot = List.of(users.toArray(new User[0]));
        }
        return usersSnapshot;
    }

    @Override
    public void close() throws Exception {
        synchronized (this){
            for(User user : users){
                user.getSocket().close();
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public void add(User obj) {
        synchronized (this){
            if(nonNull(obj)){
                users.offer(obj);
            }
        }
    }
}
