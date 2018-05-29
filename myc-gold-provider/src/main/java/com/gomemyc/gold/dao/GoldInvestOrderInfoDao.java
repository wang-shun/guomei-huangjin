package com.gomemyc.gold.dao;

import org.apache.ibatis.annotations.Param;
import com.gomemyc.gold.entity.GoldInvestOrderInfo;

public interface GoldInvestOrderInfoDao {
    int deleteByPrimaryKey(String id);

    int insert(GoldInvestOrderInfo record);

    int insertSelective(GoldInvestOrderInfo record);

    GoldInvestOrderInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldInvestOrderInfo record);

    int updateByPrimaryKey(GoldInvestOrderInfo record);
    
	/**
	 * 
	 * 根据订单号,查询黄金投资订单详情表
	 * 
	 * @param reqNo  订单号
	 * @return  GoldInvestOrderInfo
	 * @since JDK 1.8
	 * @author LiuQiangBin
	 * @date 2017年3月24日
	 */
    GoldInvestOrderInfo selectByReqNo(@Param("reqNo")String reqNo);

}