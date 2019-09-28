package com.cloud.crypted.client.core.utilities;

import java.lang.reflect.Method;

public final class Reflector {
	
	public static final Method getMethodByName(Class<?> classType, String methodName, Object... arguments) {
		Method[] declaredMethods = classType.getDeclaredMethods();
		
		for (int i = 0; i < declaredMethods.length; i++) {
			Method declaredMethod = declaredMethods[i];
			
			if (declaredMethod.getName().equals(methodName) &&
					declaredMethod.getParameterCount() == arguments.length) {
				return declaredMethod;
			}
		}
		
		return null;
	}
	
	public static final Object callMethod(Object instance, Method method, Object... arguments) {
		try {
			return method.invoke(instance, arguments);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
	public static final Object callMethodByName(Object instance, String methodName, Object... arguments) {
		Method method = getMethodByName(instance.getClass(), methodName, arguments);
		
		try {
			return method.invoke(instance, arguments);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
}