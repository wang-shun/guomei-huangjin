package com.gomemyc.invest.constant;


/**
 * 标的详情缓存hash的field名称
 * @author lujixiang
 * @creaTime 2017年3月6日
 */
public interface ProductCacheConstant {
    
    String ID = "id";   // 产品id
    
    String LOAN_ID = "loanId";   // 标的ID
    
    String USER_ID = "userId";    // 产品拥有者(债转时记录转让人)
    
    String LOANTYPE_KEY = "typeKey";   // 标的类型键值
    
    String LOANTYPE_ID = "typeId"; // 标的类型id
    
    String PRODUCT_TITLE = "title";     // 产品标题
    
    String AMOUNT = "amount";   // 标的金额

    String INVEST_AMOUNT = "investAmount";  // 已投金额

    String INVEST_NUMBER = "investNum";    // 已投资笔数
    
    String DURATION_YEAR = "years";  // 期限（年）
    
    String DURATION_MONTH = "months";    // 期限（月）
    
    String DURATION_DAY = "days";    // 期限（日）
    
    String RATE = "rate";   // 基础利率
    
    String PLUSRATE = "plusRate";   // 加息利率
    
    String METHOD = "method";   // 还款方式

    String STATUS = "status";   // 产品状态
    
    String ISUSECOUPON = "useCoupon";     // 是否可用奖券
    
    String DEBTISTRANSFER = "debtTransfer"; // 是否支持债转
    
    String INVESTRULE_MINAMOUNT = "minAmount";    // 最小投资金额(单次)
    
    String INVESTRULE_MAXAMOUNT = "maxAmount";    // 最大投资金额(单次)
    
    String INVESTRULE_TOTAL_MAXAMOUNT = "maxTotalAmount"; // 最大投资总额(单个产品)
    
    String INVESTRULE_STEPAMOUNT = "stepAmount";  // 投资金额增量
    
    String INVESTRULE_MAXTIMES = "investMaxTimes";  // 最大投资次数(单个产品)
    
    String INVESTRULE_LOANTYPE_TOTAL_MAXAMOUNT = "typeMaxTotalAmount"; // 标的类型的最大投资总额
    
    String INVESTRULE_LOANTYPE_MAXTIMES = "typeMaxTimes";  // 标的类型的最大投资次数
    
    String ROOTPRODUCTID = "rootProductId";   // 原产品id
    
    String TIMEOPEN = "openTime";   // 开标时间
    
    String DATEEND = "endTime";  //募集截止时间
    
    String TIMEFINISHED = "finishTime";   // 满标日期
    
    String TIMESETTLED = "settleTime"; // 结标日期
    
    String TIMECLEARED = "clearTime"; // 结清日期
    
    String PRODUCTDETAIL = "productDetail";     // 产品详情
    
    String PROJECTDETAIL = "projectDetail";   // 项目详情
    
    String DEBTPLANID = "debtPlanId";   // 债转方案
    
    String VALUEDATE = "valueTime"; // 起息日
    
    String DEBTED = "debted"; // 起息日
    
    String DUEDATE = "dueDate"; // 应还款日
    
    String DEBTASSIGNDATE = "debtAssignDate";   // 持有多少天可转让
    
    String DEBTPLANONEORMANY = "peopleCount";   // 转让人数要求

    String RISK_TYPE = "riskType";  // 风险等级
    
    String PRODUCT_TAG_KEY = "productTagKey"; // 产品标签键
    
    String PRODUCT_TAG_VALUE = "productTagValue";  // 产品标签值
    
    String PRODUCT_TAG_COLOR = "productTagColor";  // 产品标签颜色

    String ACCEPTANCEBANK = "acceptanceBank";//票据-承兑银行
}
