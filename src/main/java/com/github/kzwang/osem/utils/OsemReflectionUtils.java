package com.github.kzwang.osem.utils;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;
import static org.reflections.ReflectionUtils.withName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Set;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import com.github.kzwang.osem.annotations.IndexableId;
import com.github.kzwang.osem.exception.ElasticSearchOsemException;

/**
 * Reflection Util class
 */
public class OsemReflectionUtils {

    private static final ESLogger logger = Loggers.getLogger(OsemReflectionUtils.class);

    public static Field getField(Class clazz, String fieldName) {
        Set<Field> fields = getAllFields(clazz, withName(fieldName));

        Field field = fields.iterator().next();
        field.setAccessible(true);
        return field;
    }
    
    public static Method getMethod(Class clazz, String methodName) {
        Set<Method> methods = getAllMethods(clazz, withName(methodName));

        Method method = methods.iterator().next();
        method.setAccessible(true);
        return method;
    }

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getField(object.getClass(), fieldName);
        return getFieldValue(object, field);
    }

    public static Object getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            logger.error("Failed to get value from field", e);
            throw new ElasticSearchOsemException(e);
        }
    }
    
    public static Object getMethodValue(Object object, String methodName) {
        Method method = getMethod(object.getClass(), methodName);
        return getMethodValue(object, method);
    }

    public static Object getMethodValue(Object object, Method method) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (Exception e) {
            logger.error("Failed to get value from field", e);
            throw new ElasticSearchOsemException(e);
        }
    }

    public static Field getIdField(Class clazz) {
        Set<Field> fields = getAllFields(clazz, withAnnotation(IndexableId.class));
        if (fields.isEmpty()) {
            return null;
        }
        return fields.iterator().next();
    }

    public static Method getIdMethod(Class clazz) {
        Set<Method> methods = getAllMethods(clazz, withAnnotation(IndexableId.class));
        if (methods.isEmpty()) {
            return null;
        }
        return methods.iterator().next();
    }


    public static Class getGenericType(Field field) {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            return field.getType();
        }
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        return (Class) type.getActualTypeArguments()[0];
    }

    public static Class getGenericType(Method method) {
        if (!Collection.class.isAssignableFrom(method.getReturnType())) {
            return method.getReturnType();
        }
        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
        return (Class) type.getActualTypeArguments()[0];
    }
}
