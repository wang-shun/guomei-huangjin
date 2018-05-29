package com.gomemyc.trade.constant;

public interface MQConstant {
	
	/**
	 * 推送资管消息:topic
	 */
	public static String ZGTOPIC = "zgsystem";
	
	/**
	 * 推送资管票据相关消息:tag
	 */
	public static String ZGBILLTAG = "bill";
	
	/**
	 * 推送消息到资管系统：操作类型
	 */
	public static String TYPE = "type";
	
	/**
	 * 推送消息到资管系统：产品编号
	 */
	public static String ID = "id";
	
	/**
	 * 推送消息到资管系统：产品状态
	 */
	public static String STATUSTYPE = "status";
	
	
	/*********************************************操作类型枚举***********************************************/
	/**
	 * 产品推送
	 */
	public static String SENDPRODUCT = "sendProduct";
	/**
	 * 撤回产品
	 */
	public static String CANCELPRODUCT = "cancelProduct";
	/**
	 * 产品状态
	 */
	public static String PRODUCTSTATUS = "productStatus";
	/**
	 * 还款
	 */
	public static String REPAYMENT = "repayment";
	/**
	 * 退票
	 */
	public static String RETURNTICKET = "returnTicket";
	
	/*********************************************产品状态枚举(资管系统)***********************************************/
	/**
	 * 未推送（资管初始）
	 */
	public static String STATUS0 = "0";
	/**
	 * 已推送(资管系统)
	 */
	public static String STATUS1 = "1";
	/**
	 * 已安排(资管系统)
	 */
	public static String STATUS2 = "2";
	/**
	 * 产品投放(资管系统)
	 */
	public static String STATUS3 = "3";
	/**
	 * 已满标(资管系统)
	 */
	public static String STATUS4 = "4";
	/**
	 * 流标(资管系统)
	 */
	public static String STATUS5 = "5";
	/**
	 * 已结算(资管系统)
	 */
	public static String STATUS6 = "6";
	/**
	 * 还款中(资管系统)
	 */
	public static String STATUS7 = "7";
	/**
	 * 已还清(资管系统)
	 */
	public static String STATUS8 = "8";
	/**
	 * 已存档(资管系统)
	 */
	public static String STATUS9 = "9";
	/**
	 * 撤回中(资管系统)
	 */
	public static String STATUS10 = "10";
	/**
	 * 已取消(资管系统)
	 */
	public static String STATUS11 = "11";
	
	
	
	/*********************************************推送产品状态：已结算（生产MQ）***********************************************/
	/**
	 * 产品的募集金额
	 */
	public static String AMOUNT = "amount";
	
	/**
	 * 待还款金额（本+息总金额）
	 */
	public static String PAYAMOUNT = "payAmount";
	
	/**
	 * 实际结标时间
	 */
	public static String FACTKNOTTIME = "factknotTime";

}
