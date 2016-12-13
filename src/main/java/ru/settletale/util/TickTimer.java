package ru.settletale.util;

public class TickTimer {
	long waitTimeNano;
	long startTimeNano;
	public long lastTime;

	public TickTimer(int countOfTicksPerSecond) {
		this.waitTimeNano = (long) ((1D / (double) countOfTicksPerSecond) * 1_000_000_000D);
	}
	
	public void start() {
		startTimeNano = System.nanoTime();
	}
	
	public void waitTimer() {
		long end = System.nanoTime();
		lastTime = end - startTimeNano;
		long timeToSleep = waitTimeNano - lastTime;
		
		if(timeToSleep < 0) {
			return;
		}
		try {
			Thread.sleep(timeToSleep / 1_000_000L, (int)(timeToSleep % 1_000_000L));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
