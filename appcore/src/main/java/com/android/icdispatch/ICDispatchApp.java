package com.android.icdispatch;

/**
 * Copyright 2013 Johan Risch (johan.risch@gmail.com) and Simon Evertsson (simon.evertsson2@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Application;
import android.os.Looper;

import com.app.activity.WeakHandler;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by johanrisch on 6/21/13.
 */
public class ICDispatchApp extends Application {

	/**
	 * The normal priority thread.
	 */
	private static ICQueue mNormalThread;
	/**
	 * The low priority thread
	 */
	private static ICQueue mLowThread;
	/**
	 * the high priority thread.
	 */
	private static ICQueue mHighThread;

	private static ICDispatachMainQueue mMainQueue;

	private static ICConCurrentQueue mConcurrentThread;
	/**
	 * Handler used to execute on UI thread.
	 */
	private WeakHandler mMainHandler;
	/**
	 * This parameter sets the maximum messages to be queued per UI thread run
	 * loop. Default is 10.
	 */
	private int mMaxMessagesOnUILoop = 10;

	/**
	 * This parameter sets the maximum number of threads in the concurrent
	 * queue. The default (0) is number of cores.
	 */
	private int mMaxThreadsInConcurrent = 0;

	private boolean isInitialized = false;

	@Override
	public void onCreate() {
		super.onCreate();
		initICDispatch(); // 初始化 ICDispatch组件配置
		mConcurrentThread = new ICConCurrentQueue(new LinkedBlockingQueue<ICBlock>(), mMaxThreadsInConcurrent);
		mConcurrentThread.setPriority(Thread.NORM_PRIORITY);
		mConcurrentThread.start();

		mHighThread = new ICQueue(new LinkedBlockingQueue<ICBlock>());
		mHighThread.setPriority(Thread.MAX_PRIORITY);
		mHighThread.start();

		mMainHandler = new WeakHandler();
		mMainQueue = new ICDispatachMainQueue(new LinkedBlockingQueue<ICBlock>(), mMainHandler, mMaxMessagesOnUILoop);
		mMainQueue.setPriority(Thread.MAX_PRIORITY);
		mMainQueue.start();

		mNormalThread = new ICQueue(new LinkedBlockingQueue<ICBlock>());
		mNormalThread.setPriority(Thread.NORM_PRIORITY);
		mNormalThread.start();

		mLowThread = new ICQueue(new LinkedBlockingQueue<ICBlock>());
		mLowThread.setPriority(Thread.MIN_PRIORITY);
		mLowThread.start();
		isInitialized = true;
	}

	public static void executeOn(ICQueue queue, ICBlock block) {
		if (null != queue)
			queue.putBlock(block);
	}

	public static void executeAllOn(ICQueue queue, Collection<ICBlock> blocks) {
		if (null != queue)
			queue.putAll(blocks);
	}

	public int getMaxMessagesOnUILoop() {
		return mMaxMessagesOnUILoop;
	}

	/**
	 * Sets the maximum number of messages per UI run loop. If more than max
	 * {@link ICBlock} is dispatched to the main thread in one run loop the
	 * blocks will wait on a separate thread until next run loop.
	 * 
	 * @param max
	 *            the maximum number of {@link ICBlock} in queue on the main
	 *            thread. Default = 10
	 * @throws RuntimeException
	 *             if this method is called from the main thread OR before
	 *             ICDispatch.initICDispatch has been called.
	 */
	public void setMaxMessagesOnUILoop(int max) throws RuntimeException {
		checkForThreadException("setMaxMessagesOnUILoop");
		checkForInitException("setMaxMessagesOnUILoop");
		mMaxMessagesOnUILoop = max;
	}

	public int getMaxThreadsInConcurrent() {
		return mMaxThreadsInConcurrent;
	}

	/**
	 * Sets the maximum number of threads in the concurrent queue.
	 * 
	 * @param max
	 *            number of threads in the concurrent queue. Default is the
	 *            number of cores on the current device.
	 * @throws RuntimeException
	 *             if this method is called from the main thread OR before
	 *             ICDispatch.initICDispatch has been called.
	 */
	public void setMaxThreadsInConcurrent(int max) throws RuntimeException {
		checkForThreadException("setMaxThreadsInConcurrent");
		checkForInitException("setMaxThreadsInConcurrent");
		mMaxThreadsInConcurrent = max;
	}

	private void checkForInitException(String method) throws RuntimeException {
		if (isInitialized) {
			throw new RuntimeException("Method " + method + " called after initialisation");
		}
	}

	private void checkForThreadException(String method) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Method setMaxMethodsHashed not called from UI thread.");
		}
	}

	public static ICQueue getNormalThread() {
		return mNormalThread;
	}

	public static ICQueue getLowThread() {
		return mLowThread;
	}

	public static ICQueue getHighThread() {
		return mHighThread;
	}

	public static ICDispatachMainQueue getMainQueue() {
		return mMainQueue;
	}

	public static ICConCurrentQueue getConcurrentThread() {
		return mConcurrentThread;
	}

	/**
	 * Override this method if you want to initialize {@link ICDispatch} with
	 * custom params.
	 * 
	 */
	protected void initICDispatch() {

	}

}
