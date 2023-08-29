package com.mao.core.conn.client;

import com.mao.core.exception.P698TimeOutException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 请求数据的 future，用于同步获取请求的结果
 *
 * @author mao
 * @date 2023/8/24 16:08
 */
public class DataFuture<T> implements Future<T> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private T response;
    private final long beginTime = System.currentTimeMillis();

    public DataFuture() {
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public T get() throws InterruptedException {
        latch.await();
        return this.response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch.await(timeout, unit)) {
            return this.response;
        }
        throw new P698TimeOutException("请求超时");
    }

    public void setResponse(T response) {
        this.response = response;
        latch.countDown();
    }

    public long getBeginTime() {
        return beginTime;
    }
}
