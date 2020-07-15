package com.intest.kafka.common.error;

import java.util.concurrent.ExecutionException;

/**
 * @author yusheng
 * @datetime 2020/7/13 9:21
 */
public class ZooKeeperClientException extends RuntimeException{
    public ZooKeeperClientException(ExecutionException e) {
        super(e);
    }
}
