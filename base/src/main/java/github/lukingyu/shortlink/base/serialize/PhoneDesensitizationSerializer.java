package github.lukingyu.shortlink.base.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class PhoneDesensitizationSerializer extends ValueSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        String phoneDesensitization = DesensitizedUtil.mobilePhone(value);
        gen.writeString(phoneDesensitization);
    }
}