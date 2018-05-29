package com.gomemyc.trade.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 返回响应
 * Created by huiwu on 2016/8/2
 *
 */
public enum GoldInfoCode {

    /**
     * 成功响应码
     */
    SUCCESS(200, "成功"),

    NOT_LOGIN(10000,"未登录或登录已过期！"),

    WRONG_PARAMETERS(30000,"参数错误"),
    
    PREPAY_NOT_EXIST(30001, "预下单信息不存在"),
    
    CHECK_NOT_EXIST(30002, "对账文件信息不存在"),
    
    INVEST_ORDER_DETAIL_NOT_EXIST(30003, "投资订单详情信息不存在"),
    
    PROCESSING_ORDER_NOT_EXIST(30004, "处理中订单信息不存在"),
    
    CONFIRM_NOT_EXIST(30005, "确认下单信息不存在"),
    
    URL_NOT_FOUND(40000,"请求服务不存在"),

    INVALID_REQUEST(40001,"不支持的请求方式"),

    SERVICE_UNAVAILABLE(50000, "操作失败，请重试"),
    
    PREPAY_ORDER_ERROR(50001, "预下单失败"),
    
    AFFIRM_ORDER_ERROR(50002, "确认下单失败"),
    
    AFFIRM_ORDER_PROCESSING(50003, "确认下单处理中"),
    
    PREPAY_ORDER_EXPIRE(50004,"预下单过期"),
    
    INVEST_INFO_ORDER_NOT_EXIST(50005,"投资订单详情不存在"),
    
    INVEST_SAVE_ERROR(50006,"保存投资信息错误"),
    
    INVEST_NOT_EXIST(50007,"投资记录不存在"),
    
    REPEAT_SUBMIT_ORDERS(50008,"重复提交订单"),
    
    BALANCE_ERROR(51000,"剩余可投资金额扣除失败"),
    
    LOCAL_FREEZE_AMOUNT_FAIL(52000,"本地冻结资金失败"),
    
    INVEST_STATUS_NOT_AVALIABLE_SYNC(53000, "投资状态必须为本地冻结和北京银行同步成功"),
    
    INVEST_INVOKE_BJ_INVEST_RESULT_NULL(53001, "同步北京银行投资接口返回结果失败"),
    
    ORDER_QUERY_FAIL(53002, "北京银行订单查询失败"),
    
    INVEST_SUCCESS_INVOKE_FAIL(53003, "北京银行同步投资失败"),
    
    INVEST_SUCCESS_INVOKE_LOCAL_FAIL(53004, "调用本地投资接口失败"),
    
    INVEST_LOAN_INVOKE_BJ_FAIL(53005,"调用北京银行批量投标接口失败"),
    
    INVEST_SUCCESS_UPDATE_INVEST_STATUS_FAIL(54000, "无法更新投资成功状态"),
    
    INVEST_UPDATE_LOCAL_FROZEN_NO_FAIL(54001, "记录冻结流水号失败"),
    
    INVEST_UPDATE_FROZEN_FAILED(54002, "更新订单状态失败"),
    
    INVEST_SUCCESS_UPDATE_BJ_STATUS(54003, "无法更新北京银行投资成功状态"),
    
    INVEST_SUCCESS_UPDATE_BJ_FAIL_STATUS(54004, "无法更新北京银行投资失败状态"),
    

    
    
    
	/**
	 * 产品响应码
	 */
	LOAN_NOT_EXITS(60000,"产品不存在"),
	LOAN_NOT_OPEN(60001,"产品未开标"),
	
	/**
	 * 账户响应吗
	 */
	ACCOUNT_ERROR(70000,"账户异常"),
	ACCOUNT_NOT_OPEN(70001,"账户未开户"),
	ACCOUNT_BALANCE_NOT_ENOUGH(70002,"账户余额不足"),
	
	/**
	 * 用户异常
	 */
	USER_ERROR(80000,"用户异常"),
	USER_NOT_EXITS(80001,"用户不存在"),
	
	/**
	 * 产品规则校验
	 */
	PRODUCT_ERROR(90000,"产品规则校验出错"),
	PRODUCT_NOT_RULES(90001,"投资金额不满足产品规则"),
	
	/**
	 * 红包规则校验
	 */
	COUPON_ERROR(91000,"红包错误"),
	COUPON_NOT_EXIST(91001,"红包不存在"),
	COUPON_NOT_RULES(91002,"红包不满足规则"),
	COUPON_FREEZE_ERROR(91003,"冻结红包失败"),
	
	/**
	 * 黄金钱包
	 */
	GOLD_ERROR(92000,"黄金钱包错误"),
	
	
	;

    private int status;

    private String msg;

    private GoldInfoCode(int status, String msg) {
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
