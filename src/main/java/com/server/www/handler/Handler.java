package com.server.www.handler;

import java.io.IOException;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class Handler implements Runnable{
    private final Socket socket;

    public Handler(final Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void handle() throws IOException {
        try(Writer responseOutput = new PrintWriter(socket.getOutputStream())){
            responseOutput.write("handled " + Thread.currentThread().getName()+ "\n");
        }
        socket.close();
    }
}
