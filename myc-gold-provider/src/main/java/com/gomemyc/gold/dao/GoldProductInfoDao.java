package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldProductInfo;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

public interface GoldProductInfoDao {
    int deleteByPrimaryKey(String id);

    int insert(GoldProductInfo record);

    int insertSelective(GoldProductInfo record);

    GoldProductInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldProductInfo record);

    int updateByPrimaryKey(GoldProductInfo record);

    /**
     *
     * 查询产品附加信息
     *
     * @param id 产品id
     * @return  GoldProductInfo
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月14日
     */
    GoldProductInfo getByid(@Param("productId") String productId);
    
    /**
    *
    * 利用乐观锁，更新产品剩余可投资金额
    *
    * @param id 产品id
    * @param version 版本号 (锁)
    * @param balance 新的剩余投资金额
    * @return  int
    * @since JDK 1.8
    * @author liuqiangbin
    * @date 2017年3月28日
    */
    int updateByProductIdAndVersion(@Param("productId") String productId, @Param("version") Integer version, @Param("balance") BigDecimal balance);
}