package com.server.www.chat.rooms;

import com.server.www.chat.user.User;

import java.net.Socket;

public interface Room extends AutoCloseable, Runnable{
    void addUser(User user);
}
