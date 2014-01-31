package com.github.kzwang.osem.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;

/**
 * Jackson module used to serialize/deserialize object
 */
public class JacksonElasticSearchOsemModule extends Module {
    @Override
    public String getModuleName() {
        return "jackson-datatype-elasticsearch-osem";
    }

    @Override
    public Version version() {
        return VersionUtil.versionFor(JacksonElasticSearchOsemModule.class);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.appendAnnotationIntrospector(new ElasticSearchOsemAnnotationIntrospector());
        context.addBeanSerializerModifier(new OsemBeanSerializerModifier());

    }

}
