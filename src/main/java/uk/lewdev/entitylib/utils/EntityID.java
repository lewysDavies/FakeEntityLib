package uk.lewdev.entitylib.utils;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityID {

    public static int nextAndIncrement() {
        if (MCVersion.CUR_VERSION.ordinal() >= MCVersion.V1_17.ordinal()) {
            return nextEntityId1_17();
        } else if (MCVersion.CUR_VERSION().ordinal() >= MCVersion.V1_14.ordinal()) {
            return nextEntityId1_14();
        } else {
            return nextEntityId1_9();
        }
    }

    // 1.9 - 1.13
    public static int nextEntityId1_9() {
        try {
            Field f = ReflectionUtil.getNMSClass("Entity").getDeclaredField("entityCount");
            f.setAccessible(true);
            int id = f.getInt(null);
            f.set(null, id + 1);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 1.14 - 1.16
    public static int nextEntityId1_14() {
        try {
            Field f = ReflectionUtil.getNMSClass("Entity").getDeclaredField("entityCount");
            f.setAccessible(true);
            Object obj = f.get(null);
            Object idObj = obj.getClass().getMethod("incrementAndGet").invoke(obj);

            if (idObj instanceof AtomicInteger) {
                return ((AtomicInteger) idObj).get(); // 1.15
            } else {
                return (int) idObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 1.17+
    public static int nextEntityId1_17() {
        try {
            Field f = ReflectionUtil.getClass("net.minecraft.world.entity.Entity").getDeclaredField("b");
            f.setAccessible(true);
            Object obj = f.get(null);
            Object idObj = obj.getClass().getMethod("incrementAndGet").invoke(obj);

            if (idObj instanceof AtomicInteger) {
                return ((AtomicInteger) idObj).get(); // 1.15
            } else {
                return (int) idObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
