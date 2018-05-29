package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldDueAccountCheck;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface GoldDueAccountCheckDao {

    int deleteByPrimaryKey(String id);

    int insert(GoldDueAccountCheck record);

    int insertSelective(GoldDueAccountCheck record);

    GoldDueAccountCheck selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldDueAccountCheck record);

    int updateByPrimaryKey(GoldDueAccountCheck record);
    /**
     * 根据创建时间查询今天所有的需要对比的数据
     * This method corresponds to the database table tb_gold_due_account_check
     *
     * @mbg.generated
     */
    List<GoldDueAccountCheck> getByCreateTime(@Param("createTime") String createTime);

    /**
     * 根据订单号更新对比结果
     * This method corresponds to the database table tb_gold_due_account_check
     *
     * @mbg.generated
     */
    int updateComparingStatusBuOrderNo(@Param("orderNo") String orderNo, @Param("comparingStatus") Integer comparingStatus);

    /**
     * 根据订单号和对账数据类型查询
     * @param reqNo 订单号
     * @param  dataCheckType 对账数据类型
     * @return GoldDueAccountCheck
     * @mbg.generated
     */
    GoldDueAccountCheck getByReqNoAndDataCheckType(@Param("reqNo") String reqNo, @Param("dataCheckType")Integer dataCheckType);
    /**
     * 根据订单号和对账数据类型查询
     * @param orderNo 订单号
     * @param  dataCheckType 对账数据类型
     * @return GoldDueAccountCheck
     * @mbg.generated
     */
    GoldDueAccountCheck getByOrderNo(@Param("orderNo")String orderNo,@Param("dataCheckType")Integer dataCheckType);
}