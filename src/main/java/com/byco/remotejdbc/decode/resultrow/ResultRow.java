package com.byco.remotejdbc.decode.resultrow;

/**
 * @Classname ResultRow
 * @Description TODO
 * @Date 2022/7/12 10:57
 * @Created by byco
 */
public interface ResultRow {
    boolean hasNext();

    boolean isEmpty();

    Object[] get();

    void put(Object[] o) throws InterruptedException;

    void putAll(Object[][] objects) throws InterruptedException;

    int size();
}
