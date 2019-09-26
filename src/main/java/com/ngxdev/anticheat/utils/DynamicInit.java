package com.ngxdev.anticheat.utils;

import com.google.common.collect.Sets;
import org.atteo.classindex.ClassIndex;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicInit {
	public static Set<Class<?>> getClasses(String packageName, Class<? extends Annotation> annotation) throws IOException, InvocationTargetException {
		Set<Class<?>> classes = Sets.newHashSet();
		ClassLoader loader = annotation.getClassLoader();

		boolean fallback = true;
		try {
			if (!loader.getClass().getSimpleName().startsWith("Plugin")) {
				Set<String> names = new HashSet<>();
				for (Field field : loader.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					Object obj = field.get(loader);
					if (obj instanceof Map) {
						names.addAll(((Map<String, Object>) obj).keySet());
						fallback = false;
					}
				}
				for (String info : names) {
					if (info.startsWith(packageName)) {
						try {
							Class<?> clazz = loader.loadClass(info);
							if (clazz.isAnnotationPresent(annotation)) classes.add(clazz);
						} catch (NoClassDefFoundError | ClassNotFoundException ex) {

						}
					}
				}
			}
		} catch (Exception e) {
		}
		if (fallback) {
			Log.println("Falling back to classindex...");
			ClassIndex.getAnnotated(annotation, loader).forEach(classes::add);
		}
		return classes;
	}
}
