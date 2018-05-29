package com.gomemyc.gold.dao;

import java.math.BigDecimal;
import java.util.List;

import com.gomemyc.gold.entity.GoldInvestAccountCheck;
import com.gomemyc.gold.entity.extend.ContractInvest;
import com.gomemyc.gold.entity.extend.FinishOrders;
import com.gomemyc.gold.entity.extend.GoldInvestOrderAndInfoExtend;
import org.apache.ibatis.annotations.Param;
import com.gomemyc.gold.dto.FinishOrdersDTO;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.extend.GoldInvestOrderExtend;

public interface GoldInvestOrderDao {

    int deleteByPrimaryKey(String id);

    int insert(GoldInvestOrder record);

    int insertSelective(GoldInvestOrder record);

    GoldInvestOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldInvestOrder record);

    int updateByPrimaryKey(GoldInvestOrder record);
    
    
	/**
	 * 查询黄金总克数
	 * @param userId  用户id
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月14日
	 */
    BigDecimal getGoidWeight(@Param("userId")String userId, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 * 
	 * 根据用户id分页查询募集中、收益中、已完结订单
	 * 
	 * @param userId  用户id
	 * @param pageStart  起始条数
	 * @param pageSize  每一页显示的条数
	 * @param orderStatus
	 * @param orderType
	 * @return  List<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
//    List<FinishOrders> listGoldInvestProduct(@Param("userId")String userId, @Param("pageStart")Integer pageStart, @Param("pageSize")Integer pageSize , @Param("tblInvestStatus")Integer tblInvestStatus , @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
	/**
	 *
	 * 根据用户id分页查询募集中、收益中、已完结订单   用调用标的服务获取投资id查询
	 *
	 * @param userId  用户id
	 * @param pageStart  起始条数
	 * @param pageSize  每一页显示的条数
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  List<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	FinishOrders getFinishOrders(@Param("userId")String userId,@Param("investId")String investId,@Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
	/**
	 * 
	 * 查询订单数
	 * 
	 * @param userId  用户id
	 * @return  int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    int getGoldInvestOrderAmount(@Param("productId")String productId,@Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
    
	/**
	 * 
	 * 根据用户id和订单号查询交易结果
	 * 
	 * @param userId  用户id
	 * @param reqNo  订单号
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  GoldInvestOrder
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    GoldInvestOrder getResultByUserIdReqNo(@Param("userId")String userId,@Param("reqNo")String reqNo, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
    
	/**
	 * 
	 * 分页查询客户的购买记录
	 * 
	 * @param userId  用户id
	 * @param productId  产品id
	 * @param pageStart  起始条数
	 * @param pageSize  每一页显示的条数
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  List<GoldInvestOrder>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    List<GoldInvestOrder> listPageByUserIdLoanId(@Param("productId")String productId,@Param("pageStart")int pageStart,@Param("pageSize") int pageSize, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 * 
	 *
	 * 
	 * @param userId  用户id
	 * @param pageStart  起始条数
	 * @param pageSize  每一页显示的条数
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  List<FinishOrdersDTO>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    List<FinishOrdersDTO> listPageCollectByUserId(@Param("userId")String userId,@Param("userId") int pageStart, @Param("pageSize")int pageSize, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
    
	/**
	 * 
	 * 查询客户募集中,收益中，已完结订单总数
	 * 
	 * @param userId  用户id
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    int getCollectAmount(@Param("userId")String userId,@Param("tblInvestStatus")Integer tblInvestStatus, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 * 
	 * 查询订单信息
	 * 
	 * @param userId  用户id
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  GoldInvestOrder
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    GoldInvestOrder selectByUseId(@Param("userId")String userId, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 * 
	 * 查询订单信息
	 * 
	 * @param userId  用户id
	 * @param reqNo  订单号
	 * @return  GoldInvestOrder
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    GoldInvestOrder selectObjByUserIdReqNo(@Param("userId") String userId,@Param("reqNo")String reqNo, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
	/**
	 *
	 * 查询订单信息
	 *
	 * @param userId  用户id
	 * @param reqNo  订单号
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  GoldInvestOrderAndInfoExtend
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年04月18日
	 */
	GoldInvestOrderAndInfoExtend getObjByUserIdReqNo(@Param("userId") String userId, @Param("reqNo")String reqNo, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 * 
	 * 查询投资订单表和产品表
	 * 
	 * @param currentTime 当前时间
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  List<GoldInvestOrderExtend>
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月22日
	 */
    List<GoldInvestOrderExtend> listGoldInvestOrderAndProductByCurrentTime(@Param("currentTime")String currentTime, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);
	/**
	 *
	 * 根据商品id查询所有关联订单的投资总额
	 *
	 * @param productId  商品id
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	BigDecimal getByProductId(@Param("productId")String productId, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 *
	 * 根据订单的userId和productId查询所有关联订单个数
	 *
	 * @param userId  商品id
	 * @param orderStatus 订单状态
	 * @param  orderType 交易类型
	 * @return  Integer
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	Integer getByUserIdAndProductId(@Param("userId")String userId,@Param("productId")String productId, @Param("orderStatus")Integer orderStatus , @Param("orderType")Integer orderType);

	/**
	 *
	 * 根据userId，reqNo更新预下单状态状态
	 *
	 * @param userId  商品id
	 * @param reqNo  订单号
	 * @param orderType  订单类型
	 * @param orderStatus  订单状态
	 * @return  Integer
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月28日
	 */
	Integer updateByUseridAndReqNo(@Param("userId")String userId,@Param("reqNo")String reqNo, @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus, @Param("newOrderStatus")Integer newOrderStatus);

	/**
	 *
	 * 根据当前时间，查询已过期的预下单订单
	 *
	 * @param currentTime  当前时间
	 * @param orderType  订单类型
	 * @param orderStatus  订单状态
	 * @return  List<GoldInvestOrder>
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月28日
	 */
	List<GoldInvestOrder> listByCurrentTime(@Param("currentTime")String currentTime, @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);

	/**
	 *
	 * 根据订单状态，查询预下单订单信息
	 *
	 * @param orderType  订单类型
	 * @param orderStatus  订单状态
	 * @return  List<GoldInvestOrder>
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月28日
	 */
	List<GoldInvestOrder> listByOrderStatus(@Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);

	/**
	 *
	 * 根据订单号和订单状态唯一查询订单
	 *
	 * @param reqNo  订单号
	 * @param orderType  订单类型
	 * @param orderStatus  订单状态
	 * @return  GoldInvestOrder
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月30日
	 */
	GoldInvestOrder selectByReqNo(@Param("reqNo")String reqNo, @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);
	/**
	 *
	 * 根据订单号和订单状态唯一查询订单
	 *
	 * @param productId  商品id
	 * @param orderType  订单状态
	 * @param orderStatus  订单状态
	 * @return  List<GoldInvestOrder>
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月30日
	 */
	List<GoldInvestOrder> selectGoldInvestOrderByProductId(@Param("productId")String productId, @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);
	
	/**
	 *
	 * 根据id查询相应用户Id及购买克重
	 *
	 * @param id  订单id
	 * @return  ContractInvest
	 * @since JDK 1.8
	 * @author zhuyunpeng
	 * @date 2017年4月10日
	 */
	ContractInvest getUserIdAndRealWeightById(@Param("investId")String investId);
	/**
	 *
	 * 根据订单号和订单状态/开标时间查询订单
	 *
	 * @param openTime  商品id
	 * @param orderType  订单状态
	 * @param orderStatus  订单状态
	 * @return  List<GoldInvestAccountCheck>
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月30日
	 */
	List<GoldInvestAccountCheck> findtGoldInvestOrderByOpenTime(@Param("openTime")String openTime, @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);

	/**
	 *
	 * 根据id查询相应用户Id及购买克重
	 *
	 * @param productId  商品id
	 * @param orderType  订单状态
	 * @param orderStatus  订单状态
	 * @return  List<GoldInvestOrder>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月20日
	 */
	List<GoldInvestOrder> getInvestOrderByProductId(@Param("productId")String productId , @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);

	/**
	 *
	 * 根据产品id,查询发布金额
	 *
	 * @param productId  商品id
	 * @param orderType  订单
	 * @param orderStatus  订单状态
	 * @return  BigDecimal
	 * @since JDK 1.8
	 * @author zhunyunpeng
	 * @date 2017年4月24日
	 */
	BigDecimal getAmountByProductId(@Param("productId")String productId , @Param("orderType")Integer orderType , @Param("orderStatus")Integer orderStatus);
}