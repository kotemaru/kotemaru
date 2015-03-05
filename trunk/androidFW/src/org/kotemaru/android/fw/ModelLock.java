package org.kotemaru.android.fw;

import java.util.HashMap;
import java.util.Map;

public class ModelLock {
	private static final long DEFAULT_TIMEOUT = 5000; // 5sec

	private Map<Thread,Long> mLockedThreads;
	private boolean mIsDebug = false;
	private int mReadLock = 0;
	private int mWriteLock = 0;
	private Thread mWriteLockThread = null;

	public ModelLock(boolean isDebug) {
		mIsDebug = isDebug;
		if (isDebug) {
			mLockedThreads = new HashMap<Thread,Long>();
		}
	}
	public ModelLock() {
		this(false);
	}
	public void readLock(long timeout){
		if (!tryReadLock(timeout)) {
			throw new LockException("Read lock timeout.\n"+getLockedThreadInfo());
		}
	}
	public boolean tryReadLock(){
		return tryReadLock(0);
	}
	public synchronized boolean tryReadLock(long timeout){
		if (mIsDebug) {
			mLockedThreads.put(Thread.currentThread(), System.currentTimeMillis());
		}
		long timeoutMs = System.currentTimeMillis() + timeout;
		while (mWriteLockThread != null) {
			if (System.currentTimeMillis() > timeoutMs) {
				return false;
			}
			try {
				wait(timeoutMs-System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mReadLock++;
		return true;
	}
	public synchronized void readUnlock(){
		if (mReadLock == 0) {
			throw new LockException("Read unlock under flow.");
		}
		mReadLock--;
		notifyAll();
		if (mIsDebug) {
			mLockedThreads.remove(Thread.currentThread());
		}
	}
	public synchronized void writeLock(long timeout) {
		if (mIsDebug) {
			mLockedThreads.put(Thread.currentThread(), System.currentTimeMillis());
		}
		Thread curThread = Thread.currentThread();
		long timeoutMs = System.currentTimeMillis() + timeout;
		while ((mWriteLockThread != null && mWriteLockThread != curThread) || mReadLock != 0) {
			if (System.currentTimeMillis() > timeoutMs) {
				throw new LockException("Write lock timeout.\n"+getLockedThreadInfo());
			}
			try {
				wait(timeoutMs-System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mWriteLockThread = curThread;
		mWriteLock++;
	}
	public synchronized void writeUnlock() {
		if (mWriteLockThread == null) {
			throw new LockException("Write unlock under flow.");
		}
		if (mWriteLockThread != Thread.currentThread()) {
			throw new LockException("Write unlock: has not write lock.");
		}
		mWriteLock--;
		if (mWriteLock == 0) {
			mWriteLockThread = null;
			notifyAll();
			if (mIsDebug) {
				mLockedThreads.remove(Thread.currentThread());
			}
		}
	}
	public void readLock(){
		readLock(DEFAULT_TIMEOUT);
	}
	public void writeLock() {
		writeLock(DEFAULT_TIMEOUT);
	}

	private StringBuilder getLockedThreadInfo() {
		StringBuilder sbuf = new StringBuilder("\n----\n");
		long curTime = System.currentTimeMillis();
		for (Thread th : mLockedThreads.keySet()) {
			Long time = mLockedThreads.get(th);
			sbuf.append("thread=").append(th.getName())
				.append(": lock having or waiting time=").append(curTime-time).append("ms\n");
			StackTraceElement[] trace = th.getStackTrace();
			for (int i=0; i<6; i++) {
				sbuf.append("  ").append(trace[i].toString()).append("\n");
			}
		}
		sbuf.append("\n----\n");
		return sbuf;
	}

	public static class LockException extends RuntimeException {
		public LockException(String detailMessage) {
			super(detailMessage);
		}
	}
}
