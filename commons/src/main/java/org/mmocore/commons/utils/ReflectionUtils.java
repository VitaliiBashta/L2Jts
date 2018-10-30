package org.mmocore.commons.utils;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Reflection;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import static com.google.common.reflect.ClassPath.ClassInfo;

/**
 * @author Java-man
 */
public class ReflectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    public static <T> void loadClasses(final String prefix) {
        loadClasses(getSubTypesOf(prefix, Object.class));
    }

    public static <T> void loadClasses(final String prefix, final Class<T> type) {
        loadClasses(getSubTypesOf(prefix, type));
    }

    public static <T> void loadClassesWithConsumer(final String prefix, final Class<T> type, final Consumer<T> consumer) {
        loadClassesWithConsumer(getSubTypesOf(prefix, type), consumer);
    }

    public static <T> void loadClassesFromPackage(final Package pack) {
        final Thread thread = Thread.currentThread();
        final ClassPath classPath;
        try {
            classPath = ClassPath.from(thread.getContextClassLoader());
        } catch (IOException e) {
            LOGGER.error("Can't load classes.", e);
            return;
        }

        for (final ClassInfo classInfo : classPath.getTopLevelClasses(pack.getName())) {
            final Class<?> clazz = classInfo.load();

            final int modifiers = clazz.getModifiers();
            if (Modifier.isAbstract(modifiers))
                continue;

            Reflection.initialize(clazz);
        }
    }

    public static <T> void loadClasses(final Iterable<Class<? extends T>> classes) {
        for (final Class<? extends T> clazz : classes) {
            try {
                clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.warn(clazz.getSimpleName() + "can't be loaded.", e);
            }
        }
    }

    public static <T> void loadClassesWithConsumer(final Iterable<Class<? extends T>> classes, final Consumer<T> consumer) {
        for (final Class<? extends T> clazz : classes) {
            try {
                final T instance = clazz.getConstructor().newInstance();
                if (consumer != null) {
                    consumer.accept(instance);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.warn(clazz.getSimpleName() + "can't be loaded.", e);
            }
        }
    }

    private static <T> Iterable<Class<? extends T>> getSubTypesOf(final String prefix, final Class<T> type) {
        final Reflections reflections = new Reflections(prefix);
        return reflections.getSubTypesOf(type);
    }
}
