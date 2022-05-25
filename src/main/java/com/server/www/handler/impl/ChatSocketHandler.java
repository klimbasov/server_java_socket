package com.server.www.handler.impl;

import com.server.www.chat.rooms.Room;
import com.server.www.chat.rooms.impl.PersonalRoom;
import com.server.www.handler.SocketHandler;
import com.server.www.pool.SocketPool;
import com.server.www.response.builder.ResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

public class ChatSocketHandler implements SocketHandler{
    private final SocketPool socketPool;
    private final Room room;
    private final Thread roomThread;
    private final Logger logger;

    ChatSocketHandler(){
        this.socketPool = new SocketPool();
        this.room = new PersonalRoom();
        this.logger = Logger.getLogger(this.getClass().getName()+ this);
        this.roomThread = new Thread(room);
    }


    @Override
    public void run() {
        //todo maybe shall not use Runnable? refactor SocketHandler.class
    }

    public void addSocket(Socket socket){
        //todo impl
    }
    private void handle() throws IOException {
        roomThread.start();
    }


}
