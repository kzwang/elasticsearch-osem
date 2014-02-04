package com.github.kzwang.osem.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.github.kzwang.osem.annotations.IndexableComponent;
import com.github.kzwang.osem.annotations.IndexableProperties;
import com.github.kzwang.osem.annotations.IndexableProperty;


/**
 * Override to use custom serializer for null value
 */
public class OsemBeanPropertyWriter extends BeanPropertyWriter {

    protected OsemBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
        Object value = get(bean);
        // Null handling is bit different, check that first
        if (value == null) {
            if (_nullSerializer != null) {
                jgen.writeFieldName(_name);
                // check has custom serializer first
                IndexableProperty indexableProperty = _member.getAnnotation(IndexableProperty.class);
                IndexableComponent indexableComponent = _member.getAnnotation(IndexableComponent.class);
                IndexableProperties indexableProperties = _member.getAnnotation(IndexableProperties.class);
                if (indexableProperty != null && indexableProperty.serializer() != JsonSerializer.class) {
                    JsonSerializer serializer = indexableProperty.serializer().newInstance();
                    serializer.serialize(null, jgen, prov);
                } else if (indexableComponent != null && indexableComponent.serializer() != JsonSerializer.class) {
                    JsonSerializer serializer = indexableComponent.serializer().newInstance();
                    serializer.serialize(null, jgen, prov);
                } else if (indexableProperties != null && indexableProperties.serializer() != JsonSerializer.class) {
                    JsonSerializer serializer = indexableProperties.serializer().newInstance();
                    serializer.serialize(null, jgen, prov);
                } else {
                    _nullSerializer.serialize(null, jgen, prov);
                }

            }
            return;
        }
        // then find serializer to use
        JsonSerializer<Object> ser = _serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = _dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(map, cls, prov);
            }
        }
        // and then see if we must suppress certain values (default, empty)
        if (_suppressableValue != null) {
            if (MARKER_FOR_EMPTY == _suppressableValue) {
                if (ser.isEmpty(value)) {
                    return;
                }
            } else if (_suppressableValue.equals(value)) {
                return;
            }
        }
        // For non-nulls: simple check for direct cycles
        if (value == bean) {
            _handleSelfReference(bean, ser);
        }
        jgen.writeFieldName(_name);
        if (_typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        } else {
            ser.serializeWithType(value, jgen, prov, _typeSerializer);
        }
    }
}
