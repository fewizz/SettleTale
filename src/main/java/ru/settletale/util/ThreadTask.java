package ru.settletale.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class ThreadTask {
	private List<Thread> waiters;
	private List<TaskListener> listeners;
	private AtomicBoolean started = new AtomicBoolean();
	private boolean done = false;
	private Object lock = new Object();
	final Runnable runnable;

	public ThreadTask(Runnable runnable) {
		this.runnable = runnable;
	}

	public void addListener(TaskListener listener) {
		synchronized (lock) {
			if (done) {
				listener.onCompleted();
				return;
			}
			if (listeners == null) {
				listeners = new ArrayList<>();
			}
			listeners.add(listener);
		}
	}

	public void await() {
		synchronized (lock) {
			if (done) {
				return;
			}

			if (waiters == null) {
				waiters = new ArrayList<>();
			}
			waiters.add(Thread.currentThread());
		}

		LockSupport.park();
	}

	public void run() {
		if (started.getAndSet(true)) {
			throw new Error("Task is already done");
		}
		runnable.run();
		done();
	}

	protected void done() {
		synchronized (lock) {
			done = true;

			if (waiters != null)
				waiters.forEach(waiter -> {
					LockSupport.unpark(waiter);
				});
			if (listeners != null)
				listeners.forEach(listener -> {
					listener.onCompleted();
				});
		}
	}
}
