package com.cloud.crypted.client.core.services;

import java.util.LinkedList;
import java.util.Queue;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.models.Task;

public final class TaskExecutor extends Thread {
	
	private static final Queue<Task> TASKS = new LinkedList<Task>();
	
	public TaskExecutor() {
		setPriority(Thread.MAX_PRIORITY);
	}
	
	public void addTask(Task task) {
		if (task == null) {
			return;
		}
		
		synchronized (TASKS) {
			TASKS.add(task);
			
			if (TASKS.size() == 1) {
				synchronized (this) {
					notifyAll();
				}
			}
		}
	}
	
	@Override
	public void run() {
		while (Application.running) {
			Task task = null;
			
			synchronized (TASKS) {
				task = TASKS.poll();
			}
			
			if (task == null) {
				System.gc();
				
				synchronized (this) {
					try {
						wait();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			} else {
				task.execute();
			}
		}
	}
	
}