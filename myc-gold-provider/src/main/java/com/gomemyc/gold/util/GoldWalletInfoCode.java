package com.gomemyc.gold.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 黄金钱包接口返回响应
 * Created by liuqiangbin on 2017/03/16
 *
 */
public enum GoldWalletInfoCode {

    /**
     * 成功响应码
     */
    SUCCESS(000000, "成功"),

    SYSTEM_ERROR(999999,"系统错误！"),

    SIGN_ERROR(000001,"验证签名错误"),

    REQUEST_PARAM_ERROR(100001,"请求参数错误"),

    NOT_SUFFICIENT_FUNDS(100002,"余额不足"),

    REPEAT_ORDERS_SUBMITTED(100003, "订单重复提交"),

    ACCOUNT_NOT_EXIST_OR_LOCKED(200001, "商户账户不存在或已锁定"),
	
    ACCOUNT_NOT_SUFFICIENT_FUNDS(200002, "商户余额不足"),

    BUSINESS_ACCOUNT_TREATMENT_FAILURE(200003, "商户账务处理失败"),

    ACCOUNT_SUSPENDED(200004, "账户被冻结");

    private int status;

    private String msg;

    private GoldWalletInfoCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
	
}
