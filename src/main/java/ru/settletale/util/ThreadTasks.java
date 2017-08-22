package ru.settletale.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ThreadTasks {
	private final ConcurrentLinkedDeque<ThreadTask> tasks = new ConcurrentLinkedDeque<>();
	public final Semaphore semaphore = new Semaphore(0);
	
	public ThreadTask add(Runnable run) {
		Objects.requireNonNull(run);
		ThreadTask task = new ThreadTask(run);
		tasks.add(task);
		semaphore.release();
		return task;
	}
	
	public ThreadTask addAhead(Runnable run) {
		Objects.requireNonNull(run);
		ThreadTask task = new ThreadTask(run);
		tasks.addFirst(task);
		semaphore.release();
		return task;
	}
	
	public void waitAndExecute(long maxWaitTimeNano) throws InterruptedException {
		if (semaphore.tryAcquire(maxWaitTimeNano, TimeUnit.NANOSECONDS))
			tasks.poll().run();
	}

	public void waitAndExecute() throws InterruptedException {
		semaphore.acquire();
		tasks.poll().run();
	}
	
	public void executeAvailable() {
		for(;;) {
			if(semaphore.tryAcquire())
				tasks.poll().run();
			else
				break;
		}
	}
	
	public ThreadTask last() {
		return tasks.getLast();
	}
	
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
}
