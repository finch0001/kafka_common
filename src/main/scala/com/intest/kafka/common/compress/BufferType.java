package com.intest.kafka.common.compress;

import java.nio.ByteBuffer;

public enum BufferType
{
    /**
     *  堆内
     * */
    ON_HEAP
            {
                @Override
                public ByteBuffer allocate(int size)
                {
                    return ByteBuffer.allocate(size);
                }
            },

    /**
     * 堆外
     * */
    OFF_HEAP
            {
                @Override
                public ByteBuffer allocate(int size)
                {
                    return ByteBuffer.allocateDirect(size);
                }
            };

    public abstract ByteBuffer allocate(int size);

    public static BufferType typeOf(ByteBuffer buffer)
    {
        return buffer.isDirect() ? OFF_HEAP : ON_HEAP;
    }
}