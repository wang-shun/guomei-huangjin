package com.gomemyc.wallet.constant;

/**
 * @author zhuyunpeng
 * @description: 黄金钱包的请求地址 V如：/queryPrice
 * @date 2017年3月8日
 */
public class WalletUrl {
	
   
    /**
     * 2.1查询实时金价请求地址
     */
    public static String QUERY_THE_REAL_GOLD_PRICE = "/queryPrice";
    
    /**
     * 2.2账户信息查询请求地址
     */
    public static String QUERY_ACCOUNT_INFORMATION = "/queryMyAccount";
    
    /**
     * 2.3定期金产品查询请求地址
     */
    public static String QUERY_REGULAR_GOLD_PRODUCT = "/queryTimeProductList";
    
    /**
     * 2.4购买定期金请求地址
     */
    public static String BUY_REGULAR_GOLD = "/buyTimeGold";
    
    /**
     * 2.5购买定期金确认请求地址
     */
    public static String CONFIRM_BUY_REGULAR_GOLD = "/confirmBuyTimeGold";
    
    /**
     * 2.6购买定期金交易结果查询请求地址
     */
    public static String QUERY_BUY_REGULAR_GOLD_ORDER = "/queryBuyTimeOrder";
    
    /**
     * 2.7.1买定期金对账文件查询请求地址
     */
    public static String CHECK_TIME_ORDER = "/checkTimeOrder";
    
    /**
     * 2.7.2定期金到期对账文件查询请求地址
     */
    public static String CHECK_EXPIRE_ORDER = "/checkExpireOrder";
    
    /**
     * 2.7.3每天利息对账文件查询请求地址
     */
    public static String CHECK_DAILY_INTEREST = "/checkDailyInterest/";
    
    /**
     * 2.7.4定期到期利息汇总对账文件请求地址
     */
    public static String CHECK_SUM_INTEREST = "/checkSumInterest/";
    
}
