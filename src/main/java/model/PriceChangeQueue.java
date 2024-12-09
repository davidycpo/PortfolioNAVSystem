package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PriceChangeQueue {
	private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

	public void publish(byte[] priceChange) {
		try {
			queue.put(priceChange);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public byte[] consume() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
}
