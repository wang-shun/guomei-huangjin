package com.gomemyc.gold.enums;

/**
 *@ClassName:GoldProductSettleStyleEnum.java 
 *@Description:
 *@author zhuyunpeng
 *@date 2017年3月29日
 */
public enum GoldProductSettleStyleEnum {

	
	/**
	 * 结标方式(是否可用奖券)
	 * Whether a lottery ticketa vailable
	 */
	PRODUCT_SWITH_IS_TICKET(1,"是否可用奖券"),
	
	/**
	 * 结标方式(用户是否可获取奖励)
	 */
	PRODUCT_SWITH_CAN_USER_GET_REWARD(2,"用户是否可获取奖励"),
	
	/**
	 * 结标方式(是否允许债转)
	 */
	PRODUCT_SWITH_IS_DEBT(4,"是否允许债转"),
	
	/**
	 * 结标方式(满标自动结标)
	 */
	PRODUCT_SWITH_FULL_AUTO_SETTLE(8,"满标自动结标"),
	
	/**
	 * 结标方式(募集期结束并满标自动结标)
	 */
	PRODUCT_SWITH_FINISH_TIME_AND_AUTO_SETTEL(16,"募集期结束并满标自动结标"),
	
	/**
	 * 结标方式(到期自动流标)
	 */
	PRODUCT_SWITH_DUE_ATUO_FLOW_LOAN(32,"到期自动流标"),
	;
	
	private int style;
	
	private String msg;

	private GoldProductSettleStyleEnum(int style, String msg) {
		this.style = style;
		this.msg = msg;
	}
	
}
