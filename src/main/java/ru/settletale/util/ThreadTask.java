package ru.settletale.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ThreadTask {
	private Queue<ITaskListener> listeners;
	private boolean done = false;
	final Runnable runnable;

	public ThreadTask(Runnable runnable) {
		this.runnable = runnable;
	}

	public synchronized void whenExecuted(ITaskListener listener) {
		if (done) {
			listener.onCompleted();
			return;
		}
		if (listeners == null) {
			listeners = new ConcurrentLinkedDeque<>();
		}
		listeners.add(listener);

	}

	public synchronized void await() {
		if (done)
			return;
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		runnable.run();
		done();
	}

	protected synchronized void done() {
		done = true;

		this.notifyAll(); // Wake waiters

		if (listeners != null) {
			listeners.forEach(listener -> listener.onCompleted());
		}
	}
}
