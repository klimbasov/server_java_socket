package com.server.www.chat.rooms.impl;

import com.server.www.chat.listener.UserListener;
import com.server.www.chat.notifyer.UserNotifier;
import com.server.www.chat.pool.impl.MessagePool;
import com.server.www.chat.pool.impl.UserPool;
import com.server.www.chat.rooms.Room;
import com.server.www.chat.user.User;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;

public class PersonalRoom implements Room {
    private int id;
    private static final int JOIN_TIMEOUT = 5000;
    private static final int USER_MAX_SIZE = 2;
    private final UserPool usersPool;
    private final UserListener listener;
    private final UserNotifier notifier;
    private final Thread listenerThread;
    private final Thread notifierThread;



    public PersonalRoom(int id){
        this();
        this.id = id;
    }

    public PersonalRoom(){
        MessagePool messagePool = new MessagePool();
        usersPool = new UserPool(USER_MAX_SIZE);
        listener = new UserListener(messagePool, usersPool);
        notifier = new UserNotifier(messagePool, usersPool);
        listenerThread = new Thread(listener);
        notifierThread = new Thread(notifier);
    }

    @Override
    public void addUser(User user) {
        if(nonNull(user)){
            usersPool.add(user);
        }
    }

    @Override
    public void close() throws Exception {
        listener.stop();
        notifier.stop();
        listenerThread.join(JOIN_TIMEOUT);
        notifierThread.join(JOIN_TIMEOUT);
        usersPool.close();
    }

    @Override
    public void run() {
        listenerThread.start();
        notifierThread.start();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
