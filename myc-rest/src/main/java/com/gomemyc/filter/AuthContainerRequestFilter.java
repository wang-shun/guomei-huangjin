package com.gomemyc.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.gomemyc.annotation.Validate;
import com.gomemyc.constants.CommonConstant;
import com.gomemyc.exception.AppRuntimeException;
import com.gomemyc.util.InfoCode;

/**
 * 权限过滤
 * Created by wuhui on 2017/1/20.
 */
@Provider
@Component
public class AuthContainerRequestFilter extends BaseContainerRequestFilter {



    private static final Logger logger = LoggerFactory.getLogger(AuthContainerRequestFilter.class);

	@Autowired
    @Qualifier("redisStringStringTemplate")
	private RedisTemplate<String,String> redisStringTemplate;
	
    @Override
    public void filter(ContainerRequestContext rc) throws IOException {
    	
        UriInfo ui = rc.getUriInfo();
        ResourceInfo ri = null;
        if (ui instanceof ResourceInfo) {
            ri = (ResourceInfo) ui;
        }
        if (ri==null){
            throw new AppRuntimeException(InfoCode.SERVICE_UNAVAILABLE);
        }

        /**
         * 检查方法上是否有权限验证注解
         */
        Method method = ri.getResourceMethod();
        Validate validate = AnnotationUtils.findAnnotation(method,Validate.class);
        if (validate !=null&&validate.login()){
            if (!isLogin(rc)) {
                throw new AppRuntimeException(InfoCode.NOT_LOGIN);
            }
        }

        /**
         * 检查方法上是否有权限验证注解
         */
        Class clazz = ri.getResourceClass();
        validate = AnnotationUtils.findAnnotation(clazz,Validate.class);
        if (validate !=null&&validate.login()){
            if (!isLogin(rc)) {
                throw new AppRuntimeException(InfoCode.NOT_LOGIN);
            }
        }
    }

    private Boolean isLogin(ContainerRequestContext rc){
        MultivaluedMap<String, String> params = getParameters(rc);
        
        //判断userId是否为空
        String uid = params.getFirst("uid");
        if (StringUtils.isBlank(uid)){
            return false;
        }
        
        //判断是否传递了token
        String token = params.getFirst("token");
        if (StringUtils.isBlank(token) && getUserId(rc)==null){
            return false;
        }  
        return true;
    }
    
    /**
     * 
     * getUserId:(cookie中获取ccat信息)
     * 
     * @author TianBin 
     * @param containerRequestContext
     * @return 
     * @since JDK 1.8
     */
    
    protected String getUserId(ContainerRequestContext containerRequestContext){
        try {
        	
        	Map<String, Cookie> cookieMap= containerRequestContext.getCookies();
            logger.info("getUserId in AuthContainerRequestFilter,the cookieMap is [{}]",cookieMap);
        	Collection<Cookie> cookieCollection=cookieMap.values();
            int cookieCollectionSize=cookieCollection.size();
            logger.info("getUserId in AuthContainerRequestFilter,the cookieCollectionSize is [{}]",cookieCollectionSize);
            if(cookieCollectionSize>0){
                final  String redisCcat=null;
                List<Cookie> cookieList=new ArrayList<Cookie>(cookieCollection);
                String ccat = null;
                if(cookieList!=null && cookieList.size()>0){
                    for (int i = 0; i < cookieList.size(); i++) {
                        Cookie cookie=cookieList.get(i);
                        logger.info("getUserId in AuthContainerRequestFilter,the cookieName is [{}]",cookie.getName());
                        if(cookie.getName().equalsIgnoreCase(CommonConstant.COOKIE_SESSION_KEY_NAME)){
                            ccat = cookie.getValue();
                            logger.info("getUserId in AuthContainerRequestFilter,the cookieValue is [{}]",ccat);
                            break;
                        }
                    }
                }
                logger.info("getUserId in AuthContainerRequestFilter,the redis key is [{}]",CommonConstant.SESSION_KEY+ccat);
                return redisStringTemplate.opsForValue().get(CommonConstant.SESSION_KEY+ccat);
            }
           return  null;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return null;
    }
}

