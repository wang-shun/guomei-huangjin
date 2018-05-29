package com.gomemyc.invest.bridge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.query.SortCriterion;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.constant.ProductCacheConstant;
import com.gomemyc.invest.dao.DebtPlanDao;
import com.gomemyc.invest.dao.LoanTypeRulesDao;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.entity.DebtPlan;
import com.gomemyc.invest.entity.LoanTypeRules;
import com.gomemyc.invest.entity.Product;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.entity.ProductRules;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.OrderPlan;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.ProductSwitch;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.model.RedisProduct;
import com.gomemyc.invest.service.impl.ProductServiceImpl;
import com.gomemyc.invest.util.DateUtil;
import com.gomemyc.invest.util.EnumsUtil;
import com.gomemyc.trade.dto.LoanRepaymentDTO;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.trade.service.LoanRepaymentService;
import com.gomemyc.util.BeanMapper;

/**
 * 标的redis业务层
 * @author lujixiang
 * @creaTime 2017年3月6日
 */
@Component
public class LoanRedisBridge {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    @Autowired 
    @Qualifier("redisLoanTemplate")
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ProductRegularDao productRegularDao;
    
    @Autowired
    private ProductBillDao productBillDao;
    
    @Autowired
    private ProductRulesDao productRulesDao;
    
    @Autowired
    private LoanTypeRulesDao loanTypeRulesDao;
    
    @Autowired
    private DebtPlanDao debtPlanDao;
    
    @Reference
    private InvestService investService;
    
    @Reference
    private LoanRepaymentService loanRepaymentService;
    
    // 产品列表显示字段
    public static List<String> PRODUCT_LIST_FIELD = Arrays.asList(ProductCacheConstant.ID,                  // 产品id
                                                                  ProductCacheConstant.PRODUCT_TITLE,       // 产品标题
                                                                  ProductCacheConstant.LOANTYPE_KEY,        // 标的类型键值
                                                                  ProductCacheConstant.AMOUNT,   // 标的金额
                                                                  ProductCacheConstant.INVEST_AMOUNT,  // 已投金额
                                                                  ProductCacheConstant.INVEST_NUMBER,    // 已投资笔数
                                                                  ProductCacheConstant.STATUS,   // 产品状态
                                                                  ProductCacheConstant.RATE,   // 基础利率
                                                                  ProductCacheConstant.PLUSRATE,   // 加息利率
                                                                  ProductCacheConstant.METHOD,   // 还款方式
                                                                  ProductCacheConstant.DURATION_YEAR,  // 期限（年）
                                                                  ProductCacheConstant.DURATION_MONTH,    // 期限（月）
                                                                  ProductCacheConstant.DURATION_DAY,    // 期限（日）
                                                                  ProductCacheConstant.TIMEOPEN,   // 开标时间
                                                                  ProductCacheConstant.ISUSECOUPON,     // 是否可用奖券
                                                                  ProductCacheConstant.DEBTISTRANSFER, // 是否支持债转
                                                                  ProductCacheConstant.ACCEPTANCEBANK,//票据-承兑银行
                                                                  ProductCacheConstant.RISK_TYPE,  // 风险等级
                                                                  ProductCacheConstant.PRODUCT_TAG_KEY, // 产品标签键
                                                                  ProductCacheConstant.PRODUCT_TAG_VALUE,  // 产品标签值
                                                                  ProductCacheConstant.PRODUCT_TAG_COLOR,  // 产品标签颜色
                                                                  ProductCacheConstant.INVESTRULE_STEPAMOUNT, // 递增金额
                                                                  ProductCacheConstant.INVESTRULE_MINAMOUNT); // 起投金额
    
    
    // redis的标的详情前缀
    public static final String PREFIX = "PRODUCT_INFO_%s"; 
    
    // 标的集合前缀
    private static final String PRODUCT_SET_PREFIX = "PRODUCT_SET_PRODUCT_%1$s_STATUS_%2$s_ORDER_%3$s_LIMIT_%4$s_%5$s"; 
    
    // 标的集合总数统计
    private static final String PRODUCT_SET_COUNT_PREFIX = "PRODUCT_SET_PRODUCT_%1$s_STATUS_%2$s_COUNT";
    
    private static final String CLEAR_CACHE_PRODUCT_PREFIX = "PRODUCT_SET_*";
    
    /**
     * 获取标的详情,优先从redis读取,
     * 如果读取不到,从数据库读取,并更新redis
     * @param productId
     * @return
     * @author lujixiang
     * @date 2017年3月6日
     *
     */
    public RedisProduct getProduct(String productId) throws Exception{
        
        if (StringUtils.isBlank(productId)) {
            return null;
        }
        
        // 从redis中获取
        RedisProduct redisProduct = null;
        try {
            redisProduct = this.getProductInRedis(productId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (null != redisProduct) {
            return redisProduct;
        }
        
        Product product = null;
        if (productId.startsWith("dq-")) {  // 定期产品
            product = this.getProductRegularFromMaster(productId);
            redisProduct = null == product ? null : BeanMapper.map((ProductRegular)product, RedisProduct.class);
            
        }else if (productId.startsWith("pj-")){ //票据
        	product = this.getProductBillFromMaster(productId);
            redisProduct = null == product ? null : BeanMapper.map((ProductBill)product, RedisProduct.class);
        }
        
        if (null == redisProduct) {
            return null;
        }
        
        // 绑定规则
        this.bindProductRules(product, redisProduct);
        
        // 异步缓存单个标的
        this.asyncPutProductInRedis(redisProduct);
        
        return redisProduct;
        
    }
    
    /**
     * 从主库中获取
     * @param productId
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private ProductRegular getProductRegularFromMaster(String productId){
        
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        return productRegularDao.findById(productId);
    }
    
    
    /**
     * 从主库中获取
     * @param productId
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private ProductBill getProductBillFromMaster(String productId){
        
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        return productBillDao.findById(productId);
    }
    
    /**
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月8日
     *
     */
    private RedisProduct getProductInRedis(String productId) throws Exception{
        
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps(String.format(PREFIX, productId));
        
        Map<String, String> productMap = boundHashOperations.entries();
        
        return null == productMap || productMap.isEmpty() ? null : this.getProductFromMap(productMap);
        
    }
    
    
    /**
     * 从redis的map解析封装product
     * @param loanId
     * @return
     * @author lujixiang
     * @date 2017年3月6日
     *
     */
    private RedisProduct getProductFromMap(Map<String, String> map){
        
        RedisProduct redisProduct = new RedisProduct();
        
        // 设置产品基础属性值  
        // 产品id
        redisProduct.setId(map.get(ProductCacheConstant.ID));
        // 标的id
        redisProduct.setLoanId(map.get(ProductCacheConstant.LOAN_ID));
        // 借款人
        redisProduct.setUserId(map.get(ProductCacheConstant.USER_ID));
        // 产品标题
        redisProduct.setTitle(map.get(ProductCacheConstant.PRODUCT_TITLE));
        // 标的类型键值
        redisProduct.setTypeKey(map.get(ProductCacheConstant.LOANTYPE_KEY));
        // 标的类型id
        redisProduct.setTypeId(map.get(ProductCacheConstant.LOANTYPE_ID));
        // 发布金额
        redisProduct.setAmount(StringUtils.isBlank(map.get(ProductCacheConstant.AMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.AMOUNT)) );
        // 已投金额
        redisProduct.setInvestAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVEST_AMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVEST_AMOUNT)) );
        // 已投笔数
        redisProduct.setInvestNum(StringUtils.isBlank(map.get(ProductCacheConstant.INVEST_NUMBER)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.INVEST_NUMBER)));
        // 产品状态
        redisProduct.setStatus(StringUtils.isBlank(map.get(ProductCacheConstant.STATUS)) ? null : EnumsUtil.getEnumByNameOrNull(ProductStatus.class, map.get(ProductCacheConstant.STATUS)));
        // 最小投资金额(单次)
        redisProduct.setMinAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_MINAMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVESTRULE_MINAMOUNT)) );
        // 最大投资金额(单次)
        redisProduct.setMaxAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_MAXAMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVESTRULE_MAXAMOUNT)) );
        // 最大投资总额(单个产品)
        redisProduct.setMaxTotalAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_TOTAL_MAXAMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVESTRULE_TOTAL_MAXAMOUNT)));
        // 投资增量
        redisProduct.setStepAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_STEPAMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVESTRULE_STEPAMOUNT)));
        // 最大投资次数
        redisProduct.setInvestMaxTimes(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_MAXTIMES)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.INVESTRULE_MAXTIMES)));
        // 标的类型的最大投资额
        redisProduct.setTypeMaxTotalAmount(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_LOANTYPE_TOTAL_MAXAMOUNT)) ? BigDecimal.ZERO : new BigDecimal(map.get(ProductCacheConstant.INVESTRULE_LOANTYPE_TOTAL_MAXAMOUNT)));
        // 标的类型的投资次数
        redisProduct.setTypeMaxTimes(StringUtils.isBlank(map.get(ProductCacheConstant.INVESTRULE_LOANTYPE_MAXTIMES)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.INVESTRULE_LOANTYPE_MAXTIMES)));
        // 基础利率
        redisProduct.setRate(StringUtils.isBlank(map.get(ProductCacheConstant.RATE)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.RATE)));
        // 加息利率
        redisProduct.setPlusRate(StringUtils.isBlank(map.get(ProductCacheConstant.PLUSRATE)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.PLUSRATE)));
        // 还款方式
        redisProduct.setMethod(StringUtils.isBlank(map.get(ProductCacheConstant.METHOD)) ? null : EnumsUtil.getEnumByNameOrNull(RepaymentMethod.class, map.get(ProductCacheConstant.METHOD)));
        // 期限(年)
        redisProduct.setYears(StringUtils.isBlank(map.get(ProductCacheConstant.DURATION_YEAR)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.DURATION_YEAR)));
        // 期限(月)
        redisProduct.setMonths(StringUtils.isBlank(map.get(ProductCacheConstant.DURATION_MONTH)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.DURATION_MONTH)));
        // 期限(日)
        redisProduct.setDays(StringUtils.isBlank(map.get(ProductCacheConstant.DURATION_DAY)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.DURATION_DAY)));
        // 开标时间
        redisProduct.setOpenTime(StringUtils.isBlank(map.get(ProductCacheConstant.TIMEOPEN)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.TIMEOPEN))));
        // 是否可用奖券
        redisProduct.setUseCoupon("1".equals(map.get(ProductCacheConstant.ISUSECOUPON)) ? true : false);
        // 产品详情
        redisProduct.setProductDetail(map.get(ProductCacheConstant.PRODUCTDETAIL));
        // 项目详情
        redisProduct.setProjectDetail(map.get(ProductCacheConstant.PROJECTDETAIL));
        // 起息日
        redisProduct.setValueTime(StringUtils.isBlank(map.get(ProductCacheConstant.VALUEDATE)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.VALUEDATE))));
        // 应还款日
        redisProduct.setDueDate(StringUtils.isBlank(map.get(ProductCacheConstant.DUEDATE)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.DUEDATE))));
        // 结标时间
        redisProduct.setSettleTime(StringUtils.isBlank(map.get(ProductCacheConstant.TIMESETTLED)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.TIMESETTLED))));
        // 满标时间
        redisProduct.setFinishTime(StringUtils.isBlank(map.get(ProductCacheConstant.TIMEFINISHED)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.TIMEFINISHED))));
        // 结清时间
        redisProduct.setClearTime(StringUtils.isBlank(map.get(ProductCacheConstant.TIMECLEARED)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.TIMECLEARED))));
        // 是否支持债转
        redisProduct.setDebtTransfer("1".equals(map.get(ProductCacheConstant.DEBTISTRANSFER)) ? true : false);
        // 债转方案
        redisProduct.setDebtPlanId(map.get(ProductCacheConstant.DEBTPLANID));
        // 持有多少天可转让
        redisProduct.setDebtAssignDate(StringUtils.isBlank(map.get(ProductCacheConstant.DEBTASSIGNDATE)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.DEBTASSIGNDATE)));
        // 原产品id
        redisProduct.setRootProductId(map.get(ProductCacheConstant.ROOTPRODUCTID));
        // 转让人数要求
//        redisProduct.setC
        // 募集结束时间
        redisProduct.setEndTime(StringUtils.isBlank(map.get(ProductCacheConstant.DATEEND)) ? null : new Date(Long.parseLong(map.get(ProductCacheConstant.DATEEND))));
        // 风险等级
        redisProduct.setRiskType(StringUtils.isBlank(map.get(ProductCacheConstant.RISK_TYPE)) ? 0 : Integer.parseInt(map.get(ProductCacheConstant.RISK_TYPE)));
        // 产品标签键
        redisProduct.setProductTagKey(map.get(ProductCacheConstant.PRODUCT_TAG_KEY));
        // 产品标签值
        redisProduct.setProductTagValue(map.get(ProductCacheConstant.PRODUCT_TAG_VALUE));
        // 产品标签颜色
        redisProduct.setProductTagColor(map.get(ProductCacheConstant.PRODUCT_TAG_COLOR));
        // 是否债转标
        redisProduct.setDebted("1".equals(map.get(ProductCacheConstant.DEBTED)) ? true : false);
        
        // 票据
        if ("PJ".equals(redisProduct.getTypeKey())) {
            // 承兑银行
            redisProduct.setAcceptanceBank(map.get(ProductCacheConstant.ACCEPTANCEBANK));
        }
        
        return redisProduct;
    }
    
    
    
    
    @Async
    private void asyncPutProductInRedis(RedisProduct product){
        
        if (null == product) {
            return;
        }
        
        logger.info("ready to async put product in redis, productId = {}", product.getId());
        
        try {
            this.putProductInRedis(product);
            
        } catch (Exception ex) {
            logger.error("put product in redis failed, there is a exception ", ex);
        }
    }
    /**
     * 将标的详情放到redis中
     * @param product
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月6日
     *
     */
    private boolean putProductInRedis(RedisProduct product) throws Exception{
        
            
        HashOperations<String, String, String> hashOperation = redisTemplate.opsForHash();
        hashOperation.putAll(String.format(PREFIX, product.getId()), this.getRedisProductMap(product));
        return true;
    }
    
    
    /**
     * 获取产品详情的redis结构map
     * @param product
     * @return
     * @author lujixiang
     * @date 2017年3月6日
     *
     */
    private Map<String, String> getRedisProductMap(RedisProduct product){
        
        Map<String, String> map = new HashMap<String, String>();
        
        // 理财基本字段缓存
        // 产品ID
        map.put(ProductCacheConstant.ID, null == product.getId() ? "" : product.getId());
        // 标的id
        map.put(ProductCacheConstant.LOAN_ID, null == product.getLoanId() ? "" : product.getLoanId());
        // 借款人(债转时记录转让人)
        map.put(ProductCacheConstant.USER_ID, null == product.getUserId() ? "" : product.getUserId());
        // 标题
        map.put(ProductCacheConstant.PRODUCT_TITLE,  null == product.getTitle() ? "" : product.getTitle());
        // 标的类型键值
        map.put(ProductCacheConstant.LOANTYPE_KEY,  null == product.getTypeKey() ? "" : product.getTypeKey());
        // 标的类型id
        map.put(ProductCacheConstant.LOANTYPE_ID,  null == product.getTypeId() ? "" : product.getTypeId());
        // 发布金额
        map.put(ProductCacheConstant.AMOUNT, null == product.getAmount() ? "0" : product.getAmount().toPlainString());
        // 已投金额
        map.put(ProductCacheConstant.INVEST_AMOUNT, null == product.getInvestAmount() ? "0" : product.getInvestAmount().toPlainString());
        // 已投笔数
        map.put(ProductCacheConstant.INVEST_NUMBER, String.valueOf(product.getInvestNum()));
        // 产品状态
        map.put(ProductCacheConstant.STATUS, null == product.getStatus() ? "" : product.getStatus().name());
        // 最小投资额(单次)
        map.put(ProductCacheConstant.INVESTRULE_MINAMOUNT, null == product.getMinAmount() ? "0" : product.getMinAmount().toPlainString());
        // 最大投资金额(单次)
        map.put(ProductCacheConstant.INVESTRULE_MAXAMOUNT, null == product.getMaxAmount() ? "0" : product.getMaxAmount().toPlainString());
        // 最大投资总额(单个产品)
        map.put(ProductCacheConstant.INVESTRULE_TOTAL_MAXAMOUNT, null == product.getMaxTotalAmount() ? "0" : product.getMaxTotalAmount().toPlainString());
        // 投资金额增量
        map.put(ProductCacheConstant.INVESTRULE_STEPAMOUNT, null == product.getStepAmount() ? "0" : product.getStepAmount().toPlainString());
        // 最大投资次数(单个产品)
        map.put(ProductCacheConstant.INVESTRULE_MAXTIMES, String.valueOf(product.getInvestMaxTimes()));
        // 标的类型的最大投资额
        map.put(ProductCacheConstant.INVESTRULE_LOANTYPE_TOTAL_MAXAMOUNT, null == product.getTypeMaxTotalAmount() ? "0" : product.getTypeMaxTotalAmount().toPlainString());
        // 标的类型的最大投资次数
        map.put(ProductCacheConstant.INVESTRULE_LOANTYPE_MAXTIMES, String.valueOf(product.getTypeMaxTimes()));
        // 基础利率
        map.put(ProductCacheConstant.RATE, String.valueOf(product.getRate()));
        // 加息利率
        map.put(ProductCacheConstant.PLUSRATE, String.valueOf(product.getPlusRate()));
        // 还款方式
        map.put(ProductCacheConstant.METHOD, null == product.getMethod() ? "" : product.getMethod().name());
        // 期限（年）
        map.put(ProductCacheConstant.DURATION_YEAR, String.valueOf(product.getYears()));
        // 期限（月）
        map.put(ProductCacheConstant.DURATION_MONTH, String.valueOf(product.getMonths()));
        // 期限（日）
        map.put(ProductCacheConstant.DURATION_DAY, String.valueOf(product.getDays()));
        // 开标时间
        map.put(ProductCacheConstant.TIMEOPEN, null == product.getOpenTime() ? "" : String.valueOf(product.getOpenTime().getTime()));
        // 募集截止时间
        map.put(ProductCacheConstant.DATEEND, null == product.getEndTime() ? "" : String.valueOf(product.getEndTime().getTime()));
        // 是否可用奖券
        map.put(ProductCacheConstant.ISUSECOUPON, product.isUseCoupon() ? "1" : "0");
        // 产品详情
        map.put(ProductCacheConstant.PRODUCTDETAIL, null ==  product.getProductDetail() ? "" : product.getProductDetail());
        // 项目详情
        map.put(ProductCacheConstant.PROJECTDETAIL, null ==  product.getProjectDetail() ? "" : product.getProjectDetail());
        // 起息日
        map.put(ProductCacheConstant.VALUEDATE, null == product.getValueTime() ? "" : String.valueOf(product.getValueTime().getTime()));
        // 还款日
        map.put(ProductCacheConstant.DUEDATE, null == product.getDueDate() ? "" : String.valueOf(product.getDueDate().getTime()));
        // 结标日期
        map.put(ProductCacheConstant.TIMESETTLED, null == product.getSettleTime() ? "" : String.valueOf(product.getSettleTime().getTime()));
        // 满标时间
        map.put(ProductCacheConstant.TIMEFINISHED, null == product.getFinishTime() ? "" : String.valueOf(product.getFinishTime().getTime()));
        // 结清时间
        map.put(ProductCacheConstant.TIMECLEARED, null == product.getClearTime() ? "" : String.valueOf(product.getClearTime().getTime()));
        // 是否支持债转
        map.put(ProductCacheConstant.DEBTISTRANSFER, product.isDebtTransfer() ? "1" : "0");
        // 债转方案
        map.put(ProductCacheConstant.DEBTPLANID, null == product.getDebtPlanId() ? "" : product.getDebtPlanId());
        // 持有多少天可转让
        map.put(ProductCacheConstant.DEBTASSIGNDATE, String.valueOf(product.getDebtAssignDate()));
        // 原产品id
        map.put(ProductCacheConstant.ROOTPRODUCTID, null == product.getRootProductId() ? "" : product.getRootProductId());
        // 转让人数要求
//        map.put(ProductCacheConstant.DEBTPLANONEORMANY, product.getRootProductId());
        // 风险等级
        map.put(ProductCacheConstant.RISK_TYPE, String.valueOf(product.getRiskType()));
        // 产品标签键
        map.put(ProductCacheConstant.PRODUCT_TAG_KEY, null == product.getProductTagKey() ? "" : product.getProductTagKey());
        // 产品标签值
        map.put(ProductCacheConstant.PRODUCT_TAG_VALUE, null == product.getProductTagValue() ? "" : product.getProductTagValue());
        // 产品标签颜色
        map.put(ProductCacheConstant.PRODUCT_TAG_COLOR, null == product.getProductTagColor() ? "" : product.getProductTagColor());
        // 产品标签颜色
        map.put(ProductCacheConstant.DEBTED, product.isDebted() ? "1" : "0");
        
        // 票据
        if ("PJ".equals(product.getTypeKey())) {
            
            // 承兑银行
            map.put(ProductCacheConstant.ACCEPTANCEBANK, null == product.getAcceptanceBank() ? "" : product.getAcceptanceBank());
        }
        
        return map;
    }
    
    /**
     * 封装redis传输对象
     * @param product
     * @return
     * @author lujixiang
     * @date 2017年3月10日
     *
     */
    public RedisProduct bindProductRules(Product product, RedisProduct redisProduct){
        
        // 获取产品规则 
        List<ProductRules> productRules = productRulesDao.listByProductId(product.getId());
        
        // 获取标的类型规则
        List<LoanTypeRules> typeRules = loanTypeRulesDao.listByLoanTypeId(product.getTypeId());
        
        // 是否可用奖券
        redisProduct.setUseCoupon((product.getProductSwitch() & ProductSwitch.IS_COUPON.getIndex().intValue())  
                                     == ProductSwitch.IS_COUPON.getIndex().intValue() ? true : false);
        // 是否支持债转
        redisProduct.setDebtTransfer((product.getProductSwitch() & ProductSwitch.IS_DEBT.getIndex().intValue())  
                                        == ProductSwitch.IS_DEBT.getIndex().intValue() ? true : false);
        // 设置产品规则
        if (null != productRules && !productRules.isEmpty()) {
            
            for(ProductRules rule : productRules){
                // 捕捉异常,不能让规则异常导致无法加载缓存
                try {
                        switch (rule.getType()) {
                        
                        case MIN_SINGLE_AMOUNT: // 最小单次投资额
                            redisProduct.setMinAmount(null == rule.getValue() ? BigDecimal.ZERO : new BigDecimal(rule.getValue()));
                            break;
                        case MAX_SINGLE_AMOUNT: // 最大单次投资额
                            redisProduct.setMaxAmount(null == rule.getValue() ? BigDecimal.ZERO : new BigDecimal(rule.getValue()));
                            break;
                        case STEP_AMOUNT: // 投资增量
                            redisProduct.setStepAmount(null == rule.getValue() ? BigDecimal.ZERO : new BigDecimal(rule.getValue()));
                            break;
                        case MAX_TOTAL_AMOUNT: // 单个产品最大投资总额
                            redisProduct.setMaxTotalAmount(null == rule.getValue() ? BigDecimal.ZERO : new BigDecimal(rule.getValue()));
                            break;
                        case MAX_TIME: // 单个产品最大投资次数
                            redisProduct.setInvestMaxTimes(null == rule.getValue() ? 0 : Integer.parseInt(rule.getValue()));
                            break;
                        default:
                            break;
                    }
                    
                } catch (Exception ex) {
                    logger.error("bind product rules failed, there is a exception ", ex);
                }
            }
        }
        
        // 设置标的类型规则
        if (null != typeRules && !typeRules.isEmpty()) {
            
            for(LoanTypeRules rule : typeRules){
                // 捕捉异常,不能让规则异常导致无法加载缓存
                try {
                        switch (rule.getType()) {
                        
                        case MAX_TOTAL_AMOUNT: // 标的类型的最大投资总额
                            redisProduct.setTypeMaxTotalAmount(null == rule.getValue() ? BigDecimal.ZERO : new BigDecimal(rule.getValue()));
                            break;
                        case MAX_TIME: // 标的类型的最大投资次数
                            redisProduct.setTypeMaxTimes(null == rule.getValue() ? 0 : Integer.parseInt(rule.getValue()));
                            break;
                        default:
                            break;
                    }
                    
                } catch (Exception ex) {
                    logger.error("bind product rules failed, there is a exception ", ex);
                }
            }
        }
        
        if (product instanceof ProductBill ) {
            
            ProductBill productBill = (ProductBill) product;
            redisProduct.setAcceptanceBank(productBill.getAcceptanceBank());
        }
        
        // 设置持有天数
        try {
        	if(!StringUtils.isBlank(product.getDebtPlanId())){
        		DebtPlan debtPlan = debtPlanDao.findById(product.getDebtPlanId());
        		if(null != debtPlan){
        			redisProduct.setDebtAssignDate(product.isDebted() ? debtPlan.getSecondDebtDate() : debtPlan.getFirstDebtDate());
        		}
        	}
			
		} catch (Exception ex) {
			logger.error("set debt assign date failed, there is a exception ", ex);
		}
        
        // 统计投资金额和投资笔数
        try {
            
            BigDecimal investAmount = investService.sumInvestAmountByProductIdAndStatus(product.getId(), 
                                                                                        InvestStatus.getEffectiveInvestStatusArray());
            logger.info("sum investAmount success, productId = {}, the investAmount = {}", product.getId(), investAmount);
            
            Long count = investService.countInvestNumByProductAndStatus(product.getId(), 
                                                                        InvestStatus.getEffectiveInvestStatusArray());
            logger.info("count investNum success, productId = {}, the investNum = {}", product.getId(), count);
        
            redisProduct.setInvestAmount(null == investAmount ? BigDecimal.ZERO : investAmount);
            
            redisProduct.setInvestNum(null == count ? 0 : count.intValue());
        
        } catch (Exception ex) {
            logger.error("sum and count invest failed, there is a exception: ", ex);
        }
        
        try {
            
            // 定期产品介绍模板替换
            if (product instanceof ProductRegular ) {
                redisProduct.setProductDetail(this.getProductDetail(redisProduct));
            }
            
        } catch (Exception ex) {
            logger.error("get product detail failed, there is a exception: ", ex);
        }
        
        try {
            
            if (ProductStatus.SCHEDULED.equals(product.getStatus()) || 
                    ProductStatus.OPENED.equals(product.getStatus()) ||
                    ProductStatus.FINISHED.equals(product.getStatus()) ||
                    ProductStatus.FAILED.equals(product.getStatus())) {
                
                // 起息时间: 募集时间+1天
                Calendar valueDateCalendar = Calendar.getInstance();
                valueDateCalendar.setTime(product.getEndTime());
                valueDateCalendar.add(Calendar.DATE, 1);
                redisProduct.setValueTime(valueDateCalendar.getTime());
                
                redisProduct.setDueDate(DateUtil.offset(valueDateCalendar.getTime(), 
                                                        DateUtil.getTotalDays(product.getYears(), 
                                                                              product.getMonths(),
                                                                              product.getDays())
                                       ));
                
            }else{
                redisProduct.setValueTime(product.getValueTime());
                LoanRepaymentDTO loanRepayment = loanRepaymentService.findByLoanId(product.getLoanId());
                redisProduct.setDueDate(null == loanRepayment ? null : loanRepayment.getDueDate());
            }
            
        } catch (Exception ex) {
            logger.error("fail to get valueDate and dueDate, there is a exception : ", ex);
        }
        
        
        
        
        return redisProduct;
        
    }
    
    /**
     * 获取产品介绍
     * @param product
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    private String getProductDetail(RedisProduct product){
        
        if (StringUtils.isBlank(product.getProductDetail())) {
            return null;
        }
        
        
        Date endDate = null; // 募集结束时间
        Date valueDate = null; //起息日
        
        if (ProductStatus.SCHEDULED.equals(product.getStatus()) || 
                ProductStatus.OPENED.equals(product.getStatus()) ||
                ProductStatus.FINISHED.equals(product.getStatus()) ||
                ProductStatus.FAILED.equals(product.getStatus())) {
            
            endDate = product.getEndTime();
            
            // 起息时间: 募集时间+1天
            Calendar valueDateCalendar = Calendar.getInstance();
            valueDateCalendar.setTime(endDate);
            valueDateCalendar.add(Calendar.DATE, 1);
            valueDate = valueDateCalendar.getTime();
            
        }else{
            endDate = product.getSettleTime();
            valueDate = product.getValueTime();
        }
        
        // 募集时间描述
        String investTimeStr = DateUtil.toYYYYMMDDString(product.getOpenTime()) + 
                            "至" +
                            DateUtil.toYYYYMMDDString(endDate);
        // 起息时间描述
        String valueDateStr = DateUtil.toYYYYMMDDString(valueDate);
        // 到期时间描述
        String dueDateStr = DateUtil.toYYYYMMDDString(DateUtil.offset(valueDate, 
                                                                      DateUtil.getTotalDays(product.getYears(), 
                                                                                            product.getMonths(),
                                                                                            product.getDays())
                                                                      )
                                                        );
        
        return product.getProductDetail().replaceAll("\\{计息日期\\}", valueDateStr)
                                         .replaceAll("\\{募集时间\\}", investTimeStr)
                                         .replaceAll("\\{起投金额\\}", product.getMinAmount() + "元")
                                         .replaceAll("\\{金额增量\\}", product.getStepAmount() + "元")
                                         .replaceAll("\\{投资限额\\}", product.getMaxAmount() + "元")
                                         .replaceAll("\\{到期日期\\}", dueDateStr);
    }
    
    /**
     * 使用sort命令从redis中获取集合列表
     * @param productKeys: 产品键值集合
     * @param status: 产品状态集合
     * @param pageNo: 页码
     * @param pageSize: 页数
     * @return
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    public List<RedisProduct> listRedisProductByPages(String productKey, String productStatus, 
                                                      OrderPlan orderPlan, Integer pageNo, Integer pageSize) throws Exception{
        
        if (StringUtils.isBlank(productKey) ||
                StringUtils.isBlank(productStatus) ||
                null == orderPlan || null == pageNo || null == pageSize) {
            return null;
        }
        
        // 初始化集合
        SortCriterion<String> sortCriterion = SortQueryBuilder.sort(this.getProductSetKey(productKey, 
                                                                                          productStatus, 
                                                                                          orderPlan, 
                                                                                          pageNo, 
                                                                                          pageSize)
                                                                    ).noSort();
        
        // 定义获取产品详情的字段
        for (String field : PRODUCT_LIST_FIELD) {
            sortCriterion.get(new StringBuffer().append(String.format(PREFIX, "*"))
                                                .append("->")
                                                .append(field).toString());
        }
        
        // 执行命令
        List<String> results = redisTemplate.sort(sortCriterion.build());
        
        if (null == results || results.isEmpty()) {
            return null;
        }
        
        // 返回结果
        List<RedisProduct> redisProducts = new ArrayList<RedisProduct>();
        
        for(int i = 0; i < results.size(); i = i + PRODUCT_LIST_FIELD.size()){
            
            if (StringUtils.isBlank(results.get(i))) {
                continue;
            }
            
            // 列表只显示部分字段
            RedisProduct redisProduct = new RedisProduct();
            // 产品id
            redisProduct.setId(results.get(i));
            // 产品标题
            redisProduct.setTitle(results.get(i+1));
            // 产品标的类型
            redisProduct.setTypeKey(results.get(i+2));
            // 产品金额
            redisProduct.setAmount(null == results.get(i+3) ? null : new BigDecimal(results.get(i+3)));
            // 已投资产品
            redisProduct.setInvestAmount(null == results.get(i+4) ? null : new BigDecimal(results.get(i+4)));
            // 已投资笔数
            redisProduct.setInvestNum(StringUtils.isBlank(results.get(i+5)) ? 0 : Integer.parseInt((results.get(i+5))));
            // 产品状态
            redisProduct.setStatus(StringUtils.isBlank(results.get(i+6)) ? null : EnumUtils.getEnum(ProductStatus.class, results.get(i+6)));
            // 基础利率
            redisProduct.setRate(StringUtils.isBlank(results.get(i+7)) ? 0 : Integer.parseInt((results.get(i+7))));
            // 加息利率
            redisProduct.setPlusRate(StringUtils.isBlank(results.get(i+8)) ? 0 : Integer.parseInt((results.get(i+8))));
            // 还款方式
            redisProduct.setMethod(StringUtils.isBlank(results.get(i+9)) ? null : EnumUtils.getEnum(RepaymentMethod.class, results.get(i+9)));
            // 期限-年
            redisProduct.setYears(StringUtils.isBlank(results.get(i+10)) ? 0 : Integer.parseInt((results.get(i+10))));
            // 期限-月
            redisProduct.setMonths(StringUtils.isBlank(results.get(i+11)) ? 0 : Integer.parseInt((results.get(i+11))));
            // 期限-日
            redisProduct.setDays(StringUtils.isBlank(results.get(i+12)) ? 0 : Integer.parseInt((results.get(i+12))));
            // 开标时间
            redisProduct.setOpenTime(StringUtils.isBlank(results.get(i+13)) ? null : new Date(Long.parseLong(results.get(i+13))));
            // 是否可用奖券
            redisProduct.setUseCoupon("1".equals(results.get(i+14)) ? true : false);
            // 是否可债转
            redisProduct.setDebtTransfer("1".equals(results.get(i+15)) ? true : false);
            // 承兑银行
            redisProduct.setAcceptanceBank(results.get(i+16));
            // 风险等级
            redisProduct.setRiskType(StringUtils.isBlank(results.get(i+17)) ? 0 : Integer.parseInt((results.get(i+17))));
            // 产品标签key
            redisProduct.setProductTagKey(results.get(i+18));
            // 产品标签value
            redisProduct.setProductTagValue(results.get(i+19));
            // 产品标签颜色
            redisProduct.setProductTagColor(results.get(i+20));
            // 递增金额
            redisProduct.setStepAmount(null == results.get(i+21) ? null : new BigDecimal(results.get(i+21)));
            // 起投金额
            redisProduct.setMinAmount(null == results.get(i+22) ? null : new BigDecimal(results.get(i+22)));
            
            redisProducts.add(redisProduct);
        }
        
        return redisProducts;
    }
    
    /**
     * 从redis中获取集合数量
     * @param productKeys: 产品键值集合
     * @param status: 产品状态集合
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    public Long countProductTotalSize(String productKey, String status) throws Exception{
        
           if (StringUtils.isBlank(productKey) || StringUtils.isBlank(status)) {
                return 0L;
           }
           
           String totalSize = redisTemplate.opsForValue().get(this.getProductSetCountKey(productKey, status)); 
           
           return StringUtils.isBlank(totalSize) ? 0L : Long.parseLong(totalSize);
    }
    
    /**
     * 存储产品id集合
     * @param productIds: 产品id集合
     * @param productKeys: 产品键值集合
     * @param status: 产品状态集合
     * @param pageNo: 页码
     * @param pageSize: 页数
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    public boolean putProductSetIds(List<String> productIds, String productKey, String status, 
                                    OrderPlan orderPlan, Integer pageNo, Integer pageSize) throws Exception{
        
            if (null == productIds || productIds.isEmpty() ||
                    StringUtils.isBlank(productKey) || StringUtils.isBlank(status) ||
                    null == orderPlan || null == pageNo || null == pageSize) {
                return false;
            }
            
            String setKey = this.getProductSetKey(productKey, 
                                                  status, 
                                                  orderPlan, 
                                                  pageNo, 
                                                  pageSize);
            
            // 清空原集合
            redisTemplate.delete(setKey);
            
            // 添加redis有序集合成员
            Set<TypedTuple<String>> values = new HashSet<TypedTuple<String>>(); 
            
            // 添加有序集合成员和权重
            for(int i = 0; i < productIds.size(); i++){
                TypedTuple<String> tuple = new DefaultTypedTuple<String>(productIds.get(i), Double.valueOf("" + i));
                values.add(tuple);
            }
            
            Long result = redisTemplate.opsForZSet().add(setKey, values);
            
            if (result > 0L) {
                return true;
            }
            
            return false;
    }
    
    /**
     * 异步缓存总条数
     * @param totalSize: 总条数
     * @param productKeys: 产品键值集合
     * @param status: 产品状态集合
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    @Async
    public void asyncPutProductTotalSize(Long totalSize, String productKeys, String status){
        
        try {
            
            this.putProductTotalSize(totalSize, productKeys, status);
            
        } catch (Exception ex) {
            logger.error("put size in redis failed, there is a exception ", ex);
        }
        
    }
    
    /**
     * 存储总条数
     * @param totalSize: 总条数
     * @param productKeys: 产品键值集合
     * @param status: 产品状态集合
     * @return
     * @throws Exception
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    public boolean putProductTotalSize(Long totalSize, String productKey, String status) throws Exception{
        
       if (StringUtils.isBlank(productKey) || StringUtils.isBlank(status)) {
            return false;
       }
       
           
       redisTemplate.opsForValue().set(this.getProductSetCountKey(productKey, status), 
                                       totalSize.toString());
       return true;
        
    }
    
    
    
    /**
     * 异步缓存列表详情
     * @param redisProducts
     * @param productKeys
     * @param status
     * @param pageNo
     * @param pageSize
     * @param totalSize
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    @Async
    public void asyncPutRedisProductAndTotalSize(List<RedisProduct> redisProducts, String productKey, 
                                                 String status, OrderPlan orderPlan, Integer pageNo, 
                                                 Integer pageSize, Long totalSize){
        
        try {
            
            logger.info("ready to put redis product and size failed, productKeys = {}, status = {}, "
                    + "orderPlan = {}, pageNo = {}, pageSize = {}, totalSize = {}", 
                    productKey, status, orderPlan, pageNo, pageSize, totalSize);

            
            this.putRedisProductAndTotalSize(redisProducts, productKey, status, orderPlan, pageNo, pageSize, totalSize);
            
        } catch (Exception ex) {
            logger.error("async put redis product and size failed, there is a exception ", ex);
        }
        
    }
    
    /**
     * 异步缓存标的详情,标的列表,数量
     * @param productIds
     * @param productKeys
     * @param status
     * @param pageNo
     * @param pageSize
     * @param totalSize
     * @return
     * @author lujixiang
     * @date 2017年3月15日
     *
     */
    public boolean putRedisProductAndTotalSize(List<RedisProduct> redisProducts, String productKey, 
                                               String status, OrderPlan orderPlan, Integer pageNo, 
                                               Integer pageSize, Long totalSize) throws Exception{
        
            if (null == redisProducts || redisProducts.isEmpty() ||
                    StringUtils.isBlank(productKey) || StringUtils.isBlank(status) || 
                    null == orderPlan || null == pageNo || null == pageSize || null == totalSize) {
                   
                return false;
            }
            
            List<String> setIds = new ArrayList<String>();// 存储集合
            
            // 加载产品详情
            for (RedisProduct redisProduct : redisProducts) {
                setIds.add(redisProduct.getId());
                // 放到缓存中
                if (!redisTemplate.hasKey(String.format(PREFIX, redisProduct.getId()))) {
                    this.putProductInRedis(redisProduct);
                }
            }
            
            // 缓存redis集合id
            this.putProductSetIds(setIds, productKey, status, orderPlan, pageNo, pageSize);
            
            // 缓存总数量
            this.putProductTotalSize(totalSize, productKey, status);
            
            return true;
    }
    
    private String getProductSetKey(String productKey, String productStatus, 
                                    OrderPlan orderPlan, Integer pageNo, Integer pageSize){
        
        return String.format(PRODUCT_SET_PREFIX, 
                             productKey,
                             productStatus,
                             orderPlan.name(),
                             pageNo.toString(),
                             pageSize.toString());
        
    }
    
    private String getProductSetCountKey(String productKey, String status){
        
        return String.format(PRODUCT_SET_COUNT_PREFIX, 
                             productKey,
                             status);
    }
    
    /**
     * 删除单个产品
     * @param productId
     * @author lujixiang
     * @date 2017年4月7日
     *
     */
    public void clearProductCache(String productId){
        redisTemplate.delete(String.format(PREFIX, productId));
    }
    
    /**
     * 清除所有列表
     * 
     * @author lujixiang
     * @date 2017年4月7日
     *
     */
    public void clearProductListCache() {
        
        // 获取所有列表key
        Set<String> listCacheKeys = redisTemplate.keys(CLEAR_CACHE_PRODUCT_PREFIX);
        
        if (null != listCacheKeys && !listCacheKeys.isEmpty()) {
            redisTemplate.delete(listCacheKeys);
        }
    }
    
    public boolean clearProductCacheAndList(String productId) throws Exception{
        
        try {
            // 清除单个标的
            logger.info("clear product cache, productId = {}", productId);
            this.clearProductCache(productId);
            
            // 清楚所有集合
            logger.info("clear product cache list");
            this.clearProductListCache();
            
            return true;
            
        } catch (Exception ex) {
            
            logger.error("fail to clear product cache, there is a exception : ", ex);
            
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }

}
