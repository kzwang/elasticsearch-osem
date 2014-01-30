package com.github.kzwang.osem.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import com.github.kzwang.osem.annotations.*;
import com.github.kzwang.osem.converter.RawJsonDeSerializer;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;


public class ElasticSearchOsemAnnotationIntrospector extends JacksonAnnotationIntrospector {


    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        if (!(m instanceof AnnotatedField) && !(m instanceof AnnotatedMethod)) return false;

        return (m.getAnnotation(IndexableProperty.class) == null
                && m.getAnnotation(IndexableComponent.class) == null && m.getAnnotation(IndexableProperties.class) == null);
    }


    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        if (!(a instanceof AnnotatedField) && !(a instanceof AnnotatedMethod)) {
            return super.findNameForSerialization(a);
        }

        String name = null;

        IndexableProperty property = a.getAnnotation(IndexableProperty.class);
        if (property != null) {
            name = property.name();
        }

        IndexableComponent component = a.getAnnotation(IndexableComponent.class);
        if (component != null && name == null) {
            name = component.name();
        }

        IndexableProperties properties = a.getAnnotation(IndexableProperties.class);
        if (properties != null && name == null) {
            name = properties.name();
        }

        if (name == null || name.isEmpty()) { // empty String means 'default'
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(name);
    }


    @Override
    public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
        IndexableProperty property = a.getAnnotation(IndexableProperty.class);
        if (property != null && property.jsonInclude() != com.github.kzwang.osem.annotations.JsonInclude.DEFAULT) {
            return JsonInclude.Include.valueOf(property.jsonInclude().toString());
        }

        IndexableComponent component = a.getAnnotation(IndexableComponent.class);
        if (component != null && component.jsonInclude() != com.github.kzwang.osem.annotations.JsonInclude.DEFAULT) {
            return JsonInclude.Include.valueOf(component.jsonInclude().toString());
        }
        return defValue;
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        if (!(a instanceof AnnotatedField) && !(a instanceof AnnotatedMethod)) {
            return super.findNameForDeserialization(a);
        }
        String name = null;
        IndexableProperty property = a.getAnnotation(IndexableProperty.class);
        if (property != null) {
            name = property.name();
        }

        IndexableComponent component = a.getAnnotation(IndexableComponent.class);
        if (component != null && name == null) {
            name = component.name();
        }

        IndexableProperties properties = a.getAnnotation(IndexableProperties.class);
        if (properties != null && name == null) {
            name = properties.name();
        }
        if (name == null || name.isEmpty()) { // empty String means 'default'
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(name);
    }


    @Override
    public JsonFormat.Value findFormat(Annotated annotated) {
        if (annotated instanceof AnnotatedField || annotated instanceof AnnotatedMethod) {
            IndexableProperty property = annotated.getAnnotation(IndexableProperty.class);
            if (property != null && property.format() != null && !property.format().isEmpty()) {  // has format
                Class clazz = annotated.getRawType();
                if (Collection.class.isAssignableFrom(clazz)) {
                    ParameterizedType type = (ParameterizedType) annotated.getGenericType();
                    clazz = (Class) type.getActualTypeArguments()[0];
                }
                if (clazz.equals(Date.class) || property.type().equals(TypeEnum.DATE)) { // handle date
                    JsonFormat.Value format = new JsonFormat.Value().withShape(JsonFormat.Shape.STRING).withPattern(property.format()).withTimeZone(TimeZone.getDefault());
                    return format;
                }
            }
        }
        return super.findFormat(annotated);
    }


    @Override
    public Object findSerializer(Annotated a) {
        if (a instanceof AnnotatedField || a instanceof AnnotatedMethod) {

            IndexableComponent indexableComponent = a.getAnnotation(IndexableComponent.class);
            if (indexableComponent != null && indexableComponent.serializer() != JsonSerializer.class) {
                return indexableComponent.serializer();
            }

            IndexableProperty indexableProperty = a.getAnnotation(IndexableProperty.class);
            Object indexablePropertySerializer = getSerializer(indexableProperty, a);
            if (indexablePropertySerializer != null) {
                return indexablePropertySerializer;
            }

            IndexableProperties indexableProperties = a.getAnnotation(IndexableProperties.class);
            if (indexableProperties != null && indexableProperties.properties().length > 0) {
                Object serializer = null;
                for (IndexableProperty property : indexableProperties.properties()) {
                    Object s = getSerializer(property, a);
                    if (s != null) {
                        if (serializer != null && !s.equals(serializer)) {
                            throw new RuntimeException("Can't have different serializer for multi-field");
                        }
                        serializer = s;
                    }
                }
                if (serializer != null) {
                    return serializer;
                }
            }

        } else if (a instanceof AnnotatedClass) {  // handle class
            Indexable indexable = a.getAnnotation(Indexable.class);
            if (indexable != null && indexable.serializer() != JsonSerializer.class) {
                return indexable.serializer();
            }
        }


        return super.findSerializer(a);
    }

    private Object getSerializer(IndexableProperty indexableProperty, Annotated a) {
        if (indexableProperty != null && indexableProperty.serializer() != JsonSerializer.class) {
            return indexableProperty.serializer();
        }
        if (indexableProperty != null && indexableProperty.type().equals(TypeEnum.JSON)) {
            Class<?> cls = a.getRawType();
            return new RawSerializer<Object>(cls);
        }
        return null;
    }


    @Override
    public Class<? extends JsonDeserializer<?>> findDeserializer(Annotated a) {
        if (a instanceof AnnotatedField || a instanceof AnnotatedMethod) {  // handle field

            IndexableComponent indexableComponent = a.getAnnotation(IndexableComponent.class);
            if (indexableComponent != null && indexableComponent.deserializer() != JsonDeserializer.class) {
                return (Class<? extends JsonDeserializer<?>>) indexableComponent.deserializer();
            }

            IndexableProperty indexableProperty = a.getAnnotation(IndexableProperty.class);
            Class<? extends JsonDeserializer<?>> indexablePropertyDeserializer = getDeserializer(indexableProperty, a);
            if (indexablePropertyDeserializer != null) {
                return indexablePropertyDeserializer;
            }

            IndexableProperties indexableProperties = a.getAnnotation(IndexableProperties.class);
            if (indexableProperties != null && indexableProperties.properties().length > 0) {
                Class<? extends JsonDeserializer<?>> deserializer = null;
                for (IndexableProperty property : indexableProperties.properties()) {
                    Class<? extends JsonDeserializer<?>> s = getDeserializer(property, a);
                    if (s != null) {
                        if (deserializer != null && !s.equals(deserializer)) {
                            throw new RuntimeException("Can't have different deserializer for multi-field");
                        }
                        deserializer = s;
                    }
                }
                if (deserializer != null) {
                    return deserializer;
                }
            }


        } else if (a instanceof AnnotatedClass) {  // handle class
            Indexable indexable = a.getAnnotation(Indexable.class);
            if (indexable != null && indexable.deserializer() != JsonDeserializer.class) {
                return (Class<? extends JsonDeserializer<?>>) indexable.deserializer();
            }
        }

        return super.findDeserializer(a);
    }

    private Class<? extends JsonDeserializer<?>> getDeserializer(IndexableProperty indexableProperty, Annotated a) {
        if (indexableProperty != null && indexableProperty.deserializer() != JsonDeserializer.class) {
            return (Class<? extends JsonDeserializer<?>>) indexableProperty.deserializer();
        }
        if (indexableProperty != null && indexableProperty.type().equals(TypeEnum.JSON)) {
            return RawJsonDeSerializer.class;
        }
        return null;
    }
}
