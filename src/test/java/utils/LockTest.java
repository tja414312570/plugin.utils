package utils;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LockTest {
	static String i = "";
	volatile static boolean locked = true;
	public static void main(String[] args) throws InterruptedException {
		ReentrantLock reentrantLock = new ReentrantLock();
		Object ob = new Object();
		int times = 1000;
		int counts = 50000;
		long now = System.currentTimeMillis();
		CountDownLatch countDownLatch = new CountDownLatch(counts);
		Condition condition = reentrantLock.newCondition();
		Runnable run = ()->{
			reentrantLock.lock();
			try {
				if(locked) {
					locked = false;
					condition.await();
				}else {
					locked = true;
					condition.signalAll();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				reentrantLock.unlock();
			}
			countDownLatch.countDown();
		};
		for(int i = 0 ;i<counts;i++) {
			new Thread(run).start();
		}
		countDownLatch.await();
		System.out.println(System.currentTimeMillis() - now);
		now = System.currentTimeMillis();
		CountDownLatch countDownLatch1 = new CountDownLatch(counts);
		Runnable run1 = ()->{
			synchronized (run) {
					try {
						if(locked) {
							locked = false;
							run.wait();
						}else {
							locked = true;
							run.notifyAll();
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
				}
			}
			countDownLatch1.countDown();
		};
		for(int i = 0 ;i<counts;i++) {
			new Thread(run1).start();
		}
		countDownLatch1.await();
//		System.out.println(i);
		System.out.println(System.currentTimeMillis() - now);
	}

}
