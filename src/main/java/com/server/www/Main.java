package com.server.www;

import com.server.www.Impl.ServerImpl;

public class Main {
    public static void main(String argvs[])
    {
        // creating an object of the class ServerSide
        try(Server server = new ServerImpl(3306)){
            server.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
