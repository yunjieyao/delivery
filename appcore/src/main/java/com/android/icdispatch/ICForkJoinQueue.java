package com.android.icdispatch;

import java.util.concurrent.BlockingQueue;

public class ICForkJoinQueue extends ICConCurrentQueue {

    public ICForkJoinQueue(BlockingQueue<ICBlock> queue, int maxThreads) {
        super(queue, maxThreads);
    }


}
