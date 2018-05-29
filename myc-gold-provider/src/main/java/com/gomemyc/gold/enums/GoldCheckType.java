package com.gomemyc.gold.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @ClassName: GoldCheckTypeEnum
 * @Description: 对账文件检查状态枚举
 * @author LiuQiangBin
 * @date 2017年3月20日
 *
*/
public enum GoldCheckType {
	
	/**
	 * 对账文件检查状态
	 */
	CHECK_TYPE_GOLD(1,"黄金钱包对账数据"),
	
	/**
	 * 对账文件检查状态
	 */
	CHECK_TYPE_GOME(2,"国美对账数据"),
	;

    private int status;

    private String msg;

    private GoldCheckType(int status, String msg) {
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
