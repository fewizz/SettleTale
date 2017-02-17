package ru.settletale.util;

import java.util.concurrent.TimeUnit;

public class TickTimer {
	public long waitTimeNano;
	public long startTimeNano;
	public long endTimeNano;
	public long prevStartTimeNano;
	public long lastTimeNano;

	public TickTimer(double countOfTicksPerSecond) {
		this.waitTimeNano = (long) ((1D / countOfTicksPerSecond) * 1_000_000_000D);
	}
	
	public TickTimer(int countOfTicksPerSecond) {
		this((float)countOfTicksPerSecond);
	}
	
	public void start() {
		prevStartTimeNano = startTimeNano;
		startTimeNano = System.nanoTime();
	}
	
	public void waitAndEndTimer() {
		long timeToSleepNano = waitTimeNano - (System.nanoTime() - startTimeNano);
		
		if(timeToSleepNano < 0) {
			return;
		}
		try {
			TimeUnit.NANOSECONDS.sleep(timeToSleepNano);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		end();
	}
	
	public void end() {
		endTimeNano = System.nanoTime();
		lastTimeNano = endTimeNano - startTimeNano;
	}
}
