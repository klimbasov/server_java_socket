package com.server.www.chat.pool;

public interface Pool<T> {
    boolean isEmpty();
    void add(T obj);
}
