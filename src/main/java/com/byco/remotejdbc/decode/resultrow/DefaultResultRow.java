package com.byco.remotejdbc.decode.resultrow;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Classname DefaultResultRow
 * @Description TODO
 * @Date 2022/7/12 10:12
 * @Created by byco
 */
public class DefaultResultRow implements ResultRow {


    private final ArrayBlockingQueue<Object[]> queue;

    public DefaultResultRow(int capacity){
        queue = new ArrayBlockingQueue<Object[]>(capacity);
    }

    @Override
    public boolean hasNext() {
        return !isEmpty();
    }

    @Override
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    @Override
    public Object[] get(){
        return queue.poll();
    }

    @Override
    public void put(Object[] o) throws InterruptedException {
        queue.put(o);
    }

    @Override
    public void putAll(Object[][] objects) throws InterruptedException {
        for( Object[] o : objects ){
            queue.put(o);
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

}
