package ru.settletale.util;

public class ThreadWithTasks extends Thread {
	public final ThreadTasks tasks = new ThreadTasks();
	public static final ThreadBehavior DEFAULT_THREAD_BEHAVIOR = thread -> {
		for (;;) {
			try {
				thread.tasks.waitAndExecute();
			} catch (InterruptedException e) {
				interrupted();
				break;
			}
		}
	};
	
	private volatile ThreadBehavior behavior = DEFAULT_THREAD_BEHAVIOR;

	public ThreadWithTasks(String name) {
		super(name);
	}

	@Override
	public void run() {
		while(behavior != null && !isInterrupted()) {
			ThreadBehavior tb = behavior;
			behavior = null;
			tb.run(this);
		}
	}
	
	public void setBehavior(ThreadBehavior behavior) {
		this.behavior = behavior;
		interrupt();
	}
	
	public ThreadTask addTask(Runnable run) {
		return tasks.add(run);
	}
	
	public ThreadTask addAheadTask(Runnable run) {
		return tasks.addAhead(run);
	}

	public void waitAndExecuteTask(long maxWaitTimeNano) throws InterruptedException {
		tasks.waitAndExecute(maxWaitTimeNano);
	}

	public void waitAndExecuteTask() throws InterruptedException {
		tasks.waitAndExecute();
	}
	
	public void executeAvailableTasks() {
		tasks.executeAvailable();
	}

	public ThreadTask lastTask() {
		return tasks.last();
	}
	
	public boolean isHaveTasks() {
		return !tasks.isEmpty();
	}
	
	@FunctionalInterface
	public static interface ThreadBehavior {
		public void run(ThreadWithTasks thread);
	}
}
