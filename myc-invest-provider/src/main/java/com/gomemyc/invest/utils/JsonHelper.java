package com.gomemyc.invest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.Map;

/**
 * MQ 消息通用 json 处理。
 *
 * @author 何健
 * @time 2017年3月17日14:37:00
 */
public class JsonHelper {
    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * 把对象转为 json。
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String getJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * 将 json 串转为 map。
     *
     * @param json
     * @return
     * @throws IOException
     */
    public static Map getMap(byte[] json) throws IOException {
        return mapper.readValue(json, Map.class);
    }
}
