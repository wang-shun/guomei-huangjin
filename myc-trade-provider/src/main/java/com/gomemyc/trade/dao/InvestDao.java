package com.gomemyc.trade.dao;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import java.util.Map;

import javax.ws.rs.Path;

import com.alibaba.dubbo.config.support.Parameter;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.trade.entity.Invest;
import com.gomemyc.trade.enums.InvestStatus;

public interface InvestDao {
    
    int save(Invest invest);
    
//    int updateStatus(@Param("id") String investId, @Param("status") InvestStatus status);
    
    /**
     * 统计用户已投某个产品的总投资额和投资次数
     * @param productId
     * @param userId
     * @return
     * @author lujixiang
     * @date 2017年3月16日
     *
     */
    Map<String, Object> sumAmountAndTimesByLoan(@Param("productId") String productId, @Param("userId") String userId);
    
    
    /**
     * 统计某一类型的产品总投资额和投资次数
     * @param loanType
     * @param userId
     * @return
     * @author lujixiang
     * @date 2017年3月16日
     *
     */
    Map<String, Object> sumAmountAndTimesByLoanType(@Param("loanType") String loanType, @Param("userId") String userId);
    
    
    /**
     * 更新投资状态
     * @param id
     * @param investStatus
     * @return
     * @author lujixiang
     * @date 2017年3月17日
     *
     */
    int updateInvestStatus(@Param("id")String id, @Param("status")InvestStatus status);
    
    

    /**
     * 根据条件查询投资记录
     * @param userID
     * @param productId
     * @param loanId
     * @param status
     * @author zhangWei
     * @return
     */
    Invest findByUserAndProductAndLoan(@Param("userID") String userID,@Param("productId") String productId,@Param("loanId") String loanId,@Param("status") InvestStatus status);
    
    /**
     * 根据条件返回投资记录集合
     * @param productId
     * @param stutus 投资状态
     * @author zhangWei
     * @return
     */
    List<Invest> findListByProduct(@Param("productId") String productId,@Param("status") InvestStatus  status);
    
	/**
	 * 根据债转产品ID查询符合状态的投资记录
	 * @param productId 债转产品ID
	 * @param statuss 投资状态
	 * @author zhangWei
	 * @return
	 */
	List<Invest> findListByProductIdAndStatus(@Param("productId") String productId,@Param("statuss") InvestStatus...  statuss);
	/**
	 * 根据ID查询
	 * @param id
	 * @author zhangWei 
	 * @return
	 */
	Invest findById(@Param("id") String id);
	
	/**
	 * 修改
	 * @param invest
	 * @return
	 */
	Integer update(Invest invest);

    /**
     * 根据id,更新status
     *
     * @param map 只包含id,status
     */
    Integer updateStatusById(Map<String, Object> map);

    /**
     * 保存账户系统调北京银行解冻返回的code。
     *
     * id 订单id
     * bjDfCode 账户系统调北京银行解冻返回的code
     * @return
     */
    Integer updateBjDfCodeById(Map<String, Object> map);

    /**
     * 北京银行投资成功返回code。
     *
     *  id 订单id
     *  bjSynCode 北京银行投资成功返回code
     * @return
     */
    Integer updateBjSynCodeById(Map<String, Object> map) ;

    /**
     * 保存账户系统的本地解冻转账返回code。
     *
     * id 订单id
     * localDfCode 账户系统的本地解冻转账返回code
     * @return
     */
    Integer updateLocalDfCodeById(Map<String, Object> map) ;
    
    /**
     * @Title 检查当前产品下所有的投资是否还有未完成的
     * @param productId 产品编号
     * @param begStatus 最小未完成投资状态
     * @param endStatus 最大未完成投资状态
     * @author lifeifei
     * @date 2017年3月29日
     */
    Integer checkInvestByProduct(@Param("productId") String productId,@Param("begStatus") InvestStatus begStatus,@Param("endStatus") InvestStatus endStatus);
	
    /**
     * 查询产品的投资总金额
     * @param productId 产品编号
     * @param status 投资状态
     * @author lifeifei
     * @date 2017年3月29日
     */
    BigDecimal findAmountListByProduct(@Param("productId") String productId,@Param("status") InvestStatus status);

    /**
     *
     *
     * @param startRow
     * @param pageSize
     * @return
     */
    List<Invest> findListByLoanAndStatus(@Param("loanId")  String loanId, @Param("investStatusList") List<Integer> investStatusList,
                                         @Param("startRow") Integer startRow, @Param("pageSize")  Integer pageSize);


    Integer findCountByLoanAndStatus(@Param("loanId")  String loanId, @Param("investStatusList") List<Integer> investStatusList);

    List<Invest> findListInvestByUserIdAndInvestStatus(@Param("userId")String userId,
			@Param("investStatus")Integer investStatus, @Param("startRow")Integer startRow, 
			@Param("pageSize")Integer pageSize,@Param("loanTypeKey")String loanTypeKey);

	List<Invest> findInvestByUserIdAndStatus(@Param("userId")String userId, 
			@Param("investStatus")Integer investStatus,@Param("loanTypeKey")String loanTypeKey);

	/**
	 * 绑定本地冻结流水号
	 * @param id
	 * @param localFreezeNo
	 * @return
	 * @author lujixiang
	 * @date 2017年4月5日
	 *
	 */
	int updateLocalFrozenNo(@Param("id") String id, @Param("localFreezeNo") String localFreezeNo);
	
	
	int updateLocalDfCodeAndStatus(@Param("id") String id, @Param("localDfCode") String localDfCode, @Param("status") InvestStatus status);
	
	/**
	 * 根据用户id和状态
	 * @param userId
	 * @param investStatus
	 * @return
	 * @author lujixiang
	 * @date 2017年4月6日
	 *
	 */
	List<Invest> listByUserIdAndStatus(@Param("userId") String userId, @Param("statuslist") List<InvestStatus> investStatus, @Param("start") int start, @Param("size") int size);
	
	List<Invest> listByUserIdAndStatusNotGold(@Param("userId") String userId, @Param("statuslist") List<InvestStatus> investStatus, @Param("start") int start, @Param("size") int size);
	
	Long countByUserIdAndStatus(@Param("userId") String userId, @Param("statuslist") List<InvestStatus> investStatus);
	
	Long countByUserIdAndStatusNotGold(@Param("userId") String userId, @Param("statuslist") List<InvestStatus> investStatus);
	
	List<Invest> finByUserIdAndLoanId(@Param("userId")String userId, @Param("loanIdsList")List<String> loanIdsList);

	/**
	 * 统计用户投资某一类型产品的数量
	 * @param userId
	 * @param loanTypeKey
	 * @return
	 * @author lujixiang
	 * @date 2017年4月9日
	 *
	 */
	Long countUserInvest(@Param("userId") String userId, @Param("loanTypeKey") String loanTypeKey);
	
	
	/**
	 * 根据用户id和状态统计投资金额
	 * @param userId
	 * @param investStatus
	 * @return
	 * @author lujixiang
	 * @date 2017年4月12日
	 *
	 */
	BigDecimal sumInvestAmountByProductAndStatus(@Param("productId") String productId, @Param("statusList") InvestStatus ... investStatus);
	/**
	 * 
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @param statusList
	 * @param endSubmitTime
	 * @return
	 */
	int countByStatusAndSubmitTime(@Param("userId")String userId, 
				@Param("statusList")List<InvestStatus> statusList,
				@Param("endSubmitTime")Date endSubmitTime);
	/**
     * 根据用户ID、投资状态及用户投资产品时邀请人能否获得奖励，来统计用户投资次数(不包含当前投资)
     * @param userId 用户ID
     * @param statusList 投资状态
     * @param userIsGetReward 判断用户投资产品时邀请人能否获得奖励
     * @param investId 当前投资记录
     * @return
     */
	int countByLoanRewardAndStatusExCludeCuurentInvest(@Param("userId")String userId,
			@Param("statusList")List<InvestStatus> statusList,
			@Param("userIsGetReward")boolean userIsGetReward, 
			@Param("investId")String investId);
	
	int sumByUserAndDate(@Param("from")String from, @Param("to")String to, 
					@Param("userIds")List<String> userIds, 
					@Param("statusList")List<InvestStatus> statusList);
	
	/**
	 * 提交投资数据
	 * @param invest
	 * @return
	 * @author lujixiang
	 * @date 2017年4月13日
	 *
	 */
	int invest(Invest invest);
	
	/**
	 * 绑定北京银行冻结流水号和更改投资状态
	 * @param bjFreezeCode
	 * @param status
	 * @return
	 * @author lujixiang
	 * @date 2017年4月13日
	 *
	 */
	int updateLocalBjDfCodeAndStatus(@Param("id") String id, @Param("bjFreezeCode") String bjFreezeCode, @Param("status") InvestStatus status);
    /*
     * 根据条件返回投资记录集合
     * @param productId
     * @param stutus 投资状态
     * @author zhangWei
     * @return
     */
    int updateByProductId(@Param("productId") String productId,@Param("status") InvestStatus  status,
    		@Param("oldStatus") InvestStatus  oldStatus);
    
    /**
     * 根据用户id和状态
     * @param productId
     * @param investStatus
     * @return
     * @author lujixiang
     * @date 2017年4月6日
     *
     */
    List<Invest> listByProductIdAndStatus(@Param("productId") String productId, @Param("statuslist") List<InvestStatus> investStatus, @Param("start") int start, @Param("size") int size);

    
    Long countByProductIdAndStatus(@Param("productId") String productId, @Param("statuslist") List<InvestStatus> investStatus);

	BigDecimal getInvestStatusTotalAmount(@Param("userId")String userId, 
			@Param("productId")String productId,@Param("statusList")InvestStatus... investList);

	Integer getInvestTimes(@Param("userId")String userId, 
			@Param("productId")String productId,@Param("statusList")InvestStatus... investList);
	
	Long countInvestNumByProductAndStatus(@Param("productId") String productId, @Param("statusList") InvestStatus ... investStatus);
	/**
	 * 
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param investId
	 * @return
	 */
	Invest getInvestment(@Param("investId")String investId);
	
	
	/**
	 * 根据标的ID和投资状态查询
	 * @param loanId 不能为空
	 * @param statusList 投资状态集合可为空，为空时不做查询条件
	 * @author zhangWei
	 * @return
	 */
	List<Invest> findByLoanAndStatus(@Param("loanId") String loanId,@Param("statusList") InvestStatus...  statusList);
	/**
	 * 
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @return
	 */
	String getFirstInvestment(@Param("userId")String userId);
	
	
	/**
     * 统计某一类型的产品总投资额
     * @param loanType
     * @param userId
     * @return
     * @author lujixiang
     * @date 2017年3月16日
     *
     */
    BigDecimal sumAmountByLoanType(@Param("loanType") String loanType, @Param("userId") String userId);
    /**
     * 
    * @Title: sumAmountByStatus 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param userId
    * @param @return    设定文件 
    * @return BigDecimal    返回类型 
    * @author caoxiaoyang
    * @throws
     */
	BigDecimal sumAmountByStatusAndDebted(@Param("userId")String userId);
}

