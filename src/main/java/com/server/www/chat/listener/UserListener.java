package com.server.www.chat.listener;

import com.server.www.chat.pool.impl.MessagePool;
import com.server.www.chat.pool.impl.UserPool;
import com.server.www.chat.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

public class UserListener implements Runnable{
    private final Logger logger;
    private final UserPool userPool;
    private final MessagePool messagePool;
    private boolean runnable;

    public UserListener(final MessagePool messagePool, final UserPool userPool){
        this.logger = Logger.getLogger(UserListener.class.getName());
        this.userPool = userPool;
        this.messagePool = messagePool;
    }

    @Override
    public void run() {
        runnable = true;
        while (runnable){
            List<User> users = userPool.getUsersSnap();
            ListIterator<User> userListIterator = users.listIterator();
            try{
                while (userListIterator.hasNext()){
                    User user = userListIterator.next();
                    Socket socket = user.getSocket();
                    InputStream in = socket.getInputStream();
                    String message = in.readAllBytes().toString();
                    if(!message.isEmpty()){
                        messagePool.add(message);
                    }
                }
            } catch (IOException exception) {
                logger.warning("Exception while handling socket.");
                runnable = false;
            }
        }
    }

    public MessagePool getMessagePool() {
        return messagePool;
    }

    public UserPool getUserPool() {
        return userPool;
    }

    public void stop(){
        runnable = false;
    }
}
