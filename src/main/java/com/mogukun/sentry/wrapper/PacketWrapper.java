package com.mogukun.sentry.wrapper;

import net.minecraft.server.v1_8_R3.Packet;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class PacketWrapper {
    private final Map<String, Field> fields = new WeakHashMap<>();

    private final Packet<?> instance;
    private final Class<? extends Packet<?>> clazz;

    public PacketWrapper(final Packet<?> instance, final Class<? extends Packet<?>> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();

        // Loop around all the declared fields and make them accessible
        for (final Field declaredField : declaredFields) {
            final String fieldName = declaredField.getName();

            declaredField.setAccessible(true);
            fields.put(fieldName, declaredField);
        }

        // Set the clazz from constructor
        this.instance = instance;
        this.clazz = clazz;
    }

    /**
     * @param name - The name of the field you want to get
     * @param <T>  - The data-type
     * @return - The value (possibly null) that we got from the field.
     */
    public <T> T get(final String name) {
        try {
            //noinspection unchecked
            return (T) fields.get(name).get(instance);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}