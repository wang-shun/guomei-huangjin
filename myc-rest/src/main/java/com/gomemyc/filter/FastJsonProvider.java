package com.gomemyc.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gomemyc.util.InfoCode;
import com.gomemyc.util.RestResp;
import com.google.common.collect.Maps;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by wuhui on 2016/8/2.
 **/
@Provider
public class FastJsonProvider implements MessageBodyWriter<Object> {

    /**
     * 设置fastJson的序列化
     */
    public FastJsonProvider() {
        this.features = new SerializerFeature[] {
                SerializerFeature.QuoteFieldNames, SerializerFeature.SortField,
                SerializerFeature.DisableCircularReferenceDetect };
    }

    private SerializerFeature[] features;

    @Override
    public boolean isWriteable(Class<?> aClass, Type type,
                               Annotation[] annotations, MediaType mediaType) {
        if (!isJsonType(mediaType)) {
            return false;
        }
        return true;
    }

    @Override
    public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    /**
     * 序列化返回结果
     * @param value
     * @param aClass
     * @param type
     * @param annotations
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     * @throws IOException
     * @throws WebApplicationException
     */
    @Override
    public void writeTo(Object value, Class<?> aClass, Type type,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        if (httpHeaders != null && httpHeaders.containsKey("exception")) {
            httpHeaders.remove("exception");
        }
        if (value instanceof RestResp) {
            entityStream.write(JSON.toJSONBytes(value, features));
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                entityStream.write(JSON.toJSONBytes(
                        RestResp.build(InfoCode.SUCCESS, of("status", 1)), features));
            } else {
                entityStream.write(JSON.toJSONBytes(
                        RestResp.build(InfoCode.SUCCESS, of("status", 0)), features));
            }
        } else {
            entityStream.write(JSON.toJSONBytes(RestResp.build(InfoCode.SUCCESS, value), features));
        }
    }

    private <K, V> HashMap<K, V> of(K key, V value) {
        HashMap<K, V> map = Maps.newHashMap();
        map.put(key, value);
        return map;
    }


    protected boolean isJsonType(MediaType mediaType) {

        if (mediaType != null) {
            String subtype = mediaType.getSubtype();
            return "json".equalsIgnoreCase(subtype)
                    || subtype.endsWith("+json");
        }
        return true;
    }
}
