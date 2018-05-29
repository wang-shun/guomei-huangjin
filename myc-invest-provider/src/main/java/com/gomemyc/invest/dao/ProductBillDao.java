package com.gomemyc.invest.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.OrderPlan;
import com.gomemyc.invest.enums.ProductStatus;

/**
 * 定期理财dao
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public interface ProductBillDao {
    
    int save(ProductBill productBill);
    
    /**
     * 根据债转人和债转状态查询债转票据
     * @param userId 债转人
     * @param status  产品状态
     * @param startRow 开始行数
     * @param pageSize 显示的行数
     * @author zhangWei
     * @return
     */
    List<ProductBill> findPageByByUserIdAndStatus(@Param("userId")  String userId,@Param("status")  ProductStatus status,@Param("startRow")  Integer startRow,
    		@Param("pageSize")  Integer pageSize);

    /**
     * 查询待流标定期产品
     *
     * @param status
     * @param productSwitch
     * @return
     */
    List<String> findWaitFailRegular(@Param("status") Integer status, @Param("productSwitch") Integer productSwitch);
    
    /**
     * 根据债转人和债转状态查询债转票据条数
     * @param userId 债转人
     * @param status  产品状态
     * @author zhangWei
     * @return
     */
    Integer getCountByByUserIdAndStatus(@Param("userId")  String userId,@Param("status")  ProductStatus status);
    /**
     * 根据债转产品ID修改产品状态
     * @param productStatus  产品状态
     * @param id 产品
     * @author zhangWei
     * @return
     */
    int updateProductStatus(@Param("productStatus") ProductStatus productStatus,@Param("id") String id);
    /**
     * 根据债转产品ID修改产品状态结标时间
     * @param productStatus  产品状态
     * @param id 产品
     * @author zhangWei
     * @return
     */
    int updateProductStatusAndSettleTime(@Param("productStatus") ProductStatus productStatus,@Param("id") String id,
    		@Param("settleTime") Date settleTime,@Param("valueTime") Date valueTime);
    

    ProductBill findById(@Param("id") String id);
    
    /**
     * 分页获取定期理财产品集合
     * @param typeKeys: 标的键值集合
     * @param productStatus: 产品状态集合
     * @param debted: 是否债转
     * @param start: 开始条数
     * @param size: 查询数量
     * @return
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    List<ProductBill> listByTypeAndStatusLimit(@Param("typeKeys") List<String> typeKeys, 
                                               @Param("productStatus") List<ProductStatus> productStatus,
                                               @Param("debted") Boolean debted,
                                               @Param("orderPlan") OrderPlan orderPlain, 
                                               @Param("start") int start,
                                               @Param("size") int size);
    
    /**
     * 统计产品总数
     * @param typeKeys: 标的键值集合
     * @param productStatus: 产品状态集合
     * @param debted: 是否债转
     * @return
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    Long countByTypeAndStatusLimit(@Param("typeKeys") List<String> typeKeys, 
                                   @Param("productStatus") List<ProductStatus> productStatus,
                                   @Param("debted") Boolean debted);
    /**
     * 分页获取定期理财产品id集合
     * @param typeKeys: 标的键值集合
     * @param productStatus: 产品状态集合
     * @param start: 开始条数
     * @param size: 查询数量
     * @param debted: 是否债转
     * @return
     * @author lujixiang
     * @date 2017年3月21日
     *
     */
    List<String> listIdsByTypeAndStatusLimit(@Param("typeKeys") List<String> typeKeys, 
                                             @Param("productStatus") List<ProductStatus> productStatus,
                                             @Param("debted") Boolean debted,
                                             @Param("orderPlan") OrderPlan orderPlain, 
                                             @Param("start") int start,
                                             @Param("size") int size);
    
    /**
     * 列出待开户的产品id
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    List<String> listBillProductIdsToPublish();
    
    int updateSyncStatus(@Param("id") String id, 
                         @Param("syncStatus") LoanSyncStatus syncStatus);
    
    
    int updateLocalSyncStatus(@Param("id") String id, 
                         @Param("localSyncStatus") LoanSyncStatus localSyncStatus);

    /**
     * 根据产品标题查找产品。
     *
     * @param title 产品标题
     * @return
     */
    ProductBill findByTitle(@Param("title") String title);

    /**
     * 更新已投金额、已投笔数。
     *
     * @param investAmount 金额
     * @param investNumber 笔数
     * @param id id
     * @return
     */
    int updateInvestById(@Param("investAmount") BigDecimal investAmount, @Param("investNumber") Integer investNumber,
                         @Param("id") String id);

    /**
     * 更新募集完成时间，状态。
     *
     * @param finishTime 募集完成时间
     * @param status 状态
     * @param id id
     * @return
     */
    int updateFinshAndStatusById(@Param("finishTime") Date finishTime, @Param("status") Integer status,
                                 @Param("id") String id);
    
    /**
     * 获取需要调度的定期产品
     * @return
     * @throws ServiceException
     * @author lujixiang
     * @date 2017年3月19日
     *
     */
    List<String> listBillProductIdsToSchedule(@Param("nowDate") Date date);
    
    /**
     * 根据生命周期更新产品状态
     * @param id
     * @param status
     * @param preStatus
     * @param syncStatus
     * @param localSyncStatus
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    int updateProductStatusAtCycle(@Param("id") String id, 
                                   @Param("status") ProductStatus status, 
                                   @Param("preStatus") ProductStatus preStatus,
                                   @Param("syncStatus") LoanSyncStatus syncStatus,
                                   @Param("localSyncStatus") LoanSyncStatus localSyncStatus);

    List<ProductBill> listByIds(@Param("productIds")List<String> productIds);
    
    
    /**
     * 获取需要开标的定期产品
     * @return
     * @throws ServiceException
     * @author lujixiang
     * @date 2017年3月19日
     *
     */
    List<String> listBillProductIdsToOpen(@Param("nowDate") Date date);
    
    /**
     * 获取需要结标的定期产品
     * @return
     * @throws ServiceException
     * @author 李非非
     * @date 2017年3月19日
     *
     */
    List<String> listBillProductIdsToSettle();
    
    int updateBjAndLocalSyncStatus(@Param("id") String id, 
                                    @Param("syncStatus") LoanSyncStatus syncStatus,
                                    @Param("localSyncStatus") LoanSyncStatus localSyncStatus);
    
    /**
     * 列出待满标的产品id
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    List<String> listBillProductIdsToFinish();
    
    
    
    /**
     * 根据标的ID查询票据产品
     * @param loanId 标的ID
     * @return
     */
    List<ProductBill> listByLoanId(@Param("loanId")String loanId);
    /**
     * 
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param productId
     * @return
     */
	ProductBill getProductById(@Param("productId")String productId);
	
	
	/**
     * 满标/到期未满标
     * @param id
     * @param status
     * @param date
     * @return
     * @author lujixiang
     * @date 2017年4月22日
     *
     */
    int finishAndFailed(@Param("id") String id, 
                        @Param("status") ProductStatus status,
                        @Param("finishTime") Date date);
    
    /**
     * 实时更新投资金额
     * @param id
     * @param amount
     * @return
     * @author lujixiang
     * @date 2017年4月22日
     *
     */
    int increaseInvestAmount(@Param("id") String id, 
                             @Param("amount") BigDecimal amount);

}
