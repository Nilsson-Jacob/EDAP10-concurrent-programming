import java.util.concurrent.Semaphore;

import clock.io.ClockOutput;

public class Monitor {
	private int hour;
	private int minute;
	private int second;
	private ClockOutput out;
	private int alarmHour = 0;
	private int alarmMinute = 0;
	private int alarmSecond = 0;
	private volatile boolean alarmEnabled = false;
	Semaphore mutex = new Semaphore(1);
	Semaphore alarmSemaphore = new Semaphore(0);
	Semaphore mutex2 = new Semaphore(1);
	Semaphore mutex3 = new Semaphore(1);
	
	public Monitor(int h, int m, int s, ClockOutput out) {
		this.hour = h;
		this.minute = m;
		this.second = s;
		this.out = out;
	}
	
	public void setHour(int h) throws InterruptedException {
		mutex.acquire();
		this.hour = h;
		mutex.release();
	}
	
	public void setMinute(int m) throws InterruptedException {
		mutex.acquire();
		this.minute = m;
		mutex.release();
	}
	
	public void setSecond(int s) throws InterruptedException {
		mutex.acquire();
		this.second = s;
		mutex.release();
	}
	
	public void setAHour(int h) throws InterruptedException {
		mutex2.acquire();
		this.alarmHour = h;
		mutex2.release();
	}
	
	public void setAMinute(int m) throws InterruptedException {
		mutex2.acquire();
		this.alarmMinute = m;
		mutex2.release();
	}
	
	public void setASecond(int s) throws InterruptedException {
		mutex2.acquire();
		this.alarmSecond = s;
		mutex2.release();
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getSecond() {
		return second;
	}
	
	public Semaphore getAlarmSemaphore() {
		return alarmSemaphore;
	}
	
	public void setAlarmEnabled(boolean bool) throws InterruptedException {
		mutex3.acquire();
		alarmEnabled = bool;
		mutex3.release();
	}
	
	public boolean getAlarmEnabled() throws InterruptedException {
		mutex3.acquire();
		boolean e = alarmEnabled;
		mutex3.release();
		return e;
	}
	
	public void alarmCheck() throws InterruptedException {
		//alarmSemaphore.acquire();
		while(true) {
		alarmSemaphore.acquire();
		while(alarmEnabled) {
			if (alarmHour == hour && alarmMinute == minute && alarmSecond == second) {
				//System.out.println("ALARM");
				//THE ALARMTIME == CURRENTTIME, PLAY A BEEP EVERY SECOND FOR 20 SEC
				long t0 = System.currentTimeMillis();
				for (int i = 0; i < 20; i++) {
					long t1 = System.currentTimeMillis();
					Thread.sleep((t0+(i+1)*1000)-t1);
					if (alarmEnabled) {
						out.alarm();
					} else {
						break;
					}
					
				}
				
				mutex3.acquire();
				alarmEnabled = false;
				mutex3.release();
				break;
			}
		}
	}
	}
	
	
	
	
	
	public void updateTime() throws InterruptedException {
		int i = 0;
		long t0 = System.currentTimeMillis();
		while(true) {
			long t1 = System.currentTimeMillis();
			Thread.sleep((t0+(++i)*1000)-t1);
			mutex.acquire();
			
			second++;
			if (second > 59) {
				minute++;
				second = 0;
			}
			if (minute > 59) {
				hour++;
				minute = 0;
			}
			if (hour > 23) {
				hour = 0;
			}
			
			mutex.release();
			out.displayTime(hour, minute, second);
			
		}
	}
	
	
	
	
	
}
