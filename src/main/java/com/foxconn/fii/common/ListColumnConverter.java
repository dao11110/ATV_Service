package com.foxconn.fii.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Converter
public class ListColumnConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> stringObject) {
        try {
            return mapper.writeValueAsString(stringObject);
        } catch (JsonProcessingException e) {
            log.error("### convertToDatabaseColumn", e);
            return "";
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, new TypeReference<List<String>>(){});
        } catch (IOException e) {
            log.error("### convertToEntityAttribute", e);
            return null;
        }
    }
}
