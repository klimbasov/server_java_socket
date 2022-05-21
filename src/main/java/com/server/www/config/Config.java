package com.server.www.config;

public class Config {
    public static final int SOCKET_POOL_SIZE = 10;
    public static final int HANDLER_MAX_POOL_SIZE = 10;
    public static final int HANDLER_CORE_POOL_SIZE = 10;
    public static final int HANDLER_ALIVE_TIME = 3600000;
    public static final int SOCKET_POOL_POLLING_TIMEOUT = 1000;
    public static final int SERVER_SOCKET_POLLING_TIMEOUT = 1000;
    public static final int JOIN_TIMEOUT = 10000;
}
