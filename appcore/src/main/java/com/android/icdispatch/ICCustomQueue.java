package com.android.icdispatch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ICCustomQueue extends ICQueue {

	private ExecutorService mExecutor;

	public ICCustomQueue(BlockingQueue<ICBlock> queue, int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		super(queue);
		this.mExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public void run() {
		ICBlock currentBlock = null;

		while (running) {
			try {
				currentBlock = mQueue.take();
				mExecutor.execute(currentBlock);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
