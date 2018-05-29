package com.gomemyc.gold.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @ClassName: GoldOrderStatusEnum
 * @Description: 下单订单状态码
 * @author liuqiangbin
 * @date 2017年3月17日
 *
*/
public enum GoldOrderStatusEnum {
	
	/**
	 * 下单状态(预下单)
	 */
	ORDERSTATUS_PREPAY(1,"预下单"),
	/**
	 * 下单状态(确认下单)
	 */
	ORDERSTATUS_CONFIRM(2,"确认下单"),
	/**
	 * 订单状态(预下单成功)
	 */
	ORDERSTATUS_PREPAY_SUCCESS(0,"预下单成功"),
	/**
	 * 订单状态(预下单成功，确认下单处理中)
	 */
	ORDERSTATUS_CONFIRM_PROCESSING(1,"确认下单处理中"),
	/**
	 * 订单状态(确认下单成功)
	 */
	ORDERSTATUS_CONFIRM_SUCCESS(2,"确认下单成功"),
	/**
	 * 订单状态(确认下单失败)
	 */
	ORDERSTATUS_CONFIRM_FAIL(3,"确认下单失败"),
	/**
	 * 订单状态(预下单过期)
	 */
	ORDERSTATUS_PREPAY_EXPIRE(4,"预下单过期"),
	/**
	 * 订单状态(预下单成功，确认下单失败)
	 */
	ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_ERROR(5,"预下单成功，确认下单失败"),
	/**
	 * 订单状态(预下单成功，确认下单成功)
	 */
	ORDERSTATUS_PREPAY_SUCCESS_CONFIRM_SUCCESS(6,"预下单成功，确认下单成功"),
	
	;

    private int status;

    private String msg;

    private GoldOrderStatusEnum(int status, String msg) {
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
