package com.gomemyc.gold.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @ClassName: GoldCouponStatusType
 * @Description: 黄金钱包红包状态
 * @author LiuQiangBin
 * @date 2017年3月29日
 *
*/
public enum GoldCouponStatusType {
	
	/**
	 * 红包状态(不可用)
	 */
	DISABLED(0,"红包不可用"),
	
	/**
	 * 红包状态(可用)
	 */
	AVAILABLE(1,"红包可用"),
	;

    private int status;

    private String msg;

    private GoldCouponStatusType(int status, String msg) {
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
