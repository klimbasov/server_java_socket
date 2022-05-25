package com.server.www.handler;

import com.server.www.handler.impl.DefaultSocketHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.server.www.config.Config.*;
import static java.util.Objects.isNull;

public class HandlerPoolExecutor implements AutoCloseable{
    private final ThreadPoolExecutor handleExecutor;
    private final Logger logger = Logger.getLogger(HandlerPoolExecutor.class.getName());
    public HandlerPoolExecutor(){
        handleExecutor = new ThreadPoolExecutor(HANDLER_CORE_POOL_SIZE, HANDLER_MAX_POOL_SIZE, HANDLER_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        handleExecutor.setRejectedExecutionHandler((runnable,executor)-> logger.warning("handlers are full. Connection rejected"));
        logger.info("HandleExecutor initialize.");
    }

    public void execute(DefaultSocketHandler handler){
        if(isNull(handler)){
            throw new IllegalArgumentException("Null handler passed");
        }
        handleExecutor.execute(handler);
    }

    @Override
    public void close() throws Exception {
        if(handleExecutor.awaitTermination(JOIN_TIMEOUT, TimeUnit.MILLISECONDS)){
            logger.warning("Some threads were not terminated.");
        }
        handleExecutor.shutdown();
    }
}
