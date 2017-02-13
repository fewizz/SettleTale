package ru.settletale.util;

import java.util.concurrent.TimeUnit;

public class TickTimer {
	long waitTimeNano;
	long startTimeNano;
	public long lastTimeNano;

	public TickTimer(double countOfTicksPerSecond) {
		this.waitTimeNano = (long) ((1D / countOfTicksPerSecond) * 1_000_000_000D);
	}
	
	public TickTimer(int countOfTicksPerSecond) {
		this((float)countOfTicksPerSecond);
	}
	
	public void start() {
		startTimeNano = System.nanoTime();
	}
	
	public void waitTimer() {
		long endTimeNano = System.nanoTime();
		lastTimeNano = endTimeNano - startTimeNano;
		long timeToSleepNano = waitTimeNano - lastTimeNano;
		
		if(timeToSleepNano < 0) {
			return;
		}
		try {
			TimeUnit.NANOSECONDS.sleep(timeToSleepNano);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
