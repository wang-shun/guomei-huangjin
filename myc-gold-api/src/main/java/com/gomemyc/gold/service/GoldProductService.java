/** 
 * Project Name:myc-gold-api 
 * File Name:GoldProductService.java 
 * Package Name:com.gomemyc.gold.service 
 * Date:2017年3月6日下午2:35:50 
 * Copyright (c) 2017, tianbin@gomeholdings.com All Rights Reserved. 
 * 
*/  
  
package com.gomemyc.gold.service;

import com.gomemyc.common.page.Page;
import com.gomemyc.gold.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/** 
 * ClassName:GoldProductService <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2017年3月6日 下午2:35:50 <br/> 
 * @author   TianBin 
 * @version   
 * @since    JDK 1.8 
 * @see       
 * @description
 */

public interface GoldProductService {

	/**
	 * 
	 * 查询首页列表  
	 * 
	 * @author TianBin 
	 * @date  2017年03月06日
	 * @param page  当前页
	 * @param pageSize  每页条数
	 * @return 
	 * @since JDK 1.8
	 */
	Page<GoldProductDTO> listPageIndexList(int page,int pageSize);
	
	/**
	 * 
	 * 产品详细
	 * 
	 * @author TianBin 
	 * @date  2017年03月06日
	 * @param productId  产品ID
	 * @return 
	 * @since JDK 1.8
	 */
	GoldProductDetailsDTO findByProductId(String productId,String userId);
	
	
	/**
	 * 
	 * 投资预下单,查询产品明细
	 * 
	 * @author liujunhan 
	 * @date  2017年03月13日
	 * @param productId  产品ID
	 * @return 
	 * @since JDK 1.8
	 */
	GoldProductDetailsDTO findInfoByProductId(String productId);
	
	/**
	 *
	 * 根据开标时间查询产品id和code
	 *
	 * @author liujunhan
	 * @date  2017年03月22日
	 * @param  openTime 开标时间
	 * @return GoldProductDetailsDTO对象
	 * @since JDK 1.8
	 */
	List<GoldProductIdAndCodeDTO> findIdAndCodeByOpenTime(String openTime);

	/**
	 *
	 * 根据还清时间查询产品id和code
	 *
	 * @author liujunhan
	 * @date  2017年03月22日
	 * @param  clearTime 还清时间
	 * @return GoldProductDetailsDTO对象
	 * @since JDK 1.8
	 */
	List<GoldProductIdAndCodeDTO> findIdAndCodeByClearTime(String clearTime);
	
	/**
	 *
	 * 调用黄金钱包接口，查询所有的产品信息
	 * 
	 * @param start (Integer) 起始记录行数(选填)
	 * @param size  (Integer) 获取记录数大小(选填)
	 * @return List<GoldQueryTimeProductDTO>
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试 
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date  2017年03月26日
	 */
	List<GoldQueryTimeProductDTO> queryTimeProduct(Integer start, Integer size);
	/**
	 *
	 * 根据产品id，更新产品状态status
	 *
	 * @param productId  （String）产品id
	 * @param stusas  (Integer)状态码
	 * @return Integer 影响的行数
	 * @ServiceException
	 *                30000:参数错误
	 *                50000:操作失败，请重试
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date  2017年04月14日
	 */
	Integer updateStstusByProductId(String productId, Integer stusas, Date settleTime, Date valueTime);

	/**
	 * 查询产品信息服务
	 * @author liujunhan
	 * @date  2017年03月14日
	 * @param productId  产品ID
	 * @return GoldProductDetailsDTO
	 * @since JDK 1.8 findByProductId
	 */
	 GoldProductsExtendDTO serviceFindInfoByProductId(String productId);


	/**
	 * 查询产品信息
	 * @author liujunhan
	 * @date  2017年03月14日
	 * @param settleTime  结标时间
	 * @return GoldProductDetailsDTO
	 * @since JDK 1.8 findByProductId
	 */
	 List<GoldProductsExtendDTO> findProductsBySettleTime(String settleTime);

	 /**
	  * 根据当前时间，查询当天开标的产品
	  * 
	  * @param currentTime 当前时间  [yyyy-MM-dd]
	  * @return List<GoldProductDetailsDTO>
	  * @author LiuQiangBin
	  * @date  2017年04月15日
	  * @since JDK 1.8 
	  */
	  List<GoldProductsExtendDTO> findProductsByCurrentTime(String currentTime);
	  
	 /**
	  *
	  * 根据产品id，更新产品状态status
	  *
	  * @param productId  （String）产品id
   	  * @param status  (Integer)状态码
	  * @return Integer 影响的行数
	  * @author LiuQiangBin
	  * @date  2017年04月17日
	  * @since JDK 1.8
	  */
	  Integer updateStatusById(String productId, Integer status);
	  
	 /**
	  *
	  * 查询产品数量
	  *
	  * @return Integer 产品数量
	  * @author LiuQiangBin
	  * @date  2017年04月18日
	  * @since JDK 1.8
	  */
	  Integer selectProductCount();

	/**
	 *
	 * 查询产品id集合
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月20日
	 * @since JDK 1.8
	 */
	List<String> selectIdByStatus();

	/**
	 *
	 * 查询产品id集合根据产品状态
	 *
	 * @return List<String></> 产品数量
	 * @author liujunhan
	 * @date  2017年04月20日
	 * @since JDK 1.8
	 */
	List<GoldProductsExtendDTO> findIdByStatus(Integer status);

	/**
	 *
	 * 查询产品信息
	 *
	 * @param openTime 当前时间
	 * @return  List<GoldProductIdAndCodeDTO>
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年04月21日
	 */
	List<GoldProductIdAndCodeDTO> getProductIdCodeByOpenTime(String openTime);

}