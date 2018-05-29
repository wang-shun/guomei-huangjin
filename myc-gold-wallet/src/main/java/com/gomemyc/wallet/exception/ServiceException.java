package com.gomemyc.wallet.exception;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2963760410777899525L;
    
    //状态码
    private int retCode;
    
    //信息
    private String retMsg;

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public ServiceException(int retCode, String retMsg) {
		super();
		this.retCode = retCode;
		this.retMsg = retMsg;
	}
    
}
