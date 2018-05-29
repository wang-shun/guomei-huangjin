package com.gomemyc.wallet.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 返回响应
 * Created by zhuyunpeng on 2017/03/12
 *
 */
public enum GoldInfoCode {
	

    SUCCESS(000000, "成功"),

    SYSTEM_ERROR(999999,"系统错误"),

    SIGN_ERROR(000001,"验证签名错误"),
	
	REQUEST_PARAMETER_ERROR(100001,"请求参数错误"),

	BALANCE_DEFICIENCY(100002,"余额不足"),
	
	ORDER_REPEAT_COMMIT(100003,"订单重复提交"),
	
	USER_NOT_FIND(200001,"商户账户不存在或已锁定"),
	
	USER_BALANCE_DEFICIENCY(200002,"商户余额不足"),
	
	USER_ERROR(200003,"商户账务处理失败"),
	
	USER_FROST(200004,"账户被冻结");

    private int retCode;

    private String retMsg;

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
	
    private GoldInfoCode(int retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public int getRetCode() {
		return retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

}
