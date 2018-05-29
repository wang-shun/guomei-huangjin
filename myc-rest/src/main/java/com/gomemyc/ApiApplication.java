package com.gomemyc;

import com.gomemyc.exception.HttpExeption;
import com.gomemyc.filter.AuthContainerRequestFilter;
import com.gomemyc.http.HttpClientUtil;
import com.gomemyc.listener.GatewayApplicationEventListener;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by wuhui on 2016/12/22.
 */

public class ApiApplication extends ResourceConfig {

    public ApiApplication(){
        packages("com.gomemyc");
        register(AuthContainerRequestFilter.class);
        register(GatewayApplicationEventListener.class);
    }

}

