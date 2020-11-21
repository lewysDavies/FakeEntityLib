package uk.lewdev.entitylib.utils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityID {

	public static int nextAndIncrement() {
		if(MCVersion.CUR_VERSION().ordinal() >= MCVersion.V1_14.ordinal()) {
			return nextEntityIdNew();
		} else {
			return nextEntityIdOld();
		}
	}
	
	// 1.9 - 1.13
	public static int nextEntityIdOld(){
		try{
			Field f = ReflectionUtil.getNMSClass("Entity").getDeclaredField("entityCount");
			f.setAccessible(true);
			int id = f.getInt(null);
			f.set(null, id+1);
			return id;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	// 1.14 +
	public static int nextEntityIdNew(){
		try {
			Field f = ReflectionUtil.getNMSClass("Entity").getDeclaredField("entityCount"); 
			f.setAccessible(true);
			Object obj = f.get(null);
			Object idObj = obj.getClass().getMethod("incrementAndGet").invoke(obj);
			
			if(idObj instanceof AtomicInteger) {
				return ((AtomicInteger) idObj).get(); // 1.15
			} else {
				return (int) idObj;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
