package com.server.www.chat.user;

import java.net.Socket;

public class User {
    private final Socket socket;
    private final String username;

    public User(final Socket socket, String username){
        this.socket = socket;
        this.username= username;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }
}
