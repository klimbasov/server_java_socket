package com.server.www.exception;

public class ServerException extends Exception{
    public ServerException(String message){
        super(message);
    }
    public ServerException(Exception nested){
        super(nested);
    }
    public ServerException(Exception nested, String message){
        super(message, nested);
    }
    public ServerException(){
    }
}
