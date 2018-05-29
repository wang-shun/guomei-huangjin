package com.gomemyc.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jersey.repackaged.com.google.common.collect.Maps;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wuhui on 2016/12/22.
 */
public class GatewayRequestEventListener implements RequestEventListener {

    private final long requestTimeStart;
    private volatile long methodTimeStart;

    private static final String REQUEST_ID = "request-id";
    private static final String OPERATION_COST_TIME = "cost-time";
    private static final String URL = "url";
    private static final String METHOD = "method";
    private static final String HEAD = "head";
    private static final String ENTITY = "entity";
    private static final String STATUS = "status";
    private static final String COOKIES = "cookies";

    SerializerFeature[] features = new SerializerFeature[] {
        SerializerFeature.QuoteFieldNames, SerializerFeature.SortField,
                SerializerFeature.DisableCircularReferenceDetect };

    private static final List<String> HEAD_NOT_INCLUDE = Arrays.asList("Accept", "Accept-Encoding", "Accept-Charset",
            "Accept-Language", "Connection", "Content-Encoding", "Content-Type", "Vary", "Cache-Control", "Cookie",
            "Host", "accept", "accept-encoding", "accept-charset", "accept-language", "connection", "content-encoding",
            "content-type", "vary", "cache-control", "cookie", "host","content-length");

    private static final AtomicLong atomicLong = new AtomicLong(0);

    private static final Logger logger = LoggerFactory.getLogger(GatewayRequestEventListener.class);

    public GatewayRequestEventListener() {
        this.requestTimeStart = System.currentTimeMillis();
    }

    @Override
    public void onEvent(final RequestEvent event) {
        final long now = System.currentTimeMillis();
        switch (event.getType()) {
            case MATCHING_START: {
                this.methodTimeStart = now;
                long requestId = atomicLong.incrementAndGet();
                ContainerRequest request = event.getContainerRequest();
                Map<String,Object> requestMessage = Maps.newHashMap();
                requestMessage.put(REQUEST_ID,requestId);
                requestMessage.put(URL,request.getRequestUri());
                requestMessage.put(METHOD,request.getMethod());
                MultivaluedMap<String, String> map = request.getRequestHeaders();
                for (Map.Entry<String, List<String>> e : map.entrySet()) {
                    String header = e.getKey();
                    if (!HEAD_NOT_INCLUDE.contains(header)) {
                        requestMessage.put(header, e.getValue());
                    }
                }
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    InputStream in = request.getEntityStream();
                    if (in.available() > 0) {
                        ReaderWriter.writeTo(in, out);
                        byte[] requestEntity = out.toByteArray();
                        requestMessage.put(ENTITY, new String(requestEntity));
                        request.setEntityStream(new ByteArrayInputStream(requestEntity));
                    }
                } catch (Exception e) { }
                logger.info(JSON.toJSONString(requestMessage, features));
                break;
            }
            case FINISHED:
                try {
                    Map<String,Object> respMap = Maps.newHashMap();
                    long requestId = atomicLong.get();
                    respMap.put(REQUEST_ID,requestId);
                    respMap.put(STATUS,event.getContainerResponse().getStatus());
                    respMap.put(ENTITY,event.getContainerResponse().getEntity());
                    respMap.put(OPERATION_COST_TIME,now - requestTimeStart);
                    logger.info(JSON.toJSONString(respMap,features));
                } catch (Exception e) {
                }
        }
    }
}


