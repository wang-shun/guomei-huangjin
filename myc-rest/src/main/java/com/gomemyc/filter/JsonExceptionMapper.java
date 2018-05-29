package com.gomemyc.filter;


import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.http.MediaTypes;
import com.gomemyc.util.InfoCode;
import com.gomemyc.util.RestResp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 处理程序中的各种异常
 * Created by wuhui on 2016/8/2.
 **/
@Provider
public class JsonExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(JsonExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND) .type(MediaTypes.JSON_UTF_8)
                    .header("exception", true).entity(RestResp.build(InfoCode.URL_NOT_FOUND)).build();
        }else if (exception instanceof NotAllowedException){
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .type(MediaTypes.JSON_UTF_8).header("exception", true)
                    .entity(RestResp.build(InfoCode.SERVICE_UNAVAILABLE,Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase())).build();
        }else if (exception instanceof NotSupportedException){
            return Response.status(Response.Status.NOT_ACCEPTABLE) .type(MediaTypes.JSON_UTF_8)
                    .header("exception", true).entity(RestResp.build(InfoCode.SERVICE_UNAVAILABLE,Response.Status.NOT_ACCEPTABLE.getReasonPhrase())).build();
        } else if (exception instanceof AppRuntimeException) {
            AppRuntimeException e = (AppRuntimeException) exception;
            String msg = e.getMessage();
            return Response.ok().type(MediaTypes.JSON_UTF_8)
                    .header("exception", true)
                    .entity(RestResp.build(e.getInfoCode().getStatus(),StringUtils.isBlank(msg) ? e.getInfoCode().getMsg():msg)).build();
        }  else if (WebApplicationException.class.isAssignableFrom(exception.getClass())) {
            String msg = exception.getMessage();
            if (StringUtils.isBlank(msg)){
                msg = "服务器出错了，请稍后再试.";
            }
            RestResp resp = RestResp.build(InfoCode.SERVICE_UNAVAILABLE.getStatus(), msg);
            return Response.ok().type(MediaTypes.JSON_UTF_8).header("exception", true).entity(resp).build();
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(exception.getClass().getName());
            if (!StringUtils.isBlank(exception.getMessage())) {
                sb.append(" : ");
                sb.append(exception.getMessage());
            }
            logger.error("", exception);
            RestResp resp = RestResp.build(InfoCode.SERVICE_UNAVAILABLE.getStatus(), sb.toString());
            return Response.ok().type(MediaTypes.JSON_UTF_8).header("exception", true).entity(resp).build();
        }
    }
}
