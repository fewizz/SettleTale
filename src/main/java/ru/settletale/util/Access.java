package ru.settletale.util;

public class Access {
	volatile boolean allowed = false;
	volatile public static Thread main;

	public void getAccess() {
		if (!allowed && Thread.currentThread() != main) {
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setAccess(boolean b) {

		if (b) {
			allowed = true;
			synchronized (this) {
				notifyAll();
			}
		}
		else {
			main = Thread.currentThread();
			allowed = false;
		}
	}
}
