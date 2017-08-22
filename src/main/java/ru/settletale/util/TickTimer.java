package ru.settletale.util;

import java.util.concurrent.TimeUnit;

public class TickTimer {
	public long tickDurationNano;
	public long startTimeNano;
	public long endTimeNano;
	public long prevStartTimeNano;
	public long lastSpendTimeNano;

	public TickTimer(double countOfTicksPerSecond) {
		this.tickDurationNano = (long) ((1D / countOfTicksPerSecond) * 1_000_000_000D);
	}
	
	public TickTimer(int countOfTicksPerSecond) {
		this((float)countOfTicksPerSecond);
	}
	
	public void start() {
		prevStartTimeNano = startTimeNano;
		startTimeNano = System.nanoTime();
	}
	
	public void waitAndRestart() {
		long timeToSleepNano = getWaitTimeNano();
		
		if(timeToSleepNano < 0) {
			return;
		}
		try {
			TimeUnit.NANOSECONDS.sleep(timeToSleepNano);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		end();
		start();
	}
	
	public long getWaitTimeNano() {
		return tickDurationNano - (System.nanoTime() - startTimeNano);
	}
	
	public void end() {
		endTimeNano = System.nanoTime();
		lastSpendTimeNano = endTimeNano - startTimeNano;
	}
}
