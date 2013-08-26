package com.example.sodacloudsmsexampleclient;

import java.util.HashMap;

public class MyModule implements Module {

	HashMap<Class<?>,Object> map = new HashMap<Class<?>,Object>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getComponent(Class<T> type) {
		
		return (T) map.get(type);
	}

	@Override
	public <T> void setComponent(Class<T> type, T component) {
		map.put(type, component);

	}

}
