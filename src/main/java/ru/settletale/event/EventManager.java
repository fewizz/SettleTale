package ru.settletale.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
	private static final Map<Method, Event> map = new HashMap<>();
	
	public static void addEventListener(Class<?> clazz) {
		for(Method m : clazz.getDeclaredMethods()) {
			EventListener annotation = m.getDeclaredAnnotation(EventListener.class);
			
			if(annotation == null) {
				continue;
			}
			
			System.out.println("Add: " + m);
			
			map.put(m, annotation.event());
		}
	}
	
	public static void fireEvent(Event e) {
		
		map.forEach((method, event) -> {
			if(e == event) {
				try {
					method.setAccessible(true);
					method.invoke(null);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					e1.printStackTrace();
				}
			}
			
		});
	}
}
