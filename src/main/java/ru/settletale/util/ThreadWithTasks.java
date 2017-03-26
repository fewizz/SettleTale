package ru.settletale.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public abstract class ThreadWithTasks extends Thread {
	private final Queue<ThreadTask> taskQueue = new ArrayDeque<>();
	private final Semaphore semaphore = new Semaphore(0);
	private volatile Stage stage = Stage.DO_STUFF;

	public ThreadWithTasks(Stage stage, String name) {
		this(name);
		setStage(stage);
	}

	public ThreadWithTasks(String name) {
		super(name);
	}

	@Override
	public final void run() {
		init();

		for (;;) {
			if (interrupted()) {
				break;
			}
			stage.doStuff(this);
		}
	}

	public void init() {
	}

	public abstract void doStuff();

	private void waitAndDoAvailableTask() throws InterruptedException {
		semaphore.acquire();
		taskQueue.poll().run();
	}

	public void doAvailableTasks() {
		while (semaphore.tryAcquire()) {
			taskQueue.poll().run();
		}
	}

	public synchronized ThreadTask addRunnableTask(Runnable runnable) {
		if (runnable == null) {
			throw new Error("Runnable is null");
		}
		ThreadTask task = new ThreadTask(runnable);
		taskQueue.add(new ThreadTask(runnable));
		semaphore.release();

		return task;
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		if (getState() == Thread.State.WAITING) {
			interrupt();
		}
	}

	public enum Stage {
		ONLY_DO_TASKS {
			@Override
			public void doStuff(ThreadWithTasks thread) {
				try {
					thread.waitAndDoAvailableTask();
				} catch (InterruptedException e) {
					interrupted();
				}
			}
		},
		DO_STUFF {
			@Override
			public void doStuff(ThreadWithTasks thread) {
				thread.doStuff();
			}
		},
		STOP {
			@Override
			public void doStuff(ThreadWithTasks thread) {
				thread.interrupt();
			}
		};

		public void doStuff(ThreadWithTasks thread) {
		}
	}
}
