package com.github.kzwang.osem.utils;

import com.github.kzwang.osem.annotations.IndexableId;
import org.elasticsearch.common.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;


public class OsemReflectionUtils {


    public static Field getField(Class clazz, String fieldName) {
        Set<Field> fields = getAllFields(clazz, withName(fieldName));
        Preconditions.checkArgument(fields.size() == 1, "Unable to find field {} for class {}", fieldName, clazz.getSimpleName());
        Field field = fields.iterator().next();
        field.setAccessible(true);
        return field;
    }

    public static Object getFieldValue(Object object, String fieldName) throws IllegalAccessException {
        Field field = getField(object.getClass(), fieldName);
        return field.get(object);
    }


    public static Object getFieldValue(Object object, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }


    public static Field getIdField(Class clazz) {
        Set<Field> fields = getAllFields(clazz, withAnnotation(IndexableId.class));
        Preconditions.checkArgument(fields.size() == 1, "Unable to find id field for class {}", clazz.getSimpleName());
        return fields.iterator().next();
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
