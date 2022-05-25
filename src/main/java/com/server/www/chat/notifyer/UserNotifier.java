package com.server.www.chat.notifyer;

import com.server.www.chat.pool.impl.MessagePool;
import com.server.www.chat.pool.impl.UserPool;
import com.server.www.chat.user.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public class UserNotifier implements Runnable {
    private static final int MESSAGE_WAITING_TIMEOUT = 300;
    private final Logger logger;
    private final UserPool userPool;
    private final MessagePool messagePool;
    private boolean runnable;

    public UserNotifier(final MessagePool messagePool, final UserPool userPool){
        this.userPool = userPool;
        this.messagePool = messagePool;
        this.logger = Logger.getLogger(UserNotifier.class.getName());
    }

    @Override
    public void run() {
        runnable = true;
        while (runnable){
            try {
                waitForMessage();
                String message = messagePool.poll();
                notifyUsers(message);
            } catch (InterruptedException | IOException e) {
                logger.warning("Exception while running: " + e);
                runnable = false;
            }
        }
    }

    public void stop(){
        runnable = false;
    }

    private void notifyUsers(String message) throws IOException {
        List<User> users = userPool.getUsersSnap();
        for(User user : users){
            Socket socket = user.getSocket();
            OutputStream out = socket.getOutputStream();
            out.write(message.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void waitForMessage() throws InterruptedException {
        synchronized (this){
            while (messagePool.isEmpty()){
                this.wait(MESSAGE_WAITING_TIMEOUT);
            }
        }
    }
}
