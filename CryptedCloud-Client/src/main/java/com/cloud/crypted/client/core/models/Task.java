package com.cloud.crypted.client.core.models;

import com.cloud.crypted.client.core.events.TaskListener;

public interface Task {
	
	String getName();
	Object[] getParameters();
	void setParameters(Object... parameters);
	
	void addTaskListener(TaskListener taskListener);
	void execute();
	
}