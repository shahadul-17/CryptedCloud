package com.cloud.crypted.client.core.events;

import java.util.EventListener;

import com.cloud.crypted.client.core.models.Task;

public interface TaskListener extends EventListener {
	
	void executionSucceeded(Task task, Object result);
	void executionFailed(Task task, Exception exception);
	
}