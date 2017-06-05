package ru.settletale.util;

import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

public class ThreadWithTasks extends Thread {
	private final Deque<ThreadTask> taskQueue = new ConcurrentLinkedDeque<ThreadTask>();
	private final Semaphore semaphore = new Semaphore(0);

	public ThreadWithTasks(String name) {
		super(name);
	}

	@Override
	public final void run() {
		for (;;) {
			if (interrupted()) {
				break;
			}
			try {
				waitAndDoAvailableTask();
			} catch (InterruptedException e) {
			}
		}
	}

	private void waitAndDoAvailableTask() throws InterruptedException {
		semaphore.acquire();
		taskQueue.poll().run();
	}

	public void doAvailableTasks() {
		while (semaphore.tryAcquire()) {
			taskQueue.poll().run();
		}
	}

	public ThreadTask addRunnableTask(Runnable runnable) {
		Objects.requireNonNull(runnable, "Runnable is null");
		ThreadTask task = new ThreadTask(runnable);
		taskQueue.add(task);
		semaphore.release();
		return task;
	}
}
