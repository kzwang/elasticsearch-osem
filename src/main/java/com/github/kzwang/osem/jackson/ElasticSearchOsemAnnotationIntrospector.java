package com.github.kzwang.osem.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.*;
import com.github.kzwang.osem.annotations.*;
import com.github.kzwang.osem.converter.DateDeserializer;
import com.github.kzwang.osem.converter.DateSerializer;
import com.github.kzwang.osem.converter.RawJsonDeSerializer;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;


/**
 * Override {@link JacksonAnnotationIntrospector} to read OSEM annotations
 */
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

        IndexableProperty property = a.getAnnotation(IndexableProperty.class);
        if (property != null && !property.name().isEmpty()) {
            return new PropertyName(property.name());
        }

        IndexableComponent component = a.getAnnotation(IndexableComponent.class);
        if (component != null && !component.name().isEmpty()) {
            return new PropertyName(component.name());
        }

        IndexableProperties properties = a.getAnnotation(IndexableProperties.class);
        if (properties != null && !properties.name().isEmpty()) {
            return new PropertyName(properties.name());
        }

        return PropertyName.USE_DEFAULT;
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

        IndexableProperties properties = a.getAnnotation(IndexableProperties.class);
        if (properties != null && properties.jsonInclude() != com.github.kzwang.osem.annotations.JsonInclude.DEFAULT) {
            return JsonInclude.Include.valueOf(properties.jsonInclude().toString());
        }
        return defValue;
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        if (!(a instanceof AnnotatedField) && !(a instanceof AnnotatedMethod)) {
            return super.findNameForDeserialization(a);
        }

        IndexableProperty property = a.getAnnotation(IndexableProperty.class);
        if (property != null && !property.name().isEmpty()) {
            return new PropertyName(property.name());
        }

        IndexableComponent component = a.getAnnotation(IndexableComponent.class);
        if (component != null && !component.name().isEmpty()) {
            return new PropertyName(component.name());
        }

        IndexableProperties properties = a.getAnnotation(IndexableProperties.class);
        if (properties != null && !properties.name().isEmpty()) {
            return new PropertyName(properties.name());
        }

        return PropertyName.USE_DEFAULT;
    }

    private boolean isDate(Annotated annotated) {
        Class clazz = annotated.getRawType();
        if (Collection.class.isAssignableFrom(clazz)) {
            ParameterizedType type = (ParameterizedType) annotated.getGenericType();
            clazz = (Class) type.getActualTypeArguments()[0];
        }
        return clazz.equals(Date.class);
    }

    @Override
    public Object findSerializer(Annotated a) {
        Class clazz = a.getRawType();
        if (Collection.class.isAssignableFrom(clazz)) {
            return null;  // Collection should be handled in findContentSerializer(Annotated a)
        }
        Object serializer = findSerializerToUse(a);
        if (serializer != null) {
            return serializer;
        }
        return super.findSerializer(a);
    }

    @Override
    public Class<? extends JsonSerializer<?>> findContentSerializer(Annotated a) {
        Class clazz = a.getRawType();
        if (!Collection.class.isAssignableFrom(clazz)) {
            return null;  // Only handle Collection
        }
        Object serializer = findSerializerToUse(a);
        if (serializer != null) {
            return (Class<? extends JsonSerializer<?>>) serializer;
        }
        return super.findContentSerializer(a);
    }

    private Object findSerializerToUse(Annotated a) {
        if (a instanceof AnnotatedField || a instanceof AnnotatedMethod) {
            IndexableComponent indexableComponent = a.getAnnotation(IndexableComponent.class);
            if (indexableComponent != null && indexableComponent.serializer() != JsonSerializer.class) {
                return indexableComponent.serializer();
            }

            IndexableProperty indexableProperty = a.getAnnotation(IndexableProperty.class);
            if (indexableProperty != null) {
                if (indexableProperty.serializer() != JsonSerializer.class) {
                    return indexableProperty.serializer();
                }
                if (isDate(a)) {  // use custom date serializer
                    return DateSerializer.class;
                }

            }
            IndexableProperties indexableProperties = a.getAnnotation(IndexableProperties.class);
            if (indexableProperties != null && indexableProperties.serializer() != JsonSerializer.class) {
                return indexableProperties.serializer();
            }

        } else if (a instanceof AnnotatedClass) {  // handle class
            Indexable indexable = a.getAnnotation(Indexable.class);
            if (indexable != null && indexable.serializer() != JsonSerializer.class) {
                return indexable.serializer();
            }
        }
        return null;
    }

    @Override
    public Class<? extends JsonDeserializer<?>> findDeserializer(Annotated a) {
        Class clazz = a.getRawType();
        if (Collection.class.isAssignableFrom(clazz)) {
            return null;  // Collection should be handled in findContentDeserializer(Annotated a)
        }
        Class<? extends JsonDeserializer<?>> deserializer = findDeserializerToUse(a);
        if (deserializer != null) {
            return deserializer;
        }

        return super.findDeserializer(a);
    }

    @Override
    public Class<? extends JsonDeserializer<?>> findContentDeserializer(Annotated a) {
        Class clazz = a.getRawType();
        if (!Collection.class.isAssignableFrom(clazz)) {
            return null;  // Only handle Collection
        }
        Class<? extends JsonDeserializer<?>> deserializer = findDeserializerToUse(a);
        if (deserializer != null) {
            return deserializer;
        }
        return super.findContentDeserializer(a);
    }

    private Class<? extends JsonDeserializer<?>> findDeserializerToUse(Annotated a) {
        if (a instanceof AnnotatedField || a instanceof AnnotatedMethod) {
            IndexableComponent indexableComponent = a.getAnnotation(IndexableComponent.class);
            if (indexableComponent != null && indexableComponent.deserializer() != JsonDeserializer.class) {
                return (Class<? extends JsonDeserializer<?>>) indexableComponent.deserializer();
            }

            IndexableProperty indexableProperty = a.getAnnotation(IndexableProperty.class);
            if (indexableProperty != null) {
                if (indexableProperty.deserializer() != JsonDeserializer.class) {
                    return (Class<? extends JsonDeserializer<?>>) indexableProperty.deserializer();
                }
                if (isDate(a)) { // use custom date deserializer
                    return DateDeserializer.class;
                }
                if (indexableProperty.type().equals(TypeEnum.JSON)) {  // use custom deserializer for raw json field
                    return RawJsonDeSerializer.class;
                }
            }

            IndexableProperties indexableProperties = a.getAnnotation(IndexableProperties.class);
            if (indexableProperties != null && indexableProperties.deserializer() != JsonDeserializer.class) {
                return (Class<? extends JsonDeserializer<?>>) indexableProperties.deserializer();
            }
        } else if (a instanceof AnnotatedClass) {  // handle class
            Indexable indexable = a.getAnnotation(Indexable.class);
            if (indexable != null && indexable.deserializer() != JsonDeserializer.class) {
                return (Class<? extends JsonDeserializer<?>>) indexable.deserializer();
            }
        }
        return null;
    }

}
