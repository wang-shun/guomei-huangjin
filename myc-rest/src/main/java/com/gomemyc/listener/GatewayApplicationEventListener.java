package com.gomemyc.listener;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * Created by wuhui on 2016/12/22.
 */
public class GatewayApplicationEventListener implements ApplicationEventListener {

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
                System.out.println("Application "
                        + event.getResourceConfig().getApplicationName()
                        + " was initialized.");
                break;
            case DESTROY_FINISHED:
                System.out.println("Application "
                        + event.getResourceConfig().getApplicationName());
                break;
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
       if (requestEvent.getType()== RequestEvent.Type.START){
           return new GatewayRequestEventListener();
       }
       return null;
    }
}
