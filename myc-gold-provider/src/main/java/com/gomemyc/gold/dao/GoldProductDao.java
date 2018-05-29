package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldProduct;
import com.gomemyc.gold.entity.extend.Contract;
import com.gomemyc.gold.entity.extend.GoldProductExtend;
import com.gomemyc.gold.entity.extend.GoldProductIdAndCode;
import com.gomemyc.gold.entity.extend.GoldProductsExtend;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface GoldProductDao {
    int deleteByPrimaryKey(String id);

    int insert(GoldProduct record);

    int insertSelective(GoldProduct record);

    GoldProduct selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldProduct record);

    int updateByPrimaryKey(GoldProduct record);
    
	/**
	 * 
	 * 查询产品总数
	 * 
	 * @return  int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    int getGoldProductAmount(@Param("openTime") String openTime);
    
	/**
	 * 
	 * 查询产品列表
	 * 
	 * @param pageStart  起始条数
	 * @param pageSize  每一页显示的条数
	 * @return  List<GoldProduct>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    List<GoldProductExtend> getListGoldProduct(@Param("pageStart")int pageStart, @Param("pageSize") int pageSize,@Param("openTime") String openTime);

	/**
	 * 
	 * 查询产品详情
	 * 
	 * @param productId  产品id
	 * @return  GoldProduct
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	GoldProductsExtend getByProductId(@Param("productId")String productId);

	/**
	 * 
	 * 投资预下单,查询产品明细
	 * 
	 * @param productId  产品id
	 * @return  GoldProduct
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
    GoldProduct getInfoByProduct(@Param("productId")String productId);

	/**
	 * 
	 * 查询产品信息
	 * 
	 * @param productId 产品id
	 * @return  GoldProduct
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	GoldProductsExtend getObjByProductId(@Param("productId")String productId);

	/**
	 *
	 * 查询产品id
	 *
	 * @param  clearTime 结束时间
	 * @return  List<String>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月03日
	 */
	List<GoldProduct> getGoldProductByClearTime(@Param("clearTime")String clearTime);
	/**
	 *
	 * 查询产品信息
	 *
	 * @param openTime 开标时间
	 * @return  GoldProduct
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	List<GoldProductIdAndCode> selectIdAndCodeByOpenTime(@Param("openTime")String openTime);
	/**
	 *
	 * 查询产品信息
	 *
	 * @param openTime 当前时间
	 * @return  List<GoldProductIdAndCode>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年04月21日
	 */
	List<GoldProductIdAndCode> getIdAndCodeByOpenTime(@Param("openTime")String openTime);

	/**
	 *
	 * 通过userId获得合同所需信息
	 *
	 * @param productId 产品Id
	 * @return  Contract
	 * @since JDK 1.8
	 * @author zhuyunpeng
	 * @date 2017年3月14日
	 */
	Contract getContractByProductId(@Param("productId") String productId);
	/**
	 *
	 * 查询产品id
	 *
	 * @param  clearTime 结束时间
	 * @return  List<GoldProductIdAndCode>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年4月03日
	 */
	List<GoldProductIdAndCode> getIdAndCodeByClearTime(@Param("clearTime")String clearTime);

	/**
	 *
	 * 根据产品id，更新产品参数
	 * @param productId  （String）产品id
	 * @param status  (Integer)状态码
	 * @param settleTime 结标时
	 * @param valueTime 起息日
	 * @return Integer 影响的行数
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date  2017年04月14日
	 */
	Integer updateStatusByProductId(@Param("productId") String productId, @Param("status")  Integer status,@Param("settleTime") Date settleTime,@Param("valueTime") Date valueTime);

	/**
	 * 查询产品信息
	 * @param settleTime  结标时间
	 * @return List<GoldProductsExtendDTO>
	 * @author liujunhan
	 * @date  2017年04月15日
	 * @since JDK 1.8 findByProductId
	 */
	List<GoldProductsExtend> findProductsBySettleTime(@Param("settleTime") String settleTime);
	
	/**
	 * 根据当前时间，查询当天开标的产品
	 * 
	 * @param currentTime 当前时间  [yyyy-MM-dd]
	 * @return List<GoldProductsExtend>
	 * @author LiuQiangBin
	 * @date  2017年04月15日
	 * @since JDK 1.8 
	 */
	List<GoldProductsExtend> findProductsByCurrentTime(@Param("currentTime") String currentTime);

	/**
     * 根据Id修改状态值
     * @param productId 黄金产品Id
     * @param  status 要修改的状态值
     * @return int
     * @mbg.generated
     */
    int updateStatusById(@Param("productId")String productId, @Param("status")Integer status);

	/**
	  *
	  * 根据当前时间和状态码区间查询产品数量
	  *
	  * @param currentTime （String)当前时间
	  * @param statusStart (Integer)状态码开始
	  * @param statusEnd (Integer)状态码结束
	  * @return Integer 产品数量
	  * @author LiuQiangBin
	  * @date  2017年04月18日
	  * @since JDK 1.8
	  */
    int selectProductCountByCurrentTimeAndStatusStartAndStatusEnd(@Param("currentTime")String currentTime, @Param("statusStart")Integer statusStart, @Param("statusEnd")Integer statusEnd);
	/**
	 *
	 * 查询产品数量
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月20日
	 * @since JDK 1.8
	 */
	 List<String> listRegularProductIdsToSettle();

	/**
	 *
	 * 查询产品id集合根据产品状态
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月24日
	 * @since JDK 1.8
	 */
	List<GoldProductExtend> findIdByStatus(@Param("status") Integer status);

	/**
	 * 根据当前时间，查询当天开标的产品
	 *
	 * @param currentTime 当前时间  [yyyy-MM-dd]
	 * @return List<GoldProduct>
	 * @author LiuQiangBin
	 * @date  2017年04月15日
	 * @since JDK 1.8
	 */
	List<GoldProduct> productsByCurrentTime(@Param("currentTime") String currentTime);
}