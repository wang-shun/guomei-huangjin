package com.gomemyc.exception;


import com.gomemyc.util.InfoCode;

/**
 * 接口抛出的异常
 * Created by wuhui on 2016/8/2.
 **/
public class AppRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 2963760410777899525L;
    private InfoCode infoCode;

    public AppRuntimeException() {
        infoCode = InfoCode.SERVICE_UNAVAILABLE;

    }

    public AppRuntimeException(InfoCode infoCode) {
        this.infoCode = infoCode;
    }

    public AppRuntimeException(InfoCode infoCode, String message) {
        super(message);
        this.infoCode = infoCode;
    }

    public AppRuntimeException(String message) {
        super(message);
        infoCode = InfoCode.SERVICE_UNAVAILABLE;
    }

    public InfoCode getInfoCode() {
        return infoCode;
    }

}
