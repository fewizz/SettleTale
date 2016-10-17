package ru.settletale.util;

public class TickTimer {
	long waitTimeNano;
	long startTimeNano;

	public TickTimer(int countOfTicksPerSecond) {
		this.waitTimeNano = (long) ((1D / (double) countOfTicksPerSecond) * 1_000_000_000D);
	}
	
	public void start() {
		startTimeNano = System.nanoTime();
	}
	
	public void waitTimer() {
		long end = System.nanoTime();
		long timeToSleep = waitTimeNano - (end - startTimeNano);
		
		if(timeToSleep < 0) {
			return;
		}
		try {
			Thread.sleep(timeToSleep / 1_000_000L, (int)((double)timeToSleep % 1_000_000D));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
