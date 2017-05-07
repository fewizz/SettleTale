package ru.settletale.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class ThreadTask {
	private List<Thread> waiters;
	private List<TaskListener> listeners;
	private boolean done = false;
	final Runnable runnable;

	public ThreadTask(Runnable runnable) {
		this.runnable = runnable;
	}

	public synchronized void addListener(TaskListener listener) {
		if (done) {
			listener.onCompleted();
			return;
		}
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}

	public synchronized void await() {
		if (done) {
			return;
		}

		if (waiters == null) {
			waiters = new ArrayList<>();
		}
		waiters.add(Thread.currentThread());
		LockSupport.park();
	}

	public void run() {
		runnable.run();
		done();
	}

	protected synchronized void done() {
		done = true;

		if (waiters != null)
			waiters.forEach(waiter -> LockSupport.unpark(waiter));
		if (listeners != null)
			listeners.forEach(listener -> listener.onCompleted());
	}
}
