package com.server.www;

import com.server.www.exception.ServerException;

public interface Server extends AutoCloseable{
    void run() throws ServerException;
    void stop();
}
