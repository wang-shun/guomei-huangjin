package com.gomemyc.filter;

import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.util.InfoCode;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.Encoded;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by wuhui on 2017/2/3.
 */
@PreMatching
public abstract class BaseContainerRequestFilter implements ContainerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(BaseContainerRequestFilter.class);

    private static final Annotation encodedAnnotation = getEncodedAnnotation();

    private boolean decode = false;

    private static Annotation getEncodedAnnotation() {
        /**
         * Encoded-annotated class.
         */
        @Encoded
        final class EncodedAnnotationTemp {
        }
        return EncodedAnnotationTemp.class.getAnnotation(Encoded.class);
    }

    /**
     * 可获取GET请求参数;
     * POST请求参数，只支持获取contextType为application/x-www-form-urlencoded的请求的form参数
     * @param context
     * @return
     */
    protected MultivaluedMap<String, String> getParameters(ContainerRequestContext context) {
        if (context == null) {
            logger.error("ContainerRequestContext is null");
            return null;
        }
        if (context.getMethod().equalsIgnoreCase("get")) {
            return context.getUriInfo().getQueryParameters();
        } else if (context.getMethod().equalsIgnoreCase("post")) {
            return getFormParameters(context);
        } else {
            logger.error("Unsupport request method {}", context.getMethod());
            throw new AppRuntimeException(InfoCode.INVALID_REQUEST);
        }
    }

    private MultivaluedMap<String, String> getFormParameters(ContainerRequestContext context) {

        if (context != null && context.getRequest() instanceof ContainerRequest) {
            ContainerRequest request = (ContainerRequest) context.getRequest();
            if (MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
                request.bufferEntity();
                Form form;
                if (!decode) {
                    form = request.readEntity(Form.class);
                } else {
                    Annotation[] annotations = new Annotation[1];
                    annotations[0] = encodedAnnotation;
                    form = request.readEntity(Form.class, annotations);
                }
                return (form == null ? new Form() : form).asMap();
            }
        }
        return new Form().asMap();

    }
}
