package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldDueSumAccountCheck;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GoldDueSumAccountCheckDao {

    int deleteByPrimaryKey(String id);

    int insert(GoldDueSumAccountCheck record);

    int insertSelective(GoldDueSumAccountCheck record);

    GoldDueSumAccountCheck selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldDueSumAccountCheck record);

    int updateByPrimaryKey(GoldDueSumAccountCheck record);

    /**
     * 查询今日产生的数据
     * @param createTime
     * @return  List<GoldDueSumAccountCheck>
     */
    List<GoldDueSumAccountCheck> getByCreateTime(@Param("createTime") String createTime);
    /**
     * 更新对比结果
     * @param orderNo
     * @param comparingStatus
     * @return int
     */
    int updateComparingStatusByOrderNo(@Param("orderNo") String orderNo, @Param("comparingStatus") Integer comparingStatus);
    /**
     * 根据订单号和对账数据类型查询
     * @param reqNo 订单号
     * @param  dataCheckType 对账数据类型
     * @return GoldDueSumAccountCheck
     * @mbg.generated
     */
    GoldDueSumAccountCheck getByReqNoAndDataCheckType(@Param("reqNo") String reqNo, @Param("dataCheckType") Integer dataCheckType);
    /**
     * 根据订单号和对账数据类型查询
     * @param reqNo 订单号
     * @param  dataCheckType 对账数据类型
     * @return GoldDueSumAccountCheck
     * @mbg.generated
     */
    GoldDueSumAccountCheck getByOrderNoAndDataCheckType(@Param("orderNo") String orderNo, @Param("dataCheckType") Integer dataCheckType);
}