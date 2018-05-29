package com.gomemyc.invest.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.common.page.Page;
import com.gomemyc.invest.bridge.LoanRedisBridge;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dto.ProductBillDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.LoanStatus;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.OrderPlan;
import com.gomemyc.invest.enums.ProductSource;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.model.RedisProduct;
import com.gomemyc.invest.service.ProductService;
import com.gomemyc.invest.util.LoanKeyUtil;
import com.gomemyc.invest.util.ProductStatusUtil;
import com.gomemyc.invest.utils.DTOUtils;
import com.gomemyc.trade.dto.InvestDTO;
import com.gomemyc.trade.service.InvestService;
import com.gomemyc.util.BeanMapper;
import com.mchange.v2.codegen.bean.BeangenUtils;

@Service
public class ProductServiceImpl implements ProductService{
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private LoanRedisBridge loanRedisBridge;
    
    @Autowired
    private ProductRegularDao productRegularDao;
    
    @Autowired
    private ProductBillDao productBillDao;
    
    @Autowired
    private LoanDao loanDao;
    @Reference
    InvestService investService;
    
    @Override
    public Page<ProductDTO> listProductByPages(String frontProductKey, String frontStatus, 
                                               Integer startDuration, Integer endDuration,
                                               OrderPlan orderPlan, Integer pageNo, 
                                               Integer pageSize ) throws ServiceException {
        
        if (StringUtils.isBlank(frontProductKey)) {
            throw new ServiceException(ExceptionCode.PRODUCT_FRONTKEY_REQUIRE.getIndex(), 
                                       ExceptionCode.PRODUCT_FRONTKEY_REQUIRE.getErrMsg());
        }
        
        startDuration = null == startDuration ? 0 : startDuration;  // 开始期限默认为0
        endDuration = null == endDuration ? 100 : endDuration;  // 截至期限默认为100
        orderPlan = null == orderPlan ? OrderPlan.OPENTIME_DESC_STATUS_ASC : orderPlan; // 默认排序方案为按开标时间倒序   
        pageNo = null == pageNo ? 1 : pageNo;   // 开始页码为1
        pageSize = null == pageSize ? 10 : pageSize;   // 页数默认10
        
        if (StringUtils.isBlank(frontStatus) ) {
            frontStatus = "ALL";  // 全部(倒计时、开标中...)
        }        
        
        List<RedisProduct> redisProducts = null;  // 查询结果
        Long count = 0L;    // 总条数
        
        // 查询redis的列表
        try {
            
            redisProducts = loanRedisBridge.listRedisProductByPages(frontProductKey,
                                                                    frontStatus,
                                                                    orderPlan,
                                                                    pageNo,
                                                                    pageSize);
            
            count = loanRedisBridge.countProductTotalSize(frontProductKey, frontStatus);
            
        } catch (Exception ex) {
            logger.error("fail to list product from redis failed, there is a exception : " , ex);
        }
        
        // 获取数据表来源
        ProductSource productSource = LoanKeyUtil.getProductSourceByFrontKey(frontProductKey);
        if (null == productSource) {
            return new Page<ProductDTO>(new ArrayList<ProductDTO>(), 
                                        0, 
                                        0, 
                                        0L);
        }
        
        
        // 无法从缓存中直接获取有两种处理方式
        // 1.缓存产品的id集合,再查询缓存
        // 2.不依赖缓存,直接从库中读取标的列表
        if (null == redisProducts || redisProducts.isEmpty()) {
            
            logger.info("ready to list productIds from database, productKey = {}, status = {}, pageNo = {}, pageSize = {}",
                         frontProductKey, frontStatus, pageNo, pageSize);
            
            List<String> idSets = null;
            
            try {
                
                switch (productSource) {
                
                    case REGULAR:   // 定期表
                        
                        idSets = productRegularDao.listIdsByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                               ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                               LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey),
                                                                               orderPlan,
                                                                               (pageNo.intValue() - 1) * pageSize.intValue(),  
                                                                               pageSize.intValue());

                        count = productRegularDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey), 
                                                                            ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus), 
                                                                            LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));

                        break;
                        
                    case PJ:   // 票据
                        
                        idSets = productBillDao.listIdsByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                            ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                            LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey),
                                                                            orderPlan,
                                                                            (pageNo.intValue() - 1) * pageSize.intValue(),  
                                                                            pageSize.intValue());

                        count = productBillDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey), 
                                                                         ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus), 
                                                                         LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));
                        
                        break;
    
                    default:
                        break;
                }
                
                if (null == idSets || idSets.isEmpty()) {
                    return new Page<ProductDTO>(new ArrayList<ProductDTO>(), 
                                                0, 
                                                0, 
                                                0L);
                }
                
                // 将查询的id集合和总条数放到redis中,并再次查询
                boolean putIdsResult = loanRedisBridge.putProductSetIds(idSets, frontProductKey, frontStatus, 
                                                                        orderPlan, pageNo, pageSize);
                
                loanRedisBridge.putProductTotalSize(count, frontProductKey, frontStatus);
                
                if (putIdsResult) {
                    redisProducts = loanRedisBridge.listRedisProductByPages(frontProductKey,
                                                                            frontStatus,
                                                                            orderPlan,
                                                                            pageNo,
                                                                            pageSize);
                }
                
                // 判断id个数和产品个数不一致,需要读数据库并缓存
                if (null != redisProducts && redisProducts.size() != idSets.size()) {
                    redisProducts = null;
                }
                
            } catch (Exception ex) {
                logger.error("list productIds from database failed, there is a exception : " , ex);
            }
        }
        
        
        // 不依赖缓存,直接从库中读取标的列表  
        if (null == redisProducts || redisProducts.isEmpty()) {
            
            
            redisProducts = new ArrayList<RedisProduct>();
            
            try {
                
                switch (productSource) {
                
                    case REGULAR : 
                        
                     // 如果是债转标,不需要传入具体键值
                        List<ProductRegular> productRegulars = productRegularDao.listByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                                                          ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                                                          LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey),
                                                                                                          orderPlan,
                                                                                                          (pageNo.intValue() - 1) * pageSize.intValue(),  
                                                                                                          pageSize.intValue());
                        
                        count = productRegularDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                            ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                            LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));
                        
                        if (null != productRegulars && !productRegulars.isEmpty()) {
                            for(ProductRegular regular : productRegulars){
                                redisProducts.add(loanRedisBridge.bindProductRules(regular, 
                                                                                   BeanMapper.map(regular, 
                                                                                                  RedisProduct.class)
                                                                                   ));
                            }
                        }
                        
                        // 异步缓存所有结果
                        loanRedisBridge.asyncPutRedisProductAndTotalSize(redisProducts, frontProductKey, frontStatus, 
                                                                         orderPlan, pageNo, pageSize, count);
                        break;
                        
                    case PJ : 
                        
                           // 如果是债转标,不需要传入具体键值
                           List<ProductBill> productBills = productBillDao.listByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                                                    ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                                                    LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey),
                                                                                                    orderPlan,
                                                                                                    (pageNo.intValue() - 1) * pageSize.intValue(),  
                                                                                                    pageSize.intValue());
                           
                           count = productBillDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                            ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                            LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));
                           
                           if (null != productBills && !productBills.isEmpty()) {
                               for(ProductBill productBill : productBills){
                                   redisProducts.add(loanRedisBridge.bindProductRules(productBill, 
                                                                                      BeanMapper.map(productBill, 
                                                                                                     RedisProduct.class)
                                                                                      ));
                               }
                           }
                           
                           // 异步缓存所有结果
                           loanRedisBridge.asyncPutRedisProductAndTotalSize(redisProducts, frontProductKey, frontStatus, 
                                                                            orderPlan, pageNo, pageSize, count);
                           break;
                    default :
                        break;
                        
                
                }
                    
            } catch (Exception ex) {
                logger.error("list products from database failed, there is a exception : " , ex);
            }
            
        }
        
        // 总条数修正,如果总条数小于当前页已显示的条数,需要重新统计总条数
        if (null != redisProducts && redisProducts.size() > count) {
            
            switch (productSource) {
                case REGULAR:
                    count = productRegularDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                        ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                        LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));
                    
                    // 异步缓存总条数
                    loanRedisBridge.asyncPutProductTotalSize(count, frontProductKey, frontStatus);
                    break;
                    
                case PJ:
                    count = productBillDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(frontProductKey),
                                                                     ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus),
                                                                     LoanKeyUtil.isDebtAnalyzeFrontKey(frontProductKey));
                    
                    // 异步缓存总条数
                    loanRedisBridge.asyncPutProductTotalSize(count, frontProductKey, frontStatus);
                    break;
    
                default:
                    break;
            }
            
        }
        
        // 封装返回值
        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
        if (null != redisProducts && !redisProducts.isEmpty()) {
            
            switch (productSource) {
            case REGULAR:
                for (RedisProduct redisProduct : redisProducts) {
                    productDTOs.add(BeanMapper.map(redisProduct, ProductRegularDTO.class));
                }
                break;
                
            case PJ:
                for (RedisProduct redisProduct : redisProducts) {
                    productDTOs.add(BeanMapper.map(redisProduct, ProductBillDTO.class));
                }
                break;

            default:
                break;
            }
        }
        
        return new Page<ProductDTO>(productDTOs, 
                                    pageNo, 
                                    pageSize, 
                                    count);
    }
    
    
    @Override
    public List<String> listRegularProductIdsToPublish() throws ServiceException {
        
        try {
            List<String> publishIds = productRegularDao.listRegularProductIdsToPublish();
            
            return publishIds;
            
        } catch (Exception ex) {
            logger.error("fail to list publish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);
        }
    }


    @Override
    public List<String> listRegularProductIdsToSchedule() throws ServiceException{
        
        try {
            List<String> scheduleIds = productRegularDao.listRegularProductIdsToSchedule(new Date());
            
            return scheduleIds;
            
        } catch (Exception ex) {
            logger.error("fail to list publish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }
    
    
    @Override
    public Boolean scheduleProduct(String productId) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to schedule product, the productId = {} ", productId);
        
        try {
            
            if(productId.startsWith("dq-")){
                
                ProductRegular productRegular = productRegularDao.findById(productId);
                
                if (null == productRegular ) {
                    logger.info("fail to schedule product, the productRegular is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已调度
                if (ProductStatus.SCHEDULED.equals(productRegular.getStatus())) {
                    logger.info("success to schedule product, the product status is already scheduled, the productId = {} ", productId);
                    return true;
                }
                
                // 不是调度中的不能开标
                if (!ProductStatus.READY.equals(productRegular.getStatus())) {
                    logger.info("fail to schedule product, the product status is not ready, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_READY.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_READY.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("update to product schedule status, the productId = {} ", productId);
                
                if (1 != productRegularDao.updateProductStatusAtCycle(productId, 
                        ProductStatus.SCHEDULED, 
                        ProductStatus.READY, 
                        LoanSyncStatus.SUCCESS, 
                        LoanSyncStatus.SUCCESS)) {
                    
                    logger.info("fail to update product schedule status, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                            ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product schedule status, the productId = {} ", productId);
                
            }else if(productId.startsWith("pj-")){
                
                ProductBill productBill = productBillDao.findById(productId);
                
                if (null == productBill ) {
                    logger.info("fail to schedule product, the productBill is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已调度
                if (ProductStatus.SCHEDULED.equals(productBill.getStatus())) {
                    logger.info("success to schedule product, the product status is already scheduled, the productId = {} ", productId);
                    return true;
                }
                
                // 不是调度中的不能开标
                if (!ProductStatus.READY.equals(productBill.getStatus())) {
                    logger.info("fail to schedule product, the product status is not ready, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_READY.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_READY.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("update to product schedule status, the productId = {} ", productId);
                
                if (1 != productBillDao.updateProductStatusAtCycle(productId, 
                                                                   ProductStatus.SCHEDULED, 
                                                                   ProductStatus.READY, 
                                                                   LoanSyncStatus.SUCCESS, 
                                                                   LoanSyncStatus.SUCCESS)) {
                    
                    logger.info("fail to update product schedule status, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                                               ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product schedule status, the productId = {} ", productId);
                
                
            }else{
                
                throw new ServiceException(ExceptionCode.PRODUCT_UNKNOWN.getIndex(), 
                                           ExceptionCode.PRODUCT_UNKNOWN.getErrMsg());
            }
            
            // 删除缓存
            logger.info("clear product cache and list, the productId = {} ", productId);
            if (!loanRedisBridge.clearProductCacheAndList(productId)) {
                logger.info("fail to clear product cache and list, the productId = {} ", productId);
                return false;
            }
            return true;
                
        } catch (Exception ex) {
            
            logger.error("fail to update product status : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);  
        }
    }


    @Override
    public Long countProductsByLoantypeAndStatus(String loanType, String frontStatus) throws ServiceException{
        
        if (StringUtils.isBlank(loanType)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_FRONTKEY_REQUIRE.getIndex(),
                                       ExceptionCode.PRODUCT_FRONTKEY_REQUIRE.getErrMsg());
        }
        
        Long count = null;
        try {
            
            // 直接从redis获取
            count = loanRedisBridge.countProductTotalSize(loanType, frontStatus);
            
            if (null != count && count.compareTo(0L) > 0) {
                return count;
            }
            
            
            // 如果为0需要从数据库,根据产品类型查找
            ProductSource productSource = LoanKeyUtil.getProductSourceByFrontKey(loanType);
            switch (productSource) {
            case REGULAR:
                
                count = productRegularDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(loanType), 
                                                                    ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus), 
                                                                    LoanKeyUtil.isDebtAnalyzeFrontKey(loanType));
                
                break;
            case PJ:
                
                count = productBillDao.countByTypeAndStatusLimit(LoanKeyUtil.analyzeFrontKeyToLoanKey(loanType), 
                                                                 ProductStatusUtil.analyzeFrontStatusToProductStatus(frontStatus), 
                                                                 LoanKeyUtil.isDebtAnalyzeFrontKey(loanType));
                
                break;

            default:
                break;
            }
            
            // 异步缓存条数
            try {
                loanRedisBridge.asyncPutProductTotalSize(count, loanType, frontStatus);
                
            } catch (Exception ex) {
                logger.error("fail to async put product totalSize, there is a exception : ", ex);
            }
            
            return count;
            
            
        } catch (Exception ex) {
            logger.info("fail to countProductsByLoantypeAndStatus, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
        
    }


	@Override
	public List<ProductRegularDTO> listByIds(List<String> productIds) {
		List<ProductRegular> listByIds = productRegularDao.listByIds(productIds);
		System.err.println(listByIds);
		List<ProductRegularDTO> mapList=null;
		if(listByIds!=null&& listByIds.size()>0){
			mapList = BeanMapper.mapList(listByIds, ProductRegularDTO.class);
		}
		return mapList; 
	}
	
	@Override
    public List<String> listRegularProductIdsToOpen() throws ServiceException{
        
        try {
            List<String> openIds = productRegularDao.listRegularProductIdsToOpen(new Date());
            
            return openIds;
            
        } catch (Exception ex) {
            logger.error("fail to list open productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }
	
	
	@Override
    public Boolean openProduct(String productId) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to open product, the productId = {} ", productId);
        
        try {
            
            if(productId.startsWith("dq-")){
                
                ProductRegular productRegular = productRegularDao.findById(productId);
                
                if (null == productRegular ) {
                    logger.info("fail to open product, the productRegular is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已开标
                if (ProductStatus.OPENED.equals(productRegular.getStatus())) {
                    logger.info("success to open product, the product status is already opened, the productId = {} ", productId);
                    return true;
                }
                
                // 不是调度中的不能开标
                if (!ProductStatus.SCHEDULED.equals(productRegular.getStatus())) {
                    logger.info("fail to open product, the product status is not scheduled, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_SCHEDULED.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_SCHEDULED.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("update to product open status, the productId = {} ", productId);
                
                if (1 != productRegularDao.updateProductStatusAtCycle(productId, 
                        ProductStatus.OPENED, 
                        ProductStatus.SCHEDULED, 
                        LoanSyncStatus.SUCCESS, 
                        LoanSyncStatus.SUCCESS)) {
                    
                    logger.info("fail to update product open status, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                                               ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product open status, the productId = {} ", productId);
                
            }else if(productId.startsWith("pj-")){
                
                ProductBill productBill = productBillDao.findById(productId);
                
                if (null == productBill ) {
                    logger.info("fail to open product, the productBill is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已开标
                if (ProductStatus.OPENED.equals(productBill.getStatus())) {
                    logger.info("success to open product, the product status is already opened, the productId = {} ", productId);
                    return true;
                }
                
                // 不是调度中的不能开标
                if (!ProductStatus.SCHEDULED.equals(productBill.getStatus())) {
                    logger.info("fail to open product, the product status is not scheduled, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_SCHEDULED.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_SCHEDULED.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("update to product open status, the productId = {} ", productId);
                
                if (1 != productBillDao.updateProductStatusAtCycle(productId, 
                                                                   ProductStatus.OPENED, 
                                                                   ProductStatus.SCHEDULED, 
                                                                   LoanSyncStatus.SUCCESS, 
                                                                   LoanSyncStatus.SUCCESS)) {
                    
                    logger.info("fail to update product open status, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                                               ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product open status, the productId = {} ", productId);
                
                
            }else{
                
                throw new ServiceException(ExceptionCode.PRODUCT_UNKNOWN.getIndex(), 
                                           ExceptionCode.PRODUCT_UNKNOWN.getErrMsg());
            }
            
            // 清除缓存列表
            logger.info("clear product cache and list, the productId = {} ", productId);
            loanRedisBridge.clearProductListCache();
            return true;
                
        } catch (Exception ex) {
            
            logger.error("fail to update product status : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);  
        }
    }


    @Override
    public Boolean finishProduct(String productId, ProductStatus productStatus) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to finish product, the productId = {}, status = {} ", productId, productStatus);
        
        try {
            
            if(productId.startsWith("dq-")){
                
                ProductRegular productRegular = productRegularDao.findById(productId);
                
                if (null == productRegular ) {
                    logger.info("fail to finish product, the productRegular is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已开标
                if (ProductStatus.FINISHED.equals(productRegular.getStatus()) ||
                        ProductStatus.FAILED.equals(productRegular.getStatus())) {
                    logger.info("success to finish product, the product status is already finished, the productId = {}, status = {} ", 
                                                                                                productId, productRegular.getStatus());
                    return true;
                }
                
                // 不是开标中的不能满表或到期未满标
                if (!ProductStatus.OPENED.equals(productRegular.getStatus())) {
                    logger.info("fail to open product, the product status is not opened, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_OPENED.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_OPENED.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("ready to update product finish status, the productId = {}, status = {} ", productId, productStatus);
                
                if (1 != productRegularDao.finishAndFailed(productId, productStatus, new Date())) {
                    
                    logger.info("fail to update product open status, the productId = {}, status = {} ", productId, productStatus);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                                               ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product open status, the productId = {}, status = {} ", productId, productStatus);
                
            }else if(productId.startsWith("pj-")){
                
                ProductBill productBill = productBillDao.findById(productId);
                
                if (null == productBill ) {
                    logger.info("fail to finish product, the productBill is null, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
                }
                
                // 已开标
                if (ProductStatus.FINISHED.equals(productBill.getStatus()) ||
                        ProductStatus.FAILED.equals(productBill.getStatus())) {
                    logger.info("success to finish product, the product status is already finished, the productId = {}, status = {} ", 
                                                                                                productId, productBill.getStatus());
                    return true;
                }
                
                // 不是开标中的不能满表或到期未满标
                if (!ProductStatus.OPENED.equals(productBill.getStatus())) {
                    logger.info("fail to open product, the product status is not opened, the productId = {} ", productId);
                    throw new ServiceException(ExceptionCode.PRODUCT_NOT_OPENED.getIndex(), 
                                               ExceptionCode.PRODUCT_NOT_OPENED.getErrMsg());
                }
                
                // 更新数据库状态
                logger.info("ready to update product finish status, the productId = {}, status = {} ", productId, productStatus);
                
                if (1 != productBillDao.finishAndFailed(productId, productStatus, new Date())) {
                    
                    logger.info("fail to update product open status, the productId = {}, status = {} ", productId, productStatus);
                    throw new ServiceException(ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getIndex(), 
                                               ExceptionCode.PRODUCT_UPDATE_STATUS_FAIL.getErrMsg());
                }
                
                logger.info("success to update product open status, the productId = {}, status = {} ", productId, productStatus);
                
                
            }else{
                
                throw new ServiceException(ExceptionCode.PRODUCT_UNKNOWN.getIndex(), 
                                           ExceptionCode.PRODUCT_UNKNOWN.getErrMsg());
            }
            
            // 删除缓存
            logger.info("clear product cache and list, the productId = {} ", productId);
            if (!loanRedisBridge.clearProductCacheAndList(productId)) {
                logger.info("fail to clear product cache and list, the productId = {} ", productId);
                return false;
            }
            // 重新加载缓存
            loanRedisBridge.getProduct(productId);
            return true;
            
        } catch (Exception ex) {
            
            logger.error("fail to update product status : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);  
        }
    }
    
    @Override
    public List<String> listRegularProductIdsToSettle() throws ServiceException{
    	List<String> result = new ArrayList<>();
        try {
        	//定期理财待结标产品
            List<String> ids = productRegularDao.listRegularProductIdsToSettle();
            if(ids != null && !ids.isEmpty()){
            	result.addAll(ids);
            }
            
            //票据待结标产品
            List<String> billIds = productBillDao.listBillProductIdsToSettle();
            if(billIds != null && !billIds.isEmpty()){
            	result.addAll(billIds);
            }
            
            return result;
            
        } catch (Exception ex) {
            logger.error("fail to list open productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }


    @Override
    public Boolean openRegularProductAccount(String productId) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to open regular product account, the productId = {}", productId);
        ProductRegular regular = productRegularDao.findById(productId);
        
        if (null == regular) {
            logger.info("fail to open regular product account, the product is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                       ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
        }
        
        if (LoanSyncStatus.SUCCESS.equals(regular.getSyncStatus()) &&
                LoanSyncStatus.SUCCESS.equals(regular.getLocalSyncStatus())) {
            logger.info("success to open regular product account, the product has already open account, the productId = {}", productId);
            return true;
        }
        
        if (StringUtils.isBlank(regular.getLoanId())) {
            logger.info("fail to open regular product account, the loanId is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.LOAN_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.LOAN_ID_REQUIRED.getErrMsg());
        }
        
        Loan loan = loanDao.findById(regular.getLoanId());
        
        if (null == loan) {
            logger.info("fail to open regular product account, the loan is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(), 
                                       ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
        }
        
        if (LoanStatus.PUBLISHED.equals(loan.getStatus())) {
            logger.info("ready to update product sync status, the productId = {}", productId);
            if (1 != productRegularDao.updateBjAndLocalSyncStatus(productId, 
                                                             LoanSyncStatus.SUCCESS, 
                                                             LoanSyncStatus.SUCCESS)) {
                
                logger.info("fail to update product sync status, the productId = {}", productId);
                throw new ServiceException(ExceptionCode.PRODUCT_PRODUCT_SYNC_STATUS_FAIL.getIndex(), 
                                           ExceptionCode.PRODUCT_PRODUCT_SYNC_STATUS_FAIL.getErrMsg());
            }
            return true;
        }
        
        logger.info("fail to open regular product account, the loan is not published, the productId = {}, loanStatus = {}", 
                                                                                              productId, loan.getStatus());
        throw new ServiceException(ExceptionCode.LOAN_NOT_PUBLISHED.getIndex(), 
                                   ExceptionCode.LOAN_NOT_PUBLISHED.getErrMsg());
    }
    
    
    @Override
    public List<String> listRegularProductIdsToFinish() throws ServiceException{
        
        try {
            List<String> finishIds = productRegularDao.listRegularProductIdsToFinish();
            
            return finishIds;
            
        } catch (Exception ex) {
            logger.error("fail to list finish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }
    
    
    @Override
    public List<String> listBillProductIdsToSchedule() throws ServiceException{
        
        try {
            List<String> scheduleIds = productBillDao.listBillProductIdsToSchedule(new Date());
            
            return scheduleIds;
            
        } catch (Exception ex) {
            logger.error("fail to list publish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }
    
    
    @Override
    public List<String> listBillProductIdsToOpen() throws ServiceException{
        
        try {
            List<String> openIds = productBillDao.listBillProductIdsToOpen(new Date());
            
            return openIds;
            
        } catch (Exception ex) {
            logger.error("fail to list open productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }
    
    
    @Override
    public Boolean openBillProductAccount(String productId) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready to open bill product account, the productId = {}", productId);
        ProductBill bill = productBillDao.findById(productId);
        
        if (null == bill) {
            logger.info("fail to open bill product account, the product is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.PRODUCT_NOT_EXIST.getIndex(), 
                                       ExceptionCode.PRODUCT_NOT_EXIST.getErrMsg());
        }
        
        if (LoanSyncStatus.SUCCESS.equals(bill.getSyncStatus()) &&
                LoanSyncStatus.SUCCESS.equals(bill.getLocalSyncStatus())) {
            logger.info("success to open bill product account, the product has already open account, the productId = {}", productId);
            return true;
        }
        
        if (StringUtils.isBlank(bill.getLoanId())) {
            logger.info("fail to open bill product account, the loanId is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.LOAN_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.LOAN_ID_REQUIRED.getErrMsg());
        }
        
        Loan loan = loanDao.findById(bill.getLoanId());
        
        if (null == loan) {
            logger.info("fail to open bill product account, the loan is null, the productId = {}", productId);
            throw new ServiceException(ExceptionCode.LOAN_NOT_EXIST.getIndex(), 
                                       ExceptionCode.LOAN_NOT_EXIST.getErrMsg());
        }
        
        if (LoanStatus.PUBLISHED.equals(loan.getStatus())) {
            logger.info("ready to update product sync status, the productId = {}", productId);
            if (1 != productBillDao.updateBjAndLocalSyncStatus(productId, 
                                                                  LoanSyncStatus.SUCCESS, 
                                                                  LoanSyncStatus.SUCCESS)) {
                
                logger.info("fail to update product sync status, the productId = {}", productId);
                throw new ServiceException(ExceptionCode.PRODUCT_PRODUCT_SYNC_STATUS_FAIL.getIndex(), 
                                           ExceptionCode.PRODUCT_PRODUCT_SYNC_STATUS_FAIL.getErrMsg());
            }
            return true;
        }
        
        logger.info("fail to open bill product account, the loan is not published, the productId = {}, loanStatus = {}", 
                                                                                              productId, loan.getStatus());
        throw new ServiceException(ExceptionCode.LOAN_NOT_PUBLISHED.getIndex(), 
                                   ExceptionCode.LOAN_NOT_PUBLISHED.getErrMsg());
    }
  
    @Override
    public List<String> listBillProductIdsToPublish() throws ServiceException {
        
        try {
            List<String> publishIds = productBillDao.listBillProductIdsToPublish();
            
            return publishIds;
            
        } catch (Exception ex) {
            logger.error("fail to list publish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);
        }
    }
    
    
    @Override
    public List<String> listBillProductIdsToFinish() throws ServiceException{
        
        try {
            List<String> finishIds = productBillDao.listBillProductIdsToFinish();
            
            return finishIds;
            
        } catch (Exception ex) {
            logger.error("fail to list finish productIds, there is a exception :", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }


	@Override
	public ProductDTO getProductByInvestId(String investId) throws ServiceException{
		//判断投资id是否为空
		if(StringUtils.isEmpty(investId)){
			throw new ServiceException(ExceptionCode.INVEST_ID_REQUIRED.getIndex(), 
                    ExceptionCode.INVEST_ID_REQUIRED.getErrMsg());
		}
		InvestDTO investDTO = investService.getInvestment(investId);
		String productId = null;
		if(investDTO!=null && !investDTO.equals("")){
			productId= investDTO.getProductId();
		}
		//判断productId是否为空
		if (StringUtils.isBlank(productId)) {
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
		if (productId.startsWith("dq-")) {
			//定期
			ProductRegular productRegular=productRegularDao.getProductById(productId);
			ProductDTO map = DTOUtils.toDTO(productRegular);
			return map;
		}else if(productId.startsWith("pj-")){
			//票据产品表
			ProductBill productBill=productBillDao.getProductById(productId);
			return DTOUtils.toDTO(productBill);
		}else{
		  logger.error("没有找到该投资id下的产品 ，investId："+investId);
	      throw new ServiceException(ExceptionCode.INVEST_ID_NO_EXIST.getIndex(), 
	                                   ExceptionCode.INVEST_ID_NO_EXIST.getErrMsg()
	                               );
		}
	}


    @Override
    public Boolean increaseInvestAmount(String productId, BigDecimal amount) throws ServiceException {
        
        if (StringUtils.isBlank(productId)) {
            
            throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(), 
                                       ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
        }
        
        logger.info("ready increase invest amount, productId = {}, amount = {}", productId, amount);
        
        try {
            
            // 定期
            if (productId.startsWith("dq-")) {
                
                if (1 == productRegularDao.increaseInvestAmount(productId, amount)) {
                    logger.info("success to update regular product invest amount in database, productId = {}, amount = {}", productId, amount);
                    return true;
                }
                
                logger.info("fail to update regular product invest amount in database, productId = {}, amount = {}", productId, amount);
                throw new ServiceException(ExceptionCode.UPDATE_PRODUCT_INVEST_AMOUNT_FAIL.getIndex(), 
                                           ExceptionCode.UPDATE_PRODUCT_INVEST_AMOUNT_FAIL.getErrMsg());
                
            }else if (productId.startsWith("pj-")) {
                
                if (1 == productBillDao.increaseInvestAmount(productId, amount)) {
                    logger.info("success to update bill product invest amount in database, productId = {}, amount = {}", productId, amount);
                    return true;
                }
                
                logger.info("fail to update bill product invest amount in database, productId = {}, amount = {}", productId, amount);
                throw new ServiceException(ExceptionCode.UPDATE_PRODUCT_INVEST_AMOUNT_FAIL.getIndex(), 
                                           ExceptionCode.UPDATE_PRODUCT_INVEST_AMOUNT_FAIL.getErrMsg());
                
            }else{
                throw new ServiceException(ExceptionCode.PRODUCT_UNKNOWN.getIndex(), 
                                           ExceptionCode.PRODUCT_UNKNOWN.getErrMsg());
            }
            
        } catch (Exception ex) {
            logger.info("fail to increase invest amount, there is a exception : ", ex);
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);
        }
    }

}
